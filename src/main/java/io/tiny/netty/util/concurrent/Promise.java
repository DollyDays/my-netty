package io.tiny.netty.util.concurrent;

import java.util.concurrent.Future;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-09
 */
public interface Promise<V> extends Runnable, Future<V> {

    @Override
    void run();

}
