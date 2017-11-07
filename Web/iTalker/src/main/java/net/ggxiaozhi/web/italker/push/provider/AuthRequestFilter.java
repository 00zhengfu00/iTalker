package net.ggxiaozhi.web.italker.push.provider;

import com.google.common.base.Strings;
import net.ggxiaozhi.web.italker.push.bean.api.base.ResponseModel;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import net.ggxiaozhi.web.italker.push.bean.factory.UserFactory;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.provider
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：用于所有请求的接口的过滤和拦截
 */
@Provider
public class AuthRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //检查是否是我们的登录注册接口
        String relationPath = ((ContainerRequest) requestContext).getPath(false);
        if (relationPath.startsWith("account/login")
                || relationPath.startsWith("account/register")) {
            //直接跳过此拦截方法 走正常的逻辑 不进行拦截
            return;
        }

        //从Headers中取出第一个token节点
        String token = requestContext.getHeaders().getFirst("token");
        if (!Strings.isNullOrEmpty(token)) {

            //查询自己的信息
            final User self = UserFactory.findByToken(token);
            if (self != null) {

                //给当前请求添加一个上下文
                requestContext.setSecurityContext(new SecurityContext() {
                    //主体部分
                    @Override
                    public Principal getUserPrincipal() {
                        //User 实现 Principal接口
                        return self;
                    }

                    //是否给请求者 设置权限
                    @Override
                    public boolean isUserInRole(String role) {
                        //可以在这里写入用户的权限 role是权限名
                        //可以设置管理员权限等等
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        //默认false即可  一般是检查HTTPS
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        //不用理会
                        return null;
                    }
                });
                //写入上下文后就返回
                return;
            }

        }

        //直接返回一个账户需要登录的module
        ResponseModel model = ResponseModel.buildAccountError();
        //构建一个返回
        Response response = Response
                .status(Response.Status.OK)
                .entity(model)
                .build();

        //拦截，停止一个请求的继续下发，调用该方法后直接返回请求
        //不会走到Service中去
        requestContext.abortWith(response);
    }
}
