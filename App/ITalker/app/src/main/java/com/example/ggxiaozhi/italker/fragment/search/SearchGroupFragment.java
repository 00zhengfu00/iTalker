package com.example.ggxiaozhi.italker.fragment.search;


import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.common.app.PresenterFragment;
import com.example.ggxiaozhi.common.widget.EmptyView;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.factory.model.card.GroupCard;
import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.presenter.contact.FollowContract;
import com.example.ggxiaozhi.factory.presenter.contact.FollowPresenter;
import com.example.ggxiaozhi.factory.presenter.search.SearchContract;
import com.example.ggxiaozhi.factory.presenter.search.SearchGroupPresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.PersonalActivity;
import com.example.ggxiaozhi.italker.activity.SearchActivity;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索群的Fragment
 */
public class SearchGroupFragment extends PresenterFragment<SearchContract.SearchPresenter>
        implements SearchActivity.SearchFragment, SearchContract.SearchGroupView {

    /**
     * UI
     */
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.empty)
    EmptyView mEmptyView;

    private RecyclerAdapter<GroupCard> mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecyclerAdapter<GroupCard>() {
            @Override
            protected int getItemViewType(int position, GroupCard groupCard) {
                return R.layout.cell_search_list_group;
            }

            @Override
            protected ViewHolder<GroupCard> onCreateViewHolder(View root, int viewType) {
                return new SearchGroupFragment.ViewHolder(root);
            }
        };
        mRecycler.setAdapter(mAdapter);

        //初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        //发起首次搜索的触发
        search("");
    }

    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.SearchPresenter initPresenter() {
        return new SearchGroupPresenter(this);
    }

    @Override
    public void searchGroupDone(List<GroupCard> groupCards) {
        if (groupCards == null)
            mEmptyView.triggerOkOrEmpty(false);
        mAdapter.replace(groupCards);
        //返回数据后 如果有数据就成成显示OK 否则就显示Empty
        mEmptyView.triggerOkOrEmpty(groupCards.size() > 0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCard> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.im_join)
        ImageView mJoin;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(GroupCard groupCard, int position) {
            mPortraitView.setup(Glide.with(SearchGroupFragment.this), groupCard.getPicture());
            mName.setText(groupCard.getName());
            mJoin.setEnabled(groupCard.getJoinAt() == null);
        }

        @OnClick(R.id.im_join)
        void onJoinClick() {
            //点击加入跳转群主的个人信息界面
            PersonalActivity.show(getContext(), mData.getOwnerId());
        }

    }
}
