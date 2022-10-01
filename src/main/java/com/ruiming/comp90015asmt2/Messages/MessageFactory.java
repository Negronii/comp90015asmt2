package com.ruiming.comp90015asmt2.Messages;

import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

// each message should be
public class MessageFactory {

    /**
     * parse a String s to a Message msg, and return msg
     * @param s
     * @return msg
     */
    public static Message parseMessage(String s) {
        String[] split = s.split(",");
        return switch (split[0]) {
            // an approval message should be "approval,sender,timeStamp,username"
            case "approval" -> new ApprovalRequestMessage(split[1], Long.parseLong(split[2]), split[3]);
            // a chat message should be "chat,username,timeStamp,text"
            case "chat" -> new ChatMessage(split[1], Long.parseLong(split[2]), split[3]);
            // a creation message should be "create,username,timeStamp"
            case "create" -> new CreateRequestMessage(split[1], Long.parseLong(split[2]));
            // a draw circle message should be "circle,username,timeStamp,x,y,width,height,color"
            case "circle" -> new DrawCircleMessage(split[1], Long.parseLong(split[2]),
                    Double.parseDouble(split[3]), Double.parseDouble(split[4]),
                    Double.parseDouble(split[5]), Double.parseDouble(split[6]),
                    Color.web(split[7]));
            // a draw line message should be "line,username,timeStamp,startX,startY,endX,endY,width,color"
            case "line" -> new DrawLineMessage(split[1], Long.parseLong(split[2]),
                    Double.parseDouble(split[3]), Double.parseDouble(split[4]),
                    Double.parseDouble(split[5]), Double.parseDouble(split[6]),
                    Double.parseDouble(split[7]), Color.web(split[8]));
            // a draw rectangle message should be "rectangle,username,timeStamp,x,y,width,height,color"
            case "rectangle" -> new DrawRectMessage(split[1], Long.parseLong(split[2]),
                    Double.parseDouble(split[3]), Double.parseDouble(split[4]),
                    Double.parseDouble(split[5]), Double.parseDouble(split[6]),
                    Color.web(split[7]));
            // a draw text message should be "text,sender,timeStamp,x,y,text,color,size
            case "text" -> new DrawTextMessage(split[1], Long.parseLong(split[2]),
                    Double.parseDouble(split[3]), Double.parseDouble(split[4]),
                    split[5], Color.web(split[6]), Double.parseDouble(split[7]));
            // a draw triangle message should be "triangle,sender,timeStamp,x1,x2,x3,y1,y2,y3,color"
            case "triangle" -> new DrawTriangleMessage(split[1], Long.parseLong(split[2]),
                    new double[]{Double.parseDouble(split[3]), Double.parseDouble(split[4]), Double.parseDouble(split[5])},
                    new double[]{Double.parseDouble(split[6]), Double.parseDouble(split[7]), Double.parseDouble(split[8])},
                    Color.web(split[9]));
            // an erase message should be "erase,username,timeStamp,x,y,brushSize"
            case "erase" -> new EraseMessage(split[1], Long.parseLong(split[2]),
                    Double.parseDouble(split[3]), Double.parseDouble(split[4]), Double.parseDouble(split[5]));
            // a request message should be "request,username,timeStamp"
            case "request" -> new JoinRequestMessage(split[1], Long.parseLong(split[2]));
            // a quit message should be "quit,username,timeStamp"
            case "quit" -> new QuitMessage(split[1], Long.parseLong(split[2]));
            // a fetch user message should be "user,sender,timeStamp,userName"
            case "user" -> new FetchUserMessage(split[1], Long.parseLong(split[2]), split[3]);
            // an error message should be "error,Sender,timeStamp,errorMsg"
            case "error" -> new ErrorMessage(split[1], Long.parseLong(split[2]), split[3]);
            default -> null;
        };
    }

    public static void writeMsg(BufferedWriter bufferedWriter, Message msg) throws IOException {
        System.out.println("sending: " + msg.toString());
        bufferedWriter.write(msg.toString());
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public static Message readMsg(BufferedReader bufferedReader) throws IOException {
        String s = bufferedReader.readLine();
        if (s != null) {
            Message msg = parseMessage(s);
            System.out.println("received: " + msg.toString());
            return msg;
        } else {
            throw new IOException();
        }
    }
}
