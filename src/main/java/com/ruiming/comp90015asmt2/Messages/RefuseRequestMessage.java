package com.ruiming.comp90015asmt2.Messages;

public class RefuseRequestMessage extends Message {
    public RefuseRequestMessage(String sender, long sendTime) {
        super(sender, sendTime);
    }

    @Override
    public String toString() {
        return "refuse," + sender + "," + sendTime;
    }
}
