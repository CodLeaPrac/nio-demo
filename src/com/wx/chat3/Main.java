package com.wx.chat3;

public class Main {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);
        server.start();
    }
}
