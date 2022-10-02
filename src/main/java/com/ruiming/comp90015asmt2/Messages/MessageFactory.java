package com.ruiming.comp90015asmt2.Messages;

import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

// each message should be
public class MessageFactory {

    /**
     * parse a String s to a Message msg, and return msg
     *
     * @param s
     * @return msg
     */
    public static Message parseMessage(String s) {
        String[] split = s.split(",");
        return switch (split[0]) {
            // an approval message should be "approval,sender,username"
            case "approval" -> new ApprovalRequestMessage(split[1], split[2]);
            // a chat message should be "chat,username,text,timeStamp,"
            case "chat" -> new ChatMessage(split[1], split[2]);
            // a creation message should be "create,username"
            case "create" -> new CreateRequestMessage(split[1]);
            // a draw circle message should be "circle,username,x,y,width,height,color"
            case "circle" -> new DrawCircleMessage(split[1],
                    Double.parseDouble(split[2]), Double.parseDouble(split[3]),
                    Double.parseDouble(split[4]), Double.parseDouble(split[5]),
                    Color.web(split[6]));
            // a draw line message should be "line,username,startX,startY,endX,endY,width,color"
            case "line" -> new DrawLineMessage(split[1],
                    Double.parseDouble(split[2]), Double.parseDouble(split[3]),
                    Double.parseDouble(split[4]), Double.parseDouble(split[5]),
                    Double.parseDouble(split[6]), Color.web(split[7]));
            // a draw rectangle message should be "rectangle,username,x,y,width,height,color"
            case "rectangle" -> new DrawRectMessage(split[1],
                    Double.parseDouble(split[2]), Double.parseDouble(split[3]),
                    Double.parseDouble(split[4]), Double.parseDouble(split[5]),
                    Color.web(split[6]));
            // a draw text message should be "text,sender,x,y,text,color,size
            case "text" -> new DrawTextMessage(split[1],
                    Double.parseDouble(split[2]), Double.parseDouble(split[3]),
                    split[4], Color.web(split[5]), Double.parseDouble(split[6]));
            // a draw triangle message should be "triangle,sender,x1,x2,x3,y1,y2,y3,color"
            case "triangle" -> new DrawTriangleMessage(split[1],
                    new double[]{Double.parseDouble(split[2]), Double.parseDouble(split[3]), Double.parseDouble(split[4])},
                    new double[]{Double.parseDouble(split[5]), Double.parseDouble(split[6]), Double.parseDouble(split[7])},
                    Color.web(split[8]));
            // an erase message should be "erase,username,x,y,brushSize"
            case "erase" -> new EraseMessage(split[1],
                    Double.parseDouble(split[2]), Double.parseDouble(split[3]), Double.parseDouble(split[4]));
            // a request message should be "request,username"
            case "request" -> new JoinRequestMessage(split[1]);
            // a quit message should be "quit,username"
            case "quit" -> new QuitMessage(split[1]);
            // a fetch user message should be "user,sender,userName"
            case "user" -> new FetchUserMessage(split[1], split[2]);
            // an error message should be "error,Sender,errorMsg"
            case "error" -> new ErrorMessage(split[1], split[2]);
            // a refuse message should be "refuse,sender"
            case "refuse" -> new RefuseRequestMessage(split[1]);
            // a clear screen message should be "clear,sender"
            case "clear" -> new ClearPanelMessage(split[1]);
            // a fetch request should be "fetch,sender"
            case "fetch" -> new FetchRequestMessage(split[1]);
            // a image message should be "image,sender,encodedString"
            case "image" -> new ImageMessage(split[1], ImageMessage.decodeToImage(split[2]));
            // a fetch image message should be "fetchImage,sender,encodedString,targetUsername"
            case "fetchImage" -> new FetchReplyMessage(split[1], ImageMessage.decodeToImage(split[2]), split[3]);
            // a manager quit message should be "managerQuit,sender"
            case "managerQuit" -> new ManagerQuitMessage(split[1]);
            default -> null;
        };
    }

    public static void writeMsg(BufferedWriter bufferedWriter, Message msg) {
        try {
            System.out.println("sending: " + msg.toString());
            bufferedWriter.write(msg.toString());
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message readMsg(BufferedReader bufferedReader) {
        try {
            String s = bufferedReader.readLine();
            if (s != null) {
                Message msg = parseMessage(s);
                System.out.println("received: " + msg.toString());
                return msg;
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
