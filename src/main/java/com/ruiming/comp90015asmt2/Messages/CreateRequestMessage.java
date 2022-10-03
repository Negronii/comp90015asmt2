package com.ruiming.comp90015asmt2.Messages;

public class CreateRequestMessage extends Message {
    public CreateRequestMessage(String sender) {
        super(sender);
    }

    @Override
    public String toString() {
        return "create," + sender;
    }
}
