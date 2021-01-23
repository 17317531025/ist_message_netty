package com.ist.message.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ist.message.common.CodeConstant;
import com.ist.message.common.ResultConstant;
import com.ist.message.common.pojo.ResultVO;
import com.ist.message.common.util.DateUtil;
import com.ist.message.common.util.RedisKeyUtil;
import com.ist.message.common.util.RedisUtil;
import com.ist.message.config.IstConfig;
import com.ist.message.config.IstEnum;
import com.ist.message.config.MessageEnum;
import com.ist.message.config.NettyConfig;
import com.ist.message.config.kafka.MsgData;
import com.ist.message.dao.MsgMapper;
import com.ist.message.dao.UserFriendMapper;
import com.ist.message.domain.Msg;
import com.ist.message.domain.MsgExample;
import com.ist.message.domain.UserFriend;
import com.ist.message.domain.dto.Response;
import com.ist.message.service.ChatService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service(value = "chatService")
public class ChatServiceImpl extends BaseServiceImpl implements ChatService, ApplicationContextAware {
    private static KafkaTemplate kafkaTemplate;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static Map<String, Session> drWebSocketSet = new ConcurrentHashMap<>(); //医生web
    @Autowired
    private MsgMapper msgMapper;
    @Autowired
    private UserFriendMapper userFriendMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IstConfig istConfig;
    @Autowired
    private RestTemplate restTemplate;

    private int isKafka = 0;
    @Override
    public List<MsgData> queryByParams(Map<String, Object> params) {
        MsgExample example = new MsgExample();
        example.createCriteria().andReceiverIdEqualTo(Long.parseLong(params.get("receiverId").toString())).andStatusEqualTo((short)-1)
        .andTimeidGreaterThan(Long.parseLong(params.get("timeId").toString())).andCreatetimeGreaterThanOrEqualTo(DateUtil.formatDateStr(params.get("createTime").toString(),DateUtil.PATTERN_DATE_TIME));
        List<Msg> msgs = msgMapper.selectByExample(example);
        List<MsgData> msgDataList = new ArrayList<>();
        for (Msg msg:msgs){
            MsgData msgData = new MsgData();
            msgData.setContent(msg.getContent());
            msgData.setContentType(msg.getContenttype().intValue());
            msgData.setCreateTime(DateUtil.getString(msg.getCreatetime(), DateUtil.PATTERN_DATE_TIME));
            msgData.setMsgType(msg.getMsgtype().intValue());
            msgData.setNo(msg.getNo().toString());
            msgData.setSender(msg.getSender().toString());
            msgData.setShopId(msg.getShopid()==null?"":msg.getShopid().toString());
            msgData.setTalker(msg.getTalker().toString());
            msgData.setStatus(msg.getStatus().intValue());
            msgData.setTimeId(msg.getTimeid().toString());
            msgData.setType(msg.getType().intValue());
            msgDataList.add(msgData);
        }
        //更新为已读
        return msgDataList;
    }

    @Override
    public int updateByParams(Map<String, Object> params) {
        return msgMapper.updateByParams(params);
    }

    @Override
    public void updateByMsgId(long timeId,short status) {
        Msg updateMsg = new Msg();
        updateMsg.setStatus(status);
        MsgExample example = new MsgExample();
        example.createCriteria().andTimeidEqualTo(timeId);
        msgMapper.updateByExampleSelective(updateMsg,example);
    }

    @Override
    public void saveSocketMsg(Msg msg) {
        msgMapper.insertSelective(msg);
    }

    @Override
    public void saveUserFriend(UserFriend userFriend) {
        userFriendMapper.insertSelective(userFriend);
    }

    @Override
    public void dealOpen(String userId, Long timeId, Session session) {
        //        logger.info("getMaxTextMessageBufferSize:"+session.getMaxTextMessageBufferSize());
//        logger.info("getMaxBinaryMessageBufferSize:"+session.getMaxBinaryMessageBufferSize());
//        session.setMaxTextMessageBufferSize(4*1024);
//        session.setMaxBinaryMessageBufferSize(4*1024);
        logger.info("sessionId:" + session.getId());
        //校验tokenId
//        UserVerifyTokenReq userVerifyTokenReq = new UserVerifyTokenReq();
//        userVerifyTokenReq.setTokenId(tokenId);
//        userVerifyTokenReq.setUserId(userId);
//        UserVerifyTokenResp userVerifyTokenResp = new UserVerifyTokenResp();
//        userTokenService.verifyToken(userVerifyTokenReq,userVerifyTokenResp);
//        if(!ResultConstant.VERIFY_TOKEN_SUCC_CODE.equals(userVerifyTokenResp.getCode())){
//            try {
//                session.getAsyncRemote().sendText("token校验失败");
//                session.close();
//            } catch (IOException e) {
//                logger.error("sendText",e);
//            }
//            return;
//        }

        Session sessionTo = drWebSocketSet.get(userId);
        if (null!=sessionTo){
            try {
                sessionTo.close();
            } catch (IOException e) {
                logger.error("close session userId: "+userId+", err:",e);
            }
            drWebSocketSet.put(userId, session);
        }else{
            //查看缓存有没有
//            String redisObjectUrl = (String) redisUtil.get(RedisKeyUtil.getUIDWebSocketURL(userId));
//            if (null != redisObjectUrl && !StringUtils.equals(istConfig.getCurrentNodeUrl(),redisObjectUrl)) {
//                //通知其他节点关闭该session
//                ResultVO resultVO = restTemplate.getForObject(redisObjectUrl+"/closeSession?userId=" + userId,ResultVO.class);
//                logger.info("close session result:" + resultVO.toString());
//            }
            drWebSocketSet.put(userId, session);
        }
//        redisUtil.set(RedisKeyUtil.getUIDWebSocketURL(userId), istConfig.getCurrentNodeUrl(), CodeConstant.RedisSaveTime.USER_ID_SOCKET_URL);
        logger.info("用户连接:"+userId+",当前在线人数为drWebSocketSet:" + getOnlineCount() + ",getOpenSessions:" + session.getOpenSessions().size());
        //查看是否有未读消息
        if (timeId==null || timeId!=-1){
            Map<String,Object> params = new HashMap<>();
            params.put("receiverId",userId);
            params.put("status",-1);
            params.put("timeId",timeId);
            List<MsgData> chatMsgs = this.queryByParams(params);
            if (chatMsgs!=null && chatMsgs.size()>0){
                try {
                    session.getAsyncRemote().sendText(JSON.toJSONString(chatMsgs));
                } catch (Exception e) {
                    logger.error("session.getAsyncRemote().sendObject.error:",e);
                }
                this.updateByParams(params);
            }else {
                try {
                    session.getBasicRemote().sendText("ok");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private JSONObject getJsonObjectStatusRespBack(JSONObject jsonObject,long timeId) {
        long sender = jsonObject.getLong("sender")==null?0:jsonObject.getLong("sender");
        long talker = jsonObject.getLong("talker")==null?0:jsonObject.getLong("talker");
        long no = jsonObject.getLong("no")==null?0:jsonObject.getLong("no");
        JSONObject jsonObjectStatus  = new JSONObject();
        jsonObjectStatus.put("sender",sender);
        jsonObjectStatus.put("talker",talker);
        jsonObjectStatus.put("no",no);
        jsonObjectStatus.put("type",jsonObject.getIntValue("type"));
        jsonObjectStatus.put("status",99);//收到正常反馈
        jsonObjectStatus.put("timeId",timeId);
        return jsonObjectStatus;
    }
    @Override
    public void dealOnClose(Session session) {
        Map<String, String> pathParameters = session.getPathParameters();
        String userId = pathParameters.get("userId"); //从session中获取userId
        Session removeSession = drWebSocketSet.remove(userId);
        if (null!=removeSession){
//            drWebSocketSet.remove(userId);
            //从set中删除
//            subOnlineCount();
            logger.info("用户退出:"+userId+",当前在线人数为drWebSocketSet:" + getOnlineCount() + ",getOpenSessions:" + session.getOpenSessions().size());
        }
//        redisUtil.del(RedisKeyUtil.getUIDWebSocketURL(userId));
    }

    @Override
    public void dealOnMessage(String message, Channel channel){
        try {
            if (message.substring(0,10).equals("{\"status\":\"99\"")){
                //反馈
                return;
            }
            //{"status":"01","type":"01","len":"0001","receiverId":[20010031],
            //{"status":"01","type":"01","len":"0001","receiverId",[10001],"content":"hello websocket","sender":"10001","msgType":"1","contentType":"1","no":"1","senderName":"世纪风","talker":"10001"}
            if(message.substring(15,26).equals("\"type\":\"01\"")){
                long timeId = System.currentTimeMillis();
                sendMessageStr(message,timeId); //字符串解析
            }else{
                JSONObject jsonObject = JSONObject.parseObject(message);
                int type = jsonObject.getInteger("type");
                switch (type){
                    case 0:
                        // 获取用户ID,关联channel{"uid","12","tokenId":""}
                        String uid = jsonObject.getString("uid");
                        if (StringUtils.isNoneBlank(uid)){
                            Channel channelMap = NettyConfig.getUserChannelMap().get(uid);
                            if (channelMap!=null){
                                channelMap.writeAndFlush(new TextWebSocketFrame("offline"));
                                channelMap.close();
                            }
                            NettyConfig.getUserChannelMap().put(uid,channel);
                            // 将用户ID作为自定义属性加入到channel中，方便随时channel中获取用户ID
                            AttributeKey<String> key = AttributeKey.valueOf("userId");
                            channel.attr(key).setIfAbsent(uid);
                            // 回复消息
//                            channel.writeAndFlush(new TextWebSocketFrame("服务器连接成功！"));
                            if (isKafka==1){
                                redisUtil.set(RedisKeyUtil.getUIDWebSocketURL(uid), istConfig.getCurrentNodeUrl(), CodeConstant.RedisSaveTime.USER_ID_SOCKET_URL);
                            }
                            return;
                        }
                        break;
                    case 2:
                        statusNotify(jsonObject); //消息状态通知(已读,未读,撤回)
                        break;
                    case 3:
                        //重新登录后msgIdMax传递
//                        JSONObject dataJson = jsonObject.getJSONObject("data");
                        String timeIdMax = jsonObject.getString("timeId");
                        String createTimeMax = jsonObject.getString("createTimeMax");
                        //查看是否有未读消息
                        Map<String,Object> params = new HashMap<>();
                        params.put("receiverId",jsonObject.getString("receiverId"));
                        params.put("createTime",createTimeMax);
                        params.put("timeId",timeIdMax);
                        List<MsgData> chatMsgs = this.queryByParams(params);
                        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatMsgs)));
                        break;
                    case 4:
                        long timeId = System.currentTimeMillis();
                        //好友操作通知
                        sendMessage(jsonObject, channel,timeId);
                        break;
                    case 5:
                        //群组操作通知
                        long timeIds = System.currentTimeMillis();
//                        sendMessage(jsonObject, channel,timeIds);
                        sendMessageStr(message,timeIds); //字符串解析
                        break;
                    default:
                        logger.info("type:" + type + ",is err");
                        channel.writeAndFlush(new TextWebSocketFrame("type is err"));
                }
            }
        }catch (Exception e){
            logger.info("dealOnMessage.err:",e);
        }
    }

    @Override
    public void dealOnError(Session session, Throwable error) {
        Map<String, String> pathParameters = session.getPathParameters();
        String userId = pathParameters.get("userId"); //从session中获取userId
//        redisUtil.del(RedisKeyUtil.getUIDWebSocketURL(userId));
        //onClose(session);
        error.printStackTrace();
        logger.error("webscoket发生错误!userId:"+ userId + "," + error);
    }
    /**
     * 发送消息(subStr)
     *
     * @param message
     * @param timeId
     * @throws IOException
     */
    public void sendMessageStr(String message,long timeId)  {
        try {

            if (isKafka==1){
                //全部走kafka
                ListenableFuture<SendResult<String, String>> sender = kafkaTemplate.send(new ProducerRecord<>(istConfig.getKafkaTopic(), message));
                sender.addCallback(result -> {}, ex -> logger.error("数据发送失败!"));

            }else{
                //本地有直接发出去
                int len = Integer.valueOf(message.substring(34,38));
                if(message.substring(40,54).equals("\"receiverId\":[") && message.substring((54+13*(len-1))+12,(54+13*(len-1))+12+1).equals("]")){
                    for(int j=0;j<len;j++){
                        String receiverId = message.substring(54+13*j,(54+13*j)+12);
                        sendMessageToReceiverId(receiverId,message);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("sendMessageStr.error:", e);
        }
    }

    private void sendMessageToReceiverId(String receiverId,String message){
        //将消息入库
        //TODO 这里可以进行优化。可以首先根据接收方的userId,即receiver_id判断接收方是否在当前服务器，若在，直接获取session发送即可就不需要走Kafka了，节约资源
        Channel channelTo = NettyConfig.getUserChannelMap().get(receiverId);
        if (null!=channelTo) {
            logger.info("local session rec:" + receiverId);
            channelTo.writeAndFlush(new TextWebSocketFrame(message));
        } else {
            logger.info("else if ...");
            //判断缓存
//                            String sendMessageUrl = (String) redisUtil.get(RedisKeyUtil.getUIDWebSocketURL(receiverId));
//                            //sendMessageUrl = "http://127.0.0.1:8010/ist_im/sendMessage";
//                            if (StringUtils.isNoneBlank(sendMessageUrl)) {
//                                getMsgFromJson(jsonObject, MessageEnum.MsgStatus.NOT_READ.getStatus(),timeId,receiverId);
//                                ResultVO resultVO = restTemplate.postForObject(sendMessageUrl + "sendMessage", jsonObject.toJSONString(), ResultVO.class);
//                                logger.info("sendMessage resp==>"+ resultVO.toString());
//                            } else {
//                                logger.info("offine:" + receiverId);
//                                getMsgFromJson(jsonObject, MessageEnum.MsgStatus.NOT_DELIVERY.getStatus(),timeId,receiverId);
//                                kafkaTemplate.send("chatMessage", jsonObject);
//                            }
        }
    }
    /**
     * 发送消息
     *
     * @param jsonObject
     * @param channel
     * @throws IOException
     */
    public void sendMessage(JSONObject jsonObject, Channel channel,long timeId)  {
        try {
            JSONArray receiverIdArray = jsonObject.getJSONArray("receiverId"); //接受者ID
            if (receiverIdArray==null||receiverIdArray.size()==0){
                channel.writeAndFlush(new TextWebSocketFrame("is no receiverId"));
                logger.warn("sendMessage message:" + jsonObject.toJSONString() + ",is no receiverId");
            }
            else {
                for (int i = 0;i<receiverIdArray.size();i++){
                    String receiverId = receiverIdArray.get(i).toString();
                    //将消息入库
                    //TODO 这里可以进行优化。可以首先根据接收方的userId,即receiver_id判断接收方是否在当前服务器，若在，直接获取session发送即可就不需要走Kafka了，节约资源
//                        Session sessionTo = drWebSocketSet.get(receiverId);
                    Channel channelTo = NettyConfig.getUserChannelMap().get(receiverId);
                    if (null!=channelTo) {
                        jsonObject.put("receiverId",receiverId);
                        getMsgFromJson(jsonObject, MessageEnum.MsgStatus.NOT_READ.getStatus(),timeId,receiverId);
                        logger.info("local session rec:" + receiverId);
//                            channelTo.getAsyncRemote().sendText(jsonObject.toJSONString());
                        channelTo.writeAndFlush(new TextWebSocketFrame(jsonObject.toJSONString()));

                    } else {
                        logger.info("else if ...");
                        //判断缓存
                        String sendMessageUrl = (String) redisUtil.get(RedisKeyUtil.getUIDWebSocketURL(receiverId));
                        //sendMessageUrl = "http://127.0.0.1:8010/ist_im/sendMessage";
                        if (StringUtils.isNoneBlank(sendMessageUrl)) {
                            getMsgFromJson(jsonObject, MessageEnum.MsgStatus.NOT_READ.getStatus(),timeId,receiverId);
                            ResultVO resultVO = restTemplate.postForObject(sendMessageUrl + "sendMessage", jsonObject.toJSONString(), ResultVO.class);
                            logger.info("sendMessage resp==>"+ resultVO.toString());
                        } else {
                            logger.info("offine:" + receiverId);
                            getMsgFromJson(jsonObject, MessageEnum.MsgStatus.NOT_DELIVERY.getStatus(),timeId,receiverId);
                            kafkaTemplate.send("chatMessage", jsonObject);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("sendMessage.error:", e);
        }
    }

    private void getMsgFromJson(JSONObject jsonObject,short status,long timeId,String receiverId) throws Exception {
        //IsInsertMsgSwitch开关打开全部插入消息 否则只插入离线消息
        if ("1".equals(istConfig.getIsInsertMsgSwitch()) || (!"1".equals(istConfig.getIsInsertMsgSwitch()) && status==MessageEnum.MsgStatus.NOT_DELIVERY.getStatus())){
            //新增msg
            Date now = new Date();
            Msg msg = new Msg();
            msg.setType(jsonObject.getShort("type"));
            msg.setContent(jsonObject.getString("content"));
            Short msgType = jsonObject.getShort("msgType");
            msg.setMsgtype(msgType);
            msg.setReceiverId(receiverId);
            msg.setContenttype(jsonObject.getShort("contentType"));
            msg.setSender(jsonObject.getLong("sender"));
            msg.setTalker(jsonObject.getLong("talker")==null?0:jsonObject.getLong("talker"));
            msg.setCreatetime(now);
            msg.setUpdatetime(now);
            if (jsonObject.getShort("status")!=null){
                msg.setStatus(jsonObject.getShort("status"));
            }else{
                msg.setStatus(status);
            }
            msg.setNo(jsonObject.getLong("no"));
            msg.setTimeid(timeId);
            this.saveSocketMsg(msg);
        }

        //1好友请求通过
//        if (jsonObject.getShort("type")==4 && jsonObject.getShort("status")!=null && jsonObject.getShort("status")==1){
//            UserFriend userFriend = new UserFriend();
//            userFriend.setUserid(Long.valueOf(receiverId));
//            userFriend.setFuserid(jsonObject.getLong("sender"));
//            userFriend.setGrouptype(IstEnum.GroupType.OTHER.getCode());
//            userFriend.setFriendgroupid(0L);
//            userFriend.setCreatetime(new Date());
//            userFriend.setUpdatetime(new Date());
//            this.saveUserFriend(userFriend);
//
//            UserFriend userFriend1 = new UserFriend();
//            userFriend1.setUserid(jsonObject.getLong("sender"));
//            userFriend1.setFuserid(Long.valueOf(receiverId));
//            userFriend1.setGrouptype(IstEnum.GroupType.OTHER.getCode());
//            userFriend1.setFriendgroupid(0L);
//            userFriend1.setCreatetime(new Date());
//            userFriend1.setUpdatetime(new Date());
//            this.saveUserFriend(userFriend1);
//        }
//        //5加入组
//        else if(jsonObject.getShort("type")==5 && jsonObject.getShort("status")!=null && jsonObject.getShort("status")==1){
//
//
//        }
        jsonObject.put("timeId",timeId);
    }

    private void getMsgFromJson(String message,short status,long timeId,String receiverId) throws Exception {
        //IsInsertMsgSwitch开关打开全部插入消息 否则只插入离线消息
        if ("1".equals(istConfig.getIsInsertMsgSwitch()) || (!"1".equals(istConfig.getIsInsertMsgSwitch()) && status==MessageEnum.MsgStatus.NOT_DELIVERY.getStatus())){
            //新增msg
//            Date now = new Date();
//            Msg msg = new Msg();
//            msg.setType(jsonObject.getShort("type"));
//            msg.setContent(jsonObject.getString("content"));
//            Short msgType = jsonObject.getShort("msgType");
//            msg.setMsgtype(msgType);
//            msg.setReceiverId(receiverId);
//            msg.setContenttype(jsonObject.getShort("contentType"));
//            msg.setSender(jsonObject.getLong("sender"));
//            msg.setTalker(jsonObject.getLong("talker")==null?0:jsonObject.getLong("talker"));
//            msg.setCreatetime(now);
//            msg.setUpdatetime(now);
//            if (jsonObject.getShort("status")!=null){
//                msg.setStatus(jsonObject.getShort("status"));
//            }else{
//                msg.setStatus(status);
//            }
//            msg.setNo(jsonObject.getLong("no"));
//            msg.setTimeid(timeId);
//            this.saveSocketMsg(msg);
        }

//        //1好友请求通过
//        if (jsonObject.getShort("type")==4 && jsonObject.getShort("status")!=null && jsonObject.getShort("status")==1){
//            UserFriend userFriend = new UserFriend();
//            userFriend.setUserid(jsonObject.getLong("receiverId"));
//            userFriend.setFuserid(jsonObject.getLong("sender"));
//            userFriend.setGrouptype(IstEnum.GroupType.OTHER.getCode());
//            userFriend.setFriendgroupid(0L);
//            userFriend.setCreatetime(new Date());
//            userFriend.setUpdatetime(new Date());
//            this.saveUserFriend(userFriend);
//
//            UserFriend userFriend1 = new UserFriend();
//            userFriend1.setUserid(jsonObject.getLong("sender"));
//            userFriend1.setFuserid(jsonObject.getLong("receiverId"));
//            userFriend1.setGrouptype(IstEnum.GroupType.OTHER.getCode());
//            userFriend1.setFriendgroupid(0L);
//            userFriend1.setCreatetime(new Date());
//            userFriend1.setUpdatetime(new Date());
//            this.saveUserFriend(userFriend1);
//        }
//        //5加入组
//        else if(jsonObject.getShort("type")==5 && jsonObject.getShort("status")!=null && jsonObject.getShort("status")==1){
//
//
//        }
    }

    /**
     * 消息状态通知
     *
     * @param jsonObject
     * @param channel
     * @throws IOException
     */
    public void statusNotify(JSONObject jsonObject)  {
        String receiverId = jsonObject.getString("receiverId"); //接受者ID
        if (receiverId==null){
            logger.warn("statusNotify message:"+jsonObject.toJSONString()+",is no receiverId");
        }else{
            try {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i=0;i<data.size();i++){
                    JSONObject  jsonObjectData = data.getJSONObject(i);
                    this.updateByMsgId(jsonObjectData.getLong("timeId"),jsonObjectData.getShort("status"));
                }
                Channel channelTo = NettyConfig.getUserChannelMap().get(receiverId);
                if (null!=channelTo){
                    channelTo.writeAndFlush(new TextWebSocketFrame(data.toJSONString()));
                }else {
                    //判断缓存
//                        String sendMessageUrl = (String) redisUtil.get(RedisKeyUtil.getUIDWebSocketURL(receiverId));
//                        //sendMessageUrl = "http://127.0.0.1:8010/ist_im/sendMessage";
//                        if (StringUtils.isNoneBlank(sendMessageUrl)){
//                            ResultVO resultVO = restTemplate.postForObject(sendMessageUrl + "sendMessage", data.toJSONString(), ResultVO.class);
//                            logger.info("statusNotify" + resultVO.toString());
//                        }
                }
            }catch (Exception e){
                logger.error("statusNotify sendMessage.error:",e);
            }
        }

    }

    public static int getOnlineCount() {
        return drWebSocketSet.size();
    }
    @Override
    public void closeSession(String userId) {
        try {
            Channel channelTo = NettyConfig.getUserChannelMap().get(userId);
            if (channelTo!=null){
                channelTo.close();
            }
        }catch (Exception e){
            logger.error("closeSession.error:",e);
        }
    }

    @Override
    public Response urlReceiveMsg(String message, String receiverId) {

//        JSONObject jsonObject = JSONObject.parseObject(message);
        try {
//            String receiverId = jsonObject.getString("receiverId"); //接受者ID
            if (StringUtils.isBlank(message) || StringUtils.isBlank(receiverId)){
                return Response.fail(ResultConstant.PARAM_ERROR_MSG);
            }
            Channel channelTo = NettyConfig.getUserChannelMap().get(receiverId);
            if (null!=channelTo) {
                logger.info("local session rec:" + receiverId);
                channelTo.writeAndFlush(new TextWebSocketFrame(message));
            } else {
                logger.warn("urlReceiveMsg " + message + "不存在");
                return Response.fail(ResultConstant.SOCKET_USER_NOT_EXIST_CURRENT_NODE_MSG);
            }
        }catch (Exception e){
            logger.error("urlReceiveMsg.error:",e);
            return Response.fail(ResultConstant.APP_ERROR_MSG);
        }
        return Response.ok();
    }

    @Override
    public void kafkaReceiveMsg(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        try {
            String receiver_id = jsonObject.getString("receiverId"); //接受者ID
            if (drWebSocketSet.get(receiver_id) != null) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i=0;i<data.size();i++){
                    JSONObject  jsonObjectData = data.getJSONObject(i);
                    long msgId = jsonObjectData.getLong("timeId");
                    this.updateByMsgId(msgId,(short) 0);
                }
                drWebSocketSet.get(receiver_id).getAsyncRemote().sendText(data.toJSONString()); //进行消息发送
            }
            else{
                logger.warn(jsonObject + "不存在");
            }
        }catch (Exception e){
            logger.error("kafkaReceiveMsg.error:",e);
        }
    }

    @Override
    public void kafkaCloseWebsocket(String closeMessage) {
        JSONObject jsonObject = JSONObject.parseObject(closeMessage);
        String userId = jsonObject.getString("userId");
        Session removeSession = drWebSocketSet.remove(userId);
        if (null!=removeSession){
//            Session removeSession = ChatServiceImpl.drWebSocketSet.remove(userId);
            //从set中删除
//            subOnlineCount();
            logger.info("用户退出:"+userId+",当前在线人数为drWebSocketSet:" + getOnlineCount() + ",getOpenSessions:" + removeSession.getOpenSessions().size());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (kafkaTemplate == null) {
            kafkaTemplate = applicationContext.getBean(KafkaTemplate.class); //获取kafka的Bean实例
        }
        isKafka = istConfig.getIsKafka();
    }
}
