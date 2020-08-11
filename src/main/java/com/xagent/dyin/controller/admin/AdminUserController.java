package com.xagent.dyin.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xagent.dyin.db.entity.ZijieUserEntity;
import com.xagent.dyin.db.service.ZijieUserService;
import com.xagent.dyin.entity.EcharEntity;
import com.xagent.dyin.entity.MyResp;
import com.xagent.dyin.utils.TimesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "管理台-用户管理")
@RestController
@RequestMapping("/admin/pay")
public class AdminUserController extends AdminController
{
    @Autowired
    private ZijieUserService zijieUserService;

    @ApiOperation(value="用户注册统计", notes = "btime、etime具体到天即可，前端显示柱状图")
    @RequestMapping(value="/statistics/reg/by/days", method= RequestMethod.GET)
    public MyResp statistics_reg_by_days(@RequestParam("btime") Long btime, @RequestParam("etime") Long etime)
    {
        List<EcharEntity> unitList = new ArrayList<>();

        long htime = btime;
        for (; htime<etime; )
        {
            long hbtime = TimesUtil.zeroTimeOfCentainDay(htime);
            long hetime = TimesUtil.endTimeOfCentainDay(htime);

            QueryWrapper<ZijieUserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.ge("created", hbtime);
            queryWrapper.lt("created", hetime);

            int count = zijieUserService.count(queryWrapper);

            unitList.add(new EcharEntity(htime, count));
            htime += 24 * 3600 * 1000L;
        }

        MyResp myResp = new MyResp(0);
        myResp.data.put("infos", unitList);

        return myResp;
    }
}
