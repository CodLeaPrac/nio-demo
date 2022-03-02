package com.wx;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.charset.StandardCharsets;
import java.util.EmptyStackException;

public class PipeDemo {

    public static void main(String[] args) throws Exception {

        Pipe open = Pipe.open();
        Pipe.SinkChannel sink = open.sink();
        Pipe.SourceChannel source = open.source();

        ByteBuffer allocate = ByteBuffer.allocate(1024);
        allocate.put("hello pipe 32432432".getBytes(StandardCharsets.UTF_8));

        allocate.flip();

        sink.write(allocate);

        ByteBuffer allocate1 = ByteBuffer.allocate(1024);
//        allocate1.flip();
        int read = source.read(allocate1);
        String s = new String(allocate1.array(), 0, read);
        System.out.println(s);

        sink.close();
        source.close();

    }
}
