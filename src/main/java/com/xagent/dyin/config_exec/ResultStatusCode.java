package com.xagent.dyin.config_exec;

public enum ResultStatusCode
{
    OK(200, "OK"),
    HTTP_ERROR_100(100, "1XX错误"),
    HTTP_ERROR_300(300, "3XX错误"),
    HTTP_ERROR_400(400, "4XX错误"),
    HTTP_ERROR_500(500, "5XX错误"),
    SIGN_ERROR(120, "签名错误"),
    TIME_OUT(130, "访问超时"),
    KICK_OUT(300, "您已经在其他地方登录，请重新登录！"),
    BAD_REQUEST(407, "参数解析失败"),
    INVALID_TOKEN(401, "无效的授权码"),
    INVALID_CLIENTID(402, "无效的密钥"),
    REQUEST_NOT_FOUND(404, "访问地址不存在！"),
    METHOD_NOT_ALLOWED(405, "不支持当前请求方法"),
    REPEAT_REQUEST_NOT_ALLOWED(406, "请求重复提交"),
    REQUEST_NOT_SECURITY(407, "请升级账号"),
    REQUEST_DB_ERR(408, "数据操作异常"),
    SYSTEM_ERR(500, "操作异常");

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    ResultStatusCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
