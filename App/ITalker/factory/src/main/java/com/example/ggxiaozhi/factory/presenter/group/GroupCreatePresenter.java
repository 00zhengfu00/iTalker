package com.example.ggxiaozhi.factory.presenter.group;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.R;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.data.helper.DbHelper;
import com.example.ggxiaozhi.factory.data.helper.GroupHelper;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.model.api.group.GroupCreateModel;
import com.example.ggxiaozhi.factory.model.card.GroupCard;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.model.db.view.UserSampleModel;
import com.example.ggxiaozhi.factory.net.UploadHelper;
import com.example.ggxiaozhi.factory.presenter.BaseRecyclerPresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：群创建的Presenter
 */

public class GroupCreatePresenter extends
        BaseRecyclerPresenter<GroupCreateContract.ViewModel, GroupCreateContract.View>
        implements GroupCreateContract.Presenter, DbHelper.ChangedListener<User>, DataSource.Callback<GroupCard> {

    private Set<String> usersId = new HashSet<>();

    public GroupCreatePresenter(GroupCreateContract.View view) {
        super(view);
        registerDbChangedListener();
    }

    @Override
    public void start() {
        super.start();
        //本地初始化联系人
        Factory.runOnAsync(loader);
    }

    @Override
    public void create(final String name, final String portrait, final String desc) {
        GroupCreateContract.View view = getView();
        view.showLoading();
        //1.判断参数
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(portrait) || TextUtils.isEmpty(desc)) {
            view.showError(R.string.label_group_create_invalid);
        }
        //2.上传图片
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                //失败直接返回
                String url = uploadPicture(portrait);
                if (TextUtils.isEmpty(url))
                    return;
                //3.请求接口
                GroupCreateModel model = new GroupCreateModel(name, desc, url, usersId);
                GroupHelper.create(model, GroupCreatePresenter.this);
            }
        });

        //4.接口回调
    }

    @Override
    public void changeSelect(GroupCreateContract.ViewModel model, boolean isSelected) {
        if (isSelected)
            usersId.add(model.mAuthor.getId());
        else
            usersId.remove(model.mAuthor.getId());
    }

    /**
     * 上传头像到阿里云对象存储
     */
    private String uploadPicture(String path) {
        //上传头像到阿里云对象存储
        String url = UploadHelper.uploadPortrait(path);
        if (TextUtils.isEmpty(url)) {//上传失败
            //保证在主线程
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    GroupCreateContract.View view = getView();
                    view.showError(R.string.data_upload_error);
                }
            });
        }
        return url;
    }

    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            List<UserSampleModel> sampleContact = UserHelper.getSampleContact();
            if (sampleContact.size() == 0) {
                UserHelper.refreshContacts();
                return;
            }

            List<GroupCreateContract.ViewModel> viewModels = new ArrayList<>();
            for (UserSampleModel sampleModel : sampleContact) {
                GroupCreateContract.ViewModel viewModel = new GroupCreateContract.ViewModel();
                viewModel.mAuthor = sampleModel;
                viewModels.add(viewModel);
            }
            refreshData(viewModels);
        }
    };

    private void registerDbChangedListener() {
        DbHelper.addChangedListener(User.class, this);
    }

    @Override
    public void onDataSave(User... users) {
        List<GroupCreateContract.ViewModel> viewModels = new ArrayList<>();
        for (User sampleModel : users) {
            GroupCreateContract.ViewModel viewModel = new GroupCreateContract.ViewModel();
            viewModel.mAuthor = sampleModel;
            viewModels.add(viewModel);
        }
        refreshData(viewModels);
    }

    @Override
    public void onDataDelete(User... users) {

    }

    @Override
    public void onDataLoaded(GroupCard card) {
        //保证在主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = getView();
                view.onCreateSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int str) {
        //保证在主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = getView();
                view.showError(str);
            }
        });
    }
}
