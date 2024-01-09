import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tiny.netty.boostrap.Boostrap;
import io.tiny.netty.channel.EventLoop;
import io.tiny.netty.channel.nio.NioEventLoop;
import io.tiny.netty.channel.nio.NioEventLoopGroup;

@SuppressWarnings("all")
public class TestServer {

    private static final Logger logger = LoggerFactory.getLogger(TestServer.class);

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        SocketChannel socketChannel = serverSocketChannel.accept();
        NioEventLoopGroup workGroup = new NioEventLoopGroup(2);
        EventLoop eventLoop = workGroup.next();
        Boostrap boostrap = new Boostrap();
        workGroup.shutdownGracefully();
    }
/*
    public static void main(String[] args) throws Exception {
        // 得到服务端channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // channel设置成非阻塞的
        serverSocketChannel.configureBlocking(false);
        // 得到selector多路复用器
        Selector selector = Selector.open();
        // 服务端channel注册到多路复用器上
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, serverSocketChannel);
        // 设置感兴趣事件
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        // 绑定端口号
        serverSocketChannel.bind(new InetSocketAddress(8080));
        // 创建一个Nio线程，也就是单线程执行器
        NioEventLoop workGroup = new NioEventLoop;
        while (true) {
            logger.info("main函数阻塞在这里吧。。。。。。。");
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    // 得到客户端的channel
                    SocketChannel socketChannel = channel.accept();
                    // NioEventLoop自己把客户端的channel注册到自己的selector上
                    workGroup.register(socketChannel, workGroup);
                }
            }
        }
    }*/
}