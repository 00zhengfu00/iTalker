package com.example.ggxiaozhi.factory.model.db.view;

import com.example.ggxiaozhi.factory.model.db.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.db.view
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：简单的多表映射的model
 */
@QueryModel(database = AppDatabase.class)
public class MemberUserModel {

    @Column
    private String userId;//User->Id/Member->userId
    @Column
    private String name;// User->name
    @Column
    private String alias;//Member->alias
    @Column
    private String portrait;//User->portrait

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
