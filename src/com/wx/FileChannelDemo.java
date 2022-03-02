package com.wx;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileChannelDemo {
    public static void main(String[] args) throws Exception{
        RandomAccessFile fromFile = new RandomAccessFile("from.txt", "rw");
        FileChannel fromFileChannelChannel = fromFile.getChannel();

        RandomAccessFile toFile = new RandomAccessFile("to1.txt", "rw");
        FileChannel toFileChannelChannel = toFile.getChannel();

        long position = 0;
        long size = fromFileChannelChannel.size();

//        toFileChannelChannel.transferFrom(fromFileChannelChannel, position, size);
        fromFileChannelChannel.transferTo(position, size, toFileChannelChannel);

       toFile.close();
       fromFile.close();
    }
}
