package com.example.ggxiaozhi.italker.fragment.message;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.view.MemberUserModel;
import com.example.ggxiaozhi.factory.presenter.message.ChatContract;
import com.example.ggxiaozhi.factory.presenter.message.ChatGroupPresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.GroupMemberActivity;
import com.example.ggxiaozhi.italker.activity.PersonalActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 群聊天窗口
 */
public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {

    @BindView(R.id.im_header)
    ImageView im_header;

    @BindView(R.id.lay_members)
    LinearLayout mLayout_members;

    @BindView(R.id.txt_members_more)
    TextView mMoreMembers;

    private List<ImageView> listAddImages = new ArrayList<>();
    private Group mGroup = null;

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        Glide.with(this)
                .load(R.drawable.default_banner_group)
                .centerCrop()
                .into(new ViewTarget<CollapsingToolbarLayout, GlideDrawable>(mToolbarLayout) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setContentScrim(resource.getCurrent());
                    }
                });
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this, receiverId);
    }

    /**
     * 监听AppBarLayout的缩放 展开的监听
     *
     * @param appBarLayout   appBarLayout
     * @param verticalOffset 缩放偏移量
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
        View view = mLayout_members;

        if (view == null)
            return;

        if (verticalOffset == 0) {//完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);

        } else {
            int totalScrollRange = appBarLayout.getTotalScrollRange();//滑动的最大距离
            verticalOffset = Math.abs(verticalOffset);//取绝对值

            if (verticalOffset >= totalScrollRange) {//完全关闭
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);
            } else {//中间滑动的过程
                float progress = 1 - verticalOffset / (float) totalScrollRange;
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);
            }
        }
    }

    @Override
    protected void initData() {
        //重写父类加载群信息的方法 目的是为了实现新增群成员 Toolbar更新数据
        //请求放在onResume中 只需要加载本地数据 消耗不大
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onInit(Group group) {
        if (mGroup != null)
            return;
        mToolbarLayout.setTitle(group.getName());
        Glide.with(this)
                .load(group.getPicture())
                .centerCrop()
                .placeholder(R.drawable.default_banner_group)
                .into(im_header);
        mGroup = group;
    }

    @Override
    public void showAdminOption(final boolean isAdmin) {
        if (mGroup != null)
            return;
        if (isAdmin) {
            mToolbar.inflateMenu(R.menu.chat_add);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_add) {
                        // 成员添加
                        GroupMemberActivity.showAdmin(getContext(), receiverId);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onInitGroupMembers(List<MemberUserModel> memberUserModels, long memberCount) {
        if (memberUserModels == null || memberUserModels.size() == 0)
            return;
        //解决无法刷新的问题
        if (mLayout_members.getChildCount() >= 2) {
            for (ImageView image : listAddImages) {
                mLayout_members.removeView(image);
            }
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (final MemberUserModel model : memberUserModels) {
            //加载布局
            ImageView p = (ImageView) inflater.inflate(R.layout.lay_chat_group_portrait, mLayout_members, false);
            //向缓存集合中加入数据
            listAddImages.add(p);
            //向容器中添加布局
            mLayout_members.addView(p, 0);
            //给布局添加动画
            Glide.with(this).load(model.getPortrait())
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.default_portrait)
                    .into(p);
            //点击跳转个人页面
            p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonalActivity.show(getContext(), model.getUserId());
                }
            });
        }

        //显示更多更多成员按钮
        if (memberCount > 0) {
            mMoreMembers.setText(String.format("+%s", memberCount));
            mMoreMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 显示成员列表  receiverId就是群id
                    GroupMemberActivity.show(getContext(), receiverId);
                }
            });
        } else {
            mMoreMembers.setVisibility(View.GONE);
        }
    }
}
