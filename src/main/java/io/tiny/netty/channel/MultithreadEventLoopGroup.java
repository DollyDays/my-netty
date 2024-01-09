package io.tiny.netty.channel;

import io.tiny.netty.channel.nio.NioEventLoop;

import java.nio.channels.SocketChannel;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */

/**
 * 多线程事件执行器组件
 * 后续 Boss/Worker Thread Group继承就可以了
 */
public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup {

    protected MultithreadEventLoopGroup(int threads) {
        super(threads);
    }

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }

    @Override
    public void register(SocketChannel channel, NioEventLoop nioEventLoop) {
        next().register(channel, nioEventLoop);
    }
}
