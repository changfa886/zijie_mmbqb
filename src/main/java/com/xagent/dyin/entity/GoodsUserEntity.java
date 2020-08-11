package com.xagent.dyin.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsUserEntity implements Serializable
{
    private static final long serialVersionUID = -7280867211244328118L;

    private Long id;
    private Long catagoryid;
    private String title;
    private Integer price;
    private String headimg;
    private String imgs;
    private String remark;

    private Long buyTime;
}
