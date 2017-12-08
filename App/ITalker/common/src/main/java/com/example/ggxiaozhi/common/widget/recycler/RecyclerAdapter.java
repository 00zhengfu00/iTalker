package com.example.ggxiaozhi.common.widget.recycler;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.example.ggxiaozhi.common.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.italker.widget.recycler
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/4
 * 功能   ：RecyclerView适配器封装
 */

public abstract class RecyclerAdapter<Data> extends
        RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener, View.OnLongClickListener,
        AdapterCallBack<Data> {

    private final List<Data> mDataList;
    private AdapterListener<Data> mListener;

    /**
     * 三种不同的构造函数
     */
    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener<Data> adapterListener) {
        this(new ArrayList<Data>(), adapterListener);
    }

    public RecyclerAdapter(List<Data> dataList, AdapterListener<Data> adapterListener) {
        this.mDataList = dataList;
        this.mListener = adapterListener;
    }

    /**
     * 复写默认的布局返回类型
     *
     * @param position
     * @return 布局类型，其实复写后返回的都是XML布局ID
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    /**
     * 得到布局的类型
     *
     * @param position 坐标
     * @param data     当前的数据
     * @return XML文件的ID
     */
    @LayoutRes//@LayoutRes指定返回类型必须是XML布局资源
    protected abstract int getItemViewType(int position, Data data);

    /**
     * 创建一个ViewHolder
     *
     * @param parent   RecyclerView
     * @param viewType 界面的类型,约定为XML布局的Id就是viewType
     * @return ViewHolder
     */
    @Override
    public ViewHolder<Data> onCreateViewHolder(ViewGroup parent, int viewType) {
        // 得到LayoutInflater用于把XML初始化为View
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 把XML id为viewType的文件初始化为一个root View
        View root = inflater.inflate(viewType, parent, false);
        // 通过子类必须实现的方法，得到一个ViewHolder
        ViewHolder<Data> holder = onCreateViewHolder(root, viewType);

        //设置View的tag为ViewHolder 进行双向绑定
        root.setTag(R.id.tag_recycler_holder, holder);
        //设置点击时间
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);

        //进行界面绑定
        holder.mUnbinder = ButterKnife.bind(holder, root);
        //绑定Callback
        holder.mCallBack = this;
        return holder;
    }

    /**
     * 得到一个新的ViewHolder
     *
     * @param root     每个item的布局
     * @param viewType 布局的Id
     * @return viewholder
     */
    protected abstract ViewHolder<Data> onCreateViewHolder(View root, int viewType);

    /**
     * 绑定数据到一个Holder上
     *
     * @param holder   ViewHolder
     * @param position 坐标
     */
    @Override
    public void onBindViewHolder(ViewHolder<Data> holder, int position) {
        //得到绑定的数据
        Data data = mDataList.get(position);
        //触发Holder的绑定方法
        holder.bind(data, position);
    }

    /**
     * 得到当前集合的数据量
     */
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 得到当前集合
     */
    public List<Data> getItems() {
        return mDataList;
    }

    /**
     * 插入一条数据并通知插入
     *
     * @param data
     */
    public void add(Data data) {
        mDataList.add(data);
        //notifyItemInserted(int position);在指定位置插入并更新
        notifyItemInserted(mDataList.size() - 1);
    }

    /**
     * 插入多条数据并通知插入
     *
     * @param datas
     */
    public void add(Data... datas) {
        if (datas != null && datas.length > 0) {
            int startPos = mDataList.size();
            //Collections 此类不能实例化，就像一个工具类，服务于Java的Collection框架
            Collections.addAll(mDataList, datas);
            notifyItemRangeInserted(startPos, datas.length);
        }
    }

    /**
     * 插入数据集合并通知插入
     *
     * @param dataList
     */
    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeInserted(startPos, dataList.size());
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 替换一个新的集合，其中包括清空
     *
     * @param dataList
     */
    public void replace(Collection<Data> dataList) {
        mDataList.clear();
        if (dataList == null || dataList.size() == 0)
            return;
        mDataList.addAll(dataList);
        notifyDataSetChanged();

    }

    /**
     * 更新一条数据 (这个方法是实现AdapterCallBack接口中的方法)
     *
     * @param data
     * @param holder
     */
    @Override
    public void updata(Data data, ViewHolder<Data> holder) {
        //等到当前ViewHolder的坐标
        int position = holder.getAdapterPosition();
        if (position >= 0) {//判断这个坐标是否有效
            //更新
            mDataList.remove(position);
            mDataList.add(position, data);
            //刷新
            notifyItemChanged(position);
        }
    }

    @Override
    public void onClick(View v) {
        @SuppressWarnings("unchecked")
        ViewHolder<Data> holder = (ViewHolder<Data>) v.getTag(R.id.tag_recycler_holder);
        if (mDataList != null) {
            int position = holder.getAdapterPosition();
            if (mListener == null)
                return;
            mListener.onItemClick(holder, mDataList.get(position));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        @SuppressWarnings("unchecked")
        ViewHolder<Data> holder = (ViewHolder<Data>) v.getTag(R.id.tag_recycler_holder);
        if (mDataList != null) {
            int position = holder.getAdapterPosition();
            if (mListener == null)
                return false;
            mListener.onItemLongClick(holder, mDataList.get(position));
            //事件消费返回true不再触发单击事件
            return true;
        }
        return false;
    }

    /**
     * 设置适配器的监听事件
     *
     * @param adapterListener
     */
    public void setAdapterListener(AdapterListener<Data> adapterListener) {
        this.mListener = adapterListener;
    }

    /**
     * 点击时间的回调
     *
     * @param <Data>
     */
    public interface AdapterListener<Data> {
        //单击事件的回调
        void onItemClick(ViewHolder holder, Data data);

        //长按事件的回调
        void onItemLongClick(ViewHolder holder, Data data);
    }

    public static class AdapterListenerImpl<Data> implements AdapterListener<Data> {

        @Override
        public void onItemClick(ViewHolder holder, Data data) {

        }

        @Override
        public void onItemLongClick(ViewHolder holder, Data data) {

        }
    }

    /**
     * 自定义的ViewHolder
     *
     * @param <Data> 数据类型
     */
    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder {

        private AdapterCallBack<Data> mCallBack;
        protected Unbinder mUnbinder;
        protected Data mData;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * 绑定数据的触发
         *
         * @param data 绑定的数据
         */
        void bind(Data data, int position) {
            this.mData = data;
            onBind(mData, position);
        }

        /**
         * 绑定数据触发后的回调，必须复写
         *
         * @param data
         */
        public abstract void onBind(Data data, int position);

        /**
         * holder自己对自己对应的Data进行更新操作
         *
         * @param data
         */
        public void updataData(Data data) {
            if (mCallBack != null) {
                mCallBack.updata(data, this);
            }
        }
    }
}

