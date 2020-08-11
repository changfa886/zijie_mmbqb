package com.xagent.dyin.controller;

import com.xagent.dyin.entity.MyResp;
import com.xagent.dyin.utils.AesEncryptUtils;
import org.apache.http.util.TextUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController
{
    @RequestMapping(value = "/aes", method = RequestMethod.GET)
    public MyResp aes(@RequestParam("content") String content)
    {
        MyResp myResp = new MyResp(0);


        return myResp;
    }
}
