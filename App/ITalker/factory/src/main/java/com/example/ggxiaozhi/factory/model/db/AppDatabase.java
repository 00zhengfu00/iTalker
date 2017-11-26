package com.example.ggxiaozhi.factory.model.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.card
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：数据库的基本信息
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "AppDatabase";
    public static final int VERSION = 2;
}
