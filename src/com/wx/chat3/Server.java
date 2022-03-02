package com.wx.chat3;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Server {

    //服务器端口
    private int port;

    //接受socket请求
    private ChatAcceptor chatAcceptor;

    //处理socket请求
    private ChatProcessor chatProcessor;

    public Server(int port) {
        this.port = port;
    }

    //启动服务器
    public void start() throws Exception {

        //创建socket 处理队列
        Queue socketQueue = new ArrayBlockingQueue(1024);

        chatAcceptor = new ChatAcceptor(port, socketQueue);

        chatProcessor = new ChatProcessor(socketQueue);

        //创建接受和处理 socket 线程
        Thread acceptThread = new Thread(chatAcceptor, "acceptor");
        Thread processorThread = new Thread(chatProcessor, "processor");

        //启动接受和处理 socket 线程
        acceptThread.start();
        TimeUnit.SECONDS.sleep(1);
        processorThread.start();
    }
}
