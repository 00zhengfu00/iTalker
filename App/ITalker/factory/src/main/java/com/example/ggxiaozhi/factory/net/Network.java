package com.example.ggxiaozhi.factory.net;

import com.example.ggxiaozhi.common.Common;
import com.example.ggxiaozhi.factory.Factory;

import okhttp3.OkHttpClient;
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

    public static Retrofit getRetrofit() {
        //创建一个client
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit.Builder builder = new Retrofit.Builder();
        return builder.baseUrl(Common.Constance.API_URL)
                //设置我们自己的Json解析器
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .client(client)
                .build();
    }
}
