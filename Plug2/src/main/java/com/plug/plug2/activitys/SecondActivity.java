package com.plug.plug2.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.woodys.supercross.SuperCross;
import com.woodys.supercross.callback.SimpleServiceCallback;
import com.woodys.supercross.utils.ProcessUtils;
import com.plug.common.plugservice.RemoteService;
import com.plug.plug2.R;


public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_b_activity_second);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：SecondActivity\n当前组件：Plug2\n当前进程：" + ProcessUtils.getProcessName(this));

        findViewById(R.id.bt_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteService service = SuperCross.getRemoteService(RemoteService.class);
                if(null!=service){
                    service.login2("plug2.activitys.SecondActivity 测试", "这个是密码", new SimpleServiceCallback() {
                        @Override
                        public void onSucceed(Bundle result) {
                            String username = result.getString("username");
                            String password = result.getString("password");
                            Toast.makeText(SecondActivity.this, "组件：plug2 \t 进程：" + ProcessUtils.getProcessName(SecondActivity.this) +"成功：username->"+username+",password->"+password,Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onFailed(String reason) {
                            Toast.makeText(SecondActivity.this, "组件：plug2 \t 进程：" + ProcessUtils.getProcessName(SecondActivity.this) +"失败："+ reason,Toast.LENGTH_LONG).show();
                        }
                    });
                }



            }
        });
    }
}
