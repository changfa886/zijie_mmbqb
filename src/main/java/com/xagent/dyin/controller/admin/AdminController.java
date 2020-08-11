package com.xagent.dyin.controller.admin;

import com.xagent.dyin.db.service.ZijieAdminService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

public class AdminController
{
    @Autowired
    protected ZijieAdminService zijieAdminService;

    // 根据session得到 uid
    public Long getAdmidFromSession(HttpSession session)
    {
        long admid = 0;
        do
        {
            if (session.getAttribute("admin_id") == null)
            {
                break;
            }
            admid = (long)session.getAttribute("admin_id");
        } while (false);

        return admid;
    }
}
