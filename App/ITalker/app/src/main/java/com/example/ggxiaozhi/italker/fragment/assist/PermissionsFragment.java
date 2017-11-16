package com.example.ggxiaozhi.italker.fragment.assist;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ggxiaozhi.common.app.Application;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.fragment.media.GalleryFragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionsFragment extends BottomSheetDialogFragment
        implements EasyPermissions.PermissionCallbacks {

    public static final int RC = 0x100;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        GalleryFragment.TransStatusBottomSheetDialog dialog = new GalleryFragment.TransStatusBottomSheetDialog(getContext());
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_permissions, container, false);
        root.findViewById(R.id.btn_submit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPerm();
                    }
                });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshState(getView());
    }

    /**
     * 刷新我们布局中的图片状态
     *
     * @param root 根布局
     */
    private void refreshState(View root) {
        if (root == null)
            return;
        root.findViewById(R.id.im_state_permission_network).
                setVisibility(haveNetworkPerm(getContext()) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_read).
                setVisibility(haveReadPerm(getContext()) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_write).
                setVisibility(haveWritePerm(getContext()) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_record_audio).
                setVisibility(haveRecordAucioPerm(getContext()) ? View.VISIBLE : View.GONE);

    }

    /**
     * 获取是否有网络权限
     *
     * @param context 上下文
     * @return True 则有
     */
    private static boolean haveNetworkPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        //检查权限是否已经申请
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 获取是否有读取储存的权限
     *
     * @param context 上下文
     * @return True 则有
     */
    private static boolean haveReadPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        //检查权限是否已经申请
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 获取是否有写入内存的权限
     *
     * @param context 上下文
     * @return True 则有
     */
    private static boolean haveWritePerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //检查权限是否已经申请
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 获取是否有录音的权限
     *
     * @param context 上下文
     * @return True 则有
     */
    private static boolean haveRecordAucioPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        //检查权限是否已经申请
        return EasyPermissions.hasPermissions(context, perms);
    }

    //私有的show方法
    private static void show(FragmentManager manager) {
        new PermissionsFragment().show(manager, PermissionsFragment.class.getName());
    }

    /**
     * 检查是否具体所有权限
     *
     * @param context 上下文
     * @return True 则有所有权限
     */
    public static boolean haveAllPerms(Context context, FragmentManager manager) {
        //检查是否具有所有权限
        boolean isHaveAllPerms = haveNetworkPerm(context) &&
                haveReadPerm(context) && haveWritePerm(context) && haveRecordAucioPerm(context);
        //如果没有权限则显示授权页面
        if (!isHaveAllPerms) {
            show(manager);
        }
        return isHaveAllPerms;
    }

    @AfterPermissionGranted(RC)
    private void requestPerm() {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO

        };
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Application.showToast(R.string.label_permission_ok);
            //Fragment 中调用getView得到根布局，前提是在onCreateView()方法之后
            refreshState(getView());
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.title_assist_permissions), RC, perms);
        }

    }

    @Override//申请成功的回调
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override//申请失败的回调
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //如果权限存在没有申请成功的权限存在，则弹出弹出框 用户自己去设置界面开启权限
            new AppSettingsDialog
                    .Builder(this)
                    .build()
                    .show();
        }
    }

    /**
     * 申请权限的时候回调的方法，在这个方法中把对应的的申请状态交给EasyPermissions框架
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //传递对应的参数，并且告知接收权限的处理者是我自己
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
