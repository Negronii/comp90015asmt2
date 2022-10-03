package com.ruiming.comp90015asmt2.Messages;

import javafx.scene.image.Image;

public class FetchReplyMessage extends Message {
    public Image image;
    public String username;

    public FetchReplyMessage(String sender, Image image, String username) {
        super(sender);
        this.image = image;
        this.username = username;
    }

    @Override
    public String toString() {
        return "fetchImage," + sender + "," + ImageMessage.encodeToString(image) + "," + username;
    }
}
