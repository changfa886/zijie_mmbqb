package com.xagent.dyin.utils;

import java.util.HashMap;
import java.util.Map;

public class ErrorCode
{
    public final static Map respMap = new HashMap<Integer, String>(){{
        put(0, "操作成功");
        put(1000, "执行错误");
        put(1001, "登录超时,请重新登录");
        put(1003, "账号或密码错误");




    }};
}
