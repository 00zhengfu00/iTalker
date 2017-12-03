package com.example.ggxiaozhi.factory.model.db.base;

import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.db.base
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：主要的作用是限定传入的泛型 Model
 * 我们APP中的基础的一个BaseDbModel，
 * 基础了数据库框架DbFlow中的基础类
 * 同时定义类我们需要的方法
 */

public abstract class BaseDbModel<Model> extends BaseModel implements DiffUiDataCallback.UiDataDiff<Model> {
}
