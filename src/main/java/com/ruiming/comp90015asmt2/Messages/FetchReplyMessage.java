package com.ruiming.comp90015asmt2.Messages;

import javafx.scene.image.Image;

public class FetchReplyMessage extends Message {
    public Image image;
    public String username;

    public FetchReplyMessage(String sender, long sendTime, Image image, String username) {
        super(sender, sendTime);
        this.image = image;
        this.username = username;
    }

    @Override
    public String toString() {
        return "fetchImage," + sender + "," + sendTime + "," + ImageMessage.encodeToString(image) + "," + username;
    }
}
