package com.wx;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ServerSocketChannelDemo {

    public static void main(String[] args) throws Exception{
        //端口号
        int port = 9999;

        //buffer
        ByteBuffer buffer = ByteBuffer.wrap("hello buffer\n".getBytes(StandardCharsets.UTF_8));

        //serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket socket = serverSocketChannel.socket();
        socket.bind(new InetSocketAddress(port));

        //设置非阻塞模式
        serverSocketChannel.configureBlocking(false);

        while (true) {
            System.out.println("waiting for connections!");
            SocketChannel socketChannel = serverSocketChannel.accept();
            if(socketChannel == null) {
                System.out.println("null");
                Thread.sleep(2000);
            }else {
                System.out.println("Incoming connection from:" + socketChannel.socket().getRemoteSocketAddress());
                buffer.rewind();
                socketChannel.write(buffer);
                socketChannel.close();
            }
        }
    }
}
