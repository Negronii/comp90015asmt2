package com.ruiming.comp90015asmt2.Messages;

public class FetchUserMessage extends Message {
    public String username;

    public FetchUserMessage(String sender, String username) {
        super(sender);
        this.username = username;
    }

    @Override
    public String toString() {
        return "user," + sender + "," + username;
    }
}
