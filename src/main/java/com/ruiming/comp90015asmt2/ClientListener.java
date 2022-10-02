package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

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
        GraphicsContext g = whiteBoardController.canvas.getGraphicsContext2D();
        while (!isInterrupted()) {
            try {
                Message message = readMsg(bufferedReader);
                if (message instanceof DrawRectMessage drawRectMessage) {
                    g.setFill(drawRectMessage.color);
                    g.fillRect(drawRectMessage.x, drawRectMessage.y, drawRectMessage.width, drawRectMessage.height);
                } else if (message instanceof DrawCircleMessage drawCircleMessage) {
                    g.setFill(drawCircleMessage.color);
                    g.fillOval(drawCircleMessage.x, drawCircleMessage.y, drawCircleMessage.width, drawCircleMessage.height);
                } else if (message instanceof  DrawLineMessage drawLineMessage) {
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
                } else if (message instanceof QuitMessage quitMessage) {
                    // to be implemented
                } else if (message instanceof ClearPanelMessage) {
                    g.clearRect(0, 0, whiteBoardController.canvas.getWidth(), whiteBoardController.canvas.getHeight());;
                } else if (message instanceof JoinRequestMessage joinRequestMessage) {
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
                            try {
                                writeMsg(bufferedWriter, new ApprovalRequestMessage(whiteBoardController.username, date.getTime(), message.sender));
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            window.close();
                        }); //window.close() to close the stage
                        Button closeButton = new Button();
                        closeButton.setText("Refuse");
                        closeButton.setOnAction(e -> {
                            try {
                                writeMsg(bufferedWriter, new RefuseRequestMessage(whiteBoardController.username, date.getTime()));
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            window.close();
                        });

                        VBox layout = new VBox(10);
                        layout.getChildren().addAll(label,acceptButton, closeButton);
                        layout.setAlignment(Pos.CENTER);
                        Scene scene = new Scene(layout);
                        window.setScene(scene);
                        window.showAndWait(); // wait until user close stage
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
