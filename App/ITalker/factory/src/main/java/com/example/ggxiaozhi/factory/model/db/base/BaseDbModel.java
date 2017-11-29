package com.example.ggxiaozhi.factory.model.db.base;

import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.db.base
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：
 */

public abstract class BaseDbModel<Model> extends BaseModel implements DiffUiDataCallback.UiDataDiff<Model> {
}
