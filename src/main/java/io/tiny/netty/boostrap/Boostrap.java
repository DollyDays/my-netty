package io.tiny.netty.boostrap;

import io.tiny.netty.channel.EventLoopGroup;
import io.tiny.netty.channel.nio.NioEventLoop;

import java.nio.channels.SocketChannel;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */
public class Boostrap {

    private EventLoopGroup eventLoopGroup; 

    public Boostrap group(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        return this;
    }

    public void register(SocketChannel channel, NioEventLoop nioEventLoop) {
        eventLoopGroup.register(channel, nioEventLoop);
    }
}
