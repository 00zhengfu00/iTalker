package net.ggxiaozhi.web.italker.push;


import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.ggxiaozhi.web.italker.push.provider.AuthRequestFilter;
import net.ggxiaozhi.web.italker.push.provider.GsonProvider;
import net.ggxiaozhi.web.italker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

public class Application extends ResourceConfig {
    public Application() {
        //注册逻辑处理的包名
        //packages("net.ggxiaozhi.web.italker.push.service");
        packages(AccountService.class.getPackage().getName());

        //注册我们全局请求拦截器
        register(AuthRequestFilter.class);

        //注册Json解析器
        //register(JacksonJsonProvider.class);
        //替换解析器为Gson
        register(GsonProvider.class);
        //注册日志输出
        register(Logger.class);

    }
}
