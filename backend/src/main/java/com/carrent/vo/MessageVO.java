package com.carrent.vo;

import com.carrent.entity.Message;

/**
 * 留言视图对象：在留言基础上补充用户名
 */
public class MessageVO extends Message {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
