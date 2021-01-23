package com.ist.message.netty.handler;

import com.ist.message.domain.dto.Response;
import com.ist.message.netty.annotation.NettyHttpHandler;
import com.ist.message.netty.http.NettyHttpRequest;
import com.ist.message.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;


/**
 * @Author: sunhaitao
 */
@NettyHttpHandler(path = "/ist_im/sendMessage/",equal = false,method = "POST")
public class MessageHandler implements IFunctionHandler{
    @Autowired
    private ChatService chatService;
    @Override
    public Response execute(NettyHttpRequest request) throws IOException {
        Map<String, String> result = request.parse();
        Response resultVO = chatService.urlReceiveMsg(result.get("message"), result.get("receiverId"));
        return resultVO;
    }
}
