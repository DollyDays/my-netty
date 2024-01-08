package io.tiny.netty.channel.epoll;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */
@Slf4j
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor {

    public SingleThreadEventLoop() {

    }

    public void register(SocketChannel socketChannel, NioEventLoop nioEventLoop) {
        // 如果执行该方法的线程就是执行器中的线程，直接执行方法即可
        if (inEventLoop(Thread.currentThread())) {
            register0(socketChannel, nioEventLoop);
        } else {
            nioEventLoop.execute(() -> {
                register0(socketChannel, nioEventLoop);
                log.info("客户端的channel已经注册到新线程的多路复用器上了");
            });
        }
    }

    private void register0(SocketChannel channel, NioEventLoop nioEventLoop) {
        try {
            channel.configureBlocking(false);
            channel.register(nioEventLoop.selector(), SelectionKey.OP_READ);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
