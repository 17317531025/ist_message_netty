//package com.ist.message.config.kafka;
//
//import com.ist.message.config.IstConfig;
//import com.ist.message.service.ChatService;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//import javax.websocket.*;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.util.Map;
//
//@ServerEndpoint("/imserver/{userId}/{timeId}")
//@Component
//public class ChatWebsocket implements ApplicationContextAware {
//    private Log logger = LogFactory.getLog(ChatWebsocket.class);
//    private static ChatService chatService;
//    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
//    private static int onlineCount = 0;
//    @Autowired
//    private IstConfig istConfig;
//
//
//    /**
//     * 连接建立成功调用的方法
//     *
//     * @param userId     用户标识
//     */
//    @OnOpen
//    public void onOpen(@PathParam("userId") String userId, @PathParam("timeId") Long timeId,Session session) {
//        chatService.dealOpen(userId,timeId,session);
//    }
//
//
//    /**
//     * s
//     * 收到客户端消息后调用的方法
//     *
//     * @param message 客户端发送过来的消息
//     * @param session 可选的参数
//     */
//    @OnMessage
//    public void onMessage(String message, Session session) throws Exception {
//        chatService.dealOnMessage(message,session);
//    }
//
//
//
//    /**
//     * 连接关闭调用的方法
//     */
//    @OnClose
//    public void onClose(Session session) {
//        chatService.dealOnClose(session);
//    }
//
//
//    /**
//     * 关闭连接
//     *
//     * @param map 当前登录客户端的map
//     */
//    private void close(Map<String, Session> map, String username) {
//        if (StringUtils.isNotBlank(username)) {
//            logger.info("关闭websocket链接,关闭客户端username:" + username);
//            if (map.get(username) != null) {
//                map.remove(username);
//            }
//        }
//    }
//
//    /**
//     * kafka发送消息监听事件，有消息分发
//     *
//     * @param message
//     */
//    public void kafkaReceiveMsg(String message) {
//
//    }
//
//    /**
//     * kafka监听关闭websocket连接
//     *
//     * @param closeMessage
//     */
//    public void kafkaCloseWebsocket(String closeMessage) {
//
//    }
//
//    /**
//     * 发生错误时调用
//     *
//     * @param session
//     * @param error
//     */
//    @OnError
//    public void onError(Session session, Throwable error) {
//        chatService.dealOnError(session,error);
//    }
//
//
//    public static void addOnlineCount() {
//        ChatWebsocket.onlineCount++;
//    }
//    public static int getOnlineCount() {
//        return onlineCount;
//    }
//    public static void subOnlineCount() {
//        ChatWebsocket.onlineCount--;
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        Integer isKafka = istConfig.getIsKafka();
//        if (isKafka==1){
//            this.chatService = (ChatService) applicationContext.getBean("chatServiceKafka");
//        }else{
//            this.chatService = (ChatService) applicationContext.getBean("chatService");
//        }
//    }
//
//}
