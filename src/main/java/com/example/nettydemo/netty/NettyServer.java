package com.example.nettydemo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.core.codec.StringDecoder;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:40 PM 1/23/2019
 */
public class NettyServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap(); //引导类

        NioEventLoopGroup boos = new NioEventLoopGroup();   //监听窗口 用于创建新连接的线程组
        NioEventLoopGroup worker = new NioEventLoopGroup();//处理每一条连接的数据读写的线程组
        serverBootstrap
                .group(boos, worker) //给引导类配置两大线程
                .channel(NioServerSocketChannel.class) //指定服务端的IO模型为NIO,IO模型为BIO，那么这里配置上OioServerSocketChannel.class类型
                .option(ChannelOption.SO_BACKLOG, 1024)//表示系统用于临时存放已完成三次握手请求的队列的最大长度
                .childHandler(new ChannelInitializer<NioSocketChannel>() { //用于定义每条连接的数据读写，业务处理逻辑
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new io.netty.handler.codec.string.StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                System.out.println(msg);
                            }
                        });
                    }
                });
        bind(serverBootstrap,8000);
        serverBootstrap.handler(new ChannelInitializer<NioServerSocketChannel>() { //服务器端启动过程中的逻辑
            @Override
            protected void initChannel(NioServerSocketChannel ch) {
                System.out.println("服务端启动中");
            }
        });
//                .bind(8000).addListener(new GenericFutureListener<Future<? super Void>>() { //绑定并检测端口
//            @Override
//            public void operationComplete(Future<? super Void> future) throws Exception {
//                if(future.isSuccess()){
//                    System.out.println("端口绑定成功!");
//                }
//                else
//                {
//                    System.out.println("端口绑定失败!");
//                }
//            }
//        });


    }
    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() { //绑定并查询状态
            @Override
            public void operationComplete(Future<? super Void> future) {
                if (future.isSuccess()) {
                    System.out.println("端口[" + port + "]绑定成功!");
                } else {
                    System.err.println("端口[" + port + "]绑定失败!");
                    bind(serverBootstrap, port + 1);
                }
            }
        });
    }
}
