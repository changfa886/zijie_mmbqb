package com.xagent.dyin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xagent.dyin.db.entity.ZijieBuyRecordEntity;
import com.xagent.dyin.db.entity.ZijieGoodsEntity;
import com.xagent.dyin.db.entity.ZijiePayOnlineEntity;
import com.xagent.dyin.db.service.ZijieBuyRecordService;
import com.xagent.dyin.db.service.ZijieGoodsService;
import com.xagent.dyin.db.service.ZijiePayOnlineService;
import com.xagent.dyin.utils.GlobleConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayOnlineServiceImpl implements PayOnlineService
{
    private static final Logger logger = LoggerFactory.getLogger(PayOnlineServiceImpl.class);

    @Autowired
    private ZijiePayOnlineService zijiePayOnlineService;
    @Autowired
    private ZijieBuyRecordService zijieBuyRecordService;
    @Autowired
    private ZijieGoodsService zijieGoodsService;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor=Exception.class)
    public void processByOrderId(String out_trade_no)
    {
        QueryWrapper<ZijiePayOnlineEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no", out_trade_no);
        ZijiePayOnlineEntity payOnlineEntity = zijiePayOnlineService.getOne(queryWrapper);
        if (payOnlineEntity == null){
            logger.error("not exist: "+out_trade_no);
            return;
        }

        if (payOnlineEntity.getStatus() != GlobleConsts.PayonlineStatus_Paying){
            logger.error("订单已处理过了: "+out_trade_no);
            return;
        }

        long curMillSec = System.currentTimeMillis();

        // 订单状态
        UpdateWrapper<ZijiePayOnlineEntity> payOnlineEntityUpdateWrapper = new UpdateWrapper<>();
        payOnlineEntityUpdateWrapper.eq("out_trade_no", out_trade_no);
        payOnlineEntityUpdateWrapper.set("updated", curMillSec);
        payOnlineEntityUpdateWrapper.set("status", GlobleConsts.PayonlineStatus_Success);
        if (! zijiePayOnlineService.update(payOnlineEntityUpdateWrapper)){
            throw new RuntimeException();
        }

        ZijieGoodsEntity goodsEntity = zijieGoodsService.getById(payOnlineEntity.getGoodsId());

        // 购买记录
        ZijieBuyRecordEntity buyRecordEntity = new ZijieBuyRecordEntity();
        buyRecordEntity.setCreated(curMillSec);
        buyRecordEntity.setUid(payOnlineEntity.getUid());
        buyRecordEntity.setAmount(payOnlineEntity.getTotalFee());
        buyRecordEntity.setGoodsid(payOnlineEntity.getGoodsId());
        buyRecordEntity.setCatagoryid(goodsEntity.getCatagoryid());
        buyRecordEntity.setType(2);
        if (! zijieBuyRecordService.save(buyRecordEntity)){
            throw new RuntimeException();
        }

        // 套图购买次数
        UpdateWrapper<ZijieGoodsEntity> goodsEntityUpdateWrapper = new UpdateWrapper<>();
        goodsEntityUpdateWrapper.eq("id", goodsEntity.getId());
        goodsEntityUpdateWrapper.set("buytimes", goodsEntity.getBuytimes()+1);
        if (! zijieGoodsService.update(goodsEntityUpdateWrapper)){
            logger.error("套图计数增加失败: "+out_trade_no);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor=Exception.class)
    public void gainGoods(Long uid, Long goodsId)
    {
        long curMillSec = System.currentTimeMillis();

        ZijieGoodsEntity goodsEntity = zijieGoodsService.getById(goodsId);

        // 购买记录
        ZijieBuyRecordEntity buyRecordEntity = new ZijieBuyRecordEntity();
        buyRecordEntity.setCreated(curMillSec);
        buyRecordEntity.setUid(uid);
        buyRecordEntity.setAmount(0);
        buyRecordEntity.setGoodsid(goodsId);
        buyRecordEntity.setCatagoryid(goodsEntity.getCatagoryid());
        buyRecordEntity.setType(0);
        if (! zijieBuyRecordService.save(buyRecordEntity)){
            throw new RuntimeException();
        }

        // 套图购买次数
        UpdateWrapper<ZijieGoodsEntity> goodsEntityUpdateWrapper = new UpdateWrapper<>();
        goodsEntityUpdateWrapper.eq("id", goodsEntity.getId());
        goodsEntityUpdateWrapper.set("buytimes", goodsEntity.getBuytimes()+1);
        if (! zijieGoodsService.update(goodsEntityUpdateWrapper)){
            logger.error("套图计数增加失败: ");
        }
    }
}
