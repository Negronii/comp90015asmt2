package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.ruiming.comp90015asmt2.Messages.MessageFactory.readMsg;
import static com.ruiming.comp90015asmt2.Messages.MessageFactory.writeMsg;

public class JoinWhiteBoard extends Application {
    public static Stage window;

    public static BufferedReader bufferedReader;
    public static BufferedWriter bufferedWriter;
    public static String username;

    static Socket socket;

    ClientListener clientListener;

    @Override
    public void start(Stage stage) throws IOException {
        WhiteBoardController.isManager = false;
        // main stage
        window = stage;
        // load fxml
        FXMLLoader fxmlLoader = new FXMLLoader(CreateWhiteBoard.class.getResource("WhiteBoardView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        window.setTitle("Distributed Shared White Board Client");
        window.setScene(scene);
        window.show();
        clientListener = new ClientListener(bufferedReader, bufferedWriter, fxmlLoader.getController());
        clientListener.start();
        writeMsg(bufferedWriter,new FetchRequestMessage(username));
    }

    @Override
    public void stop() throws IOException {
        writeMsg(bufferedWriter, new QuitMessage(username));
        clientListener.interrupt();
        socket.close();
    }

    public static void main(String[] args) throws UnknownHostException {
        username = "hello1";
        InetAddress idxAddress = InetAddress.getByName("localhost");
        int idxPort = 3201;
        try {
            socket = new Socket(idxAddress, idxPort);
            // set up reader and writer for IO stream
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            writeMsg(bufferedWriter, new JoinRequestMessage(username));
            Message msg = readMsg(bufferedReader);
            System.out.println("Please wait for manager to approve");
            if (msg instanceof ApprovalRequestMessage) launch();
            else if (msg instanceof RefuseRequestMessage) System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
