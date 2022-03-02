package com.wx.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ChatServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));

        //set no blocking
        serverSocketChannel.configureBlocking(false);

        //use selector
        Selector selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int select = selector.select();
            if(select > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();

                    iterator.remove();

                    if (next.isAcceptable()) {
                        System.out.println("accept");
                        SocketChannel accept = serverSocketChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);

                        String responseContent = "welcome coming in chat room!";
                        accept.write(ByteBuffer.wrap(responseContent.getBytes(StandardCharsets.UTF_8)));
                    }

                    if(next.isValid() && next.isReadable()) {

                        System.out.println("close socket");
                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        SocketChannel channel = (SocketChannel) next.channel();
                        int read = channel.read(buffer);
                        if(read == -1) {
                            //socket channel close
                            continue;
                        }
                        handleResponse(selector, next);
                    }



                }
            }
        }
    }

    private static void handleResponse(Selector selector, SelectionKey currentKey)  {

        SelectableChannel currentChannel = currentKey.channel();
        SocketChannel socketChannel = (SocketChannel)currentChannel;
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        try {
            int i = socketChannel.read(allocate);
            if(i == -1) {

//                currentKey.channel();
                System.out.println("-1");
                if(socketChannel != null) {
                    System.out.println("!null");
                }
                boolean valid = currentKey.isValid();
                System.out.println(valid);

            }
            allocate.flip();
            System.out.println(socketChannel.getRemoteAddress() +":" + socketChannel.socket().getPort());
            System.out.println("receive content:" + new String(allocate.array(), 0, allocate.limit()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<SelectionKey> iterator = selector.keys().iterator();

        //broadcast other socket channel client
        while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            SelectableChannel channel = selectionKey.channel();

            if(channel instanceof SocketChannel) {
                SocketChannel otherChannel = (SocketChannel) channel;

                if(otherChannel != currentChannel) {
                    try {
                        allocate.rewind();
                        otherChannel.write(ByteBuffer.wrap(otherChannel.getRemoteAddress().toString().getBytes(StandardCharsets.UTF_8)));
                        otherChannel.write(ByteBuffer.wrap(": ".getBytes(StandardCharsets.UTF_8)));
                        otherChannel.write(allocate);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
