package com.example.ggxiaozhi.italker.fragment.account;


import android.content.Context;

import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.italker.R;

/**
 * 登录页面的Fragment
 */
public class LoginFragment extends Fragment {

    private AccountTrigger mAccountTrigger;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAccountTrigger = (AccountTrigger) context;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAccountTrigger.triggerView();
    }
}
