package com.example.ggxiaozhi.factory.presenter.contact;

import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presenter.BaseContract;


/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.contract
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：联系人列表的基本契约
 */

public interface ContactContract {

    //什么都不需要做 只需要调用基类的start()方法开始显示Loading
    interface Presenter extends BaseContract.Presenter {

    }

    //什么都不用做 基本的操作已经由基类完成了 耗时的操作我将在Presenter的start()方法中完成
    interface View extends BaseContract.RecyclerView<Presenter, User> {

    }
}
