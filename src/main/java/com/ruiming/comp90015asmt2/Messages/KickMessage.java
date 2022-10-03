package com.ruiming.comp90015asmt2.Messages;

public class KickMessage extends Message {
    public String username;

    public KickMessage(String sender, String username) {
        super(sender);
        this.username = username;
    }

    @Override
    public String toString() {
        return "kick," + sender + "," + username;
    }
}
