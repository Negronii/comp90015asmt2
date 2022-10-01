package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.ApprovalRequestMessage;
import com.ruiming.comp90015asmt2.Messages.CreateRequestMessage;
import com.ruiming.comp90015asmt2.Messages.JoinRequestMessage;
import com.ruiming.comp90015asmt2.Messages.MessageFactory;
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

    @Override
    public void start(Stage stage) throws IOException {
        WhiteBoardController.isManager = false;
        // main stage
        window = stage;
        // load fxml
        FXMLLoader fxmlLoader = new FXMLLoader(CreateWhiteBoard.class.getResource("WhiteBoardView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        window.setTitle("Distributed Shared White Board");
        window.setScene(scene);
        window.show();
    }

    public static void main(String[] args) throws UnknownHostException {
        username = "hello1";
        InetAddress idxAddress = InetAddress.getByName("localhost");
        int idxPort = 3201;
        try {
            Socket socket = new Socket(idxAddress, idxPort);
            // set up reader and writer for IO stream
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            writeMsg(bufferedWriter, new JoinRequestMessage(username, new Date().getTime()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        launch();
    }
}
