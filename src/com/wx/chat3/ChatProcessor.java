package com.wx.chat3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 聊天服务处理器
 */
public class ChatProcessor implements Runnable {

    //申请1M buffer空间
    private ByteBuffer readByteBuffer  = ByteBuffer.allocate(1024 * 1024);
    private ByteBuffer writeByteBuffer = ByteBuffer.allocate(1024 * 1024);

    //每个Socket分配一个Id
    private long socketId = 1;

    //存储每个 Id 对应的 ChatSocket
    private Map<Long, ChatSocket>  socketMap  = new HashMap<>();

    //多路复用器 socket 事件监听
    private Selector selector;

    //阻塞队列
    private Queue socketQueue;

    public ChatProcessor(Queue socketQueue) throws IOException {
        this.selector = Selector.open();

        this.socketQueue = socketQueue;

    }

    /**
     * 处理 从队列获取的每个socket
     */
    @Override
    public void run() {
        System.out.println("thread start: " + Thread.currentThread().getName());
        while (true) {
            try{
                handleSocket();
            } catch(Exception e){
                e.printStackTrace();
            }

            try {
                //每100ms 休眠一次
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSocket() throws IOException {

        //获得每一个SocketChannel 注册到 Selector
        registerSocketToSelector();

        //处理每一个Socket 事件
        handleEachSocketEvent();
    }

    private void handleEachSocketEvent() throws IOException{

        //判断是否有事件发生
        int select = this.selector.selectNow();

        if(select > 0)  {

            //有事件发生
//            System.out.println("event !!");

            //从selector 获得SelectionKey集合
            Set<SelectionKey> selectionKeys = this.selector.selectedKeys();

            //迭代集合
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                //防止重复 删除集合当前 selectionKey
                iterator.remove();

                //如果有读事件
                if(selectionKey.isReadable()) {

                    //处理读事件
                    handleRead(selectionKey);
                }

//                //如果写事件
//                if(selectionKey.isWritable()) {
//                    System.out.println("可以写入了");
//                    handleWrite(selectionKey);
//                }

            }

            //清空 selector 所有selectionKey
            selectionKeys.clear();
        }


    }

    private void handleWrite(SelectionKey selectionKey) throws IOException {
        //获得当前Socket
        ChatSocket chatSocket = (ChatSocket) selectionKey.attachment();

        Iterator<SelectionKey> iterator = this.selector.keys().iterator();
        while (iterator.hasNext()) {
            SelectionKey selectionKey1 = iterator.next();


            if(selectionKey1.attachment() instanceof ChatSocket && selectionKey1.attachment() != chatSocket ) {
                int write = 0;
                try {
                    if(this.writeByteBuffer.position() != 0) {
                        ChatSocket otherSocket =  (ChatSocket)selectionKey1.attachment();
                        write = otherSocket.write(writeByteBuffer);
                    }

                    System.out.println("write byte length: " + write);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                chatSocket.write("you current ID is " + chatSocket.getSocketId());
            }
        }

    }


    private void handleRead(SelectionKey selectionKey) throws IOException {

        //获得当前Socket
        ChatSocket chatSocket = (ChatSocket) selectionKey.attachment();

        //清空read buffer
        readByteBuffer.clear();

        int size = chatSocket.read(readByteBuffer);
        //读到通道末尾 通道已经关闭
        if(size == -1) {
            //结束socket连接
            chatSocket.setEndOfStreamReached(true);
        }else {
            //切换读模式
//            readByteBuffer.flip();
            String s = new String(readByteBuffer.array(), 0, size);
            s = chatSocket.getSocketId() +": " + s;

            this.writeByteBuffer.clear();
            this.writeByteBuffer.put(s.getBytes(StandardCharsets.UTF_8));

            handleWrite(selectionKey);
        }

        if(chatSocket.isEndOfStreamReached()){
            System.out.println("Socket closed: " + chatSocket.getSocketId());
            this.socketMap.remove(chatSocket.getSocketId());
            selectionKey.attach(null);
            selectionKey.cancel();
            selectionKey.channel().close();
        }

    }

    private void registerSocketToSelector() throws IOException {

        //从队列获取 Socket
        ChatSocket chatSocket = (ChatSocket)this.socketQueue.poll();

        while (chatSocket != null) {

            //获得SocketChannel
            SocketChannel socketChannel = chatSocket.getSocketChannel();

            //设置非阻塞
            socketChannel.configureBlocking(false);

            //socketChannel 注册到 selector, 并监听 读事件
            SelectionKey selectionKey = socketChannel.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

            //关联 当前socket 到 selectionKey
            selectionKey.attach(chatSocket);

            chatSocket.setSocketId(this.socketId);

            //为当前Socket 关联id
            this.socketMap.put(this.socketId++, chatSocket);

            //获得队列下一个Socket
            chatSocket = (ChatSocket)this.socketQueue.poll();

        }


    }
}
