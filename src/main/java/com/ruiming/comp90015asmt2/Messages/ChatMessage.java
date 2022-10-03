package com.ruiming.comp90015asmt2.Messages;

public class ChatMessage extends Message {
    public String chatContent;
    public long time;

    public ChatMessage(String sender, String chatContent) {
        super(sender);
        this.chatContent = chatContent;
    }

    @Override
    public String toString() {
        return "chat," + sender + "," + chatContent;
    }
}
