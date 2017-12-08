package com.example.ggxiaozhi.italker.fragment.search;


import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.PresenterFragment;
import com.example.ggxiaozhi.common.widget.EmptyView;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.presenter.contact.FollowContract;
import com.example.ggxiaozhi.factory.presenter.contact.FollowPresenter;
import com.example.ggxiaozhi.factory.presenter.search.SearchContract;
import com.example.ggxiaozhi.factory.presenter.search.SearchUserPresenter;
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
 * 搜索人的Fragment
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.SearchPresenter>
        implements SearchActivity.SearchFragment, SearchContract.SearchUserView {

    /**
     * UI
     */
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.empty)
    EmptyView mEmptyView;

    private RecyclerAdapter<UserCard> mAdapter;
    private SearchUserFragment.ViewHolder mHolder;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecyclerAdapter<UserCard>() {
            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                return R.layout.cell_search_list;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                mHolder=new SearchUserFragment.ViewHolder(root);
                return mHolder;
            }
        };
        mRecycler.setAdapter(mAdapter);

        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListenerImpl<UserCard>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, UserCard userCard) {
                super.onItemClick(holder, userCard);
                ((SearchUserFragment.ViewHolder)holder).onFollowClick();
            }
        });
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
        //Activity->Fragment->Presenter->Net
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.SearchPresenter initPresenter() {
        //初始化Presenter
        return new SearchUserPresenter(this);
    }

    @Override
    public void searchUserDone(List<UserCard> userCards) {
        mAdapter.replace(userCards);
        //返回数据后 如果有数据就成成显示OK 否则就显示Empty
        mEmptyView.triggerOkOrEmpty(userCards.size() > 0);

    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard> implements FollowContract.View {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.im_follow)
        ImageView mFollow;

        private FollowContract.Presenter mPresenter;

        public ViewHolder(View itemView) {
            super(itemView);
            new FollowPresenter(this);
        }

        @Override
        public void onBind(UserCard userCard, int position) {
            mPortraitView.setup(Glide.with(SearchUserFragment.this), userCard.getPortrait());
            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());

        }

        @OnClick(R.id.im_follow)
        void onFollowClick() {
            mPresenter.follow(mData.getId());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            PersonalActivity.show(getContext(), mData.getId());
        }

        @Override
        public void showError(@StringRes int str) {
            if (mFollow.getDrawable() instanceof LoadingCircleDrawable) {
                //失败停止动画 并且显示一个圆圈
                LoadingCircleDrawable loading = (LoadingCircleDrawable) mFollow.getDrawable();
                loading.setProgress(1);
                loading.stop();
            }
        }

        @Override
        public void showLoading() {
            int minSize = (int) Ui.dipToPx(getResources(), 22);
            int maxSize = (int) Ui.dipToPx(getResources(), 20);

            //圆形Loading 带动画的加载框
            LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
            //设置背景透明
            drawable.setBackgroundColor(0);
            int[] color = new int[]{UiCompat.getColor(getResources(), com.example.ggxiaozhi.factory.R.color.white_alpha_208)};
            //设置前置背景
            drawable.setForegroundColor(color);
            mFollow.setImageDrawable(drawable);
            //启动动画
            drawable.start();
        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public void followUserSuccessed(UserCard userCard) {
            if (mFollow.getDrawable() instanceof LoadingCircleDrawable) {
                //加载成功停止动画
                ((LoadingCircleDrawable) mFollow.getDrawable()).stop();
                //设置回默认状态
                mFollow.setImageResource(R.drawable.sel_opt_done_add);
            }
            //发起更新数据
            updataData(userCard);
        }
    }
}
