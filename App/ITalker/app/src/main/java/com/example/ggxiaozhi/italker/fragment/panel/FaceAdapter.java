package com.example.ggxiaozhi.italker.fragment.panel;

import android.view.View;

import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.face.FaceUtil;
import com.example.ggxiaozhi.italker.R;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.italker.fragment.panel
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：
 */

public class FaceAdapter extends RecyclerAdapter<FaceUtil.Bean> {

    public FaceAdapter(List<FaceUtil.Bean> been, AdapterListener<FaceUtil.Bean> adapterListener) {
        super(been, adapterListener);
    }

    @Override
    protected int getItemViewType(int position, FaceUtil.Bean bean) {
        return R.layout.cell_face;
    }

    @Override
    protected ViewHolder<FaceUtil.Bean> onCreateViewHolder(View root, int viewType) {
        return new FaceHolder(root);
    }
}
