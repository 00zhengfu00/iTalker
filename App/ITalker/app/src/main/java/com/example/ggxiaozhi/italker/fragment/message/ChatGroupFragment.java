package com.example.ggxiaozhi.italker.fragment.message;


import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.view.MemberUserModel;
import com.example.ggxiaozhi.factory.presenter.message.ChatContract;
import com.example.ggxiaozhi.factory.presenter.message.ChatGroupPresenter;
import com.example.ggxiaozhi.italker.R;

import java.util.List;

/**
 * 群聊天窗口
 */
public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {


    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_group;
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this, receiverId);
    }

    @Override
    public void onInit(Group group) {

    }

    @Override
    public void showAdminOption(boolean isAdmin) {

    }

    @Override
    public void onInitGroupMembers(List<MemberUserModel> memberUserModels, int memberCount) {

    }


}
