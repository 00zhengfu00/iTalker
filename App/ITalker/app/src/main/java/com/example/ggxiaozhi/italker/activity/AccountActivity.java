package com.example.ggxiaozhi.italker.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.ggxiaozhi.common.app.Activity;
import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.fragment.account.UpdateInfoFragment;

import butterknife.BindView;

/**
 * 账户登录Activity
 */
public class AccountActivity extends Activity {

    Fragment mFragment;

    /**
     * 当前Activity入口
     *
     * @param context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mFragment = new UpdateInfoFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.lay_container, mFragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFragment.onActivityResult(requestCode, resultCode, data);
    }
}
