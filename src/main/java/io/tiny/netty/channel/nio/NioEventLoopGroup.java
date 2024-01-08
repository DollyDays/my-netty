package io.tiny.netty.channel.nio;

import io.tiny.netty.channel.EventLoop;
import io.tiny.netty.channel.MultithreadEventLoopGroup;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */
public class NioEventLoopGroup extends MultithreadEventLoopGroup {

    public NioEventLoopGroup(int threads) {
        super(threads);
    }

    @Override
    protected EventLoop newChild() {
        return new NioEventLoop();
    }
}