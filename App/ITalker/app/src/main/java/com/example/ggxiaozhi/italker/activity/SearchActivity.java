package com.example.ggxiaozhi.italker.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.common.app.ToolBarActivity;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.fragment.search.SearchGroupFragment;
import com.example.ggxiaozhi.italker.fragment.search.SearchUserFragment;

/**
 * 搜索Activity
 */
public class SearchActivity extends ToolBarActivity {

    /**
     * Data
     */
    private static final String EXTRA_TYPR = "KEY_TYPR";//传递参数的Key
    public static final int TYPE_USER = 1;//搜索人
    public static final int TYPE_GROUP = 2;//搜索群

    private SearchFragment mSearchFragment;
    private int type;

    /**
     * SearchActivity 界面显示的入口
     *
     * @param context 源Activity
     * @param type    想要显示那个界面 1搜索人 2搜索群
     */
    public static void show(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TYPR, type);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        type = bundle.getInt(EXTRA_TYPR);
        return type == TYPE_USER || type == TYPE_GROUP;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        Fragment fragment;
        if (type == TYPE_USER) {//显示搜索用户的界面
            SearchUserFragment searchUser = new SearchUserFragment();
            fragment = searchUser;
            mSearchFragment = searchUser;
        } else {//显示搜索群组的界面
            SearchGroupFragment searchGroup = new SearchGroupFragment();
            fragment = searchGroup;
            mSearchFragment = searchGroup;
        }

        //添加Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, fragment)
                .commit();
    }

    /**
     * 初始化ToolBar菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //初始化菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem itemView = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) itemView.getActionView();
        if (itemView != null) {
            //拿到一个管理器
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            //添加搜索监听
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //当点击提交按钮或是键盘的上的提交按钮的时候
                    search(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //当文字变化的时候我们不进行搜索 只有在空的情况下进行搜素
                    if (TextUtils.isEmpty(newText)) {
                        search("");
                        return true;
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 搜索的发起点
     *
     * @param query 搜索的条件
     */
    private void search(String query) {
        mSearchFragment.search(query);
    }

    /**
     * 搜索Fragmnet需要实现的接口
     * content 搜索输入的内容
     */
    public interface SearchFragment {
        void search(String content);
    }
}
