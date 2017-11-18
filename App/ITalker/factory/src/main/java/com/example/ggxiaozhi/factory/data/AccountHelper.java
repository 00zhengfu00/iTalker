package com.example.ggxiaozhi.factory.data;

import com.example.ggxiaozhi.factory.R;
import com.example.ggxiaozhi.factory.model.api.RegisterModel;
import com.example.ggxiaozhi.factory.model.db.User;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：
 */

public class AccountHelper {

    /**
     * 注册的接口 异步的调用
     *
     * @param model    传递一个注册的model进来
     * @param callback 成功与失败返回的接口回调
     */
    public static void Register(RegisterModel model, final DataSource.Callback<User> callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                callback.onDataAvailable(R.string.data_rsp_error_parameters);
            }

        }).start();
    }
}
