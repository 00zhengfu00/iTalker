package com.example.ggxiaozhi.italker.fragment.message;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.common.widget.adapter.TextWatcherAdapter;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presistance.Account;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.MessageActivity;

import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊天窗口的基类
 */
public abstract class ChatFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    /**
     * UI
     */
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
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

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        receiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initToolbar();
        initAppbar();
        initEditContent();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
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

    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {

    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {

        if (mViewSubmit.isActivated()) {
            //发送
        } else {
            //点击更多
            onMoreClick();
        }
    }

    protected void onMoreClick() {

        //TODO
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
                    return new TextViewHolder(root); //左右文字2个布局公用一个ViewHolder
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

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Message message) {

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
                    mLoading.setProgress(1);
                    mLoading.setVisibility(View.VISIBLE);
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

            if (mLoading != null) {
                //只有在右侧的情况下 才允许重新发送

                //TODO 重新发送
            }
        }
    }

    /**
     * 文本类型的ViewHolder
     */
    class TextViewHolder extends BaseViewHolder {

        @BindView(R.id.txt_content)
        TextView mContent;

        public TextViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Message message) {
            super.onBind(message);
            //把内容设置到文字上去
            mContent.setText(message.getContent());
        }
    }

    /**
     * 图片类型的ViewHolder
     */
    class PicViewHolder extends BaseViewHolder {

        public PicViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Message message) {
            super.onBind(message);
            //TODO
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
        public void onBind(Message message) {
            super.onBind(message);
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
        public void onBind(Message message) {
            super.onBind(message);
            //TODO
        }
    }
}
