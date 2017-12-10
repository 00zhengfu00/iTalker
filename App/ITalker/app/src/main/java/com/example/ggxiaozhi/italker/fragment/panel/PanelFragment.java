package com.example.ggxiaozhi.italker.fragment.panel;


import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.common.tools.UiTool;
import com.example.ggxiaozhi.common.widget.GalleryView;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.face.FaceUtil;
import com.example.ggxiaozhi.italker.R;

import net.qiujuer.genius.ui.Ui;

import java.io.File;
import java.util.List;

/**
 * 表情 更多 语音Fragment
 */
public class PanelFragment extends Fragment {

    private View mFacePanel, mGalleryPanel, mRecordPanel;
    //输入框界面实现的Callback
    private PanelCallback mCallback;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_panel;

    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initFace(root);
        initRecord(root);
        inirMore(root);
    }

    /**
     * 初始化
     *
     * @param callback 获取输入文本框空间
     */
    public void setup(PanelCallback callback) {
        mCallback = callback;

    }

    private void initFace(View root) {
        View viewPanel = mFacePanel = root.findViewById(R.id.lay_panel_face);
        View backspace = viewPanel.findViewById(R.id.im_backspace);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除操作
                if (mCallback == null)
                    return;

                //模拟一次键盘删除点击   KeyEvent.KEYCODE_DEL 删除点击CODE flags KeyEvent.KEYCODE_ENDCALL结束标识
                KeyEvent keyEvent = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                EditText inputEditText = mCallback.getInputEditText();
                inputEditText.dispatchKeyEvent(keyEvent);
            }
        });
        ViewPager viewPager = (ViewPager) viewPanel.findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) viewPanel.findViewById(R.id.tab);
        //TabLayout与ViewPager进行绑定
        tabLayout.setupWithViewPager(viewPager);
        //每一个表情显示48dp
        final int minFaceSize = (int) Ui.dipToPx(getResources(), 48);
        //当前Activity的屏幕宽度
        final int totalScreenWidth = UiTool.getScreenWidth(getActivity());
        //每一行显示多少个表情
        final int spanCount = totalScreenWidth / minFaceSize;

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return FaceUtil.all(getContext()).size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.lay_face_content, container, false);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
                //设置Adapter
                List<FaceUtil.Bean> faces = FaceUtil.all(getContext()).get(position).faces;
                FaceAdapter adapter = new FaceAdapter(faces, new RecyclerAdapter.AdapterListenerImpl<FaceUtil.Bean>() {
                    @Override
                    public void onItemClick(RecyclerAdapter.ViewHolder holder, FaceUtil.Bean bean) {
                        super.onItemClick(holder, bean);
                        if (mCallback == null)
                            return;
                        //表情添加到输入框
                        EditText inputEditText = mCallback.getInputEditText();
                        FaceUtil.inputFace(getContext(), inputEditText.getText(), bean, (int) (inputEditText.getTextSize() + Ui.dipToPx(getResources(), 2)));
                    }
                });
                recyclerView.setAdapter(adapter);
                //添加进父布局
                container.addView(recyclerView);
                return recyclerView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return FaceUtil.all(getContext()).get(position).name;
            }
        });
    }

    private void initRecord(View root) {
        View viewRecord = mRecordPanel = root.findViewById(R.id.lay_panel_face);
    }

    private void inirMore(View root) {
        final View viewMore = mGalleryPanel = root.findViewById(R.id.lay_gallery_panel);
        final GalleryView galleryView = (GalleryView) viewMore.findViewById(R.id.view_gallery);
        final TextView selectedSize = (TextView) viewMore.findViewById(R.id.txt_gallery_select_count);
        galleryView.setup(getLoaderManager(), new GalleryView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {
                String resStr = getText(R.string.label_gallery_selected_size).toString();
                selectedSize.setText(String.format(resStr, count));
            }
        });
        Button send = (Button) viewMore.findViewById(R.id.btn_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleryOnClick(galleryView, galleryView.getSelectedPath());
            }
        });
    }

    /**
     * 点击发送图片 回调给聊天界面
     *
     * @param galleryView 图片选择控件
     * @param paths       选中的路径
     */
    private void onGalleryOnClick(GalleryView galleryView, String[] paths) {

        //通知给聊天界面
        //清空选中的状态
        galleryView.clear();
        //删除操作
        if (mCallback == null)
            return;
        mCallback.onSendGallery(paths);
    }

    public void showFace() {
        mRecordPanel.setVisibility(View.GONE);
        mGalleryPanel.setVisibility(View.GONE);
        mFacePanel.setVisibility(View.VISIBLE);
    }

    public void showRecord() {
        mRecordPanel.setVisibility(View.VISIBLE);
        mGalleryPanel.setVisibility(View.GONE);
        mFacePanel.setVisibility(View.GONE);
    }

    public void showMore() {
        mRecordPanel.setVisibility(View.GONE);
        mGalleryPanel.setVisibility(View.VISIBLE);
        mFacePanel.setVisibility(View.GONE);
    }

    public interface PanelCallback {
        EditText getInputEditText();

        //发送图片的回调 给聊天界面发送路径
        void onSendGallery(String[] paths);

        //发送语音的回调 给聊天界面发送语音文件以及时长
        void onRecordDone(File file, long time);
    }
}
