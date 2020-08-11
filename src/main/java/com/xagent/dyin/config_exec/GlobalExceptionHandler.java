package com.xagent.dyin.config_exec;

import com.xagent.dyin.entity.MyResp;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.ConstraintViolationException;

/**
 * Created by jonty. 统一异常处理
 * ControllerAdvice类注解, 作用于 整个 Spring 工程. ControllerAdvice 注解定义了一个全局的异常处理器
 * 需要注意的是, ExceptionHandler 的优先级比 ControllerAdvice 高
 *   即 Controller 抛出的异常如果既可以让 ExceptionHandler 标注的方法处理,又可以让 ControllerAdvice 标注的类中的方法处理,
 *   则优先让 ExceptionHandler 标注的方法处理.
 *
 * Spring Boot 中, 当用户访问了一个不存在的链接时, Spring 默认会将页面重定向到 **\/error**上,而不会抛出异常.
 * 既然如此,那我们就告诉 Spring Boot,当出现 404错误时,抛出一个异常即可.在 application.properties 中添加两个配置:
 * spring.mvc.throw-exception-if-no-handler-found=true
 * spring.resources.add-mappings=false
 * 上面的配置中, 第一个 spring.mvc.throw-exception-if-no-handler-found 告诉 SpringBoot 当出现 404 错误时, 直接抛出异常. 第二个 spring.resources.add-mappings 告诉 SpringBoot 不要为我们工程中的资源文件建立映射. 这两个配置正是 RESTful 服务所需要的
 */
@Slf4j
@EnableWebMvc
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            BindException.class,
            ServletRequestBindingException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class})
    public MyResp handleHttpMessageNotReadableException(Exception e) {
        //log.warn("参数解析失败: " + e.getClass().getName()+": "+e.getMessage());
        if (e instanceof BindException){
            return new MyResp(ResultStatusCode.BAD_REQUEST.getCode(), ((BindException)e).getAllErrors().get(0).getDefaultMessage());
        }
        else if (e instanceof MissingServletRequestParameterException){
            return new MyResp(ResultStatusCode.BAD_REQUEST.getCode(), "缺失参数");
        }
        return new MyResp(ResultStatusCode.BAD_REQUEST.getCode(), "空空");
    }

    /**
     * 405 - Method Not Allowed
     * 带有@ResponseStatus注解的异常类会被ResponseStatusExceptionResolver 解析
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public MyResp handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        //log.warn("不支持当前请求方法: "+ e.getClass().getName()+": "+e.getMessage());
        return new MyResp(ResultStatusCode.METHOD_NOT_ALLOWED.getCode(), null);
    }

    /**
     * 数据解析异常.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NumberFormatException.class)
    public MyResp handleException(NumberFormatException e) {
        log.warn("数据解析异常: " + e.getClass().getName()+": "+e.getMessage());
        return new MyResp(ResultStatusCode.BAD_REQUEST.getCode(), "数据格式错误");
    }

    // @RequestParam中的参数解析，异常时会到这里
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public MyResp handleException(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型异常: " + e.getClass().getName()+": "+e.getMessage());
        //e.printStackTrace();
        return new MyResp(ResultStatusCode.BAD_REQUEST.getCode(), "参数格式错误");
    }

    // NoHandlerFoundException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoHandlerFoundException.class)
    public MyResp handleException(NoHandlerFoundException e) {
        log.warn("方法不存在: " + e.getClass().getName()+": "+e.getMessage());
        return new MyResp(ResultStatusCode.REQUEST_NOT_FOUND.getCode(), "方法不存在");
    }

    /**
     * 其他全局异常在此捕获
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Throwable.class)  //Exception
    public MyResp handleException(Throwable e) {
        log.warn(e.getClass().getName() +": "+ e.getMessage());
        if (e instanceof NoHandlerFoundException)
        {
            return new MyResp(ResultStatusCode.REQUEST_NOT_FOUND.getCode(), null);
        }
        else if (e instanceof DataAccessException)
        {
            return new MyResp(ResultStatusCode.REQUEST_DB_ERR.getCode(), "数据获取异常");
        }
        //logger.info("handleException xxxxxx");
        // return new MyResp(ResultStatusCode.SYSTEM_ERR.getCode(), "未知错误");
        return new MyResp(1000, "异常错误");
    }
}
