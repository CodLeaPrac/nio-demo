package com.wx.chat2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ChatTask {

    private Selector selector;
    private ByteBuffer buffer;
    private SocketChannel socketChannel;

    public ChatTask(Selector selector, SocketChannel socketChannel) {
        this.selector = selector;
        buffer = ByteBuffer.allocate(1024);
        this.socketChannel = socketChannel;
    }

    public void read() throws Exception{
        if(socketChannel.read(buffer) != -1) {
            System.out.println("read content is");
            buffer.flip();
            String s = new String(buffer.array(), 0, buffer.limit());
            System.out.println(s);

            buffer.clear();

            //broad Other client
            Iterator<SelectionKey> iterator = selector.keys().iterator();
            iterator.forEachRemaining(sk -> {
                SelectableChannel channel = sk.channel();
                if(channel instanceof SocketChannel && channel != socketChannel) {
                    try {
                        ((SocketChannel) channel).write(ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            //close socket channel
            socketChannel.close();
        }

    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
