package io.tiny.netty.util.concurrent;

import io.tiny.netty.channel.EventLoop;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-09
 */
@SuppressWarnings("all")
public class DefaultPromise<V> implements Promise<V> {

    // 执行后得到的结果
    private volatile Object result;

    private Callable<V> callable;


    @Override
    public void run() {
        V object = null;
        Callable<V> c = callable;
        try {
            object = c.call();
        } catch (Throwable ex) {
            result = null;
        }
        set(object);
    }

    protected void set(V v) {
        result = v;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
