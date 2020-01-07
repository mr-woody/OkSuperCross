package com.plug.mainproject.activitys

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.okay.supercross.SuperCross

import com.okay.supercross.utils.ProcessUtils
import com.plug.mainproject.R

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        val tv_info = findViewById<TextView>(R.id.tv_info)
        tv_info.text = "当前组件：app\n当前进程：" + ProcessUtils.getProcessName(this)!!

        addViewClickEvent(R.id.btn_jump_ar, View.OnClickListener {
            val intent2 = Intent(this@DemoActivity, com.plug.plug1.activitys.MainActivity::class.java)
            startActivity(intent2)
        })
        addViewClickEvent(R.id.btn_jump_eb, View.OnClickListener {
            val intent2 = Intent(this@DemoActivity, com.plug.plug2.activitys.MainActivity::class.java)
            startActivity(intent2)
        })
    }

    protected fun addViewClickEvent(id: Int, mOnClickListener: View.OnClickListener) {
        val view = findViewById<View>(id)
        view?.setOnClickListener(mOnClickListener)
    }
}
