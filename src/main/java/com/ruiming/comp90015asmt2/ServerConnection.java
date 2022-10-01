package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import static com.ruiming.comp90015asmt2.Messages.MessageFactory.*;

public class ServerConnection extends Thread {
    public Socket socket;
    public String username;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public Server server;

    public boolean isApproved = true;

    public ServerConnection(Socket socket, String username, BufferedReader bufferedReader, BufferedWriter bufferedWriter, Server server) {
        this.socket = socket;
        this.username = username;
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.server = server;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Message message = readMsg(bufferedReader);
                if (message instanceof ErrorMessage) {
                    System.out.println(message);
                } else if (message instanceof ApprovalRequestMessage approvalRequestMessage) {
                    if (message.sender.equals(server.manager)) {
                        writeMsg(server.nameThreadMap.get(approvalRequestMessage.username).bufferedWriter, message);
                    }
                } else {
                    for (String u : server.nameThreadMap.keySet()) {
                        if (!u.equals(username)) {
                            writeMsg(server.nameThreadMap.get(u).bufferedWriter, message);
                        }
                    }
                    server.allMessage.add(message);
                    if (message instanceof QuitMessage) {
                        server.nameThreadMap.remove(username);
                        this.interrupt();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
