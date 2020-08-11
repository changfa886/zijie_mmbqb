package com.xagent.dyin.service;

public interface PayOnlineService
{
    void processByOrderId(String out_trade_no);
    void gainGoods(Long uid, Long goodsId);
}
