package com.wx;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class SelectDemo {

    public static void main(String[] args) throws Exception{

        //create select
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(9999));

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey next = iterator.next();
            if(next.isAcceptable()) {

            }else if(next.isConnectable()) {

            }else if(next.isReadable()) {

            }else if(next.isWritable()) {

            }
        }
        iterator.remove();

    }
}
