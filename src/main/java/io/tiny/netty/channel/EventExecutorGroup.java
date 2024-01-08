package io.tiny.netty.channel;

import java.util.concurrent.TimeUnit;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */

/**
 * 这个接口是一个抽象的，它是一个执行器组，它有一个next方法，返回一个执行器，
 * 这个执行器是一个抽象的，它有一个执行方法，它是一个异步的方法，它是一个执行器，
 * 它有一个关闭方法，它是一个同步的方法
 */
public interface EventExecutorGroup {

    /**
     * 返回下一个执行器，执行器其实就是一个线程池
     */
    EventExecutor next();

    /**
     * 优雅关闭线程池
     */
    void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit);

}
