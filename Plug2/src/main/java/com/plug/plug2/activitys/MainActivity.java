package com.plug.plug2.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.woodys.supercross.SuperCross;
import com.woodys.supercross.event.Event;
import com.woodys.supercross.event.SimpleEventCallback;
import com.woodys.supercross.utils.ProcessUtils;
import com.plug.common.event.EventConstants;
import com.plug.plug2.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_b_activity_main);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：MainActivity\n当前组件：Plug2\n当前进程：" + ProcessUtils.getProcessName(this));

        addViewClickEvent(R.id.btn_subscribe, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //订阅事件
                SuperCross.subscribe(EventConstants.EVENT_A, new SimpleEventCallback() {
                    @Override
                    public void onEvent(Bundle eventBundle) {
                        String content = eventBundle.getString("content");
                        Log.e("TAG", "onEventCallBack: " + content + " " + (Looper.myLooper() == Looper.getMainLooper()));
                        String process = eventBundle.getString("process");
                        Toast.makeText(MainActivity.this, "收到" + process + "发出的" + content,Toast.LENGTH_LONG).show();
                    }
                });

                Toast.makeText(MainActivity.this, "订阅事件A",Toast.LENGTH_SHORT).show();
            }
        });
        addViewClickEvent(R.id.btn_unsubscribe, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuperCross.unsubscribe(EventConstants.EVENT_A);
                Toast.makeText(MainActivity.this, "退订事件A",Toast.LENGTH_SHORT).show();
            }
        });
        addViewClickEvent(R.id.btn_publish, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("content", "事件A");
                bundle.putString("process", ProcessUtils.getProcessName(MainActivity.this));
                SuperCross.publish(new Event(EventConstants.EVENT_A,bundle));
            }
        });
        addViewClickEvent(R.id.btn_jump_publish, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClassName("com.plug.mainproject","com.plug.plug1.activitys.SecondActivity");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
