package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import static com.ruiming.comp90015asmt2.Messages.MessageFactory.*;

public class ServerConnection extends Thread {

    // the username of client
    public String username;

    // the buffered reader/writer from socket ioStream to send message
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;

    // the main server
    public Server server;

    // is the user approved by manager or not
    public boolean isApproved = false;

    public ServerConnection(String username, BufferedReader bufferedReader, BufferedWriter bufferedWriter, Server server) {
        this.username = username;
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.server = server;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            // read one message from client
            Message message;
            try {
                message = readMsg(bufferedReader);
            } catch (IOException e) {
                interrupt();
                throw new RuntimeException(e);
            }
            if (message instanceof ErrorMessage) {
                System.out.println(message);
                this.interrupt();
            } else if (message instanceof FetchRequestMessage) {
                // forward the message to manager
                try {
                    writeMsg(server.nameThreadMap.get(server.manager).bufferedWriter, message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (message instanceof FetchReplyMessage fetchReplyMessage) {
                // forward to joiner
                BufferedWriter newUser = server.nameThreadMap.get(fetchReplyMessage.username).bufferedWriter;
                try {
                    writeMsg(newUser, message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // fetch user to joiner, and add joiner to all users
                for (String s : server.nameThreadMap.keySet()) {
                    if (server.nameThreadMap.get(s).isApproved) {
                        try {
                            writeMsg(newUser, new FetchUserMessage("System", s));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!s.equals(fetchReplyMessage.username)) {
                            try {
                                writeMsg(server.nameThreadMap.get(s).bufferedWriter, new FetchUserMessage("System", fetchReplyMessage.username));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            } else if (message instanceof ApprovalRequestMessage approvalRequestMessage) {
                if (message.sender.equals(server.manager)) {
                    server.nameThreadMap.get(approvalRequestMessage.username).isApproved = true;
                    try {
                        writeMsg(server.nameThreadMap.get(approvalRequestMessage.username).bufferedWriter, message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (message instanceof KickMessage kickMessage) {
                if (message.sender.equals(server.manager)) {
                    try {
                        writeMsg(server.nameThreadMap.get(kickMessage.username).bufferedWriter, kickMessage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (message instanceof RefuseRequestMessage refuseRequestMessage) {
                try {
                    writeMsg(server.nameThreadMap.get(refuseRequestMessage.username).bufferedWriter, message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                server.nameThreadMap.remove(refuseRequestMessage.username);
            } else {
                // broadcast to all approved users
                for (String u : server.nameThreadMap.keySet()) {
                    if (server.nameThreadMap.get(u).isApproved) {
                        try {
                            writeMsg(server.nameThreadMap.get(u).bufferedWriter, message);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                // if the user quited, interrupt this
                if (message instanceof QuitMessage) {
                    server.nameThreadMap.remove(username);
                    this.interrupt();
                }
            }

        }
    }
}
