package com.xagent.dyin.utils;

import java.util.ResourceBundle;

/**
 * 访问配置文件 - 单例
 *
 * @author: yingjie.wang
 * @since : 2015-10-08 16:13
 */

public class GlobleConfig
{
    private static ResourceBundle rb = null;
    private volatile static GlobleConfig instance = null;

    private GlobleConfig()
    {
        rb = ResourceBundle.getBundle("globle-cfg");
    }

    public static GlobleConfig instance()
    {
        if (instance == null)
        {
            synchronized (GlobleConfig.class)
            {
                if (instance == null)
                {
                    instance = new GlobleConfig();
                }
            }
        }
        return instance;
    }

    public String getValue(String key)
    {
        return (rb.getString(key));
    }
}
