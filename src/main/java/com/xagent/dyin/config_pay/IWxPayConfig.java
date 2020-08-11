package com.xagent.dyin.config_pay;

import com.xagent.dyin.wxpaysdk.IWXPayDomain;
import com.xagent.dyin.wxpaysdk.WXPayConfig;
import com.xagent.dyin.wxpaysdk.WXPayConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

//@PropertySource(value = {"classpath:wxpay.properties"},encoding = "UTF-8")
@Component
public class IWxPayConfig extends WXPayConfig
{
    private byte[] certData;

    @Value("${vendor.wx.config.app_id}")
    private String app_id;

    @Value("${vendor.wx.pay.key}")
    private String wx_pay_key;

    @Value("${vendor.wx.pay.mch_id}")
    private String wx_pay_mch_id;

    @Value("${vendor.wx.notify.host}")
    private String wx_pay_notify_host;

//    private static IWxPayConfig instance = null;
//    public static IWxPayConfig getInstance(){
//        if (instance == null)
//        {
//            synchronized (IWxPayConfig.class)
//            {
//                if (instance == null)
//                {
//                    instance = new IWxPayConfig();
//                    return instance;
//                }
//            }
//        }
//        return instance;
//    }

    public IWxPayConfig() { // 构造方法读取证书, 通过getCertStream 可以使sdk获取到证书
//        String certPath = "/data/config/chidori/apiclient_cert.p12";
//        File file = new File(certPath);
//        InputStream certStream = new FileInputStream(file);
//        this.certData = new byte[(int) file.length()];
//        certStream.read(this.certData);
//        certStream.close();
    }

    @Override
    public String getAppID() {
        return app_id;
    }

    @Override
    public String getMchID() {
        return wx_pay_mch_id;
    }

    public String getWx_pay_notify_host() {
        return wx_pay_notify_host;
    }

    public void setWx_pay_notify_host(String wx_pay_notify_host){
        this.wx_pay_notify_host = wx_pay_notify_host;
    }

    @Override
    public String getKey() {
        return wx_pay_key;
    }

    @Override
    public InputStream getCertStream() {
        return new ByteArrayInputStream(this.certData);
    }

    @Override
    public IWXPayDomain getWXPayDomain() { // 这个方法需要这样实现, 否则无法正常初始化WXPay
        IWXPayDomain iwxPayDomain = new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }
            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new IWXPayDomain.DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        };
        return iwxPayDomain;
    }
}
