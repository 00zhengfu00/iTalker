package com.example.ggxiaozhi.italker.fragment.main;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.PresenterFragment;
import com.example.ggxiaozhi.common.widget.EmptyView;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.face.FaceUtil;
import com.example.ggxiaozhi.factory.data.helper.MessageHelper;
import com.example.ggxiaozhi.factory.model.db.Session;
import com.example.ggxiaozhi.factory.presenter.message.SessionContract;
import com.example.ggxiaozhi.factory.presenter.message.SessionPresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.MessageActivity;
import com.example.ggxiaozhi.utils.DateTimeUtils;

import net.qiujuer.genius.ui.Ui;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

/**
 * 主界面Fragment
 */
public class ActiveFragment extends PresenterFragment<SessionContract.Presenter>
        implements SessionContract.View {


    /**
     * UI
     */
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.empty)
    EmptyView mEmptyView;
    private RecyclerAdapter<Session> mAdapter;
    private boolean isFrist = false;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecyclerAdapter<Session>() {
            @Override
            protected int getItemViewType(int position, Session session) {
                return R.layout.cell_chat_list;
            }

            @Override
            protected ViewHolder<Session> onCreateViewHolder(View root, int viewType) {
                return new ActiveFragment.ViewHolder(root);
            }
        };
        mRecycler.setAdapter(mAdapter);

        //初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);

        //监听事件
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListenerImpl<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                super.onItemClick(holder, session);
                //跳转聊天页面
                MessageActivity.show(getContext(), session);
            }
        });
    }

    @Override
    protected void initFirstData() {
        super.initFirstData();
        //MessageHelper.findMessages();
        //查询数据
        mPresenter.start();
        isFrist = true;
    }

    @Override
    protected SessionContract.Presenter initPresenter() {
        return new SessionPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFrist)
            mPresenter.start();
        isFrist = false;
    }

    @Override
    public RecyclerAdapter<Session> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<Session> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_content)
        TextView mContent;
        @BindView(R.id.txt_time)
        TextView mTime;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Session session, int position) {
            mPortraitView.setup(Glide.with(ActiveFragment.this), session.getPicture());
            mName.setText(session.getTitle());
            String str = TextUtils.isEmpty(session.getContent()) ? "" : session.getContent();
            Spannable spannable = new SpannableString(str);
            //解析表情
            FaceUtil.decode(mContent, spannable, (int) Ui.dipToPx(getResources(), 20));
            //把内容设置到文字上去
            mContent.setText(spannable);
            Calendar oldCalendar = Calendar.getInstance();
            Calendar newCalendar = Calendar.getInstance();
            Date currentDate = new Date(System.currentTimeMillis());
            Date modifyAt = session.getModifyAt();
            newCalendar.setTime(currentDate);
            oldCalendar.setTime(modifyAt);
            if (((currentDate.getTime() - modifyAt.getTime()) / (60 * 1000)) < 1) {
                mTime.setText("刚刚");
            } else if (newCalendar.get(Calendar.DAY_OF_YEAR) - oldCalendar.get(Calendar.DAY_OF_YEAR) < 1) {
                mTime.setText(DateTimeUtils.getSimpleDateHour(modifyAt));
            } else {
                mTime.setText(DateTimeUtils.getSimpleDate(modifyAt));
            }
            if (newCalendar.get(Calendar.DAY_OF_YEAR) - oldCalendar.get(Calendar.DAY_OF_YEAR) == 1) {
                Log.d("TAG", "onBind: newCalendar.get(Calendar.DAY_OF_YEAR):" + newCalendar.get(Calendar.DAY_OF_YEAR)
                        + "       oldCalendar.get(Calendar.DAY_OF_YEAR):" + oldCalendar.get(Calendar.DAY_OF_YEAR));
                mTime.setText("昨天");
            }
            if (newCalendar.get(Calendar.DAY_OF_YEAR) - oldCalendar.get(Calendar.DAY_OF_YEAR) == 2) {
                mTime.setText("3天前");
            }
            if (newCalendar.get(Calendar.DAY_OF_YEAR) - oldCalendar.get(Calendar.DAY_OF_YEAR) > 4)
                mTime.setText(DateTimeUtils.getSimpleDate(modifyAt));
        }
    }

}
