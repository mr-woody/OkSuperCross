package com.plug.mainproject.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.okay.supercross.utils.ProcessUtils;
import com.plug.mainproject.R;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前组件：app\n当前进程：" + ProcessUtils.getProcessName(this));

        addViewClickEvent(R.id.btn_jump_ar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(DemoActivity.this,com.plug.plug1.activitys.MainActivity.class);
                startActivity(intent2);
            }
        });
        addViewClickEvent(R.id.btn_jump_eb, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(DemoActivity.this,com.plug.plug2.activitys.MainActivity.class);
                startActivity(intent2);
            }
        });
    }

    protected void addViewClickEvent(int id, View.OnClickListener mOnClickListener) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(mOnClickListener);
        }
    }
}
