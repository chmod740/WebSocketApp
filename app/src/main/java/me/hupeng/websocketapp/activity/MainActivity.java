package me.hupeng.websocketapp.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.hupeng.websocketapp.websocket.Message;
import me.hupeng.websocketapp.websocket.MessageWebSocketClient;
import me.hupeng.websocketapp.R;
import me.hupeng.websocketapp.bean.User;

import me.hupeng.websocketapp.websocket.MyMessageWebSocketClient;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Date;


@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    @ViewInject(R.id.btn_send)
    private Button btnSend;

    @ViewInject(R.id.et_message)
    private EditText etMessage;

    @ViewInject(R.id.tv_message)
    private TextView tvMessage;


    private MessageWebSocketClient messageWebSocketClient;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private MessageWebSocketClient.MessageEncoder messageEncoder = new MessageWebSocketClient.MessageEncoder() {
        @Override
        public String encode(Object s) {
            if(s instanceof Message){
                return gson.toJson(s);
            }
            return null;
        }
    };

    private MessageWebSocketClient.MessageDecoder messageDecoder = new MessageWebSocketClient.MessageDecoder() {
        @Override
        public Object decode(String s) {
            try{
                return gson.fromJson(s, Message.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    };

    private MessageWebSocketClient.ConnectStatusListener connectStatusListener = new MessageWebSocketClient.ConnectStatusListener() {
        @Override
        public void onConnect() {
            Message message = new Message();
            message.setFrom(User.getCurrentUser().getId());
            message.setSendTime(new Date(System.currentTimeMillis()));
            message.setOperate(Message.ON_LINE);
            messageWebSocketClient.sendMessage(message, null);
        }

        @Override
        public void onDisconnect() {

        }
    };

    private MessageWebSocketClient.MessageListener messageListener = new MessageWebSocketClient.MessageListener() {
        @Override
        public void onMessage(Object object) {
            Message message = (Message) object;
//            Toast.makeText(MainActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();
            tvMessage.setText(tvMessage.getText().toString() + "\n" + message.getMessage());
        }
    };



    private MessageWebSocketClient.SendMessageResultListener sendMessageResultListener= new MessageWebSocketClient.SendMessageResultListener() {
        @Override
        public void onSuccess(long ts) {
            Toast.makeText(MainActivity.this, "消息发送成功!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFail(long ts) {
            Toast.makeText(MainActivity.this, "消息发送失败!", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 设置发送按钮的监听事件
     * */
    @Event(value = R.id.btn_send, type = View.OnClickListener.class)
    private void onSendButtonCliock(View view){
        Message message = new Message();
        message.setFrom(User.getCurrentUser().getId());
        message.setTo(User.getCurrentUser().getId());
        message.setOperate(Message.SEND_MESSAGE);
        message.setAccessKey("");
        message.setMessage(etMessage.getText().toString());
        message.setTo(1);
        messageWebSocketClient.sendMessage(message, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageWebSocketClient = MyMessageWebSocketClient.getInstance(User.getCurrentUser().getId());
        messageWebSocketClient.setMessageDecoder(messageDecoder);
        messageWebSocketClient.setMessageEncoder(messageEncoder);
        messageWebSocketClient.addConnectStatusListener(connectStatusListener);
        messageWebSocketClient.addMessageListener(messageListener);
    }

}
