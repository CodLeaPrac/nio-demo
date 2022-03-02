package com.wx.chat3;

import javax.imageio.IIOException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ChatSocket {

    //socketId
    private long socketId;

    //关联 SocketChannel
    private SocketChannel socketChannel;

    //socket 关闭标志 true：socket已关闭  false: socket正常运行
    public boolean endOfStreamReached = false;

    public ChatSocket(SocketChannel sc) {
        this.socketChannel = sc;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public int read(ByteBuffer readByteBuffer) throws IOException {

        //读取SocketChannel 数据到 ByteBuffer
        int read = this.socketChannel.read(readByteBuffer);

        //socket 已关闭
        if(read == -1) {
            return read;
        }

        return read;
    }


    public long getSocketId() {
        return socketId;
    }

    public void setSocketId(long socketId) {
        this.socketId = socketId;
    }

    public boolean isEndOfStreamReached() {
        return endOfStreamReached;
    }

    public void setEndOfStreamReached(boolean endOfStreamReached) {
        this.endOfStreamReached = endOfStreamReached;
    }

    public int write(ByteBuffer writeByteBuffer) throws IOException {
        writeByteBuffer.flip();
        return this.socketChannel.write(writeByteBuffer);
    }

    public void write(String message) throws IOException {
        this.socketChannel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
    }
}
