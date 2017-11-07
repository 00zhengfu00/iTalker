package net.ggxiaozhi.web.italker.push.service;

import net.ggxiaozhi.web.italker.push.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.service
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：封装请求Service 为子类提供上下文
 */
public class BaseService {


    //添加一个上下文注解，该注解会给securityContext赋值
    //具体的值为我们在AuthRequestFilter拦截器中所返回的SecurityContext
    @Context
    protected SecurityContext securityContext;

    /**
     * 从上下文中获取自己的信息
     * @return 通过Token查询的user
     */
    protected User getSelf() {
        //securityContext.getUserPrincipal()
        //的值就是我们在SecurityContext方法中主体部分返回的值
        return (User) securityContext.getUserPrincipal();
    }

}
