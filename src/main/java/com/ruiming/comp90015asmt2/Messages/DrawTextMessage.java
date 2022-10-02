package com.ruiming.comp90015asmt2.Messages;

import javafx.scene.paint.Color;

public class DrawTextMessage extends Message {
    public double x;
    public double y;
    public String text;
    public Color color;
    public double size;

    public DrawTextMessage(String sender,
                           double x, double y,
                           String text, Color color, double size) {
        super(sender);
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
        this.size = size;
    }

    @Override
    public String toString() {
        return "text," + sender + "," +
                x + "," + y + "," +
                text + "," + color.toString() + "," +size;
    }
}
