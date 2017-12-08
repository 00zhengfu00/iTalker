package com.example.ggxiaozhi.italker.fragment.member;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.factory.presenter.group.GroupCreateContract;
import com.example.ggxiaozhi.factory.presenter.group.GroupMemberAddContract;
import com.example.ggxiaozhi.factory.presenter.group.GroupMemberAddPresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.fragment.media.GalleryFragment;

import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * 群成员添加的Fragment
 */
public class GroupMemberAddFragment extends BottomSheetDialogFragment implements GroupMemberAddContract.View {

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Adapter mAdapter;
    private GroupMemberAddContract.Presenter mPresenter;
    private Callback mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // context 就是咱们的Activity
        mCallback = (Callback) context;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 返回一个我们复写的
        return new GalleryFragment.TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 初始化
        initPresenter();

        // 获取我们的GalleryView
        View root = inflater.inflate(R.layout.fragment_group_member_add, container, false);
        // 控件绑定
        ButterKnife.bind(this, root);
        initRecycler();
        initToolbar();
        return root;
    }

    private void initRecycler() {
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new Adapter());
    }

    private void initToolbar() {
        mToolbar.inflateMenu(R.menu.group_create);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_create) {
                    if (mPresenter != null)
                        mPresenter.submit();
                    return true;
                }
                return false;
            }
        });

        //给Icon重新设置颜色
        //获取item
        MenuItem item = mToolbar.getMenu().findItem(R.id.action_create);
        //获取item的icon
        Drawable drawable = item.getIcon();
        //将icon包装成drawable
        drawable = DrawableCompat.wrap(drawable);
        //设置颜色
        DrawableCompat.setTint(drawable, UiCompat.getColor(getResources(), R.color.textPrimary));
        //重新设置
        item.setIcon(drawable);
    }


    private void initPresenter() {
        new GroupMemberAddPresenter(this);
    }

    @Override
    public void showError(int str) {
        if (mCallback != null)
            mCallback.showError(str);
    }

    @Override
    public void showLoading() {
        if (mCallback != null)
            mCallback.showLoading();
    }

    @Override
    public void setPresenter(GroupMemberAddContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onAddedSucceed() {
        if (mCallback != null) {
            mCallback.hideLoading();
            mCallback.refreshMembers();
        }
        dismiss();
    }

    @Override
    public String getGroupId() {
        return mCallback.getGroupId();
    }

    @Override
    public RecyclerAdapter<GroupCreateContract.ViewModel> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        if (mCallback != null)
            mCallback.hideLoading();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detach();
    }


    private class Adapter extends RecyclerAdapter<GroupCreateContract.ViewModel> {

        @Override
        protected int getItemViewType(int position, GroupCreateContract.ViewModel viewModel) {
            return R.layout.cell_group_create_contact;
        }

        @Override
        protected ViewHolder<GroupCreateContract.ViewModel> onCreateViewHolder(View root, int viewType) {
            return new GroupMemberAddFragment.ViewHolder(root);
        }
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCreateContract.ViewModel> {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.cb_select)
        CheckBox mSelect;


        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(GroupCreateContract.ViewModel viewModel, int position) {
            mPortrait.setup(Glide.with(GroupMemberAddFragment.this), viewModel.mAuthor);
            mName.setText(viewModel.mAuthor.getName());
            mSelect.setChecked(viewModel.isSelected);
        }

        @OnCheckedChanged(R.id.cb_select)
        void onCheckedChanged(boolean checked) {
            // 进行状态更改
            mPresenter.changeSelect(mData, checked);
        }


    }

    // Fragment 与 Activity 之间的交互接口
    public interface Callback {
        String getGroupId();

        void hideLoading();

        // 公共的：显示一个字符串错误
        void showError(@StringRes int str);

        // 公共的：显示进度条
        void showLoading();

        // 刷新成员
        void refreshMembers();
    }

}
