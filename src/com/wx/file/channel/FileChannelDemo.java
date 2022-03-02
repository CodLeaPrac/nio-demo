package com.wx.file.channel;

import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class FileChannelDemo {

    public static void main(String[] args) throws IOException {

//        testRead();

//        testWrite();

//        fileCopy();

//        RandomAccessFile rw = new RandomAccessFile("/Users/wx/java/idea_projects/nio/out/production/nio/com/wx/file/channel/book.txt", "rw");
//        RandomAccessFile rw1 = new RandomAccessFile("/Users/wx/java/idea_projects/nio/out/production/nio/com/wx/file/channel/book1.txt", "rw");
//
//
//        File merge = merge(rw, rw1);
//        System.out.println(merge.getPath());

        divideDemo();

    }

    private static void divideDemo() throws IOException{
        String path = FileChannelDemo.class.getResource("").getPath();
        FileChannel channel = new RandomAccessFile(path + "/a.flv", "rw").getChannel();

        System.out.println(channel.size());
        long half =  channel.size() / 2 ;
        FileChannel truncate = channel.truncate(half);
        RandomAccessFile rw = new RandomAccessFile("half1.flv", "rw");
        FileChannel channel1 = rw.getChannel();
        channel1.transferFrom(channel, truncate.position(), channel.size());


    }

    private static void fileCopy() throws IOException {
        URL resource = FileChannelDemo.class.getResource(".");
        assert resource != null;
        RandomAccessFile randomAccessFile = new RandomAccessFile(resource.getPath() + File.separator + "book.txt", "rw");
        FileChannel channel = randomAccessFile.getChannel();

        File file = new File(resource.getPath() + File.separator + "book_copy.txt");
        RandomAccessFile randomAccessFile1 = new RandomAccessFile(file, "rw");
        FileChannel channel1 = randomAccessFile1.getChannel();

        channel1.transferFrom(channel, 0, channel.size());

        File file1 = new File(resource.getPath() + File.separator + "book1.txt");
        FileChannel rw = new RandomAccessFile(file1, "rw").getChannel();
        rw.transferTo(channel1.position(), rw.size(), channel1);

    }

    private static File merge(RandomAccessFile f1, RandomAccessFile f2) throws IOException {

        FileChannel f1Channel = f1.getChannel();
        FileChannel f2Channel = f2.getChannel();

        String file = FileChannelDemo.class.getResource("").getFile() + "/merge.txt";
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileChannel merge = fileOutputStream.getChannel();
        ByteBuffer allocate = ByteBuffer.allocate(1024);

        while(f1Channel.read(allocate) != -1) {
            allocate.flip();
            merge.write(allocate);
            allocate.clear();
        }

        allocate.clear();
        allocate.put((byte) '\n');

        while (f2Channel.read(allocate) != -1) {
           allocate.flip();
           merge.write(allocate);
           allocate.clear();
        }
        return new File(file);
    }

    private static void testWrite() throws IOException{
        URL resource = FileChannelDemo.class.getResource(".");
        File file = new File(resource.getPath() + File.separator + "book1.txt");
        System.out.println(file.exists());
        if(file.exists()) {
            file.delete();
        }
        System.out.println(file.getAbsolutePath());
        RandomAccessFile rw = new RandomAccessFile(file, "rw");
        FileChannel channel = rw.getChannel();
        String content = "四书是啥？\n答: 论语 大学 中庸 孟子";

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put(content.getBytes(StandardCharsets.UTF_8));

        System.out.println(buffer.limit());
        System.out.println(buffer.position());
        System.out.println(buffer.capacity());

        buffer.flip();
        int i = 0;
        if (buffer.hasRemaining()) {
            channel.write(buffer);
            System.out.println(++i);
            System.out.println(buffer.position());
            System.out.println(buffer.limit());
        }

        rw.close();
    }

    //从FileChannel读取数据到Buffer
    private static void testRead() throws IOException {

        URL resource = FileChannelDemo.class.getResource(".");
        System.out.println(resource);
        System.out.println(resource.getPath());

        File file = new File(resource.getPath() + File.separator + "book.txt");
        System.out.println(file);
        RandomAccessFile rw = new RandomAccessFile(file, "rw");
        FileChannel channel = rw.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int read = channel.read(buffer);
        while (read != -1) {
            read = channel.read(buffer);
        }
        buffer.flip();
        String content = new String(buffer.array(), 0, buffer.limit());
        System.out.println("read content is:");
        System.out.println(content);

        rw.close();

    }




}