# Netty 学习
Netty 与 NIO的关系： Netty封装了JDK 的NIO，且可以切换底层的IO 与NIO模式。
## 1、环境
idea springboot
## 2、配置
在pom.xml下加入依赖
```xml

            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
```     

## 3、核心代码
### （1）sever端
```java
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
                }).bind(8000);
```
### （2）client端
```java
 Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });

        Channel channel = bootstrap.connect("127.0.0.1", 8000).channel();

        while(true){
            channel.writeAndFlush(new Date() + ": hello world!");
            Thread.sleep(2000);
        }
    }
```

## 4、学习参考链接
https://www.jianshu.com/p/a4e03835921a  
https://www.jianshu.com/p/ec3ebb396943
