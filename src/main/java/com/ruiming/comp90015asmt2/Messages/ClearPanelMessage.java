package com.ruiming.comp90015asmt2.Messages;

public class ClearPanelMessage extends Message {
    public ClearPanelMessage(String sender) {
        super(sender);
    }

    @Override
    public String toString() {
        return "clear," + sender;
    }
}
