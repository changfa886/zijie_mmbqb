package com.xagent.dyin.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xagent.dyin.db.entity.ZijieAdminEntity;
import com.xagent.dyin.entity.MyResp;
import com.xagent.dyin.utils.GlobleConsts;
import com.xagent.dyin.utils.Md5Utils;
import com.xagent.dyin.utils.PublicMethod;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

@Api(tags = "管理台-登录账号")
@RestController
@RequestMapping("/admin/account")
public class AdminAccountController extends AdminController
{
    private static Logger logger = LoggerFactory.getLogger(AdminAccountController.class);

    @ApiOperation(value="登录管理台", notes = "loginpw是源密码，无需加密")
    @RequestMapping(value="/login", method= RequestMethod.POST)
    public MyResp login(@ApiIgnore HttpSession session, @RequestParam("admname") String admname, @RequestParam("loginpw") String loginpw)
    {
        MyResp myResp = null;
        do
        {
            QueryWrapper<ZijieAdminEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admname", admname.toLowerCase().trim());

            ZijieAdminEntity adminEntity = zijieAdminService.getOne(queryWrapper);
            if (adminEntity == null)
            {
                myResp = new MyResp(1003);
                break;
            }
            if (!adminEntity.getLoginpw().equals(Md5Utils.MD5Encode(loginpw+ GlobleConsts.Passwd_Salt)))
            {
                myResp = new MyResp(1003);
                break;
            }

            session.setAttribute("admin_id", adminEntity.getId());
            session.setMaxInactiveInterval(6*3600);
            myResp = new MyResp(0);
        }while (false);

        return myResp;
    }

    @ApiOperation(value="账号信息")
    @RequestMapping(value="/info", method= RequestMethod.GET)
    public MyResp info(@ApiIgnore HttpSession session)
    {
        MyResp myResp = null;
        do
        {
            long admid = getAdmidFromSession(session);
            if (admid <= 0)
            {
                myResp = new MyResp(1001);
                break;
            }
            ZijieAdminEntity adminEntity = zijieAdminService.getById(admid);

            String[] roleArray = new String[1];
            roleArray[0] = adminEntity.getRole();

            myResp = new MyResp(0);
            myResp.data.put("name", adminEntity.getAdmname());
            myResp.data.put("aid", adminEntity.getId());
            myResp.data.put("avatar", "");
            myResp.data.put("roles", roleArray);
        } while (false);
        return myResp;
    }

    @ApiOperation(value="登出管理台")
    @RequestMapping(value="/logout", method= RequestMethod.POST)
    public MyResp logout(@ApiIgnore HttpSession session)
    {
        MyResp myResp = null;
        do
        {
            long admid = getAdmidFromSession(session);
            if (admid > 0)
            {
                session.removeAttribute("admin_id");
            }

            myResp = new MyResp(0);

        } while (false);
        return myResp;
    }

    @ApiOperation(value="修改密码", notes = "oldpasswd是源密码，无需加密")
    @RequestMapping(value="/loginpw/update", method= RequestMethod.POST)
    public MyResp loginpw_update(@ApiIgnore HttpSession session, @RequestParam("oldpasswd") String oldpasswd, @RequestParam("newpasswd") String newpasswd)
    {
        MyResp myResp = null;
        do
        {
            long admid = getAdmidFromSession(session);
            if (admid <= 0)
            {
                myResp = new MyResp(1001);
                break;
            }
            ZijieAdminEntity adminEntity = zijieAdminService.getById(admid);

            if (TextUtils.isEmpty(oldpasswd) || TextUtils.isEmpty(newpasswd))
            {
                myResp = new MyResp(1000, "输入不完整");
                break;
            }
            if (! adminEntity.getLoginpw().equals(Md5Utils.MD5Encode(oldpasswd+ GlobleConsts.Passwd_Salt)))
            {
                myResp = new MyResp(1000, "原密码错误");
                break;
            }
            if (!PublicMethod.isValidPasswd(newpasswd))
            {
                myResp = new MyResp(1000, "密码格式错误");
                break;
            }
            String newLoginPw = Md5Utils.MD5Encode(newpasswd+ GlobleConsts.Passwd_Salt);
            if (newLoginPw.equals(adminEntity.getLoginpw()))
            {
                myResp = new MyResp(0);
                break;
            }
            adminEntity.setLoginpw(newLoginPw);

            if (! zijieAdminService.updateById(adminEntity))
            {
                myResp = new MyResp(1000);
                break;
            }

            myResp = new MyResp(0);
        } while (false);

        return myResp;
    }
}
