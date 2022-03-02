package com.wx;


import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class NioDemo {

    public static void main(String[] args) throws Exception{
//        readDemo();

        writeDemo();
    }

    private static void writeDemo() throws Exception{
        //create file channel
        RandomAccessFile accessFile = new RandomAccessFile("b.txt", "rw");
        FileChannel channel = accessFile.getChannel();

        //create byte buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        String writeData = "hello nio! file channel write 1112 你哈";

        buffer.clear();

        buffer.put(writeData.getBytes(StandardCharsets.UTF_8));

        buffer.flip();

        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        channel.close();
        accessFile.close();
    }

    private static void readDemo() throws Exception{
        //create file channel
        RandomAccessFile rw = new RandomAccessFile("a.txt", "rw");
        FileChannel channel = rw.getChannel();

        //create buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int read = channel.read(buffer);
        while (read != -1) {
            System.out.println("read: " + read);
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char)buffer.get());
            }

            buffer.clear();
            read = channel.read(buffer);
        }

        rw.close();
        System.out.println("\nfile channel: end");
    }
}
