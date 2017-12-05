package net.ggxiaozhi.web.italker.push.bean.db;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.db
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：申请添加好友(或群)的记录表
 */

@Entity//表明为实体类
@Table(name = "TB_APPLY")//对应数据库中的表
public class  Apply {

    public static final int TYPE_ADD_USER = 1; // 添加好友
    public static final int TYPE_ADD_GROUP = 2; // 加入群

    @Id//标明字段含义
    @PrimaryKeyJoinColumn//这是一个主键
    @GeneratedValue(generator = "uuid")//主键生成存储类型为UUID
    //把uuid的生成器定义为uuid2,uuid2是将常规的uuid.toString，这样方便我们处理
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //列的约束：不允许更改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //描述部分 对我们申请的信息做描述
    //eg：我想加你为好友！！！
    @Column(nullable = false)
    private String desciption;

    //附件 可为空
    //可以 附带图片地址 或其他
    @Column
    private String attach;

    //当前申请的类型
    @Column(nullable = false)
    private int type;

    //目标Id， 不进行强关联，不建立主外键关系
    //type->TYPE_ADD_USER:User_Id;
    //type->TYPE_ADD_GROUP:Group_Id;
    @Column(nullable = false)
    private String targetId;

    //定义为创建时间戳 在创建时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳 在创建时就写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //申请人
    //可为空 (因为申请人可以为User也可以为系统信息，当系统信息时就是空的情况)
    @ManyToOne
    @JoinColumn(name = "applicantId")
    private User applicant;
    @Column(updatable = false, insertable = false)
    private String applicantId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }
}
