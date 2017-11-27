package com.example.ggxiaozhi.factory.data.helper;

import com.example.ggxiaozhi.factory.model.db.AppDatabase;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.GroupMember;
import com.example.ggxiaozhi.factory.model.db.Group_Table;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.Session;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.helper
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：数据库辅助工具类封装 主要是封装数据库的增删改-->查询由用户自己去实现具体根据什么查询
 */

public class DbHelper {

    private static final DbHelper instance;

    static {
        instance = new DbHelper();
    }

    private DbHelper() {
    }

    /**
     * 观察者集合
     * Class<?> 观察的表
     * Set<ChangedListener> 每一个表对应的观察者有很多
     */
    private final Map<Class<?>, Set<ChangedListener>> mClassSetMap = new HashMap<>();

    /**
     * 得到当点表的所有监听
     *
     * @param tClass 表对应的Class信息
     * @return 返回这个标下的所有监听集合
     */
    private <Model extends BaseModel> Set<ChangedListener> getChangedListener(Class<Model> tClass) {
        if (mClassSetMap.containsKey(tClass)) {
            return mClassSetMap.get(tClass);

        }
        return null;
    }

    /**
     * 给某个数据库表添加一个观察者监听
     *
     * @param tClass    表对应的Class信息
     * @param listeners 要添加的监听
     */
    public static <Model extends BaseModel> void addChangedListener(Class<Model> tClass, ChangedListener<Model> listeners) {
        //得到当前数据库表 监听的所以集合
        Set<ChangedListener> listenerSet = instance.getChangedListener(tClass);
        if (listenerSet == null) {
            //如果这个数据库的表还没有添加观察者监听
            listenerSet = new HashSet<>();
            // 那么我们就创建一个集合 加入传入的观察者监听
            instance.mClassSetMap.put(tClass, listenerSet);
        }
        listenerSet.add(listeners);


    }

    /**
     * 给某个数据库表删除一个观察者监听
     *
     * @param tClass    表对应的Class信息
     * @param listeners 要添加的监听
     */
    public static <Model extends BaseModel> void removeChangedListener(Class<Model> tClass, ChangedListener<Model> listeners) {
        //得到当前数据库表 监听的所以集合
        Set<ChangedListener> listenerSet = instance.getChangedListener(tClass);
        if (listenerSet == null) {
            //表示没有添加过 所以无法删除
            return;
        }
        listenerSet.remove(listeners);
    }

    /**
     * 封装数据库保存与修改统一的方法
     *
     * @param tClass  传入model对应的数据库的.class
     * @param models  数据库储存的model 对象
     * @param <Model> 数据库储存的model 类 限定条件是继承BaseModel
     */
    public static <Model extends BaseModel> void save(final Class<Model> tClass, final Model... models) {

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                //保存或修改
                adapter.saveAll(Arrays.asList(models));
                //唤醒通知
                instance.notifySave(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 封装数据删除的方法
     *
     * @param tClass  传入model对应的数据库的.class
     * @param models  数据库储存的model 对象
     * @param <Model> 数据库储存的model 类 限定条件是继承BaseModel
     */
    public static <Model extends BaseModel> void delete(final Class<Model> tClass, final Model... models) {

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                //删除
                adapter.deleteAll(Arrays.asList(models));
                //唤醒通知
                instance.notifyDelete(tClass, models);
            }//同步执行
        }).build().execute();
    }

    /**
     * 数据库保存修改后通知用户
     *
     * @param tClass  传入model对应的数据库的.class
     * @param models  数据库储存的model 对象
     * @param <Model> 数据库储存的model 类 限定条件是继承BaseModel
     */
    @SuppressWarnings("unchecked")
    private final <Model extends BaseModel> void notifySave(final Class<Model> tClass, final Model... models) {
        Set<ChangedListener> listenerSet = getChangedListener(tClass);
        if (listenerSet != null && listenerSet.size() > 0) {
            for (ChangedListener<Model> listener : listenerSet) {
                listener.notifyChangedSave(models);
            }

            //例外情况
            if (GroupMember.class.equals(tClass)) {
                // 群成员变更，需要通知对应群信息更新
                updateGroup((GroupMember[]) models);
            } else if (Message.class.equals(tClass)) {
                // 消息变化，应该通知会话列表更新
                updateSession((Message[]) models);
            }
        }
    }


    /**
     * 从成员中找出成员对应的群，并对群进行更新
     *
     * @param members 群成员列表
     */
    private void updateGroup(GroupMember... members) {
        //不重复集合
        final Set<String> groupIds = new HashSet<>();
        for (GroupMember groupMember : members) {
            groupIds.add(groupMember.getGroup().getId());
        }

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //查询groupIds和Group表中的id列中有有条数据是相同的
                List<Group> groups = SQLite.select()
                        .from(Group.class)
                        .where(Group_Table.id.in(groupIds))
                        .queryList();
                // 调用直接进行一次通知分发
                instance.notifySave(Group.class, groups.toArray(new Group[0]));
            }//同步执行
        }).build().execute();

    }

    /**
     * 从消息列表中，筛选出对应的会话，并对会话进行更新
     *
     * @param messages Message列表
     */
    private void updateSession(Message... messages) {
        // 标示一个Session的唯一性
        final Set<Session.Identify> identifySet = new HashSet<>();
        for (Message message : messages) {
            Session.Identify identify = Session.createSessionIdentify(message);
            identifySet.add(identify);
        }
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Session> adapter = FlowManager.getModelAdapter(Session.class);
                Session[] sessions = new Session[identifySet.size()];
                int index = 0;
                for (Session.Identify identify : identifySet) {
                    Session session = SessionHelper.findFromLocal(identify.id);
                    //表示第一次会话
                    if (session == null) {
                        session = new Session(identify);
                    }
                    // 把会话，刷新到当前Message的最新状态
                    session.refreshToNow();
                    //保存当但Session信息
                    session.save();
                    sessions[index++] = session;
                }
                // 调用直接进行一次通知分发
                instance.notifySave(Session.class, sessions);
            }//同步执行
        }).build().execute();

    }

    /**
     * 数据库删除后通知用户
     *
     * @param tClass  传入model对应的数据库的.class
     * @param models  数据库储存的model 对象
     * @param <Model> 数据库储存的model 类 限定条件是继承BaseModel
     */
    private final <Model extends BaseModel> void notifyDelete(final Class<Model> tClass, final Model... models) {
        //TODO 数据库保存后通知用户


    }

    @SuppressWarnings("unchecked")
    public interface ChangedListener<Data extends BaseModel> {
        //数据存储完成后通知外界
        void notifyChangedSave(Data... datas);

        //数据删除完成后通知外界
        void notifyChangedDelete(Data... datas);
    }
}
