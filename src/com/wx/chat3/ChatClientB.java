package com.wx.chat3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class ChatClientB {
    public static void main(String[] args) throws Exception{
        InetSocketAddress inetSocketAddress = new InetSocketAddress(8080);
        SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);

        socketChannel.configureBlocking(false);

        Selector selector = Selector.open();

        socketChannel.register(selector, SelectionKey.OP_READ);


        new Thread(() -> {

            ByteBuffer allocate = ByteBuffer.allocate(1024);
            while (true) {
                int select = 0;
                try {
                    select = selector.select();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (select > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey next = iterator.next();
                        iterator.remove();
                        if(next.isReadable()) {
                            SocketChannel socketChannel1 = (SocketChannel) next.channel();
                            try {
                                socketChannel1.configureBlocking(false);

                                allocate.clear();

                                socketChannel1.read(allocate);

                                allocate.flip();

                                System.out.println(new String(allocate.array(), 0, allocate.limit()));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }

        }).start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter your message!");



        while (scanner.hasNext()) {
            String message = scanner.nextLine();

            socketChannel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));

        }
    }
}
