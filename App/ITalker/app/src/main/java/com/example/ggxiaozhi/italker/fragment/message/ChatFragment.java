package com.example.ggxiaozhi.italker.fragment.message;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.PresenterFragment;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.common.widget.adapter.TextWatcherAdapter;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.face.FaceUtil;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presenter.message.ChatContract;
import com.example.ggxiaozhi.factory.presistance.Account;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.MessageActivity;
import com.example.ggxiaozhi.italker.fragment.panel.PanelFragment;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;
import net.qiujuer.widget.airpanel.AirPanel;
import net.qiujuer.widget.airpanel.Util;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊天窗口的基类
 */
public abstract class ChatFragment<InitModel>
        extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener
        , ChatContract.View<InitModel>, PanelFragment.PanelCallback {

    /**
     * UI
     */
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.edit_content)
    EditText mEditContent;
    @BindView(R.id.btn_face)
    ImageView mViewFace;
    @BindView(R.id.btn_record)
    ImageView mViewRecord;
    @BindView(R.id.btn_submit)
    ImageView mViewSubmit;


    /**
     * Data
     */
    protected Adapter mAdapter;
    protected String receiverId;
    private int isFirst = 0;//解决弹出软键盘问题
    private AirPanel.Boss boss;//面板 解决他出框与软键盘不协调问题
    private PanelFragment mFragPanel;

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        receiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected final int getContentLayoutId() {
        return R.layout.fragment_chat_common;
    }

    //得到子类要替换的布局Id
    @LayoutRes
    protected abstract int getHeaderLayoutId();

    @Override
    protected void initWidget(View root) {
        //拿到占位布局
        //替换顶部布局 一定要在super之前
        //防止控件绑定异常
        ViewStub viewStub = (ViewStub) root.findViewById(R.id.view_stub_header);
        viewStub.setLayoutResource(getHeaderLayoutId());
        viewStub.inflate();
        //在这里绑定界面布局
        super.initWidget(root);

        //初始化面板
        boss = (AirPanel.Boss) root.findViewById(R.id.lay_content);
        boss.setup(new AirPanel.PanelListener() {
            @Override
            public void requestHideSoftKeyboard() {
                //请求隐藏软键盘
                Util.hideKeyboard(mEditContent);
            }
        });
        //初始化更多界面
        mFragPanel = (PanelFragment) getChildFragmentManager().findFragmentById(R.id.frag_panel);
        mFragPanel.setup(this);
        initToolbar();
        initAppbar();
        initEditContent();
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        refreshRecyclerView();
    }

    @Override
    protected void initData() {
        super.initData();
        //开始进行初始化操作
        mPresenter.start();
    }


    /**
     * 初始化 Appbar
     */
    protected void initAppbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    /**
     * 初始化 Toolbar
     */
    protected void initToolbar() {
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override//实现面板的接口 给面板提供输入框
    public EditText getInputEditText() {
        return mEditContent;
    }

    @Override
    public void onSendGallery(String[] paths) {
        //图片回来的回调
    }

    @Override
    public void onRecordDone(File file, long time) {
        //TODO 语音回来的回调
    }

    //设置appBarLayout滑动距离监听 让子类去实现
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    /**
     * 设置发送状态改变图标
     */
    private void initEditContent() {
        mEditContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                mViewSubmit.setActivated(needSendMsg);
            }
        });
    }


    @OnClick(R.id.btn_face)
    void onFaceClick() {
        // 弹出面板
        boss.openPanel();
        mFragPanel.showFace();
    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {
        // 弹出面板
        boss.openPanel();


    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {

        if (mViewSubmit.isActivated()) {
            //发送
            String content = mEditContent.getText().toString();
            mEditContent.setText("");
            mPresenter.pushText(content);
            refreshRecyclerView();
        } else {
            //点击更多
            onMoreClick();
        }
    }

    protected void onMoreClick() {
        // 弹出面板
        boss.openPanel();
        mFragPanel.showMore();
    }

    @Override
    public RecyclerAdapter<Message> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //这里什么也不做 这个方法主要是用来更新占位布局的状态 因为没有占位布局 Recycler是一直显示的 所以什么已不做
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取当前屏幕内容的高度
        getActivity().getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //获取View可见区域的bottom
                Rect rect = new Rect();
                getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                if (bottom != 0 && oldBottom != 0 && bottom - rect.bottom <= 0) {
                    if (isFirst > 3) {//隐藏=2时是第一次进入 等于3是第一次点击
                    }
                } else {
//                    if (isFirst > 1)//显示
//                        onBottomPanelOpened();
                }
//                isFirst++;
            }
        });

        if (boss.isOpen()) {
            Toast.makeText(getContext(), "打开", Toast.LENGTH_SHORT).show();
        }
    }

    private void onBottomPanelOpened() {
        if (mAppBarLayout != null) {
            mAppBarLayout.setExpanded(false, true);
        }
        refreshRecyclerView();
    }

    /**
     * 刷新RecyclerView() 并移动到最新一条数据
     */
    private void refreshRecyclerView() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        }, 200);
    }

    /**
     * 聊天的适配器
     */
    private class Adapter extends RecyclerAdapter<Message> {

        @Override
        protected int getItemViewType(int position, Message message) {
            //这里返回6种布局-->发送文字和表情x2/发送语音x2/发送图片x2  x2是因为左右区分创建相应的布局

            //获取当前发送的类型
            int type = message.getType();
            //判断发送者是我还是对方
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (type) {
                //文本类型
                case Message.TYPE_STR:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
                //图片类型
                case Message.TYPE_PIC:
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;
                //文件类型
                case Message.TYPE_FILE:
                    return isRight ? R.layout.cell_chat_file_right : R.layout.cell_chat_file_left;
                //语音类型
                case Message.TYPE_AUDIO:
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;
                default:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
            }
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                //左右文字2个布局公用一个ViewHolder
                case R.layout.cell_chat_text_right:
                case R.layout.cell_chat_text_left:
                    return new TextViewHolder(root);
                //左右文字2个布局公用一个ViewHolder
                case R.layout.cell_chat_pic_right:
                case R.layout.cell_chat_pic_left:
                    return new PicViewHolder(root); //左右文字2个布局公用一个ViewHolder
                case R.layout.cell_chat_file_right:
                case R.layout.cell_chat_file_left:
                    return new TextViewHolder(root); //左右文字2个布局公用一个ViewHolder
                case R.layout.cell_chat_audio_right:
                case R.layout.cell_chat_audio_left:
                    return new TextViewHolder(root);
                default:
                    //默认情况下 就返回Text类型的Holder 进行处理
                    return new TextViewHolder(root);
            }
        }
    }

    /**
     * 6种布局封装3个ViewHolder 每一种状态的2个布局公用一个Holder
     * 首先封装一个共用的父类BaseViewHolder
     */
    class BaseViewHolder extends RecyclerAdapter.ViewHolder<Message> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @Nullable//允许为空 当左侧的时候为空
        @BindView(R.id.loading)
        Loading mLoading;

        BaseViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Message message, int position) {

            User sender = message.getSender();
            //由于sender是懒加载 所以我们需要重新load一次
            sender.load();
            //记载头像
            mPortrait.setup(Glide.with(ChatFragment.this), sender);
            if (mLoading != null) {//表示当前布局在右边

                int status = message.getStatus();
                if (status == Message.STATUS_DONE) {//正常状态下(指发送成功)
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) {//正在发送中的状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    //设置mLoading颜色
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {//发送失败状态 允许重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                }
                mPortrait.setEnabled(status == Message.STATUS_FAILED);
            }
        }

        /**
         * 发送失败 重新发送消息
         */
        @OnClick(R.id.im_portrait)
        void onRePushClick() {
            if (mLoading != null && mPresenter.rePush(mData)) {
                //只有在右侧的情况下 才允许重新发送
                // 状态改变需要重新刷新界面
                updataData(mData);
            }
        }
    }

    /**
     * 文本类型的ViewHolder
     */
    class TextViewHolder extends BaseViewHolder {

        @BindView(R.id.txt_content)
        TextView mContent;

        TextViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Message message, int postion) {
            super.onBind(message, postion);
            Spannable spannable = new SpannableString(message.getContent());
            //解析表情
            FaceUtil.decode(mContent, spannable, (int) Ui.dipToPx(getResources(), 20));
            //把内容设置到文字上去
            mContent.setText(spannable);
        }
    }

    /**
     * 图片类型的ViewHolder
     */
    class PicViewHolder extends BaseViewHolder {

        @BindView(R.id.im_image)
        ImageView mContent;

        public PicViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Message message, int postion) {
            super.onBind(message, postion);
            //当发送图片的时候 内容就是图片的地址
            String content = message.getContent();
            Glide.with(ChatFragment.this)
                    .load(content)
                    .fitCenter()
                    .into(mContent);
        }
    }

    /**
     * 文件类型的ViewHolder
     */
    class FileViewHolder extends BaseViewHolder {


        public FileViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Message message, int postion) {
            super.onBind(message, postion);
            //TODO
        }
    }

    /**
     * 语音类型的ViewHolder
     */
    class AudioViewHolder extends BaseViewHolder {

        public AudioViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Message message, int postion) {
            super.onBind(message, postion);
            //TODO
        }
    }
}
