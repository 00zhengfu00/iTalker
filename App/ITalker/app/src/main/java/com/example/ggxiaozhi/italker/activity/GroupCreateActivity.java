package com.example.ggxiaozhi.italker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggxiaozhi.common.app.Application;
import com.example.ggxiaozhi.common.app.PresenterToolBarActivity;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.factory.presenter.group.GroupCreateContract;
import com.example.ggxiaozhi.factory.presenter.group.GroupCreatePresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.fragment.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class GroupCreateActivity extends PresenterToolBarActivity<GroupCreateContract.Presenter>
        implements GroupCreateContract.View {

    /**
     * UI
     */
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.edit_desc)
    EditText mDesc;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    /**
     * Data
     */
    private RecyclerAdapter<GroupCreateContract.ViewModel> mAdapter;
    private String protraitFilePath;

    public static void show(Context context) {
        context.startActivity(new Intent(context, GroupCreateActivity.class));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter();
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListenerImpl<GroupCreateContract.ViewModel>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, GroupCreateContract.ViewModel model) {
                super.onItemClick(holder, model);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_create;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create) {
            hideSoftKeyboard();
            //创建群
            onCreateClick();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCreateClick() {
        hideSoftKeyboard();
        String name = mName.getText().toString().trim();
        String desc = mDesc.getText().toString().trim();
        mPresenter.create(name, protraitFilePath,desc);
    }

    @Override
    public void onCreateSuccess() {
        //请求成功的回调
        hideLoad();
        Application.showToast(R.string.label_group_create_succeed);
        finish();
    }


    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        hideSoftKeyboard();
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
                                .start(GroupCreateActivity.this);
                    }
                }).show(getSupportFragmentManager(), GalleryFragment.class.getName());
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
        Glide.with(this)
                .load(resultUri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftKeyboard() {
        //拿到当前焦点View
        View view = getCurrentFocus();
        if (view == null)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected GroupCreateContract.Presenter initPresenter() {
        return new GroupCreatePresenter(this);
    }

    @Override
    public RecyclerAdapter<GroupCreateContract.ViewModel> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //Adapter刷新时
        hideLoad();
    }

    private class Adapter extends RecyclerAdapter<GroupCreateContract.ViewModel> {

        @Override
        protected int getItemViewType(int position, GroupCreateContract.ViewModel viewModel) {
            return R.layout.cell_group_create_contact;
        }

        @Override
        protected ViewHolder<GroupCreateContract.ViewModel> onCreateViewHolder(View root, int viewType) {
            return new GroupCreateActivity.ViewHolder(root);
        }
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCreateContract.ViewModel> {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.cb_select)
        CheckBox mSelect;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @OnCheckedChanged(R.id.cb_select)
        void onCheckedChanged(boolean checked) {
            // 进行状态更改
            mPresenter.changeSelect(mData, checked);
        }

        @Override
        public void onBind(GroupCreateContract.ViewModel viewModel, int position) {
            mPortrait.setup(Glide.with(GroupCreateActivity.this), viewModel.mAuthor);
            mName.setText(viewModel.mAuthor.getName());
            mSelect.setChecked(viewModel.isSelected);

        }
    }
}
