package net.ggxiaozhi.web.italker.push.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.service
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
     * 功能   ：常用Hibernate工具类的封装
 * 主要是初始化全局SessionFactory管理session并用将session提供给外界调用
 */
public class Hib {
    // 全局SessionFactory
    private static SessionFactory sessionFactory;

    static {
        // 静态初始化sessionFactory
        init();
    }

    private static void init() {
        // 从hibernate.cfg.xml文件初始化
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            // build 一个sessionFactory
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            // 错误则打印输出，并销毁
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    /**
     * 获取全局的SessionFactory
     *
     * @return SessionFactory
     */
    public static SessionFactory sessionFactory() {
        return sessionFactory;
    }

    /**
     * 从SessionFactory中得到一个Session会话
     *
     * @return Session
     */
    public static Session session() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 关闭sessionFactory
     */
    public static void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }


    /**
     * 用户实际操作的一个接口
     * 无返回值
     */
    public interface QueryOnOnly {
        void query(Session session);
    }


    /**
     * 简化事务操作的一个工具方法
     *
     * @param query
     */
    public static void queryOnOnly(QueryOnOnly query) {

        //重新开启一个Session 避免提示重复使用相同的Session
        Session session = sessionFactory().openSession();
        //开启一个事务
        final Transaction transaction = session.beginTransaction();
        try {
            //调用传进了来的接口，并调用接口中的方法 把Session传进方法中
            //以便提供给调用者利用Session去进行数据库的操作
            query.query(session);
            //提交事务
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                //保存失败的情况下进行事务回滚
                transaction.rollback();
            } catch (RuntimeException e1) {
                e1.printStackTrace();
            }
        } finally {
            //关闭session
            session.close();
        }
    }


    /**
     * 用户实际操作的一个接口
     * 具有返回值T
     *
     * @param <T> 表示查询的实体
     */
    public interface Query<T> {
        T query(Session session);
    }

    /**
     * 简化事务操作的一个工具方法
     * 有返回值
     *
     * @param query 用于接收session
     * @param <T>   表示的实体
     * @return
     */
    public static <T> T query(Query<T> query) {

        //重新开启一个Session 避免提示重复使用相同的Session
        Session session = sessionFactory().openSession();
        //开启一个事务
        final Transaction transaction = session.beginTransaction();
        T t = null;
        try {
            //调用传进了来的接口，并调用接口中的方法 把Session传进方法中
            //以便提供给调用者利用Session去进行数据库的操作
            t = query.query(session);
            //提交事务
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                //保存失败的情况下进行事务回滚
                transaction.rollback();
            } catch (RuntimeException e1) {
                e1.printStackTrace();
            }

        } finally {
            //关闭session
            session.close();
        }
        return t;
    }
}
