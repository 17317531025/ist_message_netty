package com.ist.message.config.webSocket;

/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
import com.ist.message.domain.dto.Response;
import com.ist.message.service.ChatService;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@Component
@ChannelHandler.Sharable
public class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> implements ApplicationContextAware {
	@Autowired
	private ChatService chatService;
	@Resource(name="all_log")
	protected Logger logger;
    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
	public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws IOException {

    	if (msg instanceof HttpRequest) {
			FullHttpRequest req = (FullHttpRequest) msg;
			String url = req.uri();
			boolean keepAlive = HttpUtil.isKeepAlive(req);
			if (url.contains("sendMessage")) {
				HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
				decoder.offer(req);

				List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
				Map<String, String> result = new HashMap<>();
				for (InterfaceHttpData parm : parmList) {
					Attribute data = (Attribute) parm;
					result.put(data.getName(), data.getValue());
				}
				Response resultVO = chatService.urlReceiveMsg(result.get("message"), result.get("receiverId"));
				FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), OK,
						Unpooled.wrappedBuffer(new String("{\"code\":"+resultVO.getCode()+",\"message\":\""+resultVO.getMessage()+"\"}").getBytes()));
				response.headers().set(CONTENT_TYPE, TEXT_PLAIN).setInt(CONTENT_LENGTH,
						response.content().readableBytes());
				logger.info("req==>" + result + ",resp==>" + resultVO.toString());
				if (keepAlive) {
					if (!req.protocolVersion().isKeepAliveDefault()) {
						response.headers().set(CONNECTION, KEEP_ALIVE);
					}
				} else {
					// Tell the client we're going to close the connection.
					response.headers().set(CONNECTION, CLOSE);
				}

				ChannelFuture f = ctx.write(response);

				if (!keepAlive) {
					f.addListener(ChannelFutureListener.CLOSE);
				}
			}else{
				FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), OK,
						Unpooled.wrappedBuffer(CONTENT));
				response.headers().set(CONTENT_TYPE, TEXT_PLAIN).setInt(CONTENT_LENGTH,
						response.content().readableBytes());

				if (keepAlive) {
					if (!req.protocolVersion().isKeepAliveDefault()) {
						response.headers().set(CONNECTION, KEEP_ALIVE);
					}
				} else {
					// Tell the client we're going to close the connection.
					response.headers().set(CONNECTION, CLOSE);
				}

				ChannelFuture f = ctx.write(response);
				logger.info("hello world");
				if (!keepAlive) {
					f.addListener(ChannelFutureListener.CLOSE);
				}
			}

		}
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.chatService = (ChatService) applicationContext.getBean("chatService");
	}
}
