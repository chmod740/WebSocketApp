package me.hupeng.websocketapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import okhttp3.RequestBody;
import okhttp3.ws.WebSocket;


public class MainActivity extends AppCompatActivity {
    private WebSocketClient webSocketClient = WebSocketClient.getInstance();
    private Button button = null;
    private EditText editText = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.editText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editText.getText().toString();
                RequestBody requestBody = RequestBody.create(WebSocket.TEXT,s);
                webSocketClient.sendMessage(requestBody);
            }
        });
    }
}
