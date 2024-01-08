package io.tiny.netty.channel;

import io.tiny.netty.channel.nio.NioEventLoop;

import java.nio.channels.SocketChannel;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */
public interface EventLoopGroup extends EventExecutorGroup{

    EventLoop next();

    void register(SocketChannel channel, NioEventLoop nioEventLoop);
}
