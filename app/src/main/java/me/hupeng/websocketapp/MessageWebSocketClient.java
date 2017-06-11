package me.hupeng.websocketapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by HUPENG on 2017/6/5.
 */
public class MessageWebSocketClient {
    private static MessageWebSocketClient messageWebSocketClient = null;

    private boolean openStatus = false;

    private static int userId;

    private MessageWebSocketClient(){

    }

    public static MessageWebSocketClient getInstance(int userId){
        MessageWebSocketClient.userId = userId;
        if (MessageWebSocketClient.messageWebSocketClient == null){
            messageWebSocketClient = new MessageWebSocketClient();
            try {
                messageWebSocketClient.initSocketClient();
                messageWebSocketClient.connect();
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
                    Log.i("MessageWebSocketClient","onOpen");
                    openStatus = true;

                    //发送一个上线的消息给Socket服务器
                    sendOnLineMessage();
                }

                @Override
                public void onMessage(String s) {
                    Log.i("MessageWebSocketClient","onMessage");
                    Log.i("WebSocket收到消息", s);
                }


                @Override
                public void onClose(int i, String s, boolean remote) {
                    Log.i("MessageWebSocketClient","onClose");
                    openStatus = false;
                    reConnect();
                }


                @Override
                public void onError(Exception e) {
                    Log.i("MessageWebSocketClient","onError");
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

    public static interface SendMessageResultListener{
        /**
         * 消息发送成功回调
         * */
        public void onSuccess(long ts);

        /**
         * 消息发送失败回调
         * */
        public void onFail(long ts);
    }

    private SendMessageResultListener sendMessageResultListener = null;

    public synchronized long sendMsg(final String msg, SendMessageResultListener listener) {
        final long ts = System.currentTimeMillis();
        /**
         * 设置发送消息的监听器
         * */
        if (this.sendMessageResultListener == null && listener != null){
            this.sendMessageResultListener = listener;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (openStatus){
                            try {
                                webSocketClient.send(msg);
                                sendMessageResultListener.onSuccess(ts);
                            }catch (Exception e){
                                e.printStackTrace();
                                sendMessageResultListener.onFail(ts);
                            }
                        }else {
                            sendMessageResultListener.onFail(ts);
                        }
                    }
                });
            }
        }).start();
        return ts;
    }

    private void sendOnLineMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (openStatus){
                    Message message = new Message();
                    message.setOperate(Message.ON_LINE);
                    message.setFrom(userId);
                    webSocketClient.send(new Gson().toJson(message));
                }
            }
        }).start();
    }


    public static class Message {
        /**
         * 上线
         * */
        public final static int ON_LINE = 0;
        /**
         * 发送消息
         * */
        public final static int SEND_MESSAGE = 1;


        /**
         *  操作
         * */
        private int operate;

        /**
         * 发信方
         * */
        private int from;

        /**
         * 收信方
         * */
        private int to;

        /**
         * 消息内容
         * */
        private String message;

        public int getOperate() {
            return operate;
        }

        public void setOperate(int operate) {
            this.operate = operate;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
