package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.CreateRequestMessage;
import com.ruiming.comp90015asmt2.Messages.FetchRequestMessage;
import com.ruiming.comp90015asmt2.Messages.MessageFactory;
import com.ruiming.comp90015asmt2.Messages.QuitMessage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.ruiming.comp90015asmt2.Messages.MessageFactory.*;

public class CreateWhiteBoard extends Application {
    public static Stage window;
    public static BufferedReader bufferedReader;
    public static BufferedWriter bufferedWriter;
    public static String username;

    ClientListener clientListener;

    static Socket socket;

    @Override
    public void start(Stage stage) throws IOException {
        WhiteBoardController.isManager = true;
        // main stage
        window = stage;
        // load fxml
        FXMLLoader fxmlLoader = new FXMLLoader(CreateWhiteBoard.class.getResource("WhiteBoardView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        window.setTitle("Distributed Shared White Board Manager");
        window.setScene(scene);
        window.show();

        clientListener = new ClientListener(bufferedReader, bufferedWriter, fxmlLoader.getController());
        clientListener.start();
    }

    @Override
    public void stop() throws IOException {
        writeMsg(bufferedWriter, new QuitMessage(username));
        clientListener.interrupt();
        socket.close();
    }

    public static void main(String[] args) throws UnknownHostException {
        username = args[0];
        InetAddress idxAddress = InetAddress.getByName(args[1]);
        int idxPort = Integer.parseInt(args[2]);
        try {
            socket = new Socket(idxAddress, idxPort);
            // set up reader and writer for IO stream
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writeMsg(bufferedWriter, new CreateRequestMessage(username));
        } catch (IOException e) {
            e.printStackTrace();
        }

        launch();
    }


}