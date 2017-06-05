//package me.hupeng.websocketapp;
//
//import android.util.Log;
//import okhttp3.*;
//import okhttp3.ws.*;
//import okhttp3.ws.WebSocket;
//import okhttp3.ws.WebSocketListener;
//import okio.Buffer;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by HUPENG on 2017/6/4.
// */
//public class WebSocketClient {
//    String url="ws://192.168.3.228:8080/websocket"; //改成自已服务端的地址
//    Request request = new Request.Builder().url(url).build();
//    final private static OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
//            .readTimeout(2000, TimeUnit.SECONDS)//设置读取超时时间
//            .writeTimeout(2000, TimeUnit.SECONDS)//设置写的超时时间
//            .connectTimeout(2000, TimeUnit.SECONDS)//设置连接超时时间
//            .build();
//    WebSocketCall webSocketCall = WebSocketCall.create(mOkHttpClient, request);
//
//
//    private List<WebSocketListener>webSocketListeners = new ArrayList<>();
//    private static WebSocketClient instance = null;
//    private WebSocket webSocket = null;
//
//    private WebSocketClient(){
//
//    }
//
//    public static WebSocketClient getInstance(){
//        if (instance == null){
//            instance = new WebSocketClient();
//            instance.initialize();
//        }
//        return instance;
//    }
//
//    private void initialize(){
//        webSocketCall.enqueue(new WebSocketListener() {
//            @Override
//            public void onOpen(okhttp3.ws.WebSocket webSocket, Response response) {
//                Log.i("web_socket","onOpen");
//                WebSocketClient.this.webSocket = webSocket;
//                for (WebSocketListener webSocketListener : webSocketListeners){
//                    try {
//                        webSocketListener.onOpen(webSocket, response);
//                    }catch (Exception e){
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(IOException e, Response response) {
//                Log.i("web_socket","onFailure");
//                reConnectServer();
//                for (WebSocketListener webSocketListener : webSocketListeners){
//                    try {
//                        webSocketListener.onFailure(e, response);
//                    }catch (Exception e1){
//
//                    }
//                }
//            }
//
//            @Override
//            public void onMessage(ResponseBody responseBody) throws IOException {
//                Log.i("web_socket","onMessage");
//                for (WebSocketListener webSocketListener : webSocketListeners){
//                    try {
//                        webSocketListener.onMessage(responseBody);
//                    }catch (Exception e){
//
//                    }
//                }
////                System.out.println(responseBody.string());
//                System.out.println("WebSocketClient.onMessage");
//                responseBody.close();
//            }
//
//            @Override
//            public void onPong(Buffer buffer) {
//                Log.i("web_socket","onPong");
//                for (WebSocketListener webSocketListener : webSocketListeners){
//                    try {
//                        webSocketListener.onPong(buffer);
//                    }catch (Exception e){
//
//                    }
//                }
//            }
//
//            @Override
//            public void onClose(int i, String s) {
//                Log.i("web_socket","onClose");
//                reConnectServer();
//                for (WebSocketListener webSocketListener : webSocketListeners){
//                    try {
//                        webSocketListener.onClose(i, s);
//                    }catch (Exception e){
//
//                    }
//                }
//            }
//        });
//
//    }
//
//    public void addWebSocketListener(WebSocketListener webSocketListener){
//        try {
//            webSocketListeners.add(webSocketListener);
//        }catch (Exception e){
//
//        }
//    }
//
//    public void removeWebSocketListener(WebSocketListener webSocketListener){
//        try {
//            webSocketListeners.remove(webSocketListener);
//        }catch (Exception e){
//
//        }
//    }
//
//    /**
//     * 断线重连?嗯
//     * */
//    private synchronized void reConnectServer(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (WebSocketClient.this.webSocket != null){
//                    try {
//                        webSocket.close(0,"");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                WebSocketClient.this.webSocket = null;
//                while (WebSocketClient.this.webSocket == null){
//                    try {
//                        webSocketCall.cancel();
//                        webSocketCall = WebSocketCall.create(mOkHttpClient, request);
//                        WebSocketClient.this.webSocket = null;
//                        initialize();
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
//
//    public boolean sendMessage(final RequestBody requestBody){
//        try {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        webSocket.sendMessage(requestBody);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            return true;
//        }catch (Exception e){
//            return false;
//        }
//    }
//}
