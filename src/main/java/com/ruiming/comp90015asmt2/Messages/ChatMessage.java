package com.ruiming.comp90015asmt2.Messages;

public class ChatMessage extends Message {
    public String chatContent;

    public ChatMessage(String sender, long sendTime, String chatContent) {
        super(sender, sendTime);
        this.chatContent = chatContent;
    }

    @Override
    public String toString() {
        return "chat," + sender + "," + sendTime + "," + chatContent;
    }
}
