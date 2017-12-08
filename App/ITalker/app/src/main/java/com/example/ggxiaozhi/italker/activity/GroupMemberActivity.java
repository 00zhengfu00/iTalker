package com.example.ggxiaozhi.italker.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.PresenterToolBarActivity;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.factory.model.db.view.MemberUserModel;
import com.example.ggxiaozhi.factory.presenter.group.GroupMembersContract;
import com.example.ggxiaozhi.factory.presenter.group.GroupMembersPresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.fragment.member.GroupMemberAddFragment;
import com.raizlabs.android.dbflow.annotation.Column;

import butterknife.BindView;
import butterknife.OnClick;

public class GroupMemberActivity extends PresenterToolBarActivity<GroupMembersContract.Presenter>
        implements GroupMembersContract.View, GroupMemberAddFragment.Callback {

    /**
     * UI
     */
    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    /**
     * Data
     */
    private static final String KEY_GROUP_ID = "KEY_GROUP_ID";
    private static final String KEY_IS_ADMIN = "KEY_IS_ADMIN";
    private String groupId;

    private boolean isAdmin;
    private RecyclerAdapter<MemberUserModel> mAdapter;

    public static void show(Context context, String groupId) {
        show(context, groupId, false);

    }

    public static void showAdmin(Context context, String groupId) {
        show(context, groupId, true);

    }

    private static void show(Context context, String groupId, boolean isAdmin) {
        if (TextUtils.isEmpty(groupId))
            return;
        Intent intent = new Intent(context, GroupMemberActivity.class);
        intent.putExtra(KEY_GROUP_ID, groupId);
        intent.putExtra(KEY_IS_ADMIN, isAdmin);
        context.startActivity(intent);

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_member;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        groupId = bundle.getString(KEY_GROUP_ID);
        isAdmin = bundle.getBoolean(KEY_IS_ADMIN);
        return !TextUtils.isEmpty(groupId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle(R.string.title_member_list);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter<MemberUserModel>() {
            @Override
            protected int getItemViewType(int position, MemberUserModel model) {
                return R.layout.cell_group_create_contact;
            }

            @Override
            protected ViewHolder<MemberUserModel> onCreateViewHolder(View root, int viewType) {
                return new GroupMemberActivity.ViewHolder(root);
            }
        };
        mRecycler.setAdapter(mAdapter);

    }

    @Override
    protected void initData() {
        super.initData();
        //开始刷新
        mPresenter.refresh();
        // 显示管理员界面，添加成员
        if (isAdmin) {
            mToolbar.inflateMenu(R.menu.chat_add);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    new GroupMemberAddFragment().show(getSupportFragmentManager(), GroupMemberAddFragment.class.getName());
                    return true;
                }
            });
        }
    }

    @Override
    protected GroupMembersContract.Presenter initPresenter() {
        return new GroupMembersPresenter(this);
    }

    @Override
    public RecyclerAdapter<MemberUserModel> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        hideLoad();
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public void hideLoading() {
        super.hideLoad();
    }

    @Override
    public void refreshMembers() {
        // 重新加载成员信息
        if (mPresenter != null)
            mPresenter.refresh();
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<MemberUserModel> {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;


        ViewHolder(View itemView) {
            super(itemView);
            //隐藏 选中图标
            itemView.findViewById(R.id.cb_select).setVisibility(View.GONE);
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            PersonalActivity.show(GroupMemberActivity.this, mData.getUserId());
        }

        @Override
        public void onBind(MemberUserModel model, int position) {
            mPortrait.setup(Glide.with(GroupMemberActivity.this), model.getPortrait());
            mName.setText(model.getName());
        }
    }
}
