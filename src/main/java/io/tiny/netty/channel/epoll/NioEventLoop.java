package io.tiny.netty.channel.epoll;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */
@Slf4j
public class NioEventLoop extends SingleThreadEventLoop {

    private final SelectorProvider provider;

    private Selector selector;

    public NioEventLoop() {
        this.provider = SelectorProvider.provider();
        this.selector = openSelector();
    }

    private Selector openSelector() {
        try {
            selector = provider.openSelector();
            return selector;
        } catch (IOException e) {
            throw new RuntimeException("failed to open a new selector", e);
        }
    }

    public Selector selector() {
        return selector;
    }

    private void select() throws IOException {
        Selector selector = this.selector;
        // 这里是一个死循环
        for (; ; ) {
            // 如果没有就绪事件，就在这里阻塞3秒，有限时的阻塞
            log.info("新线程阻塞在这里3秒吧。。。。。。。");
            int selectedKeys = selector.select(3000);
            // 如果有io事件或者单线程执行器中有任务待执行，就退出循环
            if (selectedKeys != 0 || hasTasks()) {
                break;
            }
        }
    }

    private void processSelectedKeys(Set<SelectionKey> selectedKeys) throws IOException {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> iterator = selectedKeys.iterator();
        do {
            final SelectionKey key = iterator.next();
            iterator.remove();
            // 处理就绪事件
            processSelectedKey(key);
        } while (iterator.hasNext());
    }

    private void processSelectedKey(SelectionKey key) throws IOException {
        // 如果是读事件
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

    @Override
    public void run() {
        while (true) {
            try {
                // 没有事件就阻塞在这里
                select();
                // 如果走到这里，就说明selector没有阻塞了，可能有IO事件，可能任务队列中有任务
                processSelectedKeys(selector.selectedKeys());
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                // 执行单线程执行器中的所有任务
                runAllTasks();
            }
        }
    }
}
