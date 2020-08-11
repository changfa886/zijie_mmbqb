package com.xagent.dyin.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xagent.dyin.db.entity.ZijieGoodsEntity;
import com.xagent.dyin.db.service.ZijieGoodsService;
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

import java.util.List;

@Api(tags = "管理台-商品")
@RestController
@RequestMapping("/admin/goods")
public class AdminGoodsController extends AdminController
{
    @Autowired
    private ZijieGoodsService zijieGoodsService;

    @ApiOperation(value="商品列表", notes = "分类别查看，分页")
    @RequestMapping(value="/list/by/catagory", method= RequestMethod.GET)
    public MyResp list_by_catagory(@RequestParam("catagoryid") Long catagoryid, @RequestParam("current") Long current, @RequestParam("size") Long size)
    {
        QueryWrapper<ZijieGoodsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catagoryid", catagoryid);
        queryWrapper.eq("status", GlobleConsts.GoodsStatus_Normal);
        queryWrapper.orderByDesc("isort");
        queryWrapper.orderByDesc("id");

        Page<ZijieGoodsEntity> page = new Page<>(current, size);
        IPage<ZijieGoodsEntity> infosPage = zijieGoodsService.page(page, queryWrapper);
        List<ZijieGoodsEntity> goodsEntityList = infosPage.getRecords();

        MyResp myResp = new MyResp(0);
        myResp.data.put("total", infosPage.getTotal());
        myResp.data.put("infos", goodsEntityList);

        return myResp;
    }

    // 添加商品
    @ApiOperation(value="添加商品", notes = "price单位是分")
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public MyResp add(@RequestParam("catagoryid") Long catagoryid, @RequestParam(name="isort", defaultValue = "0") Integer isort, @RequestParam("price") Integer price, @RequestParam("title") String title, @RequestParam("headimg") String headimg, @RequestParam("imgs") String imgs, @RequestParam(name="remark", required = false) String remark)
    {
        if (TextUtils.isBlank(title)){
            return new MyResp(1000, "请输入标题");
        }
        if (TextUtils.isEmpty(headimg)){
            return new MyResp(1000, "请输入封面图");
        }
        if (TextUtils.isEmpty(imgs)){
            return new MyResp(1000, "请输入套图");
        }
        if (price.longValue() < 0){
            return new MyResp(1000, "价格错误");
        }

        ZijieGoodsEntity goodsEntity = new ZijieGoodsEntity();
        goodsEntity.setCatagoryid(catagoryid);
        goodsEntity.setTitle(title);
        goodsEntity.setPrice(price);
        goodsEntity.setHeadimg(headimg);
        goodsEntity.setIsort(isort);
        goodsEntity.setImgs(imgs);
        goodsEntity.setRemark(remark);

        zijieGoodsService.save(goodsEntity);

        MyResp myResp = new MyResp(0);

        return myResp;
    }


    @ApiOperation(value="修改商品")
    @RequestMapping(value="/modify", method= RequestMethod.POST)
    public MyResp modify(@RequestParam("id") Long id, @RequestParam(name="isort", defaultValue = "0") Integer isort, @RequestParam("price") Integer price, @RequestParam("title") String title, @RequestParam("headimg") String headimg, @RequestParam(name="remark", required = false) String remark)
    {
        if (TextUtils.isBlank(title)){
            return new MyResp(1000, "请输入标题");
        }
        if (TextUtils.isEmpty(headimg)){
            return new MyResp(1000, "请输入封面图");
        }
        if (price.longValue() < 0){
            return new MyResp(1000, "价格错误");
        }

        ZijieGoodsEntity goodsEntity = zijieGoodsService.getById(id);
        if (goodsEntity == null){
            return new MyResp(1000, "不存在");
        }
        if (goodsEntity.getStatus() != GlobleConsts.GoodsStatus_Normal){
            return new MyResp(1000, "已被删除");
        }

        goodsEntity.setTitle(title);
        goodsEntity.setHeadimg(headimg);
        goodsEntity.setIsort(isort);
        goodsEntity.setPrice(price);
        goodsEntity.setRemark(remark);

        zijieGoodsService.updateById(goodsEntity);

        MyResp myResp = new MyResp(0);

        return myResp;
    }

    @ApiOperation(value="删除商品")
    @RequestMapping(value="/delete", method= RequestMethod.POST)
    public MyResp delete(@RequestParam("id") Long id)
    {
        ZijieGoodsEntity goodsEntity = zijieGoodsService.getById(id);
        if (goodsEntity == null){
            return new MyResp(1000, "不存在");
        }
        if (goodsEntity.getStatus() != GlobleConsts.GoodsStatus_Normal){
            return new MyResp(0);
        }

        goodsEntity.setStatus(GlobleConsts.GoodsStatus_Deleted);

        zijieGoodsService.updateById(goodsEntity);

        MyResp myResp = new MyResp(0);

        return myResp;
    }
}
