package com.xagent.dyin.db.service;

import com.xagent.dyin.db.entity.ZijieBuyRecordEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jonty
 * @since 2020-05-21
 */
public interface ZijieBuyRecordService extends IService<ZijieBuyRecordEntity> {
    int sumOfAllAmountInRange(Long btime, Long etime);
    int sumOfCatagoryAmountInRange(Long catagoryid, Long btime, Long etime);
}
