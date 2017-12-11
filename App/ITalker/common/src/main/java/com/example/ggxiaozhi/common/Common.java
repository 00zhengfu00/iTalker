package com.example.ggxiaozhi.common;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/4
 * 功能   ： 一些持久化的数据(常量数据)
 */

public class Common {

    public interface Constance {
        //手机号的正则  11位手机号
        String REGEX_MOBILE = "[1][3,4,5,7,8][0-9]{9}$";

        //所有请求的Base_Url
        String API_URL = "http://192.168.1.103:8080/api/";
        //上传图片的最大大小860kb
        long MAX_UPLOAD_IMAGE_LENGTH = 860 * 1024;
    }
}
