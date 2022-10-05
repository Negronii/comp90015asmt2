package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

import static com.ruiming.comp90015asmt2.Messages.MessageFactory.readMsg;
import static com.ruiming.comp90015asmt2.Messages.MessageFactory.writeMsg;

public class Server extends Thread {

    // a deque to store sockets brought by ioThread
    private final LinkedBlockingDeque<Socket> socketDeque;

    // ioThread listening on port and offer socket to deque
    private final IOThread ioThread;

    // the manager of the whiteboard
    public String manager;

    // Username: ServerListener map
    public Map<String, ServerConnection> nameThreadMap;

    // ioThread listening on port and offer socket to deque
    class IOThread extends Thread {
        private ServerSocket serverSocket;
        private LinkedBlockingDeque<Socket> incomingConnections;

        public IOThread(int port,
                        LinkedBlockingDeque<Socket> incomingConnections) throws IOException {
            this.incomingConnections = incomingConnections;
            serverSocket = new ServerSocket(port);
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

    public Server(int port) throws IOException {
        socketDeque = new LinkedBlockingDeque<>();
        nameThreadMap = new HashMap<>();
        ioThread = new IOThread(port, socketDeque);
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

        // if the username is occupied, write message and close socket
        if (nameThreadMap.containsKey(msg.sender)) {
            writeMsg(bufferedWriter, new ErrorMessage("Server", "username occupied"));
            return;
        }
        if (msg instanceof CreateRequestMessage) {
            if (nameThreadMap.size() > 0) {
                writeMsg(bufferedWriter, new ErrorMessage("Server", "server occupied"));
                return;
            }
            writeMsg(bufferedWriter, new WelcomeMessage());
            manager = msg.sender;
            ServerConnection serverConnection = new ServerConnection(msg.sender, bufferedReader, bufferedWriter, this);
            serverConnection.isApproved = true;
            nameThreadMap.put(msg.sender, serverConnection);
            serverConnection.start();
            writeMsg(bufferedWriter, new FetchUserMessage("System", manager));
        } else if (msg instanceof JoinRequestMessage) {
            if (nameThreadMap.size() == 0) {
                writeMsg(bufferedWriter, new ErrorMessage("Server", "Please wait manager creating whiteboard"));
                return;
            }
            ServerConnection serverConnection = new ServerConnection(msg.sender, bufferedReader, bufferedWriter, this);
            nameThreadMap.put(msg.sender, serverConnection);
            serverConnection.start();
            writeMsg(nameThreadMap.get(manager).bufferedWriter, msg);
        } else {
            // if it is an unexpected message
            writeMsg(bufferedWriter, new ErrorMessage("Server", "invalid request"));
            socket.close();
        }
    }


    public static void main(String[] args) throws IOException {
        new Server(Integer.parseInt(args[0])).start();
    }
}
