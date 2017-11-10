package com.example.ggxiaozhi.italker.media;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.ggxiaozhi.common.tools.UiTool;
import com.example.ggxiaozhi.common.widget.GalleryView;
import com.example.ggxiaozhi.italker.R;

import net.qiujuer.genius.ui.Ui;

/**
 * 图片选择器Fragment
 */
public class GalleryFragment extends BottomSheetDialogFragment implements GalleryView.SelectedChangeListener {

    private BottomSheetBehavior mBehavior;//设置BottomSheetDialog展开状态
    private GalleryView mGallery;
    private onSelectedListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TransStatusBottomSheetDialog dialog = new TransStatusBottomSheetDialog(getContext());
//        View view = View.inflate(getContext(), R.layout.fragment_gallery, null);
//        mGallery = (GalleryView) view.findViewById(R.id.galleryView);
//        dialog.setContentView(view);
//        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGallery = (GalleryView) root.findViewById(R.id.galleryView);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
//        //默认全屏展开
//        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        //获取资源图片
        mGallery.setup(getLoaderManager(), this);

    }

    @Override
    public void onSelectedCountChanged(int count) {
        //选中一张图片后就dismiss
        if (count > 0) {
            //获取错有选中图片的路径
            String[] paths = mGallery.getSelectedPath();
            //隐藏当前Fragment
            dismiss();
            //返回第一个路径
            mListener.onSelectedImage(paths[0]);
            //取消和设置者之间的引用  加快内存回收
            mListener = null;
        }
    }

    /**
     * 设置监听事件，返回自己
     *
     * @param listener
     */
    public GalleryFragment setListener(onSelectedListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * 通知外界获取路径的接口
     */
    public interface onSelectedListener {
        void onSelectedImage(String path);
    }

    private static class TransStatusBottomSheetDialog extends BottomSheetDialog {

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window = getWindow();
            if (window == null)
                return;
            //屏幕的高度
//            int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            int screenHeight = UiTool.getScreenHeight(getOwnerActivity());
            //状态栏的高度
//            int statusHeight = (int) Ui.dipToPx(getContext().getResources(), 25);
            int statusHeight = UiTool.getStatusBarHeight(getOwnerActivity());
            //显示的高度
            int dialogHeight = screenHeight - statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
            Log.d("TAG", "onCreate: " + dialogHeight);

        }
    }
}
