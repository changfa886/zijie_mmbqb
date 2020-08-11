package com.xagent.dyin.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xagent.dyin.aop_repeat_tokencache.NoRepeatSubmitTokenCache;
import com.xagent.dyin.db.entity.ZijieBuyRecordEntity;
import com.xagent.dyin.db.entity.ZijieCatagoryEntity;
import com.xagent.dyin.db.entity.ZijieGoodsEntity;
import com.xagent.dyin.db.service.ZijieBuyRecordService;
import com.xagent.dyin.db.service.ZijieCatagoryService;
import com.xagent.dyin.db.service.ZijieGoodsService;
import com.xagent.dyin.entity.GoodsUserEntity;
import com.xagent.dyin.entity.MyResp;
import com.xagent.dyin.service.PayOnlineService;
import com.xagent.dyin.utils.GlobleConsts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "小程序-套图浏览")
@RestController
@RequestMapping("/user/imgs")
public class UserImgsController extends UserController
{
    private static final Logger logger = LoggerFactory.getLogger(UserImgsController.class);

    @Autowired
    private ZijieCatagoryService zijieCatagoryService;
    @Autowired
    private ZijieGoodsService zijieGoodsService;
    @Autowired
    private ZijieBuyRecordService zijieBuyRecordService;
    @Autowired
    private PayOnlineService payOnlineService;

    @ApiOperation(value="分类列表")
    @Cacheable(cacheNames = "tenminute", key="'user.imgs.catagory.list'")
    @RequestMapping(value="/catagory/list", method= RequestMethod.GET)
    public MyResp catagory_list()
    {
        QueryWrapper<ZijieCatagoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "name");
        queryWrapper.eq("status", GlobleConsts.CatagoryStatus_Normal);
        queryWrapper.orderByDesc("isort");
        queryWrapper.orderByAsc("id");

        MyResp myResp = new MyResp(0);
        myResp.data.put("infos",zijieCatagoryService.list(queryWrapper));

        return myResp;
    }

    @ApiOperation(value="套图浏览", notes = "按类别浏览，分页")
    @Cacheable(cacheNames = "oneminute", keyGenerator="myKeyGenerator")
    @RequestMapping(value="/content/list", method= RequestMethod.GET)
    public MyResp content_list(@RequestParam("catagoryid") Long catagoryid, @RequestParam("current") Long current, @RequestParam("size") Long size)
    {
        QueryWrapper<ZijieGoodsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "title", "price", "headimg", "imgs", "remark");
        queryWrapper.eq("catagoryid", catagoryid);
        queryWrapper.eq("status", GlobleConsts.GoodsStatus_Normal);
        queryWrapper.orderByDesc("isort");
        queryWrapper.orderByDesc("id");
        size = 15L;
        Page<ZijieGoodsEntity> page = new Page<>(current, size);
        IPage<ZijieGoodsEntity> infosPage = zijieGoodsService.page(page, queryWrapper);
        List<ZijieGoodsEntity> goodsEntityList = infosPage.getRecords();

        MyResp myResp = new MyResp(0);
        //myResp.data.put("total", infosPage.getTotal());
        myResp.data.put("infos", goodsEntityList);

        return myResp;
    }

    @ApiOperation(value="已购记录", notes = "按购买时间排序, 分页,每页最多10")
    @RequestMapping(value="/buy/record/list", method= RequestMethod.GET)
    public MyResp buy_record_list(@ApiIgnore @RequestHeader("Token") String token, @RequestParam("current") Long current, @RequestParam("size") Long size)
    {
        long uid = getUidFromToken(token);
        if (uid <= 0)
        {
            return new MyResp(1001);
        }
        if (size.longValue() > 10){
            size = 10L;
        }

        QueryWrapper<ZijieBuyRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("created", "goodsid");
        queryWrapper.eq("uid", uid);
        queryWrapper.orderByDesc("created");

        Page<ZijieBuyRecordEntity> page = new Page<>(current, size);
        IPage<ZijieBuyRecordEntity> infosPage = zijieBuyRecordService.page(page, queryWrapper);
        List<ZijieBuyRecordEntity> goodsEntityList = infosPage.getRecords();

        List<GoodsUserEntity> entityList = new ArrayList<>(goodsEntityList.size());
        for (int i=0; i<goodsEntityList.size(); i++){
            ZijieBuyRecordEntity item = goodsEntityList.get(i);
            ZijieGoodsEntity goodsEntity = zijieGoodsService.getById(item.getGoodsid());

            GoodsUserEntity entity = new GoodsUserEntity();
            entity.setId(goodsEntity.getId());
            entity.setCatagoryid(goodsEntity.getCatagoryid());
            entity.setTitle(goodsEntity.getTitle());
            entity.setHeadimg(goodsEntity.getHeadimg());
            entity.setImgs(goodsEntity.getImgs());
            entity.setBuyTime(item.getCreated());

            entityList.add(entity);
        }


        MyResp myResp = new MyResp(0);
        //myResp.data.put("total", infosPage.getTotal());
        myResp.data.put("infos", entityList);

        return myResp;
    }

    // 直接视为已购
    @ApiOperation(value = "广告浏览后,直接赠与商品")
    @NoRepeatSubmitTokenCache
    @ApiResponses({@ApiResponse(code = 200, message = "{\"resp_code\":0, }")})
    @RequestMapping(value = "/ads/gain", method = RequestMethod.POST)
    public MyResp ads_gain(@ApiIgnore @RequestHeader("Token") String token, @RequestParam("goodsid") Long goodsid)
    {
        long uid = getUidFromToken(token);

        if (uid <= 0)
        {
            return new MyResp(1001);
        }

        ZijieGoodsEntity goodsEntity = zijieGoodsService.getById(goodsid);
        if (goodsEntity == null)
        {
            return new MyResp(1000, "不存在");
        }
        if (goodsEntity.getStatus() != GlobleConsts.GoodsStatus_Normal)
        {
            return new MyResp(1000, "已下架");
        }
        // 该用户是否已经购买过该商品了 ....
        QueryWrapper<ZijieBuyRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("goodsid", goodsid);
        queryWrapper.last("limit 1");
        ZijieBuyRecordEntity recordEntity = zijieBuyRecordService.getOne(queryWrapper);
        if (recordEntity != null)
        {
            return new MyResp(1000, "已购买过，可直接下载");
        }
//
//        if (goodsEntity.getPrice().intValue() <= 0)
//        {
//            return new MyResp(1000, "无需支付");
//        }

        payOnlineService.gainGoods(uid, goodsid);

        return new MyResp(0);
    }

    @ApiOperation(value="查看图片购买记录")
    @RequestMapping(value="/buy/record", method= RequestMethod.GET)
    public MyResp selectBuyRecord(@ApiIgnore @RequestHeader("Token") String token, @RequestParam("goodsid") Long goodsid){
        long uid = getUidFromToken(token);

        if (uid <= 0)
        {
            return new MyResp(1001);
        }

        QueryWrapper<ZijieBuyRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("goodsid", goodsid);
        queryWrapper.last("limit 1");

        ZijieBuyRecordEntity recordEntity = zijieBuyRecordService.getOne(queryWrapper);

        if (recordEntity != null)
        {
            return new MyResp(0,"0");
        }

        return new MyResp(0,"1");
    }
}
