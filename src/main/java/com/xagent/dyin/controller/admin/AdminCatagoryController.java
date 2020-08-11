package com.xagent.dyin.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xagent.dyin.db.entity.ZijieCatagoryEntity;
import com.xagent.dyin.db.service.ZijieCatagoryService;
import com.xagent.dyin.entity.MyResp;
import com.xagent.dyin.utils.GlobleConsts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.List;

@Api(tags = "管理台-类别")
@RestController
@RequestMapping("/admin/catagory")
public class AdminCatagoryController extends AdminController
{
    @Autowired
    private ZijieCatagoryService zijieCatagoryService;

    @ApiOperation(value="分类列表")
    @RequestMapping(value="/list", method= RequestMethod.GET)
    public MyResp catagory_list()
    {
        QueryWrapper<ZijieCatagoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", GlobleConsts.CatagoryStatus_Normal);
        queryWrapper.orderByDesc("isort");
        queryWrapper.orderByAsc("id");

        MyResp myResp = new MyResp(0);
        myResp.data.put("infos", zijieCatagoryService.list(queryWrapper));

        return myResp;
    }

    @ApiOperation(value="修改分类")
    @RequestMapping(value="/modify", method= RequestMethod.POST)
    public MyResp catagory_modify(@RequestParam("id") Long id, @RequestParam("name") String name, @RequestParam(name="isort", defaultValue = "0") Integer isort)
    {
        name = name.trim();
        if (TextUtils.isBlank(name))
        {
            return new MyResp(1000, "请输入分类名称");
        }

        ZijieCatagoryEntity catagoryEntity = zijieCatagoryService.getById(id);
        if (catagoryEntity == null){
            return new MyResp(1000, "不存在");
        }
        if (catagoryEntity.getStatus() != GlobleConsts.CatagoryStatus_Normal){
            return new MyResp(1000, "不存在");
        }
        catagoryEntity.setName(name);
        catagoryEntity.setIsort(isort);
        zijieCatagoryService.updateById(catagoryEntity);

        MyResp myResp = new MyResp(0);
        return myResp;
    }

    @ApiOperation(value="添加类别")
    @RequestMapping(value="/add/catagory", method= RequestMethod.POST)
    public MyResp addCatagory(@RequestParam("name") String name, @RequestParam(name="isort", defaultValue = "0") Integer isort)
    {
        name = name.trim();
        if (TextUtils.isBlank(name))
        {
            return new MyResp(1000, "请输入分类名称");
        }

        QueryWrapper<ZijieCatagoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        ZijieCatagoryEntity catagoryEntity = zijieCatagoryService.getOne(queryWrapper);

        if (catagoryEntity != null){
            if (catagoryEntity.getStatus() != GlobleConsts.CatagoryStatus_Normal){
                catagoryEntity.setStatus(GlobleConsts.GoodsStatus_Normal);
                zijieCatagoryService.save(catagoryEntity);
                return new MyResp(0);
            }
            return new MyResp(1000, "分类已存在");
        }
        ZijieCatagoryEntity catagory = new ZijieCatagoryEntity();
        catagory.setName(name);
        catagory.setIsort(isort);
        zijieCatagoryService.save(catagory);

        MyResp myResp = new MyResp(0);
        return myResp;
    }

    @ApiOperation(value="删除类别")
    @RequestMapping(value="/delete/catagory", method= RequestMethod.POST)
    public MyResp deleteCatagory(@RequestParam("id") Long id)
    {
        ZijieCatagoryEntity catagoryEntity = zijieCatagoryService.getById(id);

        if (catagoryEntity == null){
            return new MyResp(1000, "不存在");
        }

        if (catagoryEntity.getStatus() != GlobleConsts.CatagoryStatus_Normal){
            return new MyResp(0);
        }

        catagoryEntity.setStatus(GlobleConsts.CatagoryStatus_Deleted);
        zijieCatagoryService.updateById(catagoryEntity);

        MyResp myResp = new MyResp(0);
        return myResp;
    }
}
