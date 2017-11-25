package com.example.ggxiaozhi.factory.presenter.contact;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.util.DiffUtil;

import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.model.db.AppDatabase;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.model.db.User_Table;
import com.example.ggxiaozhi.factory.presenter.BasePresenter;
import com.example.ggxiaozhi.factory.presistance.Account;
import com.example.ggxiaozhi.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.contact
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ： 联系人的Presenter
 */

public class ContactPresenter extends BasePresenter<ContactContract.View> implements ContactContract.Presenter {

    public ContactPresenter(ContactContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        //查询操作 这里是查询本地数据库
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<User>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction,
                                                  @NonNull List<User> tResult) {
                        //将数据传递给页面的Adapter 这里是替换原来的数据 刷新整个列表
                        getView().getAdapter().replace(tResult);
                        //数据加载完成的页面回调
                        getView().onAdapterDataChanged();
                    }
                })
                .execute();

        //服务器拉取联系人
        UserHelper.refreshContacts(new DataSource.Callback<List<UserCard>>() {
            @Override
            public void onDataNotAvailable(@StringRes int str) {
                //网络请求失败  我们不管 因为本地有数据
            }

            @Override
            public void onDataLoaded(List<UserCard> userCards) {

                final List<User> users = new ArrayList<>();
                for (UserCard userCard : userCards) {
                    users.add(userCard.build());
                }

                DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
                definition.beginTransactionAsync(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        FlowManager
                                .getModelAdapter(User.class)
                                .saveAll(users);
                    }
                }).build().execute();

                //TODO 存在的问题：
                //1.在关注操作时我们同事存储到了本地数据库 但是没有刷新联系人
                //2.如果数据库刷新 或者网络刷新 最终都是全局刷新(我们的理想是逐条刷新最新新的数据)
                //3.本地刷新和网络刷新 在添加到界面的时候可能会冲突 导致数据显示异常
                // -->本地拉取数据慢 网络拉取快 那么此时本地老的数据可能会覆盖网络数据 但是网络数据才是最新的是我们想要的
                //4.如何识别在数据库中已有的数据
            }
        });
    }

    public void diff(List<User> newList, List<User> oldList) {
        DiffUiDataCallback callback=new DiffUiDataCallback<User>();
        DiffUtil.DiffResult result=DiffUtil.calculateDiff(callback);
        result.dispatchUpdatesTo(getView().getAdapter());
    }
}
