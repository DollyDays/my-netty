import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author 赖嘉康 <laijiakang@kuaishou.com>
 * Created on 2024-01-04
 */
@SuppressWarnings("all")
public class SimpleClient {
    public static void main(String[] args) throws IOException {
        // 得到客户端的channel
        SocketChannel socketChannel = SocketChannel.open();
        // 设置非阻塞
        socketChannel.configureBlocking(false);
        // 得到Selector
        Selector selector = Selector.open();
        SelectionKey selectionKey = socketChannel.register(selector, 0);
        // 设置事件
        selectionKey.interestOps(SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(8080));
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()) {
                    if (socketChannel.finishConnect()) {
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        socketChannel.write(ByteBuffer.wrap("客户端发送成功了".getBytes()));
                    }
                }
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len = channel.read(buffer);
                    byte[] readByte = new byte[len];
                    buffer.flip();
                    buffer.get(readByte);
                    System.out.println(new String(readByte));
                }
            }
        }

    }
}
