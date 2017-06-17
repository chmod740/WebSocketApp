package me.hupeng.websocketapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public abstract class MessageWebSocketClient {

    protected int userId;

    private boolean openStatus = false;

    protected MessageWebSocketClient(int userId){
        this.userId = userId;
        try {
            initSocketClient();
            connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public static interface ConnectStatusListener{
        public void onConnect();
        public void onDisconnect();
    }

    private List<ConnectStatusListener>connectStatusListeners = new ArrayList<>();

    public void addConnectStatusListener(ConnectStatusListener connectStatusListener){
        if (connectStatusListener != null){
            connectStatusListeners.add(connectStatusListener);
        }
    }

    public void removeConnectStatusListener(ConnectStatusListener connectStatusListener){
        connectStatusListeners.remove(connectStatusListener);
    }

    public static interface MessageListener{
        public void onMessage(Object object);
    }

    private List<MessageListener>messageListeners = new ArrayList<>();

    public void addMessageListener(MessageListener messageListener){
        if (messageListener != null){
            messageListeners.add(messageListener);
        }
    }

    public void removeMessageListener(MessageListener messageListener){
        messageListeners.remove(messageListener);
    }


    private WebSocketClient webSocketClient;
    private String address = "ws://192.168.3.228:8080/websocket";

    protected void initSocketClient() throws URISyntaxException {
        if(webSocketClient == null) {
            webSocketClient = new WebSocketClient(new URI(address)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.i("MessageWebSocketClient","onOpen");
                    openStatus = true;

                    //发送一个上线的消息给Socket服务器
//                    sendOnLineMessage();
                    for (ConnectStatusListener connectStatusListener : connectStatusListeners){
                        try {
                            connectStatusListener.onConnect();
                        }catch (Exception e){

                        }
                    }
                }

                @Override
                public void onMessage(String s) {
                    Log.i("MessageWebSocketClient","onMessage");
                    Log.i("WebSocket收到消息", s);
                    Object o = null;
                    if (messageDecoder != null){
                        o = messageDecoder.decode(s);
                    }else {
                        o = s;
                    }
                    for (MessageListener messageListener : messageListeners){
                        try {
                            messageListener.onMessage(o);
                        }catch (Exception e){

                        }
                    }
                }


                @Override
                public void onClose(int i, String s, boolean remote) {
                    Log.i("MessageWebSocketClient","onClose");
                    openStatus = false;
                    reConnect();

                    for (ConnectStatusListener connectStatusListener : connectStatusListeners){
                        try {
                            connectStatusListener.onDisconnect();
                        }catch (Exception e){

                        }
                    }
                }


                @Override
                public void onError(Exception e) {
                    Log.i("MessageWebSocketClient","onError");
                }
            };
        }
    }


    //连接
    protected void connect() {
        new Thread(){
            @Override
            public void run() {
                webSocketClient.connect();

            }
        }.start();
    }


    //断线重连机制
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

    public synchronized long sendMsg(final Message message, SendMessageResultListener listener) {
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
                                if (messageEncoder != null){
                                    webSocketClient.send(messageEncoder.encode(message));
                                }else {
                                    webSocketClient.send(message.getMessage());
                                }
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

//    private void sendOnLineMessage(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (openStatus){
//                    Message message = new Message();
//                    message.setOperate(Message.ON_LINE);
//                    message.setFrom(userId);
//                    webSocketClient.send(new Gson().toJson(message));
//                }
//            }
//        }).start();
//    }


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

        /**
         * 发送时间
         * */
        private Date sendTime;

        /**
         * 访问密钥
         * */
        private String accessKey;

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

        public Date getSendTime() {
            return sendTime;
        }

        public void setSendTime(Date sendTime) {
            this.sendTime = sendTime;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }
    }


    private MessageEncoder messageEncoder = null;

    private MessageDecoder messageDecoder = null;

    public static interface MessageEncoder{
        public String encode(Object s);
    }

    public static interface MessageDecoder{
        public Object decode(String s);
    }

    protected void setMessageEncoder(MessageEncoder messageEncoder){
        this.messageEncoder = messageEncoder;
    }

    protected void setMessageDecoder(MessageDecoder messageDecoder){
        this.messageDecoder = messageDecoder;
    }

}
