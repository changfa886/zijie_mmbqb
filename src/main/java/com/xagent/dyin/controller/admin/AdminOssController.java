package com.xagent.dyin.controller.admin;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.xagent.dyin.config.AliOssConfig;
import com.xagent.dyin.entity.MyResp;
import com.xagent.dyin.utils.TimesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Api(tags = "管理台-oss")
@RestController
@RequestMapping("/admin/oss")
public class AdminOssController
{
    // private static Logger logger = LoggerFactory.getLogger(OssController.class);

    @Autowired
    private AliOssConfig aliOssConfig;

    @ApiOperation(value="获取上传参数", notes = "无图片后缀")
    @Cacheable(cacheNames = "tenminute", keyGenerator="myKeyGenerator")
    @RequestMapping(value = "/goods", method = RequestMethod.GET)
    public MyResp goods(@RequestParam("id") Long id)
    {
        String postSignature = "";
        String encodedPolicy = "";

        long expireEndTime = System.currentTimeMillis() + 1800 * 1000; //
        Date expiration = new Date(expireEndTime);

        String dir = "goods/" + id;
        try
        {
            OSSClient client = new OSSClient(aliOssConfig.getInnerEndPoint(), aliOssConfig.getKeyID(), aliOssConfig.getKeySecret());
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 15 * 1024 * 1024);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");

            encodedPolicy = BinaryUtil.toBase64String(binaryData);
            postSignature = client.calculatePostSignature(postPolicy);

            client.shutdown();
        }
        catch (UnsupportedEncodingException e)
        {
            return new MyResp(1000);
        }

        MyResp myResp = new MyResp(0);
        myResp.data.put("policy", encodedPolicy);
        myResp.data.put("signature", postSignature);
        myResp.data.put("dir", dir);
        myResp.data.put("expire", expireEndTime-300*1000); //app端
        myResp.data.put("accessid", aliOssConfig.getKeyID());
        myResp.data.put("host", aliOssConfig.getForeignHost());
        //myResp.data.put("prefix", uid+"_"); // 用户名由app端自行拼接
        //myResp.data.put("format", aliOssConfig.getSurfixFwidth());

        return myResp;
    }
}
