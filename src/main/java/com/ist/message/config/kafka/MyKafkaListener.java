//package com.ist.message.config.kafka;
//
//import com.ist.message.service.ChatService;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//
//import javax.annotation.Resource;
//
//public class MyKafkaListener {
//    @Autowired
//    private ChatService chatService;
//    @Resource(name="all_log")
//    private Logger logger;
//    /**
//     * 发送聊天消息时的监听
//     * @param record
//     */
//    @KafkaListener(topics = {"chatMessage"})
//    public void listen(ConsumerRecord<?, ?> record) {
//        logger.info("chatMessage发送聊天消息监听："+record.value().toString());
//        logger.info("chatMessage发送聊天消息监听："+record.value() +",topic:" +  record.topic() + ",partition:" + record.partition() + ",offset:" + record.offset() + ",key:" + record.key() + ",timestamp:" + record.timestamp() + ",timestampType:"+record.timestampType().toString());
////        ChatWebsocket chatWebsocket = new ChatWebsocket();
//        chatService.kafkaReceiveMsg(record.value().toString());
//    }
//
//    /**
//     * 关闭连接时的监听
//     * @param record
//     */
//    @KafkaListener(topics = {"closeWebsocket"})
//    private void closeListener(ConsumerRecord<?, ?> record) {
//        logger.info("closeWebsocket关闭websocket连接监听："+record.value().toString());
////        ChatWebsocket chatWebsocket = new ChatWebsocket();
//        chatService.kafkaCloseWebsocket(record.value().toString());
//    }
//}
