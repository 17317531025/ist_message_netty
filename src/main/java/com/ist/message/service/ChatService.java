package com.ist.message.service;

import com.ist.message.common.pojo.ResultVO;
import com.ist.message.config.kafka.MsgData;
import com.ist.message.domain.Msg;
import com.ist.message.domain.UserFriend;
import com.ist.message.domain.dto.Response;
import io.netty.channel.Channel;

import javax.websocket.Session;
import java.util.List;
import java.util.Map;

public interface ChatService {

    List<MsgData> queryByParams(Map<String, Object> params);

    int updateByParams(Map<String, Object> params);

    void updateByMsgId(long msgId,short status);

    void saveSocketMsg(Msg msg);

    void saveUserFriend(UserFriend userFriend);

    void dealOpen(String userId, Long timeId, Session session);

    void dealOnClose(Session session);

    void dealOnMessage(String message, Channel session);

    void  dealOnError(Session session, Throwable error);

    void closeSession(String userId);

    Response urlReceiveMsg(String message, String receiverId);

    void kafkaReceiveMsg(String message);

    void kafkaCloseWebsocket(String closeMessage);


}
