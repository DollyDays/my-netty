package io.tiny.netty.channel;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */
@Slf4j
public abstract class SingleThreadEventExecutor implements EventExecutor, Executor {

    // 任务队列的容量，默认是Integer的最大值
    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;

    private final Queue<Runnable> taskQueue;

    private final RejectedExecutionHandler rejectedExecutionHandler;

    private volatile boolean start = false;

    private Thread thread;

    protected SingleThreadEventExecutor() {
        this.taskQueue = newTaskQueue();
        this.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
    }

    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    protected Queue<Runnable> newTaskQueue() {
        return new LinkedBlockingQueue<>(SingleThreadEventExecutor.DEFAULT_MAX_PENDING_TASKS);
    }

    @Override
    public void execute(Runnable task) {
        // 把任务提交到任务队列中
        addTask(task);
        // 执行run方法
        SingleThreadEventExecutor.this.run();
    }

    private void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        // 如果添加失败，执行拒绝策略
        if (!offerTask(task)) {
            reject(task);
        }
    }

    final boolean offerTask(Runnable task) {
        return taskQueue.offer(task);
    }


    protected boolean hasTasks() {
        log.info("我没任务了！");
        return !taskQueue.isEmpty();
    }

    protected void runAllTasks() {
        runAllTasksFrom(taskQueue);
    }

    protected void runAllTasksFrom(Queue<Runnable> taskQueue) {
        // 从任务对立中拉取任务,如果第一次拉取就为null，说明任务队列中没有任务，直接返回即可
        Runnable task = pollTaskFrom(taskQueue);
        if (task == null) {
            return;
        }
        for (; ; ) {
            // 执行任务队列中的任务
            safeExecute(task);
            // 执行完毕之后，拉取下一个任务，如果为null就直接返回
            task = pollTaskFrom(taskQueue);
            if (task == null) {
                return;
            }
        }
    }

    private void safeExecute(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            log.warn("A task raised an exception. Task: {}", task, e);
        }
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        return taskQueue.poll();
    }

    protected final void reject(Runnable task) {
        // rejectedExecutionHandler.rejectedExecution(task, this);
    }

    protected abstract void run();

    @Override
    public void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {

    }
}
