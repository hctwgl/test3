package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.DsedResourceService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserOutDayDo;
import com.ald.fanbei.api.dal.domain.DsedResourceDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 陈金虎 2017年2月7日 下午8:49:23
 * @类描述：发送短信工具类
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("dsedSmsUtil")
public class DsedSmsUtil extends AbstractThird {

    private final static String URL = "http://www.dh3t.com/json/sms/Submit";
    private final static String ACCOUNT = "dh15433";
    private final static String MARKETING_ACCOUNT = "dh15434";
    private final static String MARKETING_ACCOUNT_PASSWORD = "aSZqA6Ub";
    private final static String MARKET_ACCOUNT_EC = "dh15434";
    private final static String MARKET_ACCOUNT_PASSWORD_EC = "aSZqA6Ub";

    private final static String CODE_ACCOUNT_EC = "dh15437";
    private final static String CODE_ACCOUNT_PASSWORD_EC = "p8AbzB4C";

    private final static String SIGN = "【爱上街金融】";
    private final static String SIGN_AISHANGJIE = "【爱上街】";

    private static String password = null;
    private static String REGIST_TEMPLATE = "注册验证码为:&param1;您正在注册爱上街，请在30分钟内完成注册";
    private static String LOGIN_TEMPLATE = "验证码:&param1,您正在确认登录，30分钟内输入有效。";
    private static String FORGET_TEMPLATE = "验证码为:&param1;您正在找回爱上街的账户密码，请在30分钟内完成";
    private static String SET_TEMPLATE = "验证码为:&param1;您正在设置爱上街的账户密码，请在30分钟内完成";
    private static String BIND_TEMPLATE = "验证码为:&param1;您正在爱上街绑定手机号，请在30分钟内完成";
    private static String SETPAY_TEMPLATE = "验证码为:&param1;您正在设置爱上街支付密码，请在30分钟内完成";
    private static String SET_JKCR_PAY_TEMPLATE = "验证码为:&param1;您正在设置借款超人支付密码，请在30分钟内完成";
    private static String EMAIL_TEMPLATE = "验证码为:&param1;您正在设置爱上街更换绑定邮箱，请在30分钟内完成";
    private static String GOODS_RESERVATION_SUCCESS = "恭喜你！预约成功！OPPOR11将于6月22日10点准时开售，提前0元预约购机享12期免息更有超级返利300元，有！ 且只在爱上街。回复td退订";
    private static String IPHONE_RESERVATION_SUCCESS = "恭喜你预约成功！9月20日正式发售苹果新机，下单立减100元！分期无忧，返利抵账单！http://t.cn/RI7CSL2 回T退订";
    private static String REGIST_SUCCESS_TEMPLATE = "认证送10元现金，借/还成功再抽现金，100%中奖，最高1888元，最低50元 http://t.cn/RI7CSL2 退订回T";
    private static String REBATE_COMPLETED = "返利入账通知，%s，您购买商品/服务的返利已入账%s元，可登录爱上街查看详情";
    private static String DEFAULT_PASSWORD = "您已注册成功，默认密码%s，登录即可领取最高20000额度！";
    private static String TRADE_PAID_SUCCESS = "信用消费提醒，您于%s成功付款%s元，最近还款日期为%s，可登录爱上街核对账单";
    private static String TRADE_HOME_PAID_SUCCESS = "信用消费提醒，您于%s成功付款%s元，最近还款日期为%s，可登录爱上街核对账单";
    private static String TEST_VERIFY_CODE = "888888";
    private static String BorrowBillMessageSuccess = "您x月份分期账单代扣还款成功，请登录爱上街查看详情。";
    private static String GAME_PAY_RESULT = "您为%s充值已经%s。";
    private static String ZHI_BIND = "验证码：&param1，您正在关联支付宝账号，请勿向他人泄露；";
    private static String RECYCLE_REBATE_SUCCESS = "您的回收订单已完成，账户到账返现%s元，其中包含回收订单金额%s元，订单返现%s元，快去我的账户中查看吧~";//回收业务成功返现
    private static String RECYCLE_MIN_AMOUNT_WARN = "有得卖在爱上街回收业务中的预存款余额为%s，请尽快打款充值！";//余额最低阀值
    private static String SIGN_REWARD_WITHDRAW_WARN = "签到领现金业务今日提现金额已超过最大值，请及时检查提现是否存在异常";

    // public static String sendUserName = "suweili@edspay.com";
    // public static String sendPassword = "Su272727";

    private static final String sendHostAddress = "smtp.mxhichina.com";// 发送邮件使用的服务器的地址
    @Resource
    AfSmsRecordService afSmsRecordService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfUserOutDayDao afUserOutDayDao;
    @Resource
    private  BizCacheUtil bizCacheUtil;
    @Resource
    DsedResourceService dsedResourceService;







    /**
     * resource配置统一短信发送
     * value:短信内容  value1:开关 1开 0关   value2:单日单个用户失败短信发送次数限制，防刷使用
     * @param mobile
     * @param errorTimes
     * @param resourceType
     * @param resourceSecType
     * @return
     */
    public boolean sendConfigMessageToMobile(String mobile, Map<String,String> replaceMapData, int errorTimes,String resourceType,String resourceSecType) {
        DsedResourceDo resourceDo = dsedResourceService.getConfigByTypesAndSecType(resourceType, resourceSecType);
        try {
            //发送短信的大开关
            if (resourceDo != null && "1".equals(resourceDo.getValue1())) {
                //单日单个用户发送次数限制校验
                int maxSendTimes = NumberUtil.objToIntDefault(resourceDo.getValue2(), 0);
                if (maxSendTimes < errorTimes) {
                    logger.error("sendConfigMessageToMobile false,name="+resourceDo.getName()+",resourceType="+resourceType+",resourceSecType="+resourceSecType+",maxSendTimes:" + maxSendTimes + ",errorTimes:" + errorTimes + ",mobile:" + mobile);
                    return false;
                }
                String content = StringUtil.null2Str(resourceDo.getValue());
                content = StringUtil.convertMessageByMapInfo("&", content, replaceMapData);
                logger.error("sendConfigMessageToMobile success,name="+resourceDo.getName()+",resourceType="+resourceType+",resourceSecType="+resourceSecType+",mobile:" + mobile + "content:" + content);
                DsedSmsResult smsResult = sendSmsToDhst(mobile, content);
                return smsResult.isSucc();
            } else {
                logger.error("sendConfigMessageToMobile false,name="+resourceDo.getName()+",resourceType="+resourceType+",resourceSecType="+resourceSecType+",send onoff status:" + (resourceDo != null ? resourceDo.getValue1() : "off") + ",mobile:" + mobile);
            }
        } catch (Exception e) {
            logger.error("sendConfigMessageToMobile exception,name="+resourceDo.getName()+",resourceType="+resourceType+",resourceSecType="+resourceSecType+",send onoff status:" + (resourceDo != null ? resourceDo.getValue1() : "off") + ",mobile:" + mobile);
        }
        return false;
    }



    /**
     * 对单个手机号发送短消息，这里不验证手机号码有效性
     *
     * @param mobiles
     * @param content
     */
    public DsedSmsResult sendSmsToDhst(String mobiles, String content) {
        DsedSmsResult result = new DsedSmsResult();
        logger.info("sendSms params=|"+mobiles+"content="+content);
        if (StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE), Constants.INVELOMENT_TYPE_TEST)) {
            result.setSucc(true);
            result.setResultStr("test");
            return result;
        }
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("account", ACCOUNT);
        paramsMap.put("password", DigestUtil.MD5(getPassword()).toLowerCase());
        paramsMap.put("phones", mobiles);
        paramsMap.put("content", content);
        paramsMap.put("sign", SIGN);
        String reqResult = HttpUtil.doHttpPost(URL, JSONObject.toJSONString(paramsMap));

        logger.info(StringUtil.appendStrs("sendSms params=|", mobiles, "|", content, "|", reqResult));

        JSONObject json = JSON.parseObject(reqResult);
        if (json.getInteger("result") == 0) {
            result.setSucc(true);
            result.setResultStr(json.getString("desc"));
        } else {
            result.setSucc(false);
            result.setResultStr(json.getString("desc"));
        }
        return result;
    }




    private static void sendEmailToDhst(String email, String content) throws Exception {

        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties(); // 参数配置
        props.setProperty("mail.transport.protocol", "smtp"); // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.host", sendHostAddress); // 发件人的邮箱的 SMTP 服务器地址


        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true); // 设置为debug模式, 可以查看详细的发送 log

        String sendUserName = AesUtil.decrypt("mGviJyFnmdEcOb6VlJ6zl/H9G6dyjgAb1cb/tUCNgZM=",
                "testC1b6x@6aH$2dlw");
        String sendPassword = AesUtil.decrypt("BeSHxmaAFBhNCE2gIdPVRg==",
                "testC1b6x@6aH$2dlw");
        // 3. 创建一封邮件
        MimeMessage message = createMimeMessage(session, sendUserName, email, content);

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器
        // 这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
        transport.connect(sendUserName, sendPassword);

        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人,
        // 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 7. 关闭连接
        transport.close();

    }

    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail, String content)
            throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "爱上街", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "", "UTF-8"));
        message.setSubject("爱上街邮箱验证吗", "UTF-8");
        message.setContent(content, "text/html;charset=UTF-8");
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        return message;
    }

    /**
     * 短信记录表中增加记录
     *
     * @param smsType
     * @param mobile
     * @param verifyCode
     * @param userId
     * @param smsResult
     * @return
     */
    private int addSmsRecord(SmsType smsType, String mobile, String verifyCode, Long userId, SmsResult smsResult) {
        if (StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE),
                Constants.INVELOMENT_TYPE_TEST)) {
            verifyCode = TEST_VERIFY_CODE;
        }
        AfSmsRecordDo recordDo = new AfSmsRecordDo();
        recordDo.setSendAccount(mobile);
        recordDo.setUserId(userId);
        recordDo.setType(smsType.getCode());
        recordDo.setVerifyCode(verifyCode);
        recordDo.setResult(smsResult.getResultStr());
        return afSmsRecordService.addSmsRecord(recordDo);
    }



    private static String getPassword() {
        if (password == null) {
            password = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SMS_DHST_PASSWORD),
                    ConfigProperties.get(Constants.CONFKEY_AES_KEY));
        }
        return password;
    }








    public   String rules(String mobile){
        String switchRule = (String)bizCacheUtil.getObject("sms_switch");
        if(switchRule == null){
            switchRule = "YF:01234,DH:56789";
            List<AfResourceDo> resouces = afResourceService.getConfigByTypes("sms_switch");
            if(resouces !=null || resouces.size()>0){
                AfResourceDo resourceDo = resouces.get(0);
                switchRule = resourceDo.getValue();
            }
            bizCacheUtil.saveObject("sms_switch",switchRule);
        }
        String[] rules = switchRule.split(",");
        for(String rule : rules){

            if(rule.split(":")[1].contains(mobile.substring(10,11))){
                return rule.split(":")[0];
            }
        }

        return "DH";
    }
    public static void main(String [] args){
        try {

            sendEmailToDhst("3184343296@qq.com", "<table border=1><tr><th>支付宝流水号</th><th>商户订单号</th><th>账务类型</th><th>收入（+元）</th><th>支出（-元）</th><th>账户余额（元）</th><th>服务费（元）</th><th>支付渠道</th><th>签约产品</th><th>对方账户</th><th>对方名称</th><th>银行订单号</th><th>商品名称</th><th>备注</th></tr><tr><td>3029672583413669</td><td> </td><td>转账</td><td>100.00</td><td> </td><td>455328.03</td><td>0</td><td> </td><td> </td><td>15293971826</td><td>秦继强</td><td> </td><td> </td><td>秦继强15293971826</td></tr></table>");
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

    }
}


class DsedSmsResult {
    private boolean isSucc;
    private String resultStr;

    public boolean isSucc() {
        return isSucc;
    }

    public void setSucc(boolean isSucc) {
        this.isSucc = isSucc;
    }

    public String getResultStr() {
        return resultStr;
    }

    public void setResultStr(String resultStr) {
        this.resultStr = resultStr;
    }

}
