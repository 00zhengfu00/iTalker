package com.example.ggxiaozhi.italker.fragment.search;


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
import com.example.ggxiaozhi.factory.presenter.search.SearchContract;
import com.example.ggxiaozhi.factory.presenter.search.SearchUserPresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.SearchActivity;

import java.util.List;

import butterknife.BindView;

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
                return new SearchUserFragment.ViewHolder(root);
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

    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mTextView;
        @BindView(R.id.im_follow)
        ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(UserCard userCard) {

            Glide.with(SearchUserFragment.this)
                    .load(userCard.getPortrait())
                    .centerCrop()
                    .into(mPortraitView);
            mTextView.setText(userCard.getName());
            mImageView.setEnabled(!userCard.isFollow());

        }
    }
}
