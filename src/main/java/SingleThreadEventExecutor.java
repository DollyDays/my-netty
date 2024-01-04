import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-04
 */
@Slf4j
public class SingleThreadEventExecutor implements Executor {

    // 任务队列的容量，默认是Integer的最大值
    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;

    private final Queue<Runnable> taskQueue;

    private final RejectedExecutionHandler rejectedExecutionHandler;

    private volatile boolean start = false;

    private final SelectorProvider provider;

    private Selector selector;

    private Thread thread;

    public SingleThreadEventExecutor() {
        // java中的方法，通过provider不仅可以得到selector，还可以得到ServerSocketChannel和SocketChannel
        this.provider = SelectorProvider.provider();
        this.taskQueue = newTaskQueue();
        this.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
        this.selector = openSelector();
    }

    protected Queue<Runnable> newTaskQueue() {
        return new LinkedBlockingQueue<>(DEFAULT_MAX_PENDING_TASKS);
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        // 把任务提交到任务队列中
        addTask(task);
        // 启动单线程执行器中的线程
        startThread();
    }

    private void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (!offer(task)) {
            reject(task);
        }
    }

    private void startThread() {
        if (start) {
            return;
        }
        start = true;
        new Thread(() -> {
            thread = Thread.currentThread();
            SingleThreadEventExecutor.this.run();
        }).start();
        log.info("新线程创建了！");
    }

    final boolean offer(Runnable task) {
        return taskQueue.offer(task);
    }

    protected boolean hashTasks() {
        return !taskQueue.isEmpty();
    }

    protected void runAllTasks() {
        runAllTasksFrom(taskQueue);
    }

    protected void runAllTasksFrom(Queue<Runnable> taskQueue) {
        // 从任务队列中拉取任务,如果第一次拉取就为null，说明任务队列中没有任务，直接返回即可
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
        } catch (Throwable t) {
            log.warn("A task raised an exception. Task: {}", task, t);
        }
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        return taskQueue.poll();
    }

    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    protected final void reject(Runnable task) {

    }

    public void register(SocketChannel socketChannel) {
        // 如果执行该方法的线程就是执行器重的线程，直接执行方法即可
        if (inEventLoop(Thread.currentThread())) {
            register0(socketChannel);
        } else {
            // 在这里，第一次向单线程执行期中提交任务的时候，执行期终于开始执行了，新的线程也开始创建
            this.execute(() -> {
                register0(socketChannel);
                log.info("客户端的channel已注册到新线程的多路复用器上了！");
            });
        }
    }

    private void register0(SocketChannel channel) {
        try {
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private Selector openSelector() {
        try {
            selector = provider.openSelector();
            return selector;
        } catch (IOException e) {
            throw new RuntimeException("failed to open a new selector", e);
        }
    }

    private void select() throws IOException {
        Selector selector = this.selector;
        for (; ; ) {
            // 如果没有就绪事件，就在这里阻塞3秒，有限时的阻塞
            log.info("新线程阻塞在这里3秒");
            int selectedKeys = selector.select(3000);
            if (selectedKeys != 0 || hashTasks()) {
                break;
            }
        }
    }

    private void processSelectKeys(Set<SelectionKey> selectedKeys) throws IOException {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> iterator = selectedKeys.iterator();
        do {
            final SelectionKey key = iterator.next();
            iterator.remove();
            processSelectKey(key);
        } while (iterator.hasNext());
    }

    private void processSelectKey(SelectionKey key) throws IOException {
        if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len = channel.read(byteBuffer);
            if (len == -1) {
                log.info("客户端通道要关闭！");
                channel.close();
                return;
            }
            byte[] bytes = new byte[len];
            byteBuffer.flip();
            byteBuffer.get(bytes);
            log.info("新线程收到客户端发送的数据:{}", new String(bytes));
        }
    }

    public void run() {
        while (true) {
            try {
                // 没有事件就阻塞在这里
                select();
                // 如果走到这里，就说明selector没有阻塞了
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                processSelectKeys(selectedKeys);
            } catch (IOException e) {
                log.error(e.getMessage());
            } finally {
                runAllTasks();
            }
        }
    }

}
