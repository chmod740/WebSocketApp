package me.hupeng.websocketapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.hupeng.websocketapp.Configuration;
import me.hupeng.websocketapp.R;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    @ViewInject(R.id.et_username)
    private EditText etUsername;
    @ViewInject(R.id.et_password)
    private EditText etPassword;
    @ViewInject(R.id.btn_login)
    private Button btnLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Event(value = R.id.btn_login,type = View.OnClickListener.class)
    private void onLoginBtnClick(View view){
        btnLogin.setEnabled(false);
        /**
         * 这里填写你的服务器所在的IP地址替换我的ip地址
         * */
        try{
            RequestParams params = new RequestParams(Configuration.BASE_URL + "login");
            params.addQueryStringParameter("username",etUsername.getText().toString());
            params.addQueryStringParameter("password",etPassword.getText().toString());
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(s);
                    int code = jsonObject.get("code").getAsInt();
                    if (code == 0){
                        //执行登录成功操作
                        goHome();
//                        Toast.makeText(LoginActivity.this,jsonObject.get("msg").getAsString(),Toast.LENGTH_SHORT).show();
                    }else {
                        //执行登录失败操作
                        Toast.makeText(LoginActivity.this,jsonObject.get("msg").getAsString(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    Toast.makeText(LoginActivity.this, "网络连接失败:" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {
                    btnLogin.setEnabled(true);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 跳转到主界面
     * */
    private void goHome(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
