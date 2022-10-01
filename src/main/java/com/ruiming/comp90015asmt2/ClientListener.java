package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;
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
    GraphicsContext g;

    String username;

    public ClientListener(BufferedReader bufferedReader, BufferedWriter bufferedWriter, GraphicsContext g, String username) {
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.g = g;
        this.username = username;
    }

    @Override
    public void run() {
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
                } else if (message instanceof JoinRequestMessage joinRequestMessage) {
                    //
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
