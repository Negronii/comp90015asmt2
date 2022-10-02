package com.ruiming.comp90015asmt2.Messages;

public class ManagerQuitMessage extends Message{
    public ManagerQuitMessage(String sender) {
        super(sender);
    }

    @Override
    public String toString() {
        return "managerQuit,"+sender;
    }
}
