package com.ruiming.comp90015asmt2.Messages;

public class ClearPanelMessage extends Message {
    public ClearPanelMessage(String sender, long sendTime) {
        super(sender, sendTime);
    }

    @Override
    public String toString() {
        return "clear," + sender + "," + sendTime;
    }
}
