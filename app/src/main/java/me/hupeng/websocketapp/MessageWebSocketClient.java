package me.hupeng.websocketapp;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by HUPENG on 2017/6/5.
 */
public class MessageWebSocketClient {
    private static MessageWebSocketClient messageWebSocketClient = null;

    private MessageWebSocketClient(){

    }

    public static MessageWebSocketClient getInstance(){
        if (MessageWebSocketClient.messageWebSocketClient == null){
            messageWebSocketClient = new MessageWebSocketClient();
            try {
                messageWebSocketClient.initSocketClient();
                messageWebSocketClient .connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
        return MessageWebSocketClient.messageWebSocketClient;
    }



    private WebSocketClient webSocketClient;
    private String address = "ws://192.168.3.228:8080/websocket";

    private void initSocketClient() throws URISyntaxException {
        if(webSocketClient == null) {
            webSocketClient = new WebSocketClient(new URI(address)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.i("MessageWebSocketClient","");
                }

                @Override
                public void onMessage(String s) {
                    Log.i("MessageWebSocketClient","");
                }


                @Override
                public void onClose(int i, String s, boolean remote) {
                    Log.i("MessageWebSocketClient","");
                    reConnect();
                }


                @Override
                public void onError(Exception e) {
                    Log.i("MessageWebSocketClient","");
                }
            };
        }
    }


    //连接
    private void connect() {
        new Thread(){
            @Override
            public void run() {
                webSocketClient.connect();
            }
        }.start();
    }


    //断开连接
    private void reConnect() {
        try {
            webSocketClient.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            webSocketClient = null;
        }
        while (webSocketClient == null){
            try {
                initSocketClient();
                connect();
                Thread.sleep(2000);
            }catch (Exception e){

            }
        }
    }



    public boolean sendMsg(String msg) {
        try {
            webSocketClient.send(msg);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
