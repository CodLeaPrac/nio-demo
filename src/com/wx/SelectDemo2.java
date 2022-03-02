package com.wx;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class SelectDemo2 {

    public static void main(String[] args) throws Exception{
        new SelectDemo2().client();
    }

    @Test
    public void client() throws Exception{
        //create channel
        SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 10000));
        //set channel non blocking
        channel.configureBlocking(false);


        //create buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);


        //set buffer fip

        //write buffer to channel
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        while (scanner.hasNext()) {

            String next = scanner.next();

            String content = LocalDateTime.now().format(dateTimeFormatter) + ":\t" + next;

            byteBuffer.put(content.getBytes(StandardCharsets.UTF_8));

            byteBuffer.flip();

            channel.write(byteBuffer);

            byteBuffer.clear();
        }


    }

    @Test
    public void server() throws Exception{

        //create server socket channel
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(10000));
        socketChannel.configureBlocking(false);


        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if(key.isAcceptable()) {
                    SocketChannel accept = socketChannel.accept();
                    accept.configureBlocking(false);

                    accept.register(selector, SelectionKey.OP_READ);

                }else if(key.isReadable()) {
                    SocketChannel channel = (SocketChannel)key.channel();

                    ByteBuffer allocate = ByteBuffer.allocate(1024);

                    int length;
                    while ((length = channel.read(allocate)) > 0) {
                        allocate.flip();
                        System.out.println(new String(allocate.array(), 0, length));
                        allocate.clear();
                    }
                }
            }

            iterator.remove();
        }
    }
}
