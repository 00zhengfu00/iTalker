package com.example.ggxiaozhi.common.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.ggxiaozhi.common.R;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.widget
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：一个简单的自定义Dialog
 */

public class CustomDialog extends ProgressDialog {
    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(Context context) {

        setContentView(R.layout.load_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
    }
}