package com.example.ggxiaozhi.italker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.data.helper.AccountHelper;
import com.example.ggxiaozhi.factory.presistance.Account;
import com.igexin.sdk.PushConsts;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.italker
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：个推的消息接收器
 */

public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = MessageReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID:
                //当初始化的时候 获取设备Id
                onClientInit(bundle.getString("clientid"));
                Log.i(TAG, "clientid: " + bundle.getString("clientid"));
                break;
            case PushConsts.GET_MSG_DATA:
                byte[] payloads = bundle.getByteArray("payload");
                if (payloads != null) {
                    String message = new String(payloads);
                    Log.i(TAG, "message: " + message);
                    onMessageArrived(message);
                }
                break;
            default:
                Log.i(TAG, "OTHER: " + bundle.toString());
                break;
        }
    }


    /**
     * 当Id初始化的时候
     *
     * @param clientid 设备Id
     */
    private void onClientInit(String clientid) {
        Account.setPushId(clientid);
        if (Account.isLogin()) {
            //账户登录状态 进行一次PushId绑定
            //没有登录是不能绑定PushId的
            AccountHelper.bindPush(null);
        }
    }

    /**
     * 消息送达时候
     *
     * @param message 消息
     */

    private void onMessageArrived(String message) {
        //交给Factory处理
        Factory.dispatchMessage(message);
    }

}
