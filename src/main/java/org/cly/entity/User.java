package org.cly.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String id;

    private String objectId;

    private String type;

    private String content;

    private String notifier;

    private Date createTime;

    private Date updateTime;

    private Byte isRead;

    private Byte isAt;

    private Byte isNew;

    private Byte isValid;

}