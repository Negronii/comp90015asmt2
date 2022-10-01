package com.ruiming.comp90015asmt2.Messages;

public class ErrorMessage extends Message {
    public String errorMsg;

    public ErrorMessage(String sender, long sendTime, String errorMsg) {
        super(sender, sendTime);
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "error," + sender + "," + sendTime + "," + errorMsg;
    }
}
