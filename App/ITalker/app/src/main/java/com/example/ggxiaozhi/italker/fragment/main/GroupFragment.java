package com.example.ggxiaozhi.italker.fragment.main;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.PresenterFragment;
import com.example.ggxiaozhi.common.widget.EmptyView;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.presenter.group.GroupContract;
import com.example.ggxiaozhi.factory.presenter.group.GroupPresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.MainActivity;
import com.example.ggxiaozhi.italker.activity.MessageActivity;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.Ui;

import butterknife.BindView;

/**
 * 群组Fragmnet
 */
public class GroupFragment extends PresenterFragment<GroupContract.Presenter>
        implements GroupContract.View {
    /**
     * UI
     */
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.empty)
    EmptyView mEmptyView;  @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mRefreshLayout;

    /**
     * Data
     */
    private RecyclerAdapter<Group> mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mAdapter = new RecyclerAdapter<Group>() {
            @Override
            protected int getItemViewType(int position, Group group) {
                return R.layout.cell_group_list;
            }

            @Override
            protected ViewHolder<Group> onCreateViewHolder(View root, int viewType) {
                return new GroupFragment.ViewHolder(root);
            }
        };
        mRecycler.setAdapter(mAdapter);
        //设置刷新颜色
        mRefreshLayout.setColorSchemeColors(Resource.Color.BLUE, Resource.Color.PINK, Resource.Color.PURPLE);
        //下拉刷新时不可用
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                if (mAdapter.getItemCount()<=0){
                    mRefreshLayout.setRefreshing(false);
                    return;
                }
                //依据传入最后一条的加入时间为基准 返回以基准时间的7天前为日期返回最近3天加入的群 进行增量更新
                ((GroupPresenter)mPresenter).refreshGroups(mAdapter.getItems().get(mAdapter.getItemCount()-1).getJoinAt());
            }
        });
        //滑动隐藏浮动按钮
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    ((MainActivity)getActivity()).actionAnimation(360, Ui.dipToPx(getResources(), 76),200);
                }else {
                    ((MainActivity)getActivity()).actionAnimation(360, 0,200);
                }
            }
        });

        //初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);

        //监听事件
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListenerImpl<Group>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Group group) {
                super.onItemClick(holder, group);
                //跳转聊天页面
                MessageActivity.show(getContext(), group);
            }
        });
    }

    @Override
    protected void initFirstData() {
        super.initFirstData();
        mRefreshLayout.setRefreshing(true);
        //查询数据
        mPresenter.start();
    }

    @Override
    protected GroupContract.Presenter initPresenter() {
        return new GroupPresenter(this);
    }

    @Override
    public RecyclerAdapter<Group> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mRefreshLayout.setRefreshing(false);
        //检查是否有数据 决定如何显示页面
        mEmptyView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<Group> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_desc)
        TextView mDesc;
        @BindView(R.id.txt_member)
        TextView mMember;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Group group, int position) {
            mPortraitView.setup(Glide.with(GroupFragment.this), group.getPicture());
            mName.setText(group.getName());
            mDesc.setText(group.getDesc());

            if (group.holder != null && group.holder instanceof String) {
                mMember.setText((String) group.holder);
            } else {
                mMember.setText("");
            }

        }
    }
}
