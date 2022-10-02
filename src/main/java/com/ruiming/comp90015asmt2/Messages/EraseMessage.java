package com.ruiming.comp90015asmt2.Messages;

public class EraseMessage extends Message {
    public double x;
    public double y;
    public double brushSize;

    public EraseMessage(String sender, double x, double y, double brushSize) {
        super(sender);
        this.x = x;
        this.y = y;
        this.brushSize = brushSize;
    }

    @Override
    public String toString() {
        return "erase," + sender + "," + x + "," + y + "," + brushSize;
    }
}
