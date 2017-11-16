package com.example.ggxiaozhi.factory.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.utils.HashUtil;

import java.util.Date;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.net
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：上传工具类 用于上传任意文件到阿里OSS储存
 */

public class UploadHelper {
    private static final String TAG = "UploadHelper";
    private static final String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    private static final String BUCKE_TNAME = "gg-italker";

    private static OSS getClient() {
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                "LTAIUd9hfGPQP2GU", "hSOfcXpviccjb1dBqKuyMTd6Nl5nGT");
        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }

    /**
     * 上传的最终方法 成功则返回一个路径
     *
     * @param objKey 上传上去后 在服务器上的独立的Key
     * @param path   需要上传的文件路径
     * @return 储存的地址
     */
    private static String upload(String objKey, String path) {
        // 构造上传请求
        PutObjectRequest request = new PutObjectRequest(BUCKE_TNAME, objKey, path);
        try {
            //初始化上传的Client
            OSS client = getClient();
            //开始同步上传
            PutObjectResult result = client.putObject(request);
            //得到一个外网可访问的地址
            String url = client.presignPublicObjectURL(BUCKE_TNAME, objKey);
            Log.d(TAG, String.format("PublicObjectURL:%s ", url));
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            //如果有异常返回null
            return null;
        }
    }

    /**
     * 上传普通图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadIamge(String path) {
        String key = getImageObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传头像图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path) {
        String key = getPortraitObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传音频
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path) {
        String key = getAudioObjKey(path);
        return upload(key, path);
    }

    /**
     * 分月储存 避免一个文件夹太多文件
     *
     * @return
     */
    private static String getDataString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    // protrait/201711/asdasd98a9s8dasdasd908.jpg
    public static String getPortraitObjKey(String path) {
        String fileMD5 = HashUtil.getMD5String(path);
        String dateString = getDataString();
        return String.format("protrait/%s/%s.jpg", dateString, fileMD5);
    }

    // image/201711/asdasd98a9s8dasdasd908.jpg
    public static String getImageObjKey(String path) {
        String fileMD5 = HashUtil.getMD5String(path);
        String dateString = getDataString();
        return String.format("image/%s/%s.jpg", dateString, fileMD5);
    }

    // audio/201711/asdasd98a9s8dasdasd908.mp3
    public static String getAudioObjKey(String path) {
        String fileMD5 = HashUtil.getMD5String(path);
        String dateString = getDataString();
        return String.format("audio/%s/%s.mp3", dateString, fileMD5);
    }
}
