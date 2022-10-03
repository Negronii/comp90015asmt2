package com.ruiming.comp90015asmt2.Messages;

public class QuitMessage extends Message{
    public QuitMessage(String sender) {
        super(sender);
    }

    @Override
    public String toString() {
        return "quit," + sender;
    }
}
