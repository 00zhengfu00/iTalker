package com.example.ggxiaozhi.factory.presenter.message;

import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.model.db.view.MemberUserModel;
import com.example.ggxiaozhi.factory.presenter.BaseContract;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：聊天基础的契约
 */

public interface ChatContract {

    interface Presenter extends BaseContract.Presenter {
        //推送文本
        void pushText(String content);

        //推送语音
        void pushAudio(String path);

        //推送图片
        void pushImages(String[] paths);

        //推送附件
        void pushAttach(String[] paths);

        //重新推送
        boolean rePush(Message message);
    }

    /**
     * 基础的聊天View
     *
     * @param <InitModel> 聊天时 附带初始化的信息 可以是人 可以是群
     */
    interface View<InitModel> extends BaseContract.RecyclerView<Presenter, Message> {

        void onInit(InitModel model);
    }

    //与人聊天的界面
    interface UserView extends View<User> {

    }

    //与群聊天的界面
    interface GroupView extends View<Group> {

        //是否是管理员 来决定是先显示添加成员图标
        void showAdminOption(boolean isAdmin);

        //初始化群成员信息
        void onInitGroupMembers(List<MemberUserModel> memberUserModels, long memberCount);
    }
}
