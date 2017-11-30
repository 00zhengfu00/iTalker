package net.ggxiaozhi.web.italker.push.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import net.ggxiaozhi.web.italker.push.bean.api.base.PushModel;
import net.ggxiaozhi.web.italker.push.bean.db.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.utils
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：消息推送工具类
 */
public class PushDispatcher {
    //采用"Java SDK 快速入门"， "第二步 获取访问凭证 "中获得的应用配置，用户可以自行替换
    private static final String appId = "xxDC50pelAA2vFjSmuvDt5";
    private static final String appKey = "4FGsuHqPUd84B9thtlY4i";
    private static final String masterSecret = "XgwdW1Nuxg9vUD2NOjgNN2";
    private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";

    //要收到消息的人的内容和列表
    private List<BatchBean> beans = new ArrayList<>();
    private IGtPush pusher;

    public PushDispatcher() {
        pusher = new IGtPush(host, appKey, masterSecret);
    }

    /**
     * 给指定接受者发送消息
     *
     * @param receiver 接受者
     * @param model    消息
     * @return True成功
     */
    public boolean add(User receiver, PushModel model) {

        //基础检查 接受者必须存在PushId
        if (receiver == null || model == null || Strings.isNullOrEmpty(receiver.getPushId()))
            return false;
        String pushString = model.getPushString();
        if (Strings.isNullOrEmpty(pushString))
            return false;
        BatchBean batchBean = buildMessage(receiver.getPushId(), pushString);
        beans.add(batchBean);
        return true;
    }

    /**
     * 对要发送的数据进行格式化封装
     *
     * @param pushId     接受者的设备ID
     * @param pushString 要接收的内容
     * @return BatchBean
     */
    private BatchBean buildMessage(String pushId, String pushString) {
        SingleMessage message = new SingleMessage();
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(pushString);
        template.setTransmissionType(0); // 这个Type为int型，填写1则自动启动app
        message.setData(template);//把透传消息设置到单消息模板中
        message.setOffline(true);//是否允许离线发送
        message.setOfflineExpireTime(2 * 24 * 3600 * 1000);//离线小时保存时长 2天内都可以接到
        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(pushId);
        //返回一个封装
        return new BatchBean(message, target);
    }

    /**
     * 提交个推推送消息
     *
     * @return True成功
     */
    public boolean submit() {
        //构建打包工具类
        IBatch batch = pusher.getBatch();

        //标记是否有数据
        boolean haveData = false;
        for (BatchBean bean : beans) {
            try {
                batch.add(bean.message, bean.target);
                haveData = true;
            } catch (Exception e) {
                e.printStackTrace();
                //错误情况下为false
                haveData = false;

            }
        }
        if (!haveData)
            return false;
        IPushResult result = null;

        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //失败情况下 重新发送
                batch.retry();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        if (result != null) {
            try {
                Logger.getLogger("PushDispatcher")
                        .log(Level.INFO, (String) result.getResponse().get("result"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Logger.getLogger("PushDispatcher")
                .log(Level.WARNING, "推送服务器响应异常!!!");
        return false;
    }


    //给每个人发送消息的Bean封装
    private static class BatchBean {
        SingleMessage message;//推送消息内容
        Target target;//推送的目标

        public BatchBean(SingleMessage message, Target target) {
            this.message = message;
            this.target = target;
        }
    }
}
