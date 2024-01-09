package io.tiny.netty.channel;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-08
 */

import java.util.concurrent.Executor;

/**
 * 思考这里为什需要继承这个EventExecutorGroup？<p>
 * 因为Executor就是线程池，需要一个优雅停机，这里做成父接口让去继承，后续方便拓展
 */
public interface EventExecutor extends EventExecutorGroup, Executor {
}
