package com.wx.socket.channel;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class MySocketChannelDemo {

    public static void main(String[] args) throws Exception {

        int port = 8080;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        ServerSocketChannel open = ServerSocketChannel.open();
        open.bind(inetSocketAddress);
        open.configureBlocking(false);

        String content = "hello are you!";

        ByteBuffer wrap = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));

        while (true) {
            SocketChannel accept = open.accept();
            if(accept == null) {
                System.out.println("wait.........");
                Thread.sleep(3000);
            }else {
                System.out.println("receive a socket, address is" +  accept.getRemoteAddress());
               accept.write(wrap);
               accept.close();
            }

        }
    }
}
