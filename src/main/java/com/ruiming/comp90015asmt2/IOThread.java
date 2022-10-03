package com.ruiming.comp90015asmt2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;


public class IOThread extends Thread {
    private ServerSocket serverSocket;
    private LinkedBlockingDeque<Socket> incomingConnections;

    public IOThread(int port,
                    LinkedBlockingDeque<Socket> incomingConnections) throws IOException {
        this.incomingConnections = incomingConnections;
        serverSocket = new ServerSocket(port);
    }

    public void shutdown() throws IOException {
        serverSocket.close();
    }

    @Override
    public void run() {
        System.out.println("IO thread running");
        while (!isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                try {
                    if (!incomingConnections.offer(socket)) {
                        socket.close();
                        System.out.println("IO thread dropped connection - incoming connection queue is full.");
                    }
                } catch (IOException e) {
                    System.out.println("Something went wrong with the connection.");
                }
            } catch (IOException e) {
                System.out.println("IO thread failed to accept.");
                break;
            }
        }
        System.out.println("IO thread completed.");
    }
}
