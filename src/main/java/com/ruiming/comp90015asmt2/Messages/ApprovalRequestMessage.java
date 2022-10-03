package com.ruiming.comp90015asmt2.Messages;

public class ApprovalRequestMessage extends Message {
    public String username;

    public ApprovalRequestMessage(String sender, String username) {
        super(sender);
        this.username = username;
    }

    @Override
    public String toString() {
        return "approval," + sender + "," + username;
    }
}
