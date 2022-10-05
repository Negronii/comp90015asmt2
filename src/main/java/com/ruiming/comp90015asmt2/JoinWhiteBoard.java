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

    // primary stage
    public static Stage window;

    // reader/writer from input/output socket streams
    public static BufferedReader bufferedReader;
    public static BufferedWriter bufferedWriter;

    // the current user's username
    public static String username;

    // the established socket
    static Socket socket;

    // the client listener listening message from server
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

        // create client listener for further messages
        clientListener = new ClientListener(bufferedReader, bufferedWriter, fxmlLoader.getController());
        clientListener.start();

        // send the server a fetch image message
        writeMsg(bufferedWriter,new FetchRequestMessage(username));
    }

    @Override
    public void stop() throws IOException {
        // send quit message to everyone
        writeMsg(bufferedWriter, new QuitMessage(username));
    }

    public static void main(String[] args) throws UnknownHostException {
        // read arg values
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

            writeMsg(bufferedWriter, new JoinRequestMessage(username));
            Message msg = readMsg(bufferedReader);
            System.out.println("Please wait for manager to approve");

            // if the next message is approval message from manager, launch app
            if (msg instanceof ApprovalRequestMessage) launch();
            // otherwise print message and exit
            else if (msg instanceof RefuseRequestMessage) {
                socket.close();
                System.out.println("You are refused");
                System.exit(0);
            } else if (msg instanceof ErrorMessage errorMessage) {
                socket.close();
                System.out.println(errorMessage.errorMsg);
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
