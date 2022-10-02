package com.ruiming.comp90015asmt2.Messages;

public class FetchRequestMessage extends Message {
    public FetchRequestMessage(String sender, long sendTime) {
        super(sender, sendTime);
    }

    @Override
    public String toString() {
        return "fetch," + sender + "," + sendTime;
    }
}
