package com.ruiming.comp90015asmt2.Messages;

public class WelcomeMessage extends Message{
    public WelcomeMessage() {
        super("Server");
    }

    @Override
    public String toString() {
        return "welcome";
    }
}
