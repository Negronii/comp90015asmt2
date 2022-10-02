package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

import static com.ruiming.comp90015asmt2.Messages.MessageFactory.readMsg;
import static com.ruiming.comp90015asmt2.Messages.MessageFactory.writeMsg;

public class Server extends Thread {
    private final int port;
    private final int timeout;
    private final LinkedBlockingDeque<Socket> socketDeque;
    private final IOThread ioThread;
    public String manager;
    public Map<String, ServerConnection> nameThreadMap;
    public ArrayList<Message> allMessage;

    public Server() throws IOException {
        port = 3201;
        timeout = 500000;
        socketDeque = new LinkedBlockingDeque<>();
        nameThreadMap = new HashMap<>();
        allMessage = new ArrayList<>();
        ioThread = new IOThread(port, socketDeque, timeout);
        ioThread.start();
    }

    @Override
    public void run() {
        System.out.println("Server thread running.");
        while (!isInterrupted()) {
            try {
                Socket socket = socketDeque.take();
                processRequest(socket);
            } catch (InterruptedException e) {
                System.out.println("Server interrupted.");
                break;
            } catch (IOException e) {
                System.out.println("Server received io exception on socket.");
            }
        }
        System.out.println("Server thread waiting for IO thread to stop...");
        ioThread.interrupt();
        try {
            ioThread.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted while joining with IO thread.");
        }
        System.out.println("Server thread completed.");
    }

    public void processRequest(Socket socket) throws IOException {
        // set up reader / writer
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        // read the message and deal with each message time
        Message msg = readMsg(bufferedReader);
        if (nameThreadMap.containsKey(msg.sender)) {
            writeMsg(bufferedWriter, new ErrorMessage("Server", new Date().getTime(), "username occupied"));
            socket.close();
            return;
        }
        if (msg instanceof CreateRequestMessage) {
            if (nameThreadMap.size() > 0) {
                writeMsg(bufferedWriter, new ErrorMessage("Server", new Date().getTime(), "server occupied"));
                socket.close();
                return;
            }
            manager = msg.sender;
            ServerConnection serverConnection = new ServerConnection(socket, msg.sender, bufferedReader, bufferedWriter, this);
            serverConnection.isApproved = true;
            nameThreadMap.put(msg.sender, serverConnection);
            serverConnection.start();
        } else if (msg instanceof JoinRequestMessage) {
            ServerConnection serverConnection = new ServerConnection(socket, msg.sender, bufferedReader, bufferedWriter, this);
            nameThreadMap.put(msg.sender, serverConnection);
            serverConnection.start();
            writeMsg(nameThreadMap.get(manager).bufferedWriter, msg);
        } else {
            // if it is an unexpected message
            writeMsg(bufferedWriter, new ErrorMessage("Server", new Date().getTime(), "invalid request"));
            socket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        new Server().start();
    }
}
