package com.ruiming.comp90015asmt2.Messages;

import javafx.scene.paint.Color;

public class DrawTriangleMessage extends Message {
    public double[] triangleXs;
    public double[] triangleYs;
    public Color color;

    public DrawTriangleMessage(String sender, long sendTime,
                               double[] triangleXs, double[] triangleYs,
                               Color color) {
        super(sender, sendTime);
        this.triangleXs = triangleXs;
        this.triangleYs = triangleYs;
        this.color = color;
    }

    @Override
    public String toString() {
        return "triangle," + sender + "," + sendTime + "," +
                triangleXs[0] + "," + triangleXs[1] + "," + triangleXs[2] + "," +
                triangleYs[0] + "," + triangleYs[1] + "," + triangleYs[2] + "," +
                color.toString();
    }
}
