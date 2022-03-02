package com.wx.chat2;

import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;

public class Server {
    public static void main(String[] args) throws Exception{
        int port = 8080;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(inetSocketAddress);

        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                //avoid repeat remove selectKey
                iterator.remove();

                if(selectionKey.isAcceptable()) {

                    SocketChannel accept = serverSocketChannel.accept();
                    accept.configureBlocking(false);
                    accept.register(selector, SelectionKey.OP_READ);
                        new Thread(() -> {
                            ChatTask chatTask = new ChatTask(selector, accept);

                            Selector selector1 = chatTask.getSelector();
                            try {
                                while (true) {
                                    if(selector1.select() > 0) {

                                        Iterator<SelectionKey> iterator1 = selector1.selectedKeys().iterator();

                                        while (iterator1.hasNext()) {
                                            SelectionKey next = iterator1.next();
                                            if (next.isReadable()) {
                                                SelectableChannel channel = next.channel();
                                                if(channel == chatTask.getSocketChannel()) {
                                                    chatTask.read();
                                                    iterator1.remove();

                                                    channel.register(selector1, SelectionKey.OP_READ);
                                                }
                                            }
                                        }
                                    }

                                }
                            }catch (Exception e) {
                                e.printStackTrace();
                            }


                        }).start();

                }
            }
        }
    }
}
