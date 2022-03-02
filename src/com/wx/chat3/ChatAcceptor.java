package com.wx.chat3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * 聊天服务接收器
 */
public class ChatAcceptor implements Runnable{


    //Socket 阻塞队列
    private Queue socketBlockingQueue;

    //服务器端口
    private int serverPort;

    //维护ServerSocketChannel
    private ServerSocketChannel ssc;

    public ChatAcceptor(int serverPort, Queue socketBlockingQueue) {
        this.serverPort = serverPort;
        this.socketBlockingQueue = socketBlockingQueue;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public Queue getSocketBlockingQueue() {
        return socketBlockingQueue;
    }

    public void setSocketBlockingQueue(Queue socketBlockingQueue) {
        this.socketBlockingQueue = socketBlockingQueue;
    }

    /**
     * 接受socket请求 然后将socket加入到 socketBlockingQueue 队列
     */
    @Override
    public void run() {
        System.out.println("thread start: " + Thread.currentThread().getName());
        try {
            //创建ServerSocketChannel实例
            this.ssc = ServerSocketChannel.open();
            this.ssc.bind(new InetSocketAddress(this.serverPort));

            //*** 接受socket 加入队列
            while (true) {

                //获得一个 socket 请求
                SocketChannel sc = this.ssc.accept();

                System.out.println("Socket accepted: " + sc);

                //使用 ChatSocket 包装SocketChannel, 并加入到阻塞队列中

                //使用add 方法如果队列满 会抛出异常
                ChatSocket chatSocket = new ChatSocket(sc);
                chatSocket.write("welcome enter chat room!! what do you want to say? every has only ID");
                this.socketBlockingQueue.add(chatSocket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
