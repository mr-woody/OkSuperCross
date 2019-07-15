package com.plug.plug1.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.okay.supercross.SuperCross;
import com.okay.supercross.utils.ProcessUtils;
import com.plug.common.plugservice.RemoteService;
import com.plug.plug1.R;
import com.plug.plug1.application.PlugRemoteService;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_a_activity_main);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：MainActivity\n当前组件：Plug1\n当前进程：" + ProcessUtils.getProcessName(this));

        addViewClickEvent(R.id.btn_registerRemoteService, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuperCross.registerRemoteService(RemoteService.class, new PlugRemoteService());
            }
        });


        addViewClickEvent(R.id.btn_getRemoteService, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteService remoteService = SuperCross.getRemoteService(RemoteService.class);
                if(null != remoteService){
                    String token = remoteService.login("岳涛","不知道密码");
                    Toast.makeText(MainActivity.this, "组件：plug1 \t 进程：" + ProcessUtils.getProcessName(MainActivity.this) +"返回值："+token,Toast.LENGTH_LONG).show();

                }

            }
        });

        addViewClickEvent(R.id.btn_unregisterRemoteService, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuperCross.unregisterRemoteService(RemoteService.class);
            }
        });


        addViewClickEvent(R.id.btn_multi_process, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClassName("com.plug.mainproject","com.plug.plug2.activitys.SecondActivity");
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
