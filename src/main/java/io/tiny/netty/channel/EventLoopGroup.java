package io.tiny.netty.channel;

import io.tiny.netty.channel.nio.NioEventLoop;

import java.nio.channels.SocketChannel;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */

/**
 * 事件循环组接口，用于管理多个事件循环
 * 为什么要继承这个EventExecutorGroup呢？<p>
 * 因为我们可能需要让EventLoop对象调用到EventExecutorGroup的方法 <p>
 * 比如：EventLoopGroup.shutdownGracefully()
 * 那么我们需要让EventLoop对象继承EventExecutorGroup <p>
 * 思考一个问题：EventLoop对象如何调用EventExecutor方法呢
 * 这里就解决了这个需要了，继承它就可以了
 */
public interface EventLoopGroup extends EventExecutorGroup {

    EventLoop next();

    void register(SocketChannel channel, NioEventLoop nioEventLoop);
}
