package com.example.ggxiaozhi.italker.fragment.main;


import android.support.v4.app.LoaderManager;

import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.common.widget.GalleryView;
import com.example.ggxiaozhi.italker.R;

import butterknife.BindView;

/**
 * 主界面Fragment
 */
public class ActiveFragment extends Fragment {

    @BindView(R.id.galleryView)
    GalleryView mGalleryView;

    public ActiveFragment() {
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initData() {
        super.initData();
        LoaderManager loaderManager = getLoaderManager();
        mGalleryView.setup(loaderManager, new GalleryView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {

            }
        });
    }
}
