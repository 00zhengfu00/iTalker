package com.example.ggxiaozhi.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ggxiaozhi.common.R;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.widget
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/7
 * 功能   ：图片选择器(利用LoaderManager加载数据)
 */
public class GalleryView extends RecyclerView {

    private final static int LOADER_ID = 0x0100;//loaderId
    private final static int IMAGE_MAX_NUM = 3;//图片选择的最大个数
    private final static int MIN_IMAGE_FILE_SIZE = 10 * 1024;//过滤图片 规定最小的图片大小
    private LoaderCallback mLoaderCallback = new LoaderCallback();//loaderManager中的查询回调
    private SelectedChangeListener mListener;//通知调用者选择状态的回调
    private List<Image> mSelectedImages = new LinkedList<>();//从ContentProider中取出的路径
    private Adapter mAdapter = new Adapter();

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setLayoutManager(new GridLayoutManager(getContext(), 4));
        this.setAdapter(mAdapter);
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                // Cell点击操作，如果说我们的点击是允许的，那么更新对应的Cell的状态
                // 然后更新界面，同理；如果说不能允许点击（已经达到最大的选中数量）那么就不刷新界面
                if (onItemSelectClick(image)) {
                    holder.updataData(image);
                }
            }
        });
    }

    /**
     * Cell点击的具体逻辑
     *
     * @param image Image
     * @return True 代表了我们进行了数据更改 你需要刷新界面；反之不刷新
     */
    @SuppressLint("StringFormatMatches")
    private boolean onItemSelectClick(Image image) {
        //是否需要进行刷新界面
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            //如果之前在就移除
            mSelectedImages.remove(image);
            image.isSelect = false;
            // 状态已经改变则需要更新
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= IMAGE_MAX_NUM) {
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                str = String.format(str, IMAGE_MAX_NUM);
                //Toast 一条信息
                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            } else {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }
        // 如果数据有更改，
        // 那么我们需要通知外面的监听者我们的数据选中改变了
        if (notifyRefresh)
            notifySelectChanged();
        return true;
    }

    /**
     * 通知外界选中状态改变
     */
    private void notifySelectChanged() {
        SelectedChangeListener listener = mListener;
        // 得到监听者，并判断是否有监听者，然后进行回调数量变化
        if (listener != null) {
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    /**
     * 初始化loader的方法
     *
     * @param loaderManager loader管理器
     * @return 返回一个loader Id用于销毁loader
     */
    public int setup(LoaderManager loaderManager, SelectedChangeListener listener) {
        this.mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, mLoaderCallback);
        return LOADER_ID;
    }

    /**
     * 得到选中图片的全部路径
     *
     * @return
     */
    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image image : mSelectedImages) {
            Log.d("TAG", "getSelectedPath: " + image.path);
            paths[index++] = image.path;
        }
        return paths;
    }

    /**
     * 清空选中的图片
     */
    public void clear() {
        for (Image image : mSelectedImages) {
            image.isSelect = false;
        }
        mSelectedImages.clear();
        mAdapter.notifyDataSetChanged();
        notifySelectChanged();
    }

    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,//图片的Id
                MediaStore.Images.Media.DATA,//图片的路径
                MediaStore.Images.Media.DATE_ADDED//图片的创建时间
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //创建一个Laoder
            if (id == LOADER_ID) {
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC");//降序
            }
            return null;
        }

        @Override//当loader加载完成时 用于更新获取数据更新UI
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            List<Image> images = new ArrayList<>();
            //判断是否有数据
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    //移动到游标开始处
                    data.moveToFirst();
                    //得到对应列的index
                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexTime = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        //循环读取数据 直到没有下一条数据
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        long dateTime = data.getLong(indexTime);

                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            //如果图片不存在或是图片太小 则跳过本次循环
                            continue;
                        }

                        //添加一条数据
                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.data = dateTime;
                        images.add(image);
                    }
                    while (data.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            //当loader销毁或重置 进行界面清空
            updateSource(null);
        }
    }

    /**
     * 图片选择数量变化时的接口回调
     */
    public interface SelectedChangeListener {
        void onSelectedCountChanged(int count);
    }

    private void updateSource(List<Image> images) {
        mAdapter.replace(images);
    }

    /**
     * 封装从相册取出的图片数据结构
     */
    private static class Image {
        int id;//数据的ID
        String path;//图片的路径
        long data;//图片创建的日期
        boolean isSelect;//是否被选中

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return path != null ? path.equals(image.path) : image.path == null;

        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleryView.ViewHolder(root);
        }
    }

    /**
     * Cell 对应的holder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {

        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        ViewHolder(View itemView) {
            super(itemView);
            mPic = (ImageView) itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = (CheckBox) itemView.findViewById(R.id.cb_check);
        }

        @Override
        public void onBind(Image image,int position) {
            Glide.with(getContext())
                    .load(image.path)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//本地图片不设置缓存
                    .placeholder(R.color.grey_200)//默认颜色
                    .into(mPic);

            mShade.setVisibility(image.isSelect ? VISIBLE : INVISIBLE);
            mSelected.setChecked(image.isSelect);
            mSelected.setVisibility(VISIBLE);
        }
    }
}