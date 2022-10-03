package com.ruiming.comp90015asmt2.Messages;

public class RefuseRequestMessage extends Message {
    public RefuseRequestMessage(String sender) {
        super(sender);
    }

    @Override
    public String toString() {
        return "refuse," + sender;
    }
}
