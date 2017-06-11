package me.hupeng.websocketapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import org.xutils.x;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }
}
