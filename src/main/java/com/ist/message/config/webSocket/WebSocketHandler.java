package com.ist.message.config.webSocket;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ist.message.common.util.HttpUtil;
import com.ist.message.common.util.RedisUtil;
import com.ist.message.config.IstConfig;
import com.ist.message.config.NettyConfig;
import com.ist.message.service.ChatService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IstConfig istConfig;
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    private ChatService chatService;

    private static int temp = 0;

    private static AtomicInteger pingAtomicInteger = new AtomicInteger();
    /**
     * 一旦连接，第一个被执行
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded 被调用"+ctx.channel().id().asLongText());
        // 添加到channelGroup 通道组
        NettyConfig.getChannelGroup().add(ctx.channel());
        //ctx.channel().writeAndFlush(new TextWebSocketFrame("connected"));
        //ctx.channel().close();
//        if (temp==0){
//            ctx.pipeline().remove(WebSocketHandler.class);
//            temp++;
//        }else {
//            System.out.println("else");
//        }
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String message  = msg.text();
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        String userId = ctx.channel().attr(key).get();
        log.info("channelId:" + ctx.channel().id().asLongText() + ",userID:" + userId + ",message:" + message+ ",ChannelSize:" + NettyConfig.getChannelGroup().size() + ",userChannelMap:" + NettyConfig.getUserChannelMap().size());
        if ("ping".equals(message)) {
            // 回复消息
            ctx.channel().writeAndFlush(new TextWebSocketFrame(message));
            return;
        }
        if (checkMsg(message)){
            //处理互相发送消息
            chatService.dealOnMessage(message,ctx.channel());
        }else {
            // 回复消息
            ctx.channel().writeAndFlush(new TextWebSocketFrame("risky"));
            return;
        }
    }
    private Boolean checkMsg(String msg) throws UnsupportedEncodingException {
        if (istConfig.getSwitchCheck()!=1){
            return true;
        }
        String accessToken = (String) redisUtil.get("wx.accessToken");
        if (StringUtils.isBlank(accessToken)){
            accessToken = getAccessToken();
            if (StringUtils.isNotBlank(accessToken)){
                redisUtil.set("wx.accessToken",accessToken,7200);
            }
        }
        Map<String,String> params = new HashMap<>();
        params.put("content",msg);
        String result = HttpUtil.doPostJson(istConfig.getMsgCheckUrl()+"?access_token=" + accessToken, params);
        log.info("checkMsg content:{},result:{},accessToken:{}",msg,result,accessToken);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getInteger("errcode")==0){
            return true;
        }
        return false;
    }

    //获取access_token
    private String getAccessToken() throws UnsupportedEncodingException {

        String url = String.format(istConfig.getWeiXinPayAccessUrl(),istConfig.getWeiXinPayAppId(),istConfig.getWeiXinPayAppSecret());
        String result = HttpUtil.doGet(url,null);
        JSONObject jsonObject = JSON.parseObject(result);
        log.info("result:{}",result);
        return jsonObject.getString("access_token");
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerRemoved 被调用"+ctx.channel().id().asLongText());
        // 删除通道
        NettyConfig.getChannelGroup().remove(ctx.channel());
        removeUserId(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("异常：{}",cause.getMessage());
        // 删除通道
//        NettyConfig.getChannelGroup().remove(ctx.channel());
//        removeUserId(ctx);
//        ctx.close();
    }

    /**
     * 删除用户与channel的对应关系
     * @param ctx
     */
    private void removeUserId(ChannelHandlerContext ctx){
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        String userId = ctx.channel().attr(key).get();
        Channel channel = NettyConfig.getUserChannelMap().get(userId);


        if(channel.id()==ctx.channel().id()){
            NettyConfig.getUserChannelMap().remove(userId);
        }
        log.info("removeUserId 被调用"+ctx.channel().id().asLongText() + ",userId:" + userId + ",ChannelSize" + NettyConfig.getChannelGroup().size() + ",userChannelMap:" + NettyConfig.getUserChannelMap().size());
    }
}
