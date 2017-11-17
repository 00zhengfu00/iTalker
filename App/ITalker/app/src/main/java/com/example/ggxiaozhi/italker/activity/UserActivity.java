package com.example.ggxiaozhi.italker.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;

import com.example.ggxiaozhi.common.app.Activity;
import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.fragment.user.UpdateInfoFragment;

public class UserActivity extends Activity {
    private Fragment mFragment;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
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
