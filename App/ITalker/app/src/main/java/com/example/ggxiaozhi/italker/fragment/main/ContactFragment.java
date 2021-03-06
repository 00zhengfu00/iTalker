package com.example.ggxiaozhi.italker.fragment.main;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.PresenterFragment;
import com.example.ggxiaozhi.common.widget.EmptyView;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presenter.contact.ContactContract;
import com.example.ggxiaozhi.factory.presenter.contact.ContactPresenter;
import com.example.ggxiaozhi.factory.presenter.group.GroupPresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.MainActivity;
import com.example.ggxiaozhi.italker.activity.MessageActivity;
import com.example.ggxiaozhi.italker.activity.PersonalActivity;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.Ui;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 联系人Fragment
 */
public class ContactFragment extends PresenterFragment<ContactContract.Presenter>
        implements ContactContract.View {
    /**
     * UI
     */
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mRefreshLayout;
    private RecyclerAdapter<User> mAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecyclerAdapter<User>() {
            @Override
            protected int getItemViewType(int position, User user) {
                return R.layout.cell_contact_list;
            }

            @Override
            protected ViewHolder<User> onCreateViewHolder(View root, int viewType) {
                return new ContactFragment.ViewHolder(root);
            }
        };
        mRecycler.setAdapter(mAdapter);
        //滑动隐藏浮动按钮
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    ((MainActivity) getActivity()).actionAnimation(360, Ui.dipToPx(getResources(), 76), 200);
                } else {
                    ((MainActivity) getActivity()).actionAnimation(360, 0, 200);
                }
            }
        });
        //设置刷新颜色
        mRefreshLayout.setColorSchemeColors(Resource.Color.BLUE, Resource.Color.PINK, Resource.Color.PURPLE);
        //下拉刷新时不可用
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                if (mAdapter.getItemCount() <= 0) {
                    mRefreshLayout.setRefreshing(false);
                    return;
                }
                //依据传入最后一条的加入时间为基准 返回以基准时间的7天前为日期返回最近3天加入的群 进行增量更新
                ((ContactPresenter) mPresenter).refreshContacts();
            }
        });
        //初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);

        //监听事件
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListenerImpl<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user) {
                super.onItemClick(holder, user);
                //跳转聊天页面
                MessageActivity.show(getContext(), user);
            }
        });
    }

    @Override
    protected void initFirstData() {
        super.initFirstData();
        //查询数据
        mPresenter.start();
    }

    @Override
    protected ContactContract.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    public RecyclerAdapter<User> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mRefreshLayout.setRefreshing(false);
        //检查是否有数据 决定如何显示页面
        mEmptyView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<User> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_desc)
        TextView mDesc;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(User user, int position) {
            mPortraitView.setup(Glide.with(ContactFragment.this), user.getPortrait());
            mName.setText(user.getName());
            mDesc.setText(user.getDesc());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            PersonalActivity.show(getContext(), mData.getId());
        }
    }
}
