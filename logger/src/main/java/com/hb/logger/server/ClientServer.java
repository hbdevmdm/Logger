package com.hb.logger.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientServer {

    public static void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(8011);
            while(true) {
                Socket socket = serverSocket.accept();
                PrintStream output = new PrintStream(socket.getOutputStream());
                output.println("123");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
