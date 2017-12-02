package com.example.ggxiaozhi.italker.fragment.message;



import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.presenter.message.ChatContract;
import com.example.ggxiaozhi.italker.R;

/**
 * 群聊天窗口
 */
public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {


    public ChatGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_group;
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return null;
    }

    @Override
    public void onInit(Group group) {

    }
}
