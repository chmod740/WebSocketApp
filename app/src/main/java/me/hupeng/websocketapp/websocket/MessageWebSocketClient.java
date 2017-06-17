package me.hupeng.websocketapp.websocket;

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


    private boolean openStatus = false;

    protected MessageWebSocketClient(){
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
    private String address = "ws://183.175.12.160:8899/chat";


    protected void initSocketClient() throws URISyntaxException {
        if(webSocketClient == null) {
            webSocketClient = new WebSocketClient(new URI(address)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.i("MessageWebSocketClient","onOpen");
                    openStatus = true;
                    for (final ConnectStatusListener connectStatusListener : connectStatusListeners){
                        try {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            connectStatusListener.onConnect();
                                        }
                                    });

                                }
                            }).start();

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
                    for (final MessageListener messageListener : messageListeners){
                        try {
                            final Object finalO = o;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            messageListener.onMessage(finalO);
                                        }
                                    });

                                }
                            }).start();
                        }catch (Exception e){

                        }
                    }
                }

                @Override
                public void onClose(int i, String s, boolean remote) {
                    Log.i("MessageWebSocketClient","onClose");
                    openStatus = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    reConnect();

                                }
                            });

                        }
                    }).start();

                    for (final ConnectStatusListener connectStatusListener : connectStatusListeners){
                        try {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            connectStatusListener.onDisconnect();
                                        }
                                    });

                                }
                            }).start();
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


    protected void connect() {
        new Thread(){
            @Override
            public void run() {
                webSocketClient.connect();

            }
        }.start();
    }


    public void reConnect() {
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
                e.printStackTrace();
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

    public synchronized long sendMessage(final Object message, final SendMessageResultListener listener) {
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
                        boolean sendResult = false;
                        if (openStatus){
                            try {
                                if (messageEncoder != null){
                                    webSocketClient.send(messageEncoder.encode(message));
                                }else {
                                    webSocketClient.send(message.toString());
                                }
                                sendResult = true;
                            }catch (Exception e){
                                e.printStackTrace();
                                sendResult = false;
                            }
                        }else {
                            sendResult = false;
                        }
                        if (sendMessageResultListener != null){
                            if (sendResult){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                sendMessageResultListener.onSuccess(ts);
                                            }
                                        });

                                    }
                                }).start();
                            }else{
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                sendMessageResultListener.onFail(ts);
                                            }
                                        });

                                    }
                                }).start();
                            }
                        }
                                         }
                });
            }
        }).start();
        return ts;
    }

    private MessageEncoder messageEncoder = null;

    private MessageDecoder messageDecoder = null;

    public static interface MessageEncoder{
        public String encode(Object s);
    }

    public static interface MessageDecoder{
        public Object decode(String s);
    }

    public void setMessageEncoder(MessageEncoder messageEncoder){
        this.messageEncoder = messageEncoder;
    }

    public void setMessageDecoder(MessageDecoder messageDecoder){
        this.messageDecoder = messageDecoder;
    }

}
