package com.example.ggxiaozhi.common.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.app
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/4
 * 功能   ：所有Activity的父类
 */

public abstract class Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在界面未初始化之前调用初始化窗口
        initWindow();
        if (initArgs(getIntent().getExtras())) {
            //等到界面Id并设置到Activity中
            int layoutId = getContentLayoutId();
            setContentView(layoutId);
            initWidget();
            initData();
        } else {
            finish();
        }
    }

    /**
     * 初始化参数
     *
     * @param bundle 传入的参数bundle
     * @return 返回参数正确true 错误false
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 初始化窗口
     */
    protected void initWindow() {

    }

    /**
     * 得到哦当前资源文件ID
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        //当点击界面导航返回时，Finish当前页面
        finish();
        Toast.makeText(this, "点击了 onSupportNavigateUp", Toast.LENGTH_SHORT).show();
        return super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "点击了 onBackPressed", Toast.LENGTH_SHORT).show();
        //得到当前Activity下的所有Fragment
        List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
        //判断是否为我们能够处理的Fragment
        for (android.support.v4.app.Fragment fragment : fragments) {
            if (fragment instanceof Fragment) {
                //判断Fragment是否拦截了返回按钮
                if (((Fragment) fragment).onBackPressed()) {
                    //如果有就直接return
                    return;
                }
            }
        }
        super.onBackPressed();
        finish();
    }
}
