package com.example.ggxiaozhi.factory.presenter.message;

import com.example.ggxiaozhi.factory.model.db.Session;
import com.example.ggxiaozhi.factory.presenter.BaseContract;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：最近会话的基本契约
 */

public interface SessionContract {


    //什么都不需要做 只需要调用基类的start()方法开始显示Loading
    interface Presenter extends BaseContract.Presenter {

    }

    //什么都不用做 基本的操作已经由基类完成了 耗时的操作我将在Presenter的start()方法中完成
    interface View extends BaseContract.RecyclerView<Presenter, Session> {

    }
}
