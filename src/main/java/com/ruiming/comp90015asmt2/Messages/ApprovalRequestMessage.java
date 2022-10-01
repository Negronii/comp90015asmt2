package com.ruiming.comp90015asmt2.Messages;

public class ApprovalRequestMessage extends Message {
    public String username;

    public ApprovalRequestMessage(String sender, long sendTime, String username) {
        super(sender, sendTime);
        this.username = username;
    }

    @Override
    public String toString() {
        return "approval," + sender + "," + sendTime + "," + username;
    }
}
