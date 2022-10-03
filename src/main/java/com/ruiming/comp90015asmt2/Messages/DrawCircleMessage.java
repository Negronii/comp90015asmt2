package com.ruiming.comp90015asmt2.Messages;

import javafx.scene.paint.Color;

public class DrawCircleMessage extends Message {
    public double x;
    public double y;
    public double width;
    public double height;
    public Color color;

    public DrawCircleMessage(String sender,
                             double x, double y,
                             double width, double height, Color color) {
        super(sender);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public String toString() {
        return "circle," + sender + "," +
                x + "," + y + "," +
                width + "," + height + "," + color.toString();
    }
}
