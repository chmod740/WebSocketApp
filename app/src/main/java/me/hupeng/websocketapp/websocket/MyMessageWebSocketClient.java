package me.hupeng.websocketapp.websocket;

/**
 * 实现单例
 * */
public class MyMessageWebSocketClient extends MessageWebSocketClient {
    private static MessageWebSocketClient messageWebSocketClient = null;

    protected MyMessageWebSocketClient(int userId) {
        super();
    }


    public static MessageWebSocketClient getInstance(int userId){
        if (MyMessageWebSocketClient.messageWebSocketClient == null){
            messageWebSocketClient = new MyMessageWebSocketClient(userId);
        }
        return MyMessageWebSocketClient.messageWebSocketClient;
    }


}
