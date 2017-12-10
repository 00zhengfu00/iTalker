package com.example.ggxiaozhi.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.ggxiaozhi.common.R;
import com.example.ggxiaozhi.utils.StreamUtil;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.face
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：表情工具类(表情盘指的是不同的资源文件加载)
 */

public class FaceUtil {

    private static final ArrayMap<String, Bean> FACE_MAP = new ArrayMap<>();//全局的表情映射ArrayMap 在数据较少的情况小 更轻量
    private static List<FaceTab> FACE_TABS = null;//表情面板集合


    /**
     * 初始化面板数据 同时进行转化
     *
     * @param context 上下文
     */
    private static void init(Context context) {
        if (FACE_TABS == null) {
            synchronized (FaceUtil.class) {
                if (FACE_TABS == null) {
                    ArrayList<FaceTab> faceTabs = new ArrayList<>();
                    //初始化 资源文件
                    FaceTab tab = initAssetsFace(context);
                    if (tab != null) {
                        faceTabs.add(tab);
                    }
                    //初始化zip文件
                    tab = initResourcesFace(context);
                    if (tab != null) {
                        faceTabs.add(tab);
                    }

                    //init map
                    for (FaceTab faceTab : faceTabs) {
                        faceTab.copyToMap(FACE_MAP);
                    }
                    //init list 不可变 创建完成不允许在修改数据集合(添加或删除等)
                    FACE_TABS = Collections.unmodifiableList(faceTabs);
                }
            }
        }
    }

    /**
     * 初始化Assets表情盘
     * 先获取zip文件资源 解析 在将文件copy到手机的文件缓存目录中去
     *
     * @param context 上下文
     * @return Assets表情盘
     */
    private static FaceTab initAssetsFace(Context context) {
        String faceAssets = "face-t.zip";//文件名称
        //定义缓存路径 data/data/包名/face/ft/*
        String faceCacheDir = String.format("%s/face/ft", context.getFilesDir());
        //创建文件夹路径
        File faceFolder = new File(faceCacheDir);
        if (!faceFolder.exists()) {
            //文件不存在就初始化
            if (faceFolder.mkdirs()) {//创建文件夹
                try {
                    InputStream inputStream = context.getAssets().open(faceAssets);
                    //创建zip文件
                    File faceSource = new File(faceFolder, "source.zip");
                    //copy
                    StreamUtil.copy(inputStream, faceSource);
                    //解压zip
                    unZipFile(faceSource, faceFolder);
                    //清理文件
                    StreamUtil.delete(faceSource.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //info.json
        File infoFile = new File(faceFolder, "info.json");
        //Gson 解析
        Gson gson = new Gson();
        JsonReader reader;
        try {
            reader = gson.newJsonReader(new FileReader(infoFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //没有找到 null
            return null;
        }
        //解析
        FaceTab tab = gson.fromJson(reader, FaceTab.class);
        //相对路径转换绝对路径
        for (Bean face : tab.faces) {
            face.preview = String.format("%s/%s", faceCacheDir, face.preview);
            face.source = String.format("%s/%s", faceCacheDir, face.source);
        }

        return tab;
    }

    /**
     * 将zip文件解压到desDir目录
     *
     * @param zipFile zip文件
     * @param desDir  解压的目录
     */
    private static void unZipFile(File zipFile, File desDir) throws IOException {
        final String folderPath = desDir.getAbsolutePath();

        ZipFile zf = new ZipFile(zipFile);
        // 判断节点进行循环
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            // 过滤缓存的文件
            String name = entry.getName();
            //文件以.开头为缓存文件 过滤掉
            if (name.startsWith("."))
                continue;

            // 输入流
            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + name;

            // 防止名字错乱
            str = new String(str.getBytes("8859_1"), "GB2312");
            //目标文件
            File desFile = new File(str);
            // 输出文件
            StreamUtil.copy(in, desFile);
            //zipFile->循环解析->得到输入流->文件目录：folderPath/f-0.gif
        }
    }

    /**
     * 初始化Resources表情盘
     * 这里用到反射的机制 利用包名去那指定资源下的文件
     *
     * @param context 上下文
     * @return Resources表情盘
     */
    private static FaceTab initResourcesFace(Context context) {
        //创建表情集合
        final List<FaceUtil.Bean> faces = new ArrayList<>();
        //拿到resources
        final Resources resources = context.getResources();
        String packageName = context.getApplicationInfo().packageName;

        //循环资源下的文件个数
        for (int i = 0; 106 >= i; i++) {
            //资源名称
            String resPng = String.format(Locale.ENGLISH, "f_static_%03d", i);
            //创建key 将资源文件重新命名 这个命名就是我们在发送时的唯一标识 fb%03d-> i=1时 fb001 3d标识三位数 0占位
            String keyPng = String.format(Locale.ENGLISH, "fb%03d", i);
            //资源名称
            String resGif = String.format(Locale.ENGLISH, "f%03d", i);
            //指定资源文件中的drawable目录下的 以resStr命名的资源 返回一个int值 例如：R.id.face_base_001
            int resPngId = resources.getIdentifier(resPng, "drawable", packageName);
            int resGigId = resources.getIdentifier(resGif, "drawable", packageName);
            if (resPngId == 0 || resGigId == 0)
                //没有找到 跳过
                continue;
            //添加表情
            faces.add(new Bean(keyPng, resPngId, resGigId));
        }
        if (faces.size() == 0)
            return null;
        return new FaceTab("NAME", faces.get(0).preview, faces);
    }

    /**
     * 获取所有表情盘
     *
     * @param context 上下文
     * @return 表情盘集合
     */
    public static List<FaceTab> all(@NonNull Context context) {
        init(context);
        return FACE_TABS;
    }

    /**
     * 输入一个表情
     *
     * @param context  上下文
     * @param editable 将表情输入到Editable上去 Editable是一个接口继承了基础的文本CharSequence接口
     *                 同时实现Spannable接口(实现对字符的标记 并且提供对字符的移除和添加标记"Span"的操作)
     *                 "Span"标记 任意类型 Object
     * @param bean     输入的一个表情
     * @param size     表情的尺寸大小
     */
    public static void inputFace(@NonNull final Context context, final Editable editable, final FaceUtil.Bean bean, final int size) {
        Glide.with(context)
                .load(bean.preview)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(size, size) {//设置宽 高
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Spannable spannable = new SpannableString(String.format("[%s]", bean.key));
                        //创建ImageSpan->可以将文字转成图片 ImageSpan.ALIGN_BASELINE对齐方式->基于基线对齐
                        ImageSpan imageSpan = new ImageSpan(context, resource, ImageSpan.ALIGN_BASELINE);
                        //字符拼接 从0到文件名字的最后 属性为前后不关联
                        spannable.setSpan(imageSpan, 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //讲spannable设置到输入框中
                        editable.append(spannable);
                    }
                });
    }

    /**
     * 解析一个表情
     *
     * @param target    将表情解析到指定的View上去
     * @param spannable 从spannable获得表情并替换显示
     * @param size      表情的大小
     * @return 返回一个表情盘
     */
    public static Spannable decode(@NonNull final View target, final Spannable spannable, final int size) {
        if (spannable == null)
            return null;
        String str = spannable.toString();
        if (TextUtils.isEmpty(str))
            return null;
        final Context context = target.getContext();

        // 进行正则匹配[][][]
        Pattern pattern = Pattern.compile("(\\[[^\\[\\]:\\s\\n]+\\])");
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            // [ft112]
            String key = matcher.group();
            if (TextUtils.isEmpty(key))
                continue;

            //去掉[ ]
            Bean bean = get(context, key.replace("[", "").replace("]", ""));
            if (bean == null)
                continue;

            final int start = matcher.start();
            final int end = matcher.end();

            // 得到一个复写后的标示  这个是今天图的方法 下面动态图的实现是不停的在子线程中刷新
            // 势必会产生消耗 目前测试没有问题后期如果有问题 可以改回来变成静态图片
            //ImageSpan span = new FaceSpan(context, target, bean.preview, size);
            InputStream is = null;
            if (bean.preview instanceof Integer)
                is = context.getResources().openRawResource((Integer) bean.source);
            else {
                File file = new File((String) bean.source);
                try {
                    is = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
            AnimatedImageSpan span = new AnimatedImageSpan(new AnimatedGifDrawable(is, new AnimatedGifDrawable.UpdateListener() {
                @Override
                public void update() {
                    target.postInvalidate();
                }
            }));
            // 设置标示
            spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        return spannable;
    }

    private static Bean get(Context context, String key) {
        init(context);
        if (FACE_MAP.containsKey(key))
            return FACE_MAP.get(key);
        return null;
    }

    /**
     * 每一个表情盘 含有很多表情
     */
    public static class FaceTab {
        FaceTab(String name, Object preview, List<Bean> faces) {
            this.faces = faces;
            this.name = name;
            this.preview = preview;
        }

        //所有表情的集合
        public List<Bean> faces = new ArrayList<>();
        //包括 assets文件下zip文件 路径为 String 类型
        public String name;
        //预览图 包括Drawable下面的资源 int类型
        Object preview;

        //集合转换
        private void copyToMap(ArrayMap<String, Bean> faceMap) {
            for (Bean bean : faces) {
                faceMap.put(bean.key, bean);
            }
        }
    }

    /**
     * 封装ImageSpan 解决无法在Glide中设置属性的问题
     */
    private static class FaceSpan extends ImageSpan {

        //自己真实绘制的Drawable
        private Drawable mDrawable;
        private View mView;
        private int mSize;

        /**
         * 构造
         *
         * @param context 上下文
         * @param view    目标View，用于加载完成时刷新使用
         * @param source  加载目标
         * @param size    图片的显示大小
         */
        FaceSpan(Context context, View view, Object source, final int size) {
            //虽然设置默认的表情图片 但只是占位 不显示
            super(context, R.drawable.default_face, ImageSpan.ALIGN_BOTTOM);
            mView = view;
            mSize = size;
            Glide.with(context)
                    .load(source)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
                    .into(new SimpleTarget<GifDrawable>() {
                        @Override
                        public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
                            mDrawable = resource.getCurrent();
                            //自己测量 得到本身的 宽高
                            int width = mDrawable.getIntrinsicWidth();
                            int height = mDrawable.getIntrinsicHeight();
                            //绘制图片的矩形位置 绘制drawable
                            mDrawable.setBounds(0, 0, width > 0 ? width : size, height > 0 ? height : size);
                            //刷新view刷新
                            mView.invalidate();
                        }
                    });
        }

        @Override
        public Drawable getDrawable() {
            //这个方法主要是在测量和绘制的时候回调用 但是可能存在为空的情况 因为在mDrawable = resource.getCurrent();是加载是个耗时
            //的操作 可能还没有赋值 就使用这个方法 导致mDrawable为空 所以我们要复写两个方法draw()->绘制方法 和getSize()->测量方法
            return mDrawable;
        }

        @Override//这里将父类的方法拿到子类中 仅仅只是加一个判断 防止getDrawable() 方法返回为空的情况
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            // 走我们自己的逻辑，进行测量
            Rect rect = mDrawable != null ? mDrawable.getBounds() :
                    new Rect(0, 0, mSize, mSize);

            if (fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }

            return rect.right;
        }

        @Override//不等于空的情况下我才让你绘制
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            if (mDrawable != null)
                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }
    }

    /**
     * 每一个表情
     */
    public static class Bean {
        Bean(String key, int preview) {
            this.key = key;
            this.source = preview;
            this.preview = preview;
        }

        Bean(String key, int preview, Object source) {
            this.key = key;
            this.preview = preview;
            this.source = source;
        }

        public String key;
        public String desc;
        public Object source;
        public Object preview;
    }
}
