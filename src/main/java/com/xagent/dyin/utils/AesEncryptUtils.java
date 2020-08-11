package com.xagent.dyin.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptUtils
{
    // iSfsNVpowkf452Dx 697366736673706f77656634353244
    private static final String XagentUserKey = "iSfsNVpowkf452Dx";

    //参数分别代表 算法名称/加密模式/数据填充方式
    // AES支持支持几种填充：NoPadding，PKCS5Padding，ISO10126Padding，PaddingMode.Zeros，PaddingMode.PKCS7;
    //PKCS5Padding 和 PKCS7Padding 是一样的

    /**
     * 加密
     * @param content 加密的字符串
     * @param encryptKey key值
     * @return
     * @throws Exception
     */
    private static String encrypt_ecb(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128); // 128位，则需要key16位  16×8=128
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
        byte[] b = cipher.doFinal(content.getBytes("utf-8"));

        //使用CBC模式，需要一个向量iv，可增加加密算法的强度
        //IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());
        //cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"), iv);

        // 采用base64算法进行转码,避免出现中文乱码
        return Base64.encodeBase64String(b);

    }
    /**
     * 解密
     * @param encryptStr 解密的字符串
     * @param decryptKey 解密的key值
     * @return
     * @throws Exception
     */
    private static String decrypt_ecb(String encryptStr, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        // 采用base64算法进行转码,避免出现中文乱码
        byte[] encryptBytes = Base64.decodeBase64(encryptStr);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }


    ////////////////// 封装调用 ecb

    ///// xagent user 加密校验
    public static String plusEncryptXagentUser(String content) throws Exception {
        return Md5Utils.MD5Encode(encrypt_ecb(Md5Utils.MD5Encode(encrypt_ecb(content, XagentUserKey)) + Md5Utils.MD5Encode("cipher"), XagentUserKey));
    }

    // 1000:exception 0:通过 1101:时间误差 1102:校验失败
    public static int verifyEncryptFromXagentUser(Long curtime, String fmark, String content)
    {
        long diffTime = System.currentTimeMillis() - curtime;
        if (diffTime < 0)
        {
            diffTime = -1 * diffTime;
        }
        if (diffTime > 900000)  // 误差x分钟
        {
            return 1101;
        }
        try
        {
            String dstr = Md5Utils.MD5Encode(encrypt_ecb(Md5Utils.MD5Encode(encrypt_ecb(content, XagentUserKey)) + Md5Utils.MD5Encode("cipher"), XagentUserKey));
            if (!dstr.equals(fmark))
            {
                return 1102;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 1000;
        }
        return 0;
    }

    ///////////////// android cbc ////////////
    public static String encrypt_cbc(String content, String encryptKey, String ivStr) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128); // 128位，则需要key、iv都是16位  16×8=128
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        //使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes("utf-8"), "AES"), iv);

        byte[] b = cipher.doFinal(content.getBytes());

        //byte[] b = cipher.doFinal(content.getBytes("utf-8"));

        // 采用base64算法进行转码,避免出现中文乱码
        return Base64.encodeBase64String(b);

    }
    public static String decrypt_cbc(String content, String decryptKey, String ivStr) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes("utf-8"), "AES"), iv);
        // 采用base64算法进行转码,避免出现中文乱码
        byte[] encryptBytes = Base64.decodeBase64(content);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    ///////////////////
    public static int verifyAes(String src, Long curtime, String fmark)
    {
        long diffTime = System.currentTimeMillis() - curtime;
        if (diffTime < 0)
        {
            diffTime = -1 * diffTime;
        }
        if (diffTime > 60000)
        {
            return 1000;
        }
        try
        {
            String dstr = plusEncryptXagentUser(src);
            if (!dstr.equals(fmark))
            {
                // logger.info(fmark + " != " + dstr);
                return 1000;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 1000;
        }
        return 0;
    }
}
