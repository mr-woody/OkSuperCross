package com.plug.plug1.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.okay.supercross.SuperCross;
import com.okay.supercross.event.Event;
import com.okay.supercross.utils.ProcessUtils;
import com.plug.common.event.EventConstants;
import com.plug.plug1.R;


public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_a_activity_second);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：SecondActivity\n当前组件：plug1\n当前进程：" + ProcessUtils.getProcessName(this));

        addViewClickEvent(R.id.bt_event_a, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("content", "事件A");
                bundle.putString("process", ProcessUtils.getProcessName(SecondActivity.this));
                SuperCross.publish(new Event(EventConstants.EVENT_A,bundle));
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
