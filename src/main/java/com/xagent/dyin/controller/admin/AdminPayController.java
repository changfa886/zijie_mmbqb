package com.xagent.dyin.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xagent.dyin.db.entity.ZijieBuyRecordEntity;
import com.xagent.dyin.db.service.ZijieBuyRecordService;
import com.xagent.dyin.entity.MyResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "管理台-支付管理")
@RestController
@RequestMapping("/admin/pay")
public class AdminPayController extends AdminController
{
    @Autowired
    private ZijieBuyRecordService zijieBuyRecordService;

    @ApiOperation(value="购买记录", notes = "catagoryid:按类别查看,0表示所有类别. ")
    @RequestMapping(value="/list", method= RequestMethod.GET)
    public MyResp list(@RequestParam(name = "catagoryid", defaultValue = "0") Long catagoryid, @RequestParam("current") Long current, @RequestParam("size") Long size)
    {
        QueryWrapper<ZijieBuyRecordEntity> queryWrapper = new QueryWrapper<>();
        if (catagoryid.longValue() > 0){
            queryWrapper.eq("catagoryid", catagoryid);
        }
        queryWrapper.orderByDesc("id");

        Page<ZijieBuyRecordEntity> page = new Page<>(current, size);
        IPage<ZijieBuyRecordEntity> infosPage = zijieBuyRecordService.page(page, queryWrapper);
        List<ZijieBuyRecordEntity> recordEntityList = infosPage.getRecords();

        MyResp myResp = new MyResp(0);
        myResp.data.put("total", infosPage.getTotal());
        myResp.data.put("infos", recordEntityList);

        return myResp;
    }

    @ApiOperation(value="支付总额", notes = "catagoryid:类别id, 0表示所有类别")
    @RequestMapping(value="/statistics", method= RequestMethod.GET)
    public MyResp statistics(@RequestParam(name = "catagoryid", defaultValue = "0") Long catagoryid, @RequestParam("btime") Long btime, @RequestParam("etime") Long etime)
    {
        int sum = 0;
        if (catagoryid.longValue() > 0){
            sum = zijieBuyRecordService.sumOfCatagoryAmountInRange(catagoryid, btime, etime);
        }else{
            sum = zijieBuyRecordService.sumOfAllAmountInRange(btime, etime);
        }

        MyResp myResp = new MyResp(0);
        myResp.data.put("sum", sum);

        return myResp;
    }
}
