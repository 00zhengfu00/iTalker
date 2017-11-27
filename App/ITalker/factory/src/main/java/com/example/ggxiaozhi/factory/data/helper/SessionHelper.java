package com.example.ggxiaozhi.factory.data.helper;

import com.example.ggxiaozhi.factory.model.db.Session;
import com.example.ggxiaozhi.factory.model.db.Session_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.helper
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：Session辅助工具类封装
 */

public class SessionHelper {
    public static Session findFromLocal(String id) {
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();

    }
}
