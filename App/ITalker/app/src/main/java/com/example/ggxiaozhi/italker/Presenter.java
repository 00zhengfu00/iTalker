package com.example.ggxiaozhi.italker;

import android.text.TextUtils;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.italker
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/4
 * 功能   ：
 */

public class Presenter implements IPresenter {

    private IView mIView;

    public Presenter(IView view) {
        this.mIView = view;
    }

    @Override
    public void search() {
        String inputString = mIView.getInputString();
        if (TextUtils.isEmpty(inputString)) {
            return;
        }

        int hashCode = inputString.hashCode();

        IUserService userService = new UserService();
        String searchResult = userService.search(hashCode);
        String result = "Result:" + inputString + "-" + searchResult;
        mIView.setResultString(result);
    }
}
