package com.example.ggxiaozhi.factory.presenter.group;


import com.example.ggxiaozhi.factory.model.Author;
import com.example.ggxiaozhi.factory.presenter.BaseContract;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：创建群的契约
 */

public interface GroupCreateContract {

    interface Presenter extends BaseContract.Presenter {
        //请求创建群的方法
        void create(String name, String portrait, String desc);

        //改变选中状态的方法
        void changeSelect(ViewModel model, boolean isSelected);
    }

    interface View extends BaseContract.RecyclerView<Presenter, ViewModel> {
        void onCreateSuccess();
    }

    class ViewModel {
        //用户
        public Author mAuthor;
        //是否选中
        public boolean isSelected;
    }
}
