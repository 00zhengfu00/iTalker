package com.example.ggxiaozhi.italker;


import com.example.ggxiaozhi.common.app.Activity;
import com.example.ggxiaozhi.italker.activity.MainActivity;
import com.example.ggxiaozhi.italker.fragment.assist.PermissionsFragment;

public class LaunchActivity extends Activity {


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionsFragment.haveAllPerms(this, getSupportFragmentManager())) {
            MainActivity.show(this);
            finish();
        }
    }
}
