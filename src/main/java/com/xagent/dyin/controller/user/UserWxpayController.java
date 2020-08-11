package com.xagent.dyin.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bytedance.caijing.tt_pay.TTPayLog;
import com.bytedance.caijing.tt_pay.TTPayService;
import com.bytedance.caijing.tt_pay.model.TradeCreateRequest;
import com.bytedance.caijing.tt_pay.model.TradeCreateResponse;
import com.xagent.dyin.aop_repeat_tokencache.NoRepeatSubmitTokenCache;
import com.xagent.dyin.config.ByteDanceConfig;
import com.xagent.dyin.config_pay.IWxPayConfig;
import com.xagent.dyin.db.entity.ZijieBuyRecordEntity;
import com.xagent.dyin.db.entity.ZijieGoodsEntity;
import com.xagent.dyin.db.entity.ZijiePayOnlineEntity;
import com.xagent.dyin.db.service.ZijieBuyRecordService;
import com.xagent.dyin.db.service.ZijieGoodsService;
import com.xagent.dyin.db.service.ZijiePayOnlineService;
import com.xagent.dyin.entity.MyResp;
import com.xagent.dyin.service.PayOnlineService;
import com.xagent.dyin.utils.GlobleConsts;
import com.xagent.dyin.wxpaysdk.WXPay;
import com.xagent.dyin.wxpaysdk.WXPayConstants;
import com.xagent.dyin.wxpaysdk.WXPayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "小程序-微信支付")
@RestController
@RequestMapping("/user/wxpay")
public class UserWxpayController extends UserController
{
    private static final Logger logger = LoggerFactory.getLogger(UserWxpayController.class);

    @Autowired
    private ZijieGoodsService zijieGoodsService;
    @Autowired
    private ZijiePayOnlineService zijiePayOnlineService;
    @Autowired
    private ZijieBuyRecordService zijieBuyRecordService;

    @Autowired
    private IWxPayConfig iWxPayConfig;
    @Autowired
    private ByteDanceConfig byteDanceConfig;

    @Autowired
    private PayOnlineService payOnlineService;

    @ApiOperation(value = "选中商品，预支付", notes = "成功时返回orderInfo")
    @NoRepeatSubmitTokenCache
    @ApiResponses({@ApiResponse(code = 200, message = "{\"resp_code\":0, }")})
    @RequestMapping(value = "/do/prepare", method = RequestMethod.POST)
    public MyResp do_prepare(HttpServletRequest request, @ApiIgnore @RequestHeader("Token") String token, @RequestParam("goodsid") Long goodsid)
    {
        long uid = getUidFromToken(token);
        if (uid <= 0)
        {
            return new MyResp(1001);
        }

        ZijieGoodsEntity goodsEntity = zijieGoodsService.getById(goodsid);
        if (goodsEntity == null){
            return new MyResp(1000, "不存在");
        }
        if (goodsEntity.getStatus() != GlobleConsts.GoodsStatus_Normal){
            return new MyResp(1000, "已下架");
        }
        // 该用户是否已经购买过该商品了 ....
        QueryWrapper<ZijieBuyRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("goodsid", goodsid);
        queryWrapper.last("limit 1");
        ZijieBuyRecordEntity recordEntity = zijieBuyRecordService.getOne(queryWrapper);
        if (recordEntity != null){
            return new MyResp(1000, "已购买过，可直接下载");
        }

        if (goodsEntity.getPrice().intValue() <= 0){
            return new MyResp(1000, "无需支付");
        }

        String out_trade_no = uid + "" + (System.currentTimeMillis() / 10);  // 微信的out_trade_no最多32位

        ZijiePayOnlineEntity payOnlineEntity = new ZijiePayOnlineEntity();
        payOnlineEntity.setCreated(System.currentTimeMillis());
        payOnlineEntity.setUpdated(payOnlineEntity.getCreated());
        payOnlineEntity.setUid(uid);
        payOnlineEntity.setOutTradeNo(out_trade_no);
        payOnlineEntity.setGoodsId(goodsid);
        payOnlineEntity.setTotalFee(goodsEntity.getPrice());

        if (!zijiePayOnlineService.save(payOnlineEntity))
        {
            return new MyResp(1000);
        }

        try
        {
            WXPay wxpay = new WXPay(iWxPayConfig);

            String clientIp = getIpAddr(request);

            Map<String, String> payInfo = new HashMap<>();
            //payInfo.put("device_info", "WEB"); //可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
            //payInfo.put("nonce_str", WXPayUtil.generateNonceStr()); // 随机字符串，长度要求在32位以内
            payInfo.put("body", goodsEntity.getTitle()); //商品简单描述
            payInfo.put("out_trade_no", out_trade_no);
            payInfo.put("total_fee", goodsEntity.getPrice() + "");  // 分
            payInfo.put("spbill_create_ip", clientIp);
            payInfo.put("notify_url", iWxPayConfig.getWx_pay_notify_host()+"/user/wxpay/notify/finish");
            payInfo.put("trade_type", "MWEB"); // H5支付的交易类型为MWEB
            payInfo.put("scene_info", "{\"h5_info\":{\"type\":\"Wap\",\"wap_url\":\"https://pay.qq.com\", \"wap_name\":\"腾讯充值\"}}");
            //payInfo.put("receipt", "N");

            Map<String, String> response = wxpay.unifiedOrder(payInfo);
            String returnCode = response.get("return_code");
            if (!WXPayConstants.SUCCESS.equals(returnCode))
            {
                logger.error(response.get("return_msg"));
                return new MyResp(1000);
            }
            String resultCode = response.get("result_code");
            if (!WXPayConstants.SUCCESS.equals(resultCode)) {
                logger.error(response.get("return_msg"));
                return new MyResp(1000);
            }
            String prepay_id = response.get("prepay_id");
            if (prepay_id == null)
            {
                return new MyResp(1000);
            }
            String mweb_url = response.get("mweb_url");
            if (mweb_url == null)
            {
                return new MyResp(1000);
            }

            logger.info("prepay_id="+prepay_id);
            logger.info("mweb_url="+mweb_url);

            // ******************************************
            //
            //  前端调起微信支付必要参数
            //
            // ******************************************
//            String packages = "prepay_id=" + prepay_id;
//            Map<String, String> wxPayMap = new HashMap<>();
//            wxPayMap.put("appId", iWxPayConfig.getAppID());
//            wxPayMap.put("timeStamp", String.valueOf(WXPayUtil.getCurrentTimestamp()));
//            wxPayMap.put("nonceStr", WXPayUtil.generateNonceStr());
//            wxPayMap.put("package", packages);
//            wxPayMap.put("signType", "MD5");
//            // 加密串中包括 appId timeStamp nonceStr package signType 5个参数, 通过sdk WXPayUtil类加密, 注意, 此处使用  MD5加密  方式
//            String sign = WXPayUtil.generateSignature(wxPayMap, iWxPayConfig.getKey());
//            wxPayMap.put("sign", sign);

//            MyResp myResp = new MyResp(0);
//            myResp.data.put("data", wxPayMap);


            // log级别可配置为 debug，info，warn，error
            TTPayLog.logLevel = TTPayLog.LogLevel.info;
            // 小程序平台开通支付后，分配给开发者的app_id（非小程序的appid），用于获取 签名/验签 的密钥信息
            TTPayService.appId = byteDanceConfig.getPayAppid();
            // 小程序平台开通支付后，分配给开发者的支付密钥(非小程序的app_secret)
            TTPayService.appSecret = byteDanceConfig.getPayAppsecret();
            // 小程序平台开通支付后，分配给开发者的merchant_id
            TTPayService.merchantId = byteDanceConfig.getPayMerchantid();

            TradeCreateRequest byteRequest = TradeCreateRequest.builder()
                    // 此处是随机生成的，使用时请填写您的业务订单号
                    .outOrderNo(out_trade_no)
                    // 填写用户的唯一标识
                    .uid(uid+"")
                    // 填写订单金额，分为单位，不能有小数点
                    .totalAmount(Long.parseLong(goodsEntity.getPrice().toString()))
                    // 填写您的订单名称
                    .subject(goodsEntity.getTitle())
                    // 填写您的订单内容
                    .body(goodsEntity.getTitle())
                    // 交易时间，此处自动生成，您也可以根据需求赋值，但必须为Unix时间戳
                    // 同一笔订单两次发起支付时，tradeTime要传相同的值，否则拉起收银台会失败
                    .tradeTime("" + payOnlineEntity.getCreated() / 1000)
                    // 填写您的订单有效时间（单位：秒）
                    .validTime("36000000")
                    // 严格json字符串格式。ip需要为用户的真实ip地址
                    .riskInfo("{\"ip\":\""+clientIp+"\"}")
                    // 调用微信H5支付统一下单接口返回的mweb_url字段值。如果值为空，则收银台不会展示微信支付;
                    .wxUrl(mweb_url)
                    // wx_url有值时，传固定值：MWEB; wx_url无值时，传空
                    .wxType("MWEB")
                    .build();

            TradeCreateResponse byteResponse = TTPayService.TradeCreate(byteRequest);
            String orderInfo = byteResponse.getAppletParams();

            MyResp myResp = new MyResp(0);
            myResp.data.put("orderInfo", orderInfo);

            return myResp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new MyResp(1000);
    }

    @ApiOperation(value = "查询订单状态", notes = "trade_state:SUCCESS—支付成功, NOTPAY—未支付, CLOSED—已关闭, PAYERROR--支付失败")
    @NoRepeatSubmitTokenCache
    @RequestMapping(value = "/query/order", method = RequestMethod.GET)
    public MyResp query_order(@ApiIgnore @RequestHeader("Token") String token, @RequestParam("order_id") String order_id)
    {
        long uid = getUidFromToken(token);
        if (uid <= 0)
        {
            return new MyResp(1001);
        }

        // 查询订单有效性
        QueryWrapper<ZijiePayOnlineEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no", order_id);
        ZijiePayOnlineEntity payOnlineEntity = zijiePayOnlineService.getOne(queryWrapper);
        if (payOnlineEntity == null)
        {
            return new MyResp(1000, "不存在该订单");
        }
        if (payOnlineEntity.getUid().longValue() != uid){
            return new MyResp(1000, "不存在的订单号");
        }

        try
        {
            WXPay wxpay = new WXPay(iWxPayConfig);
            Map<String, String> payInfo = new HashMap<>();
            payInfo.put("out_trade_no", order_id);

            Map<String, String> response = wxpay.orderQuery(payInfo);
            String returnCode = response.get("return_code");
            if (!WXPayConstants.SUCCESS.equals(returnCode))
            {
                logger.error(response.get("return_msg"));
                return new MyResp(1000);
            }
            String resultCode = response.get("result_code");
            if (!WXPayConstants.SUCCESS.equals(resultCode)) {
                logger.error(response.get("return_msg"));
                return new MyResp(1000);
            }

            String trade_state = response.get("trade_state");
            /**
             SUCCESS—支付成功
             REFUND—转入退款
             NOTPAY—未支付
             CLOSED—已关闭
             REVOKED—已撤销（付款码支付）
             USERPAYING--用户支付中（付款码支付）
             PAYERROR--支付失败(其他原因，如银行返回失败)
             */
            MyResp myResp = new MyResp(0);
            myResp.data.put("trade_state", trade_state);
            return myResp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new MyResp(1000);
    }

    @ApiIgnore
    @RequestMapping(value="/notify/finish", method= RequestMethod.POST)
    public void finish_pay(HttpServletRequest request, HttpServletResponse response)
    {
        do
        {
            StringBuffer xmlStr = new StringBuffer();
            try(BufferedReader reader = request.getReader())
            {
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    xmlStr.append(line);
                }
            }catch (Exception e)
            {
                break;
            }
            logger.info("pay_callback: "+xmlStr);

            Map<String, String> reqData = null;
            try
            {
                reqData = WXPayUtil.xmlToMap(xmlStr.toString());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                break;
            }
            if (! reqData.containsKey("return_code"))
            {
                break;
            }
            String return_code = reqData.get("return_code");
            if (! return_code.equals(WXPayConstants.SUCCESS))
            {
                logger.error("return_code = "+return_code);
                break;
            }

            try
            {
                WXPay wxpay = new WXPay(iWxPayConfig);
                if (! wxpay.isPayResultNotifySignatureValid(reqData))
                {
                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                break;
            }

            // 签名正确
            // 进行处理。
            // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功

            int total_fee = Integer.parseInt(reqData.get("total_fee").toString());
            int cash_fee = Integer.parseInt(reqData.get("cash_fee").toString());
            if (cash_fee < total_fee)
            {
                logger.error("cash_fee["+cash_fee+"] != total_fee["+total_fee+"]");
                break;
            }
            String out_trade_no = reqData.get("out_trade_no");
            // 签名通过 处理业务吧
            synchronized (this)
            {
                logger.info("收到支付回调 "+out_trade_no);
                try
                {
                    payOnlineService.processByOrderId(out_trade_no);
                }catch (Exception e){
                    logger.error("支付订单回调处理异常: "+out_trade_no);
                }
            }
        }while (false);

        try
        {
            // httpResponse.setContentType("text/html;charset=utf-8");
            // 无论结果如何，都返回处理成功，防止wx再次发送notify
            Map<String, String> respMap = new HashMap<>();
            respMap.put("return_code", "SUCCESS");
            respMap.put("return_msg", "OK");
            String str = WXPayUtil.mapToXml(respMap);
            response.getWriter().write(str);
            response.getWriter().flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {}
    }

}
