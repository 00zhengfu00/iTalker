package com.example.ggxiaozhi.factory.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.utils
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：DBFlow 数据库过滤字段 Gson
 */

public class DBFlowExclusionStrategies implements ExclusionStrategy{
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        //被跳过的字段
        //只要属于DBFlow的数据
        return f.getDeclaredClass().equals(ModelAdapter.class);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        //被跳过的类
        return false;
    }
}
