package com.xagent.dyin.utils;

public class GlobleConsts
{
    public static final String Passwd_Salt = "Uk&s23^ruk@";

    // globle-config
    public static final String DbName_paytype = "paytype";
    public static final String DbName_payversion = "payversion";

    public static final int CatagoryStatus_Normal = 0;
    public static final int CatagoryStatus_Deleted = 1;

    public static final int GoodsStatus_Normal = 0;
    public static final int GoodsStatus_Deleted = 1;


    public static final int PayonlineStatus_Paying = 0; //待支付
    public static final int PayonlineStatus_Success = 1; //成功支付
    public static final int PayonlineStatus_Failed = 2; //支付失败
}
