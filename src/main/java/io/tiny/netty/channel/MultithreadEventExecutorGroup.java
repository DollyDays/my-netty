package io.tiny.netty.channel;

import java.util.concurrent.TimeUnit;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */

/**
 * 多线程事件执行器组件
 * 后续 Boss/Worker Thread Group继承就可以了
 */
public abstract class MultithreadEventExecutorGroup implements EventExecutorGroup {

    private final EventExecutor[] eventExecutors;

    private int index = 0;

    protected MultithreadEventExecutorGroup(int threads) {
        eventExecutors = new EventExecutor[threads];
        for (int i = 0; i < threads; i++) {
            eventExecutors[i] = newChild();
        }
    }

    // 给子类去实现
    protected abstract EventExecutor newChild();

    @Override
    public EventExecutor next() {
        int id = index % eventExecutors.length;
        index++;
        return eventExecutors[id];
    }

    @Override
    public void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        next().shutdownGracefully(quietPeriod, timeout, unit);
    }
}
