package com.example.ggxiaozhi.italker.fragment.account;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.Application;
import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.net.UploadHelper;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.fragment.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 更新用户信息的Fragment
 */
public class UpdateInfoFragment extends Fragment {

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //收到从Activity中传递过来的参数然后取出其中的值进行图片加载
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null)
                showPortrait(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getContext(), "不支持此图片格式", Toast.LENGTH_SHORT).show();
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void showPortrait(Uri resultUri) {
        Glide.with(getActivity())
                .load(resultUri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);

        final String localPath = resultUri.getPath();
        Log.e("TAG", "localPath: " + localPath);
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                String url = UploadHelper.uploadPortrait(localPath);
                Log.e("TAG", "url: " + url);
            }
        });
    }
}
