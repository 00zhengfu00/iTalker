package com.example.ggxiaozhi.italker.fragment.message;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presenter.message.ChatContract;
import com.example.ggxiaozhi.factory.presenter.message.ChatUserPresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.PersonalActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户聊天窗口
 */
public class ChatUserFragment extends ChatFragment<User> implements ChatContract.UserView{
    private static final String TAG = "ChatUserFragment";
    /**
     * UI
     */
    private MenuItem menuUserItem;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_user;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        Toolbar toolbar = mToolbar;
        toolbar.inflateMenu(R.menu.chat_user);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onPortraitClick();
                return false;
            }
        });
        //拿到菜单Menu
        menuUserItem = toolbar.getMenu().findItem(R.id.action_person);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        Glide.with(this)
                .load(R.drawable.default_banner_chat)
                .centerCrop()
                .into(new ViewTarget<CollapsingToolbarLayout, GlideDrawable>(mToolbarLayout) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setContentScrim(resource.getCurrent());
                    }
                });
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
        Log.i(TAG, "verticalOffset: " + verticalOffset);
        View view = mPortrait;
        MenuItem menuItem = menuUserItem;
        if (view == null || menuItem == null)
            return;

        if (verticalOffset == 0) {//完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);
            //关闭个人菜单按钮
            menuItem.setVisible(false);
            menuItem.getIcon().setAlpha(0);
        } else {
            int totalScrollRange = appBarLayout.getTotalScrollRange();//滑动的最大距离
            Log.i(TAG, "totalScrollRange: " + totalScrollRange);
            verticalOffset = Math.abs(verticalOffset);//取绝对值

            if (verticalOffset >= totalScrollRange) {//完全关闭
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);

                //显示个人菜单按钮
                menuItem.setVisible(true);
                menuItem.getIcon().setAlpha(255);
            } else {//中间滑动的过程
                float progress = 1 - verticalOffset / (float) totalScrollRange;
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);
                //设置个人菜单按钮变化
                menuItem.setVisible(true);
                //设置和头像相反显示
                menuItem.getIcon().setAlpha((int) (255 - (255 * progress)));

            }
        }
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        PersonalActivity.show(getContext(), receiverId);
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatUserPresenter(this, receiverId);
    }

    /**
     * 初始化聊天对象的信息
     *
     * @param user 对方
     */
    @Override
    public void onInit(User user) {
        //对和你聊天的朋友的信息进行初始化操作
        mPortrait.setup(Glide.with(ChatUserFragment.this), user);
        mToolbarLayout.setTitle(user.getName());
    }
}
