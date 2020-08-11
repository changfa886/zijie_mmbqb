package com.xagent.dyin.entity;

import com.xagent.dyin.utils.ErrorCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class   MyResp implements Serializable
{
    private static final long serialVersionUID = -820535731708169856L;

    public int code;
    public String msg;
    public Map<String, Object> data;

    public MyResp(){}

    public MyResp(int code, String msg)
    {
        this.code = code;
        this.msg = msg;
        this.data = new HashMap<String, Object>();
    }

    public MyResp(int code)
    {
        this.code = code;
        this.msg = ErrorCode.respMap.get(code)==null ? "" : (String)(ErrorCode.respMap.get(code));
        this.data = new HashMap<String, Object>();
    }
}
