package com.example.ggxiaozhi.italker.activity;


import android.content.Context;
import android.content.Intent;

import com.example.ggxiaozhi.common.app.Activity;
import com.example.ggxiaozhi.factory.model.Author;
import com.example.ggxiaozhi.italker.R;

/**
 * 聊天页面
 */
public class MessageActivity extends Activity {


    /**
     * 消息显示页面的入口
     *
     * @param context 源Activity
     * @param author  目标 要聊天的人
     */
    public static void show(Context context, Author author) {
        context.startActivity(new Intent(context, MessageActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }
}
