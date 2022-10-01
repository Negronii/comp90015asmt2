package com.ruiming.comp90015asmt2.Messages;

public class FetchUserMessage extends Message {
    public String username;

    public FetchUserMessage(String sender, long sendTime, String username) {
        super(sender, sendTime);
        this.username = username;
    }

    @Override
    public String toString() {
        return "user," + sender + "," + sendTime + "," + username;
    }
}
