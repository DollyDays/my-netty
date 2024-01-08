package io.tiny.netty.channel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

import io.tiny.netty.channel.nio.NioEventLoop;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */
@Slf4j
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {
    @Override
    public void register(SocketChannel channel, NioEventLoop nioEventLoop) {
        nioEventLoop.execute(() -> {
            try {
                channel.configureBlocking(false);
                channel.register(nioEventLoop.selector(), SelectionKey.OP_READ);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }

    @Override
    public EventLoop next() {
        return this;
    }
}
