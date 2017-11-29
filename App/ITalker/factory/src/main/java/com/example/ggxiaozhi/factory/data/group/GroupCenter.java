package com.example.ggxiaozhi.factory.data.group;

import com.example.ggxiaozhi.factory.model.card.GroupCard;
import com.example.ggxiaozhi.factory.model.card.GroupMemberCard;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：群和群成员管理中心
 */

public interface GroupCenter {

    void groupDispatch(GroupCard... cards);
    void groupMemberDispatch(GroupMemberCard... cards);
}
