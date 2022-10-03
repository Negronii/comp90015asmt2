package com.ruiming.comp90015asmt2.Messages;

import javafx.scene.paint.Color;

public class DrawLineMessage extends Message {
    public double startX;
    public double startY;
    public double endX;
    public double endY;
    public double width;
    public Color color;

    public DrawLineMessage(String sender,
                           double startX, double startY,
                           double endX, double endY,
                           double width, Color color) {
        super(sender);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.width = width;
        this.color = color;
    }

    @Override
    public String toString() {
        return "line," + sender + "," +
                startX + "," + startY + "," +
                endX + "," + endY + "," +
                width + "," + color.toString();
    }
}
