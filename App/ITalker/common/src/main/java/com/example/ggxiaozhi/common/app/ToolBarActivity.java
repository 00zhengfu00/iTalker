package com.example.ggxiaozhi.common.app;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.example.ggxiaozhi.common.R;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.app
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：专门初始化ToolBar的Activity基类
 */

public abstract class ToolBarActivity extends Activity {
    protected Toolbar mToolbar;

    @Override
    protected void initWidget() {
        super.initWidget();
        initToolBar((Toolbar) findViewById(R.id.toolbar));
    }

    public void initToolBar(Toolbar toolbar) {
        this.mToolbar = toolbar;
        if (mToolbar != null) {
            //初始化Toolbar
            setSupportActionBar(toolbar);
        }
        //设置返回键
        initTitleNeedBack();
    }

    private void initTitleNeedBack() {
        //设置左上角返回键的显示与实际返回效果
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
