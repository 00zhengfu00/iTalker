package com.example.ggxiaozhi.italker;

import android.widget.EditText;
import android.widget.TextView;

import com.example.ggxiaozhi.common.app.Activity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends Activity implements IView {

    @BindView(R.id.txt_result)
    TextView mTextView;

    @BindView(R.id.edit_query)
    EditText mEditText;

    private IPresenter mPresenter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @OnClick(R.id.btn_submit)
    void submit() {
        mPresenter = new Presenter(this);
        mPresenter.search();
    }

    @Override
    public String getInputString() {
        return mEditText.getText().toString();
    }

    @Override
    public void setResultString(String result) {
        mTextView.setText(result);
    }
}
