package com.ruiming.comp90015asmt2.Messages;

public class QuitMessage extends Message{
    public QuitMessage(String sender, long sendTime) {
        super(sender, sendTime);
    }

    @Override
    public String toString() {
        return "quit," + sender + "," + sendTime;
    }
}
