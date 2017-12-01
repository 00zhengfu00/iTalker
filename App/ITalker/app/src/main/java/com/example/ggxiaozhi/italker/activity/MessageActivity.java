package com.example.ggxiaozhi.italker.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.example.ggxiaozhi.common.app.Activity;
import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.factory.model.Author;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.fragment.message.ChatGroupFragment;
import com.example.ggxiaozhi.italker.fragment.message.ChatUserFragment;

/**
 * 聊天页面
 */
public class MessageActivity extends Activity {
    /**
     * Data
     */
    //传递参数的Key
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    public static final String KEY_RECEVIER_IS_GROUP = "KEY_RECEVIER_IS_GROUP";

    private String receiverId;
    private boolean isGroup;

    /**
     * 与人聊天显示页面的入口
     *
     * @param context 源Activity
     * @param author  目标 要聊天的人
     */
    public static void show(Context context, Author author) {
        if (author == null || context == null || TextUtils.isEmpty(author.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, author.getId());
        intent.putExtra(KEY_RECEVIER_IS_GROUP, false);
        context.startActivity(intent);
    }

    /**
     * 与群聊天显示页面的入口
     *
     * @param context 源Activity
     * @param group   目标 要聊天的群
     */
    public static void show(Context context, Group group) {
        if (group == null || context == null || TextUtils.isEmpty(group.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, group.getId());
        intent.putExtra(KEY_RECEVIER_IS_GROUP, true);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        receiverId = bundle.getString(KEY_RECEIVER_ID);
        isGroup = bundle.getBoolean(KEY_RECEVIER_IS_GROUP);
        return !TextUtils.isEmpty(receiverId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        Fragment fragment;
        if (isGroup)
            fragment = new ChatGroupFragment();
        else
            fragment = new ChatUserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECEIVER_ID, receiverId);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.lay_container, fragment).commit();
    }
}
