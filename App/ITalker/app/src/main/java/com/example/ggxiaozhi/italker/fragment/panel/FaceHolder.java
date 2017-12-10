package com.example.ggxiaozhi.italker.fragment.panel;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.face.FaceUtil;
import com.example.ggxiaozhi.italker.R;

import butterknife.BindView;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.italker.fragment.panel
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：表情盘中表情的ViewHolder
 */

public class FaceHolder extends RecyclerAdapter.ViewHolder<FaceUtil.Bean> {
    @BindView(R.id.im_face)
    ImageView mFace;

    public FaceHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(FaceUtil.Bean bean, int position) {

        //内容不能为空 同时满足是drawable文件或是zip文件
        if (bean != null && (bean.preview instanceof Integer || bean.preview instanceof String)) {
            Glide.with(mFace.getContext())
                    .load(bean.preview)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(mFace);
        }
    }
}
