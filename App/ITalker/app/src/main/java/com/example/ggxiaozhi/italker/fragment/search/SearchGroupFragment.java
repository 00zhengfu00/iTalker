package com.example.ggxiaozhi.italker.fragment.search;


import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.SearchActivity;

/**
 * 搜索群的Fragment
 */
public class SearchGroupFragment extends Fragment implements SearchActivity.SearchFragment {


    public SearchGroupFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String content) {

    }
}
