package com.xagent.dyin.config_exec;

import com.xagent.dyin.entity.MyResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 解决未被全局异常处理的错误
 * 针对部分异常还是拦截不掉(比如请求接口不存在等)。
 * 这是因为Spring MVC的dodispatch前还有一些异常没有处理，此时需要单独对这类异常处理
 */
@Slf4j
@RestController
public class FinalExceptionHandlerController implements ErrorController
{
    @Override
    public String getErrorPath() {
        return "/error";
    }

    @GetMapping(value = "/error")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object error(HttpServletRequest request, HttpServletResponse response)
    {
        //log.error("response error,httpCode:" + response.getStatus());

        return new MyResp(1000, "页面不存在");
    }
}
