package com.xagent.dyin.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xagent.dyin.aop_repeat_tokencache.NoRepeatSubmitTokenCache;
import com.xagent.dyin.db.entity.ZijieBuyRecordEntity;
import com.xagent.dyin.db.entity.ZijieGlobleEntity;
import com.xagent.dyin.db.entity.ZijieGoodsEntity;
import com.xagent.dyin.db.service.ZijieGlobleService;
import com.xagent.dyin.entity.MyResp;
import com.xagent.dyin.utils.GlobleConsts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "小程序-配置信息")
@RestController
@RequestMapping("/user/conf")
public class UserConfController extends UserController
{
    //private static final Logger logger = LoggerFactory.getLogger(UserConfController.class);

    @Autowired
    private ZijieGlobleService zijieGlobleService;

    @ApiOperation(value="获取全局支付类型")
    @RequestMapping(value="/query/paytype", method= RequestMethod.GET)
    public MyResp query_paytype(@RequestParam(name="ver") String ver)
    {
        MyResp myResp = new MyResp(0);

        QueryWrapper<ZijieGlobleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", GlobleConsts.DbName_payversion);
        ZijieGlobleEntity globleEntity = zijieGlobleService.getOne(queryWrapper);
        if (globleEntity == null){
            myResp.data.put("type", -1);
            return myResp;
        }
        if (! ver.equals(globleEntity.getValue())){
            myResp.data.put("type", -1);
            return myResp;
        }

        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", GlobleConsts.DbName_paytype);
        globleEntity = zijieGlobleService.getOne(queryWrapper);
        if (globleEntity == null){
            myResp.data.put("type", -1);
            return myResp;
        }
        myResp.data.put("type", Integer.parseInt(globleEntity.getValue()));

        return myResp;
    }
}
