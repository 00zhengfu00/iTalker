package com.example.ggxiaozhi.factory.net;

import android.text.TextUtils;

import com.example.ggxiaozhi.common.Common;
import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.presistance.Account;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.net
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：网络请求的封装
 */

public class Network {

    private static Network instance;
    private Retrofit mRetrofit;

    static {
        instance = new Network();
    }

    private Network() {

    }

    private static Retrofit getRetrofit() {
        if (instance.mRetrofit != null) {
            return instance.mRetrofit;
        }
        //创建一个client
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //得到我们发起请求的request
                        Request request = chain.request();
                        //重新封装builder
                        Request.Builder builder = request.newBuilder();
                        if (!TextUtils.isEmpty(Account.getToken()))
                            //注入一个Token
                            builder.addHeader("token", Account.getToken());
                        builder.addHeader("Content-Type", "application/json");//添加数据格式 不是必须的retrofit2已经帮我们完成了
                        Request newRequest = builder.build();
                        //返回封装Header之后的Request
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Retrofit.Builder builder = new Retrofit.Builder();
        instance.mRetrofit = builder.baseUrl(Common.Constance.API_URL)
                //设置我们自己的Json解析器
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .client(client)
                .build();
        return instance.mRetrofit;
    }

    /**
     * 返回一个请求代理
     *
     * @return RemoteService代理
     */
    public static RemoteService remote() {
        return Network.getRetrofit().create(RemoteService.class);
    }
}
