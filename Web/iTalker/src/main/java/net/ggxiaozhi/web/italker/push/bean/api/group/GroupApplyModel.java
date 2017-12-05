package net.ggxiaozhi.web.italker.push.bean.api.group;

import com.google.gson.annotations.Expose;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.api.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：申请加入群的model
 */
public class GroupApplyModel {

    @Expose
    private String desciption;
    @Expose
    private String attach;

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }
}
