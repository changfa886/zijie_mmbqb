package com.xagent.dyin.controller.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xagent.dyin.config.ByteDanceConfig;
import com.xagent.dyin.db.entity.ZijieUserEntity;
import com.xagent.dyin.entity.MyResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Api(tags = "小程序-用户账号")
@RestController
@RequestMapping("/user/account")
public class UserAccountController extends UserController
{
    private static final Logger logger = LoggerFactory.getLogger(UserAccountController.class);

    @Autowired
    private ByteDanceConfig byteDanceConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${rdtime.token}")
    private Long rdtimeToken;

    @ApiOperation(value="用户账号认证")
    @RequestMapping(value="/auth", method= RequestMethod.POST)
    public MyResp auth(@RequestParam(name = "code", required = false) String code, @RequestParam(name="anonymous_code", required = false) String anonymous_code)
    {
        if (TextUtils.isEmpty(code) && TextUtils.isEmpty(anonymous_code))
        {
            return new MyResp(1000, "缺少输入信息");
        }

        String openId = "";
        try
        {
            openId = _getOpenidFromCode(code, anonymous_code);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(openId))
        {
            return new MyResp(1000, "认证失败");
        }

        QueryWrapper<ZijieUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openId);
        ZijieUserEntity userEntity = zijieUserService.getOne(queryWrapper);

        String token = UUID.randomUUID().toString();

        MyResp myResp = new MyResp(0);
        if (userEntity != null){
            redisUtil.set("zjut_"+token, userEntity.getId(), rdtimeToken);
            myResp.data.put("token", token);
        }else{
            userEntity = new ZijieUserEntity();
            userEntity.setCreated(System.currentTimeMillis());
            userEntity.setUpdated(userEntity.getCreated());
            userEntity.setOpenid(openId);
            zijieUserService.save(userEntity);

            redisUtil.set("zjut_"+token, userEntity.getId(), rdtimeToken);
            myResp.data.put("token", token);

            logger.info("add user: "+openId);
        }

        return myResp;
    }

    private String _getOpenidFromCode(String code, String anonymous_code)
    {
        //
        String params = "?appid="+byteDanceConfig.getAppAppid();
        params += "&secret="+byteDanceConfig.getAppAppsecret();
        if (! TextUtils.isEmpty(code)){
            params += "&code="+code;
        }
        if (! TextUtils.isEmpty(anonymous_code)){
            params += "&anonymous_code="+anonymous_code;
        }

        String url = "https://developer.toutiao.com/api/apps/jscode2session"+params;

        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
        JSONObject respJson = JSON.parseObject(entity.getBody());

        return respJson.getString("openid");
    }

    @ApiOperation(value="刷新token")
    @RequestMapping(value="/refresh/token", method= RequestMethod.POST)
    public MyResp refresh_token(@RequestHeader("Token") String token)
    {
        long uid = getUidFromToken(token);
        if (uid <= 0)
        {
            return new MyResp(1001);
        }

        redisUtil.del("zjut_"+token);

        String newToken = UUID.randomUUID().toString();
        redisUtil.set("zjut_"+newToken, uid, rdtimeToken);

        MyResp myResp = new MyResp(0);
        myResp.data.put("token", newToken);

        return myResp;
    }

//    @ApiOperation(value="用户资料")
//    @RequestMapping(value="/info", method= RequestMethod.GET)
//    public MyResp info(@RequestHeader("Token") String token)
//    {
//        long uid = getUidFromToken(token);
//        if (uid <= 0)
//        {
//            return new MyResp(1001);
//        }
//
//        ZijieUserEntity userEntity = zijieUserService.getById(uid);
//
//        MyResp myResp = new MyResp(0);
//        myResp.data.put("nick", userEntity.getNickname());
//        myResp.data.put("avatar", userEntity.getAvatar());
//
//        return myResp;
//    }

}
