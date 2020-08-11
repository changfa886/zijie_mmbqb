package com.xagent.dyin.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xagent.dyin.db.entity.ZijieGlobleEntity;
import com.xagent.dyin.db.service.ZijieGlobleService;
import com.xagent.dyin.entity.MyResp;
import com.xagent.dyin.utils.GlobleConsts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "管理台-配置信息")
@RestController
@RequestMapping("/admin/conf")
public class AdminConfController extends AdminController
{
    @Autowired
    private ZijieGlobleService zijieGlobleService;

    @ApiOperation(value="获取配置项内容", notes = "paytype:方式 -1表示不显示支付,0表示显示支付; payversion:针对的版本号，非该版本号的情况下，都是0-显示支付")
    @RequestMapping(value="/query/byname", method= RequestMethod.GET)
    public MyResp query_byname(@RequestParam(name="name") String name)
    {
        QueryWrapper<ZijieGlobleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        ZijieGlobleEntity globleEntity = zijieGlobleService.getOne(queryWrapper);
        if (globleEntity == null){
            return new MyResp(1000, "不存在");
        }

        MyResp myResp = new MyResp(0);
        myResp.data.put("value", globleEntity.getValue());

        return myResp;
    }

    @ApiOperation(value="设置配置项内容", notes = "paytype:方式 -1表示不显示支付,0表示显示支付; payversion:针对的版本号，非该版本号的情况下，都是0-显示支付")
    @RequestMapping(value="/set/byname", method= RequestMethod.POST)
    public MyResp set_byname(@RequestParam("name") String name, @RequestParam("value") String value)
    {
        QueryWrapper<ZijieGlobleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        ZijieGlobleEntity globleEntity = zijieGlobleService.getOne(queryWrapper);
        if (globleEntity == null){
            return new MyResp(1000, "不存在");
        }
        if (globleEntity.getType() == 1){
            int iValue = Integer.parseInt(value);
            value = iValue+"";
        }

        globleEntity.setValue(value);
        zijieGlobleService.updateById(globleEntity);

        MyResp myResp = new MyResp(0);

        return myResp;
    }
}
