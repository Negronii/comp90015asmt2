package com.ruiming.comp90015asmt2.Messages;

public class JoinRequestMessage extends Message {
    public JoinRequestMessage(String sender, long sendTime) {
        super(sender, sendTime);
    }

    @Override
    public String toString() {
        return "request," + sender + "," + sendTime;
    }
}
