package com.wx;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DatagramChannelDemo {

    //send datagram
    @Test
    public void sendDatagram() throws Exception{
        DatagramChannel datagramChannel = DatagramChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8888);

        //send
        while (true) {

            ByteBuffer buffer = ByteBuffer.wrap("send".getBytes(StandardCharsets.UTF_8));
            datagramChannel.send(buffer, socketAddress);
            System.out.println("发送完成！");
            Thread.sleep(2000);
        }
    }


    //receive datagram
    @Test
    public void receiveDatagram() throws Exception{
        DatagramChannel receiveChannel = DatagramChannel.open();
        receiveChannel.bind(new InetSocketAddress(8888));

        //buffer
        ByteBuffer allocate = ByteBuffer.allocate(1024);

        //接受
        while (true) {
            System.out.println("开始接受！");
            SocketAddress receive = receiveChannel.receive(allocate);
            System.out.println("来自于：" + receive);

            allocate.flip();

            System.out.println("内容：" + Charset.forName("UTF-8").decode(allocate));
        }
    }
}
