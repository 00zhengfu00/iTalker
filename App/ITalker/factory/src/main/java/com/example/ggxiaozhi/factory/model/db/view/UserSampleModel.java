package com.example.ggxiaozhi.factory.model.db.view;

import com.example.ggxiaozhi.factory.model.Author;
import com.example.ggxiaozhi.factory.model.db.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.db.view
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：简单数据库user表
 */
@QueryModel(database = AppDatabase.class)
public class UserSampleModel implements Author {
    @Column
    private String id;
    @Column
    private String name;
    @Column
    private String portrait;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPortrait() {
        return portrait;
    }

    @Override
    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
