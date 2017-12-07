package com.example.ggxiaozhi.common.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;

import com.example.ggxiaozhi.common.R;
import com.example.ggxiaozhi.common.widget.CustomDialog;
import com.example.ggxiaozhi.factory.presenter.BaseContract;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.app
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：Activity基类的封装 显示一些公用的Presenter方法的实现
 */

public abstract class PresenterToolBarActivity<Presenter extends BaseContract.Presenter> extends ToolBarActivity
        implements BaseContract.View<Presenter> {

    protected Presenter mPresenter;

    protected ProgressDialog mDialog;

    @Override
    protected void initBefore() {
        super.initBefore();
        initPresenter();
    }

    protected abstract Presenter initPresenter();

    @Override
    public void showError(@StringRes int str) {
        hideDialog();
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(str);
        } else {
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        } else {
            ProgressDialog dialog = mDialog;
            if (dialog == null) {
                dialog = new CustomDialog(this,R.style.AppTheme_Dialog_Alert_Light);
                //点击触摸不可取消
                dialog.setCanceledOnTouchOutside(false);
                //强制取消 关闭界面
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                mDialog = dialog;
            }
            mDialog.setMessage(getText(R.string.prompt_loading));
            mDialog.show();
        }
    }

    protected void hideDialog() {
        //隐藏
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            dialog.dismiss();
        }
    }

    /**
     * 隐藏占位布局
     */
    protected void hideLoad() {
        hideDialog();
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }
}