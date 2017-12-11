package com.example.ggxiaozhi.factory.utils;

import android.util.Log;

import com.example.ggxiaozhi.common.app.Application;
import com.example.ggxiaozhi.factory.net.Network;
import com.example.ggxiaozhi.utils.HashUtil;
import com.example.ggxiaozhi.utils.StreamUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.utils
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：语音下载的工具类 实现对语音文件的下载 下载后回调相应的方法
 */

public class FileCacheUtil<Holder> {
    private File baseDir;//本地下载的文件的路径
    private String ext;//文件后缀(格式)
    private CacheListener<Holder> mListener;
    //全局的目标
    private SoftReference<Holder> mHolderSoftReference;

    /**
     * 构造方法
     *
     * @param baseDir  创建下载文件的路径
     * @param ext      文件后缀
     * @param listener 监听
     */
    public FileCacheUtil(String baseDir, String ext, CacheListener<Holder> listener) {
        this.baseDir = new File(Application.getCacheDirFile(), baseDir);
        this.ext = ext;
        this.mListener = listener;
    }

    /**
     * 构建一个本地的缓存文件 同一个网络路径对应一个本地缓存文件
     *
     * @param path 网络云存储路径
     * @return 返回一个文件
     */
    private File buildCacheFile(String path) {
        Log.d("TAG", "buildCacheFile: path=   " + path);
        String key = HashUtil.getMD5String(path);
        Log.d("TAG", "buildCacheFile: key=   " + key);
        return new File(baseDir, key + "." + ext);
    }

    /**
     * 从云服务器下载文件
     *
     * @param path   要下载的文件的网络路径
     * @param holder 具体哪个item中的文件
     */
    @SuppressWarnings("unchecked")
    public void downloadFile(Holder holder, String path) {
        //如果文件的路径已经是本地路径了 那么就不需要下载了 直接返回
        if (path.startsWith(Application.getCacheDirFile().getAbsolutePath())) {
            mListener.onDownLoadSuccess(holder, new File(path));
            return;
        }

        //根据网络云存储的路径构建本地文件
        final File cacheFile = buildCacheFile(path);
        if (cacheFile.exists() && cacheFile.length() > 0) {
            //如果从网络下载构建的文件 已经存在了 表示此文件之前已经下载过了
            mListener.onDownLoadSuccess(holder, cacheFile);
            return;
        }
        //将目标holder缓存起来
        mHolderSoftReference = new SoftReference<>(holder);
        //开始下载
        OkHttpClient okHttpClient = Network.getOkHttpClient();
        //创建一个请求
        Request request = new Request.Builder().url(path).get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new NetCallback(holder, cacheFile));

    }

    /**
     * 是不是最后一个点击的语音  因为我们只想要最后点击的才是我们想听的
     * @return 返回最后点击的holder
     */
    private Holder getLastHolderAndClear() {
        if (mHolderSoftReference == null)
            return null;
        else {
            //获取目标
            Holder holder = mHolderSoftReference.get();
            //清除软引用的数据
            mHolderSoftReference.clear();
            return holder;
        }
    }

    private class NetCallback implements Callback {

        private SoftReference<Holder> mHolderSoftReference;
        private File mFile;

        NetCallback(Holder holder, File file) {
            mHolderSoftReference = new SoftReference<>(holder);
            mFile = file;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onFailure(Call call, IOException e) {
            //当软引用调用.get()还没有被回收器回收，直接获取 里面的值
            Holder holder = mHolderSoftReference.get();
            if (holder != null && holder == getLastHolderAndClear()) {
                //仅仅最后一次才有效
                FileCacheUtil.this.mListener.onDownLoadFailed(holder);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream inputStream = response.body().byteStream();
            if (inputStream != null && StreamUtil.copy(inputStream, mFile)) {
                //当软引用调用.get()还没有被回收器回收，直接获取 里面的值
                Holder holder = mHolderSoftReference.get();
                if (holder != null && holder == getLastHolderAndClear()) {
                    FileCacheUtil.this.mListener.onDownLoadSuccess(holder, mFile);
                }
            } else {
                onFailure(call, null);
            }

        }
    }

    public interface CacheListener<Holder> {
        //成功把目标item的holder一起丢回去 file是已经下载到了本地的文件
        void onDownLoadSuccess(Holder holder, File file);

        void onDownLoadFailed(Holder holder);
    }

}
