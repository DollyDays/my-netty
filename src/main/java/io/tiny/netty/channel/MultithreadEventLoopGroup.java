package io.tiny.netty.channel;

import io.tiny.netty.channel.nio.NioEventLoop;

import java.nio.channels.SocketChannel;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */
public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup {

    protected MultithreadEventLoopGroup(int threads) {
        super(threads);
    }

    @Override
    protected abstract EventLoop newChild();

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }

    @Override
    public void register(SocketChannel channel, NioEventLoop nioEventLoop) {
        next().register(channel, nioEventLoop);
    }
}
