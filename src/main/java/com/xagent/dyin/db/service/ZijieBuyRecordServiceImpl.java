package com.xagent.dyin.db.service;

import com.xagent.dyin.db.entity.ZijieBuyRecordEntity;
import com.xagent.dyin.db.mapper.ZijieBuyRecordMapper;
import com.xagent.dyin.db.service.ZijieBuyRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jonty
 * @since 2020-05-21
 */
@Service
public class ZijieBuyRecordServiceImpl extends ServiceImpl<ZijieBuyRecordMapper, ZijieBuyRecordEntity> implements ZijieBuyRecordService {
    @Autowired (required = false)
    private ZijieBuyRecordMapper zijieBuyRecordMapper;

    public int sumOfAllAmountInRange(Long btime, Long etime)
    {
        Integer sum = zijieBuyRecordMapper.sumOfAllAmountInRange(btime, etime);

        return sum==null ? 0 : sum.intValue();
    }
    public int sumOfCatagoryAmountInRange(Long catagoryid, Long btime, Long etime)
    {
        Integer sum = zijieBuyRecordMapper.sumOfCatagoryAmountInRange(catagoryid, btime, etime);
        return sum==null ? 0 : sum.intValue();
    }
}
