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
import android.widget.EditText;

import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.common.tools.UiTool;
import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.face.FaceUtil;
import com.example.ggxiaozhi.italker.R;

import net.qiujuer.genius.ui.Ui;

import java.util.List;

/**
 * 表情 更多 语音Fragment
 */
public class PanelFragment extends Fragment {

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
        View view = root.findViewById(R.id.lay_panel_face);
        View backspace = view.findViewById(R.id.im_backspace);
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
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab);
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

    }

    private void inirMore(View root) {

    }

    public void showFace() {

    }

    public void showRecord() {

    }

    public void showMore() {

    }

    public interface PanelCallback {
        EditText getInputEditText();
    }
}
