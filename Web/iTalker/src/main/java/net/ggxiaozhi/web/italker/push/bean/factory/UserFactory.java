package net.ggxiaozhi.web.italker.push.bean.factory;

import com.google.common.base.Strings;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import net.ggxiaozhi.web.italker.push.bean.db.UserFollow;
import net.ggxiaozhi.web.italker.push.utils.Hib;
import net.ggxiaozhi.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * factory包下为全部业务逻辑的处理
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.factory
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：User相关的业务逻辑处理
 */
public class UserFactory {

    /**
     * 通过token查询用户信息
     * 只能当前登录用户自己使用 查询信息时个人信息，非他人信息
     *
     * @param token 当前用户的token
     * @return 返回查询的user
     */
    public static User findByToken(String token) {
        //此处可以用lambda表达式 由于不熟练暂时不转化
        //Alt+Enter -> new Hib.Query<User>()与user转化lambda
        return Hib.query(new Hib.Query<User>() {
            @Override
            public User query(Session session) {
                User user = (User) session.createQuery("from User where token=:token")
                        .setParameter("token", token).uniqueResult();
                return user;
            }
        });
    }

    /**
     * 通过phone查询是否存在用户
     *
     * @param phone 注册的手机号
     * @return 返回查询的user
     */
    public static User findByPhone(String phone) {
        //此处可以用lambda表达式 由于不熟练暂时不转化
        //Alt+Enter -> new Hib.Query<User>()与user转化lambda
        return Hib.query(new Hib.Query<User>() {
            @Override
            public User query(Session session) {
                User user = (User) session.createQuery("from User where phone=:inPhone")
                        .setParameter("inPhone", phone).uniqueResult();
                return user;
            }
        });
    }

    /**
     * 通过name查询是否存在用户
     *
     * @param name 注册的手机号
     * @return 返回查询的user
     */
    public static User findByName(String name) {
        //此处可以用lambda表达式 由于不熟练暂时不转化
        //Alt+Enter -> new Hib.Query<User>()与user转化lambda
        return Hib.query(new Hib.Query<User>() {
            @Override
            public User query(Session session) {
                User user = (User) session.createQuery("from User where name=:name")
                        .setParameter("name", name).uniqueResult();
                return user;
            }
        });
    }

    /**
     * 通过Id查询是否存在用户
     *
     * @param id 传入查询的Id
     * @return 返回查询的user
     */
    public static User findById(String id) {
        //通过Id(主键)查询更方便
        return Hib.query(session -> session.get(User.class, id));
    }

    /**
     * 更新(保存)用户信息到数据库
     *
     * @param user 修改信息的user
     * @return 保存数据库中后的user
     */
    public static User update(User user) {
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }

    /**
     * 用户注册
     * 注册的操作要先写入数据库，并返回数据库中的User信息
     * <p>
     * 调用此方法之前已经进行了重复的判断
     * 进入这个方法就说明用户信息没有重复的可以进行注册成功
     *
     * @param account  账户(这里为手机号)
     * @param password 密码
     * @param name     用户名
     * @return user 返回一个带Token的User
     */
    public static User register(String account, String password, String name) {

        account = account.trim();
        password = encodePassword(password);

        User user = createUser(account, password, name);

        if (user != null) {
            user = login(user);
        }
        return user;
        /*//账户就是手机号
        user.setPhone(account);
        user.setPassword(password);
        user.setName(name);

        *//*进行数据库的操作*//*
        //首先创建一个回话
        Session session = Hib.session();
        //创建一个事务
        session.beginTransaction();
        try {
            //保存操作
            session.save(user);
            //提交事务
            session.getTransaction().commit();
            return user;
        } catch (Exception e) {
            //保存失败的情况下进行事务回滚
            session.getTransaction().rollback();
            return null;
        }
*/
    }

    /**
     * 注册部分的新建用户的逻辑
     *
     * @param account  手机号
     * @param password 加密后的密码
     * @param name     用户名
     * @return 返回一个用户
     */
    public static User createUser(String account, String password, String name) {
        User user = new User();
        //账户就是手机号
        user.setPhone(account);
        user.setPassword(password);
        user.setName(name);

        // 数据库存储
        return Hib.query(session -> {
            session.save(user);
            return user;
        });

    }

    /**
     * 登录的逻辑：首先创建密码/登录账号--->检查账号是否存在/验证账户密码是否正确login(String account, String password)
     * --->验证成功后给User一个Token以便记录跟踪用户的状态login(User user)
     * --->返回一个带Token的User
     * <p>
     * 把一个用户进行登录操作 这个方法是private不提供给外界
     * 本质上是进行token的操作 每次用户登录成功都会随机生成一个token
     * token的作用的类似于cookie 记录客户端的状态
     *
     * @param user
     * @return 带Token的user
     */
    private static User login(User user) {

        //使用一个随机的UUID充当Token
        String newToken = UUID.randomUUID().toString();
        newToken = TextUtil.encodeBase64(newToken);
        user.setToken(newToken);

        //将新增加token的user存入到数据库或更新
        return update(user);
    }


    /**
     * 使用账号和密码进行登录
     *
     * @param account
     * @param password
     * @return
     */
    public static User login(String account, String password) {
        String accountStr = account.trim();
        String passwordStr = encodePassword(password);

        User user = Hib.query(new Hib.Query<User>() {
            @Override
            public User query(Session session) {
                return (User) session.createQuery
                        ("from User where phone=:phone and password=:password")
                        .setParameter("phone", accountStr)
                        .setParameter("password", passwordStr)
                        .uniqueResult();
            }
        });
        //说明登录成功用户存在
        if (user != null) {
            login(user);
        }
        return user;
    }

    /**
     * 给当前的用户绑定PushId
     *
     * @param user   自己的User 即当前在线登录的User
     * @param pushId 自己设备的PushId
     * @return User
     */
    public static User bindPushId(User user, String pushId) {

        if (Strings.isNullOrEmpty(pushId))
            return null;

        //第一步：，查询是否有其他账户绑定了这个设备(当你登录账户1后退出账户再登录账户2，此时2个账户绑定的就是同一个设备Id)
        //查询的列表不能包括自己
        Hib.queryOnOnly(session -> {
            //lower toLowerCase() 转化小写
            @SuppressWarnings({"unchecked"})
            List<User> userList = (List<User>) session.createQuery("from User where lower(pushId)=:pushId and id!=:userId ")
                    .setParameter("pushId", pushId.toLowerCase())
                    .setParameter("userId", user.getId())
                    .list();
            // 第二步：取消其他账户绑定，避免推送混乱
            for (User u : userList) {
                //更新为null
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        });

        //第三步：判断pushId
        //equalsIgnoreCase 忽略大小写比较
        if (pushId.equalsIgnoreCase(user.getPushId())) {
            //如果当前User已经绑定过了这个设备Id,那么不需要额外绑定
            return user;
        } else {
            //如果当前账户之前在数据库中存储的设备Id，和需要绑定的设备Id不同
            //那么需要单点登录，让之前的设备退出账户。
            //给之前的设备推送一条退出的消息
            if (!Strings.isNullOrEmpty(user.getPushId())) {
                //推送一个退出消息 这里不用传入的User回去pushId是防止当推送回去时用户已经更细了新的PushID
                PushFactory.pushLogout(user,user.getPushId());
            }
            //第四步：更新设备Id
            //更新新的设备Id
            user.setPushId(pushId);
            return update(user);
        }


    }

    /**
     * 加密密码
     *
     * @param password 原文
     * @return 密文
     */
    private static String encodePassword(String password) {
        //密码去除空格
        password = password.trim();
        //进行MD5非对称加密 加盐更安全 (加盐的话 盐也要存储)
        //加盐:指的是可以随机在字符串上加上当前的时间值或是其他随机的数值
        password = TextUtil.getMD5(password);

        //再进行一次对称的Base64加密 当然可以采取加盐的方案
        return TextUtil.encodeBase64(password);
    }

    /**
     * 获取我的联系人列表
     *
     * @param self 当前用户自己
     * @return 联系人列表集合
     */
    public static List<User> contacts(User self) {
        return Hib.query(session -> {
            //重新加载一次用户信息到self中 和当前sessio绑定 不可直接获取 因为是懒加载 所以之前数据没有加载
            //同时session在上次查询完成后会随着查询一起释放掉 所以需要重新load与sess绑定
            session.load(self, self.getId());
            //获取我关注的人
            Set<UserFollow> follows = self.getFollowing();
            //使用Java8 简写方式
            return follows.stream()
                    .map(UserFollow::getTarget)
                    .collect(Collectors.toList());
        });
    }

    /**
     * 关注人的方法
     *
     * @param origin 发起者
     * @param target 被关注的人
     * @param alias  被关注人的别名
     * @return 被关注人的信息
     */
    public static User follow(final User origin, final User target, final String alias) {
        //查询两个人的关注状态
        UserFollow follow = getUserFollow(origin, target);
        if (follow != null) {//说明已经关注了
            return follow.getTarget();//直接返回
        }
        return Hib.query((Session session) -> {
            //想要操作懒加载的数据 需要重新load一次
            session.load(origin, origin.getId());
            session.load(target, target.getId());

            //  我关注人的时候 同时他也要关注我
            //  所以需要添加两条UserFollow数据
            UserFollow originFollow = new UserFollow();
            originFollow.setOrigin(origin);
            originFollow.setTarget(target);
            //备注是我对他的备注 默认他对我们是没有备注的
            originFollow.setAlias(alias);

            UserFollow targetFollow = new UserFollow();
            targetFollow.setOrigin(target);
            targetFollow.setTarget(origin);

            //保存数据到数据库
            session.save(originFollow);
            session.save(targetFollow);

            return target;

        });
    }

    /**
     * 查询两个人是否已经关注
     *
     * @param origin 发起者
     * @param target 被关注的人
     * @return 返回中间类的UserFollow
     */
    public static UserFollow getUserFollow(final User origin, final User target) {
        return Hib.query(session ->
                (UserFollow) session.createQuery("from UserFollow where originId=:originId and targetId=:targetId")
                        .setParameter("originId", origin.getId())
                        .setParameter("targetId", target.getId())
                        .setMaxResults(1)//最多返回一条数据
                        //唯一结果返回查询
                        .uniqueResult());
    }

    /**
     * 搜索联系人的实现
     *
     * @param name 查询的name 允许为空
     * @return 查询到的用户的集合 如果name为空 则返回最近的用户
     */
    @SuppressWarnings("unchecked")
    public static List<User> search(String name) {
        if (Strings.isNullOrEmpty(name))
            name = "";//保证不能为空的情况 减少后面的判断和额外一些错误
        final String searchName = "%" + name + "%";//模糊匹配

        return Hib.query(session -> {
            //查询条件：name忽略大小写 并且使用like(模糊)查询
            //头像和描述必须已经完善了才能返回
            return (List<User>) session.createQuery("from User where lower(name) like :name and portrait is not null and description is not null ")
                    .setParameter("name", searchName)
                    .setMaxResults(20)//返回的数据最多20条
                    .list();
        });
    }
}
