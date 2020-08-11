package com.xagent.dyin.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class EcharEntity implements Serializable
{
    private static final long serialVersionUID = 1157848230731603413L;

    private long xtime;
    private int yvalue;

    public EcharEntity(){}
    public EcharEntity(long xtime, int yvalue)
    {
        this.xtime = xtime;
        this.yvalue = yvalue;
    }
}
