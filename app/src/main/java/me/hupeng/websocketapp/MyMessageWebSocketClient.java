package me.hupeng.websocketapp;

import java.net.URISyntaxException;

/**
 * Created by HUPENG on 2017/6/12.
 */
public class MyMessageWebSocketClient extends MessageWebSocketClient {
    private static MessageWebSocketClient messageWebSocketClient = null;

    protected MyMessageWebSocketClient(int userId) {
        super(userId);
    }


    public static MessageWebSocketClient getInstance(int userId){
        if (MyMessageWebSocketClient.messageWebSocketClient == null){
            messageWebSocketClient = new MyMessageWebSocketClient(userId);
        }
        return MyMessageWebSocketClient.messageWebSocketClient;
    }


}
