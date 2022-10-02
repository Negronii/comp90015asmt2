package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ruiming.comp90015asmt2.Messages.MessageFactory.readMsg;
import static com.ruiming.comp90015asmt2.Messages.MessageFactory.writeMsg;
import static com.ruiming.comp90015asmt2.WhiteBoardController.date;

public class ClientListener extends Thread {
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;

    WhiteBoardController whiteBoardController;

    public ClientListener(BufferedReader bufferedReader, BufferedWriter bufferedWriter, WhiteBoardController whiteBoardController) {
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.whiteBoardController = whiteBoardController;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            Message message = readMsg(bufferedReader);
            GraphicsContext g = whiteBoardController.canvas.getGraphicsContext2D();
            if (message instanceof DrawRectMessage drawRectMessage) {
                g.setFill(drawRectMessage.color);
                g.fillRect(drawRectMessage.x, drawRectMessage.y, drawRectMessage.width, drawRectMessage.height);
            } else if (message instanceof DrawCircleMessage drawCircleMessage) {
                g.setFill(drawCircleMessage.color);
                g.fillOval(drawCircleMessage.x, drawCircleMessage.y, drawCircleMessage.width, drawCircleMessage.height);
            } else if (message instanceof DrawLineMessage drawLineMessage) {
                g.setFill(drawLineMessage.color);
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
            } else if (message instanceof ManagerQuitMessage) {
                this.interrupt();
                if (message.sender.equals(whiteBoardController.username)) {
                    whiteBoardController.onExit();
                } else
                    Platform.runLater(() -> {
                        Stage window = new Stage();
                        window.initModality(Modality.APPLICATION_MODAL); //make user deal with alert first
                        window.setTitle("The manager has left the board");
                        window.setWidth(250);
                        Label label = new Label();
                        label.setText("Please leave");
                        Button closeButton = new Button();
                        closeButton.setText("Got it");
                        closeButton.setOnAction(e -> whiteBoardController.onExit()); //window.close() to close the stage
                        VBox layout = new VBox(10);
                        layout.getChildren().addAll(label, closeButton);
                        layout.setAlignment(Pos.CENTER);
                        Scene scene = new Scene(layout);
                        window.setScene(scene);
                        window.showAndWait(); // wait until user close stage
                    });
            } else if (message instanceof QuitMessage quitMessage) {
                this.interrupt();
                Node toBedelete = null;
                for (Node node : whiteBoardController.vbox_user.getChildren()) {
                    if (node instanceof HBox hbox) {
                        for (Node node1 : hbox.getChildren()) {
                            if (node1 instanceof TextFlow textFlow) {
                                for (Node node2 : textFlow.getChildren()) {
                                    if (node2 instanceof Text text) {
                                        if (text.getText().equals(quitMessage.sender)) {
                                            toBedelete = node;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Node finalToBedelete = toBedelete;
                Platform.runLater(() -> {
                    if (finalToBedelete != null)
                        whiteBoardController.vbox_user.getChildren().remove(finalToBedelete);
                });

            } else if (message instanceof ClearPanelMessage) {
                g.clearRect(0, 0, whiteBoardController.canvas.getWidth(), whiteBoardController.canvas.getHeight());
            } else if (message instanceof ImageMessage imageMessage) {
                whiteBoardController.canvas.getGraphicsContext2D().drawImage(imageMessage.image, 0, 0);
            } else if (message instanceof FetchUserMessage fetchUserMessage) {
                boolean isSelf = fetchUserMessage.username.equals(whiteBoardController.username);
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setPadding(new Insets(5, 5, 5, 10));
                Text text = new Text(fetchUserMessage.username);
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-color: black; -fx-background-color: White; -fx-background-radius: 20;");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                if (!isSelf) hBox.getChildren().add(new Text("Me: "));
                hBox.getChildren().add(textFlow);
                Platform.runLater(() -> whiteBoardController.vbox_user.getChildren().add(hBox));
            } else if (message instanceof FetchRequestMessage) {
                Platform.runLater(() -> {
                    Image snapShot = whiteBoardController.canvas.snapshot(null, null);
                    writeMsg(bufferedWriter, new FetchReplyMessage(whiteBoardController.username, snapShot, message.sender));
                });
            } else if (message instanceof FetchReplyMessage fetchReplyMessage) {
                whiteBoardController.canvas.getGraphicsContext2D().drawImage(fetchReplyMessage.image, 0, 0);
            } else if (message instanceof ChatMessage chatMessage) {
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                Date date = new Date();
                String time = formatter.format(date);
                if (whiteBoardController.lastTime == null || !whiteBoardController.lastTime.equals(time)) {
                    whiteBoardController.lastTime = time;
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER);
                    Text text = new Text(time);
                    text.setStyle("-fx-color: Grey");
                    hBox.getChildren().add(text);
                    Platform.runLater(() -> whiteBoardController.vbox_chat.getChildren().add(hBox));
                }
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
                Platform.runLater(() -> whiteBoardController.vbox_chat.getChildren().add(hBox));
            } else if (message instanceof JoinRequestMessage) {
                Platform.runLater(() -> {
                    Stage window = new Stage();
                    window.initModality(Modality.APPLICATION_MODAL); //make user deal with alert first
                    window.setTitle("new User Join Request");
                    window.setWidth(250);
                    Label label = new Label();
                    label.setText(message.sender + " want to join the white board");
                    Button acceptButton = new Button();
                    acceptButton.setText("Accept");
                    acceptButton.setOnAction(e -> {
                        writeMsg(bufferedWriter, new ApprovalRequestMessage(whiteBoardController.username, message.sender));
                        window.close();
                    }); //window.close() to close the stage
                    Button closeButton = new Button();
                    closeButton.setText("Refuse");
                    closeButton.setOnAction(e -> {
                        writeMsg(bufferedWriter, new RefuseRequestMessage(whiteBoardController.username));
                        window.close();
                    });

                    VBox layout = new VBox(10);
                    layout.getChildren().addAll(label, acceptButton, closeButton);
                    layout.setAlignment(Pos.CENTER);
                    Scene scene = new Scene(layout);
                    window.setScene(scene);
                    window.showAndWait(); // wait until user close stage
                });
            }

        }
    }
}
