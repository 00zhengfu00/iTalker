package com.example.ggxiaozhi.factory.data;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：基础的数据库数据源接口定义
 */

public interface DbDataSource<Data> extends DataSource {

    /**
     * 对数据进行一个加载的职责
     *
     * @param callback 加载成功返回的callback
     */
    void load(DataSource.SucceedCallback<List<Data>> callback);
}
