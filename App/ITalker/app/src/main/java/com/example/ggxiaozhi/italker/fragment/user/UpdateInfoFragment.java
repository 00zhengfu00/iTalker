package com.example.ggxiaozhi.italker.fragment.user;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.Application;
import com.example.ggxiaozhi.common.app.PresenterFragment;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.factory.presenter.user.UpdateInfoContract;
import com.example.ggxiaozhi.factory.presenter.user.UpdateInfoPresenter;
import com.example.ggxiaozhi.factory.presistance.Account;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.MainActivity;
import com.example.ggxiaozhi.italker.fragment.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.EditText;
import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 更新用户信息的Fragment
 */
public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter> implements UpdateInfoContract.View {

    /**
     * UI
     */
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @BindView(R.id.im_sex)
    ImageView mSex;
    @BindView(R.id.edit_desc)
    EditText mDesc;
    @BindView(R.id.loading)
    Loading mLoading;
    @BindView(R.id.btn_submit)
    Button mSubmit;

    /**
     * Data
     */
    private String protraitFilePath;
    private boolean isMan = true;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {

        new GalleryFragment()
                .setListener(new GalleryFragment.onSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {
                        //设置剪切功能的对象
                        UCrop.Options options = new UCrop.Options();
                        //设置图片的处理格式
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        //设置压缩后的图片精度(0-100)
                        options.setCompressionQuality(96);

                        File cacheDirFile = Application.getPortraitTmpFile();

                        //UCrop.of(Uri source,Uri destination)source原路径 destination保存的路径
                        UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(cacheDirFile))
                                .withAspectRatio(1, 1)//图片比例 1:1
                                .withMaxResultSize(520, 520)//返回最大的尺寸
                                .withOptions(options)//相关参数
                                .start(getActivity());
                    }
                })
                //show的时候建议使用getChildFragmentManager
                .show(getChildFragmentManager(), GalleryFragment.class.getName());
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "ConstantConditions"})
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //收到从Activity中传递过来的参数然后取出其中的值进行图片加载
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null)
                showPortrait(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Application.showToast(R.string.data_rsp_error_unknown);
        }
    }

    /**
     * 加载Url到头像中
     *
     * @param resultUri
     */
    private void showPortrait(Uri resultUri) {
        //得到头像本地地址
        protraitFilePath = resultUri.getPath();
        Glide.with(getActivity())
                .load(resultUri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String desc = mDesc.getText().toString();
        mPresenter.update(protraitFilePath, desc, isMan);
    }

    @OnClick(R.id.im_sex)
    void onSexClick() {
        isMan = !isMan;
        Drawable drawable = getResources().getDrawable(isMan ? R.drawable.ic_sex_man : R.drawable.ic_sex_woman);
        mSex.setImageDrawable(drawable);
        //设置背景层级 切换颜色
        mSex.getBackground().setLevel(isMan ? 0 : 1);
    }

    @Override
    public void showError(@StringRes int str) {
        super.showError(str);
        //当显示错误的时候触发 一定是结束了
        //停止loading
        mLoading.stop();
        //设置输入框可以点击
        mDesc.setEnabled(true);
        mPortrait.setEnabled(true);
        mSex.setEnabled(true);
        //提交按钮可以点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        //开始loading
        mLoading.start();
        //设置输入框不可以点击
        mDesc.setEnabled(false);
        mPortrait.setEnabled(false);
        mSex.setEnabled(false);
        //提交按钮不可以点击
        mSubmit.setEnabled(false);
    }

    @Override
    public void updateSuccessed() {
        //我们需要跳转到MainActivity界面
        MainActivity.show(getContext());
        //关闭当前页面
        getActivity().finish();
    }

    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }
}
