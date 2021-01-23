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
package com.ist.message.config.webSocket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private HttpHelloWorldServerHandler httpHelloWorldServerHandler;

//    private final SslContext sslCtx;

//    public HttpHelloWorldServerInitializer(SslContext sslCtx) {
//        this.sslCtx = sslCtx;
//    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
//        if (sslCtx != null) {
//            p.addLast(sslCtx.newHandler(ch.alloc()));
//        }
        p.addLast(new HttpServerCodec());// http 编解码
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast("httpAggregator",new HttpObjectAggregator(512*1024)); // http 消息聚合器
        p.addLast(httpHelloWorldServerHandler);
    }
}
