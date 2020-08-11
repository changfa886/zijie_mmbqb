package com.xagent.dyin.db.mapper;

import com.xagent.dyin.db.entity.ZijieBuyRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jonty
 * @since 2020-05-21
 */
public interface ZijieBuyRecordMapper extends BaseMapper<ZijieBuyRecordEntity> {
    @Select("SELECT sum(amount) FROM zijie_buy_record where created >= #{btime} and created < #{etime}")
    Integer sumOfAllAmountInRange(@Param("btime")Long btime, @Param("etime")Long etime);

    @Select("SELECT sum(amount) FROM zijie_buy_record where catagoryid = #{catagoryid} and created >= #{btime} and created < #{etime}")
    Integer sumOfCatagoryAmountInRange(@Param("catagoryid")Long catagoryid, @Param("btime")Long btime, @Param("etime")Long etime);
}
