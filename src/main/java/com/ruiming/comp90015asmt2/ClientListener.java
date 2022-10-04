package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ruiming.comp90015asmt2.Messages.MessageFactory.readMsg;
import static com.ruiming.comp90015asmt2.Messages.MessageFactory.writeMsg;
import static com.ruiming.comp90015asmt2.WhiteBoardController.*;
import static com.ruiming.comp90015asmt2.WhiteBoardController.date;

public class ClientListener extends Thread {
    // buffered reader and writer from the input/output stream from socket
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;

    // the white board controller from the Client side
    WhiteBoardController whiteBoardController;

    // the manager username
    String manager;

    // last appeared chat time
    String time = "";

    /**
     * A method to initiate client listener
     * @param bufferedReader the buffered reader from socket input connecting server and client
     * @param bufferedWriter the buffered writer from socket input connecting server and client
     * @param whiteBoardController the white board controller from the Client side
     */
    public ClientListener(BufferedReader bufferedReader, BufferedWriter bufferedWriter, WhiteBoardController whiteBoardController) {
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.whiteBoardController = whiteBoardController;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            // read one piece of message
            Message message = readMsg(bufferedReader);
            GraphicsContext g = whiteBoardController.canvas.getGraphicsContext2D();
            // deal with different messages
            if (message instanceof DrawRectMessage drawRectMessage) {
                g.setFill(drawRectMessage.color);
                g.fillRect(drawRectMessage.x, drawRectMessage.y, drawRectMessage.width, drawRectMessage.height);
            } else if (message instanceof DrawCircleMessage drawCircleMessage) {
                g.setFill(drawCircleMessage.color);
                g.fillOval(drawCircleMessage.x, drawCircleMessage.y, drawCircleMessage.width, drawCircleMessage.height);
            } else if (message instanceof DrawLineMessage drawLineMessage) {
                g.setStroke(drawLineMessage.color);
                g.setLineWidth(drawLineMessage.width);
                g.strokeLine(drawLineMessage.startX, drawLineMessage.startY, drawLineMessage.endX, drawLineMessage.endY);
            } else if (message instanceof DrawTextMessage drawTextMessage) {
                g.setFill(drawTextMessage.color);
                g.setFont(new Font("Arial", drawTextMessage.size));
                g.fillText(drawTextMessage.text, drawTextMessage.x, drawTextMessage.y);
            } else if (message instanceof DrawTriangleMessage drawTriangleMessage) {
                g.setFill(drawTriangleMessage.color);
                g.fillPolygon(drawTriangleMessage.triangleXs, drawTriangleMessage.triangleYs, 3);
            } else if (message instanceof EraseMessage eraseMessage) {
                g.clearRect(eraseMessage.x, eraseMessage.y, eraseMessage.brushSize, eraseMessage.brushSize);
            } else if (message instanceof KickMessage) {
                // a client receives kick message, the client listener will end running
                this.interrupt();
                Platform.runLater(() -> {
                    whiteBoardController.showAlert("Exit Message", "Manager kicked you from white board");
                    whiteBoardController.onExit();
                });
            } else if (message instanceof QuitMessage) {
                // if the manager quits, all users exit
                if (message.sender.equals(manager))
                    Platform.runLater(() -> {
                        whiteBoardController.showAlert("Manager leave", "The white board is closing");
                        whiteBoardController.onExit();
                    });
                // if other client quits, remove user from the user list
                else
                    Platform.runLater(() -> whiteBoardController.removeUser(message.sender));
            } else if (message instanceof ClearPanelMessage) {
                g.clearRect(0, 0, whiteBoardController.canvas.getWidth(), whiteBoardController.canvas.getHeight());
            } else if (message instanceof ImageMessage imageMessage) {
                whiteBoardController.canvas.getGraphicsContext2D().drawImage(imageMessage.image, 0, 0);
            } else if (message instanceof FetchUserMessage fetchUserMessage) {
                boolean isSelf = fetchUserMessage.username.equals(whiteBoardController.username);
                // each line
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setPadding(new Insets(5, 5, 5, 10));
                // the username component, set special color if the user is itself
                Text text = new Text(fetchUserMessage.username);
                text.setFill(isSelf ? Color.web("A2D2FF") : Color.BLACK);
                // add a wrapper so that it supports long username
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-background-color: White; -fx-background-radius: 20;");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                hBox.getChildren().add(textFlow);
                // if the manager receives adding new user message, add a kick button after username
                if (isManager && !fetchUserMessage.username.equals(whiteBoardController.username)) {
                    Button button = new Button("Kick");
                    button.setStyle("-fx-text-fill: White; -fx-background-color: #3A86FF; -fx-background-radius: 20; -fx-font-size: 12;");
                    button.setPadding(new Insets(5, 10, 5, 10));
                    button.setAlignment(Pos.CENTER_RIGHT);
                    button.setOnMouseEntered(e -> button.setEffect(new DropShadow()));
                    button.setOnMouseExited(e -> button.setEffect(null));
                    button.setOnAction(e -> writeMsg(bufferedWriter, new KickMessage(whiteBoardController.username, fetchUserMessage.username)));
                    hBox.setSpacing(10);
                    hBox.getChildren().add(button);
                }
                // perform hbox to users list
                Platform.runLater(() -> whiteBoardController.vbox_user.getChildren().add(hBox));
            } else if (message instanceof FetchRequestMessage) {
                Platform.runLater(() -> {
                    Image snapShot = whiteBoardController.canvas.snapshot(null, null);
                    writeMsg(bufferedWriter, new FetchReplyMessage(whiteBoardController.username, snapShot, message.sender));
                });
            } else if (message instanceof FetchReplyMessage fetchReplyMessage) {
                manager = fetchReplyMessage.sender;
                whiteBoardController.canvas.getGraphicsContext2D().drawImage(fetchReplyMessage.image, 0, 0);
            } else if (message instanceof ChatMessage chatMessage) {
                // if time changed, show time
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                LocalDateTime now = LocalDateTime.now();
                String curTime = dtf.format(now);
                if (!time.equals(curTime)) {
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setPadding(new Insets(5, 5, 5, 10));
                    Text timeText = new Text(curTime);
                    timeText.setStyle("-fx-color: Grey");
                    hBox.getChildren().add(timeText);
                    time = curTime;
                    Platform.runLater(() -> whiteBoardController.vbox_chat.getChildren().add(hBox));
                }
                // messages from my self should be green background on the right,
                // messages from others should contain their username and the message content
                boolean isSelf = message.sender.equals(whiteBoardController.username);
                HBox hBox = new HBox();
                hBox.setAlignment(isSelf ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                hBox.setPadding(new Insets(5, 5, 5, 10));
                Text text = new Text(chatMessage.chatContent);
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle(!isSelf ? "-fx-color: black; -fx-background-color: White; -fx-background-radius: 20" :
                        "-fx-background-color: #43CC47; -fx-color: Black; -fx-background-radius: 20;");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                if (!isSelf) hBox.getChildren().add(new Text(message.sender + ": "));
                hBox.getChildren().add(textFlow);
                // update to GUI
                Platform.runLater(() -> whiteBoardController.vbox_chat.getChildren().add(hBox));
            } else if (message instanceof ErrorMessage) {
                // if any error happened
                this.interrupt();
                Platform.exit();
            } else if (message instanceof JoinRequestMessage) {
                // display accept or not alert window on manager wide
                Platform.runLater(() -> {
                    whiteBoardController.managerLetJoinWindow(message);
                });
            }

        }
    }
}
