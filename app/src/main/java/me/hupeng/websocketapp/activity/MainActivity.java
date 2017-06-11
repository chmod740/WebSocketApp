package me.hupeng.websocketapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import me.hupeng.websocketapp.MessageWebSocketClient;
import me.hupeng.websocketapp.R;
import me.hupeng.websocketapp.bean.User;
import okhttp3.RequestBody;
import okhttp3.ws.WebSocket;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.URISyntaxException;


@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
//    private WebSocketClient webSocketClient = WebSocketClient.getInstance();
    private MessageWebSocketClient messageWebSocketClient;

    @ViewInject(R.id.btn_send)
    private Button btnSend;

    @ViewInject(R.id.et_message)
    private EditText etMessage;

    @ViewInject(R.id.tv_message)
    private TextView tvMessage;

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
        sendMessage(User.getCurrentUser(), 2, toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageWebSocketClient = MessageWebSocketClient.getInstance(User.getCurrentUser().getId());
    }

    private void sendMessage(User from, int to, String msg){
        MessageWebSocketClient.Message message = new MessageWebSocketClient.Message();
        message.setFrom(from.getId());
        message.setTo(to);
        message.setMessage(msg);
        message.setOperate(MessageWebSocketClient.Message.SEND_MESSAGE);
        messageWebSocketClient.sendMsg(new Gson().toJson(message), sendMessageResultListener);
    }
}
