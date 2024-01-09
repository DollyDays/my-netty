package io.tiny.netty.channel;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */

/**
 * 这里声明EventLoop接口，不做实现，方便拓展，后面可以有Nio，Bio等等<p>
 * 思考：为什么需要继承EventLoopGroup和EventExecutor呢？<p>
 * 因为这个eventLoop需要使用Register方法和run方法以及shutdownGracefully方法
 */
public interface EventLoop extends EventLoopGroup, EventExecutor {

}
