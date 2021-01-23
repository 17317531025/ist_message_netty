//package com.ist.message.config.webSocket;
//
//        import com.ist.message.netty.iohandler.FilterLogginglHandler;
//        import com.ist.message.netty.iohandler.HttpServerHandler;
//        import com.ist.message.netty.iohandler.InterceptorHandler;
//        import io.netty.bootstrap.ServerBootstrap;
//        import io.netty.channel.ChannelFuture;
//        import io.netty.channel.ChannelInitializer;
//        import io.netty.channel.EventLoopGroup;
//        import io.netty.channel.nio.NioEventLoopGroup;
//        import io.netty.channel.socket.SocketChannel;
//        import io.netty.channel.socket.nio.NioChannelOption;
//        import io.netty.channel.socket.nio.NioServerSocketChannel;
//        import io.netty.handler.codec.http.HttpObjectAggregator;
//        import io.netty.handler.codec.http.HttpServerCodec;
//        import org.slf4j.Logger;
//        import org.slf4j.LoggerFactory;
//        import org.springframework.beans.factory.annotation.Value;
//        import org.springframework.stereotype.Component;
//        import javax.annotation.PostConstruct;
//        import javax.annotation.PreDestroy;
//        import javax.annotation.Resource;
//        import java.net.InetSocketAddress;
//
//@Component
//public class HttpNettyServer1 {
//    private static final Logger log = LoggerFactory.getLogger(HttpNettyServer1.class);
//
//    /**
//     * 端口号
//     */
//    @Value("${webSocket.netty.http.port:8011}")
//    private int port;
//
//    private EventLoopGroup bossGroup;
//    private EventLoopGroup workGroup;
//    @Resource
//    private InterceptorHandler interceptorHandler;
//
//    @Resource
//    private HttpServerHandler httpServerHandler;
//    /**
//     * 启动
//     * @throws InterruptedException
//     */
//    private void start() throws InterruptedException {
//        bossGroup = new NioEventLoopGroup();
//        workGroup = new NioEventLoopGroup();
//        ServerBootstrap bootstrap = new ServerBootstrap();
//        // bossGroup辅助客户端的tcp连接请求, workGroup负责与客户端之前的读写操作
//        bootstrap.group(bossGroup,workGroup);
//        // 设置NIO类型的channel
//        bootstrap.channel(NioServerSocketChannel.class);
//        // 设置监听端口
//        bootstrap.localAddress(new InetSocketAddress(port));
//        bootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
//        bootstrap.childOption(NioChannelOption.SO_REUSEADDR,true);
//        bootstrap.childOption(NioChannelOption.SO_KEEPALIVE,false);
//        bootstrap.childOption(NioChannelOption.SO_RCVBUF, 2048);
//        bootstrap.childOption(NioChannelOption.SO_SNDBUF, 2048);
//        // 连接到达时会创建一个通道
//        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
//
//            @Override
//            protected void initChannel(SocketChannel ch) throws Exception {
//                // 流水线管理通道中的处理程序（Handler），用来处理业务
//                // webSocket协议本身是基于http协议的，所以这边也要使用http编解码器
//                ch.pipeline().addLast("codec",new HttpServerCodec());
////                ch.pipeline().addLast(new ObjectEncoder());
//                ch.pipeline().addLast("aggregator", new HttpObjectAggregator(512 * 1024));
//                ch.pipeline().addLast("logging", new FilterLogginglHandler());
//                // 以块的方式来写的处理器
////                ch.pipeline().addLast(new ChunkedWriteHandler());
//                ch.pipeline().addLast("interceptor", interceptorHandler);
//                ch.pipeline().addLast("bizHandler", httpServerHandler);
//        /*
//        说明：
//        1、http数据在传输过程中是分段的，HttpObjectAggregator可以将多个段聚合
//        2、这就是为什么，当浏览器发送大量数据时，就会发送多次http请求
//         */
//                ch.pipeline().addLast(new HttpObjectAggregator(8192));
//        /*
//        说明：
//        1、对应webSocket，它的数据是以帧（frame）的形式传递
//        2、浏览器请求时 ws://localhost:58080/xxx 表示请求的uri
//        3、核心功能是将http协议升级为ws协议，保持长连接
//        */
//
//            }
//        });
//        // 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
//        ChannelFuture channelFuture = bootstrap.bind().sync();
//        log.info("Server started and listen on:{}",channelFuture.channel().localAddress());
//        // 对关闭通道进行监听
//        channelFuture.channel().closeFuture().sync();
//    }
//
//    /**
//     * 释放资源
//     * @throws InterruptedException
//     */
//    @PreDestroy
//    public void destroy() throws InterruptedException {
//        if(bossGroup != null){
//            bossGroup.shutdownGracefully().sync();
//        }
//        if(workGroup != null){
//            workGroup.shutdownGracefully().sync();
//        }
//    }
////    @PostConstruct()
////    public void init() {
////        //需要开启一个新的线程来执行netty server 服务器
////        new Thread(() -> {
////            try {
////                start();
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        }).start();
////    }
//}
