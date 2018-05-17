package com.ald.fanbei.api.web.third.controller;

import com.ald.fanbei.api.biz.service.AfUnionLoginChannelService;
import com.ald.fanbei.api.biz.service.AfUnionLoginLogService;
import com.ald.fanbei.api.biz.service.AfUnionLoginRegisterService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.unionlogin.Codec;
import com.ald.fanbei.api.common.unionlogin.FanBeiSecret;
import com.ald.fanbei.api.common.unionlogin.JFSecret;
import com.ald.fanbei.api.common.unionlogin.JdqMessageSign;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.AfUnionLoginChannelDo;
import com.ald.fanbei.api.dal.domain.AfUnionLoginLogDo;
import com.ald.fanbei.api.dal.domain.AfUnionLoginRegisterDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.System.out;


@Controller
@RequestMapping("/unionlogin")
public class UnionLoginController extends BaseController {
    protected final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");//第三方调用日志
    @Autowired
    HttpServletRequest request;
    @Autowired
    AfUserService afUserService;
    @Autowired
    SmsUtil smsUtil;
    @Autowired
    AfUnionLoginLogService afUnionLoginLogService;
    @Autowired
    AfUnionLoginRegisterService afUnionLoginRegisterService;
    @Autowired
    AfUnionLoginChannelService afUnionLoginChannelService;

    public static final String RETURN_URL = "/unionlogin/welcome?isNew=%s&token=%s&channel=%s";

    /**
     * 借点钱登录对接
     * see url:http://wiki.rongzhijia.com/pages/viewpage.action?pageId=11372422
     *
     * @param apply_no       借点钱对于当前申请的唯一标识
     * @param channel_no     借点钱渠道标识,由机构定义
     * @param apply_info     json 格式字符串，用户的本次申请的申请信息（参见以下apply_info 描述）
     *                       apply_amount	String	申请的贷款金额，单位，元	是	10000
     *                       apply_loan	String	申请的贷款名称	是	工薪贷
     *                       apply_terms	String	申请的贷款期限(单位:天)。如产品期限单位为月，将数字除以30。如180代表6个月	是	180
     *                       timestamp	String	申请时刻的UNIX时间戳（毫秒级）	是	1476772628537
     * @param user_attribute json 格式字符串，用户信息（参见 user_attribute 描述）
     *                       mobilephone	String	申请人手机号,经验证的手机号码	是	18670372832
     *                       name	String	申请人姓名	是	张三
     *                       idcard	String	申请人填写的身份证号码	是	421202199308183266
     *                       timestamp	String	申请时刻的UNIX时间戳（毫秒级）	是	1476772628537
     * @param timestamp
     * @param sign
     * @return code    String	返回代码，0表示成功，其他标识错误	是	0
     * msg	String	错误描述，用户可读的错误信息，可能会呈现给用户	是	“今天申请的人数已经超过限制，请明天早点再来尝试”
     * is_new_user	String	是否为机构的新注册用户；1:是 ，2：通过借点钱渠道推送的老用户，3：其他渠道推送的老用户	是	1
     * apply_url	String	用户需要跳转的url（登陆后的页面，必须带上协议头, http:// 或者 https:// ），如果用户不需要跳转页面，此项为空	否	http://m.xyqb.com
     */
    @RequestMapping(value = "/jdqLogin", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject jdqLogin(String apply_no, String channel_no, String apply_info, String user_attribute, String timestamp, String sign) throws Exception {

        //region 无论结果如何,记录loger日志

        String fanbeiChannelCode = "jdq";
        Map<String, String[]> paramsMap = request.getParameterMap();
        TreeMap<String, String> signMap = getSignMap(paramsMap);
        String paramsJsonStr = JSONObject.toJSONString(paramsMap);
        addLogs(fanbeiChannelCode, paramsJsonStr);
        //endregion

        //region 签名验证
        JSONObject jsonResult = new JSONObject();
        AfUnionLoginChannelDo channelDo = getsetChannel(fanbeiChannelCode, channel_no);
        //签名校验
        String localSign = JdqMessageSign.signParams(channelDo.getSecretKey(), signMap);


        if (!localSign.equals(sign)) {
            thirdLog.error("local sign " + sign + ",remote sign" + sign);
            jsonResult.put("code", "451");
            jsonResult.put("msg", "invalid sign,remote sign:" + sign);
            return jsonResult;
        }
        //endregion

        //region 数据解析
        if (StringUtils.isEmpty(apply_info) || StringUtils.isEmpty(user_attribute)) {
            jsonResult.put("code", "456");
            jsonResult.put("msg", "参数错误，不能被解析");
            return jsonResult;
        }
        try {
            JSONObject applyInfoJson = (JSONObject) JSONObject.parse(Codec.base64StrDecode(channelDo.getSecretKey(), apply_info).trim());
            JSONObject userAttributeJson = (JSONObject) JSONObject.parse(Codec.base64StrDecode(channelDo.getSecretKey(), user_attribute).trim());
            //endregion

            String phone = userAttributeJson.getString("mobilephone");
            if (StringUtils.isEmpty(phone)) {
                jsonResult.put("code", "456");
                jsonResult.put("msg", "参数错误，不能被解析");
                return jsonResult;
            }
            //数据库验证用户是否登录
            //1、查询用户信息

            int is_new_user = getsetUserInfo(phone, fanbeiChannelCode, paramsJsonStr);


            afUnionLoginLogService.addLog(fanbeiChannelCode, phone, paramsJsonStr);
            String token = UserUtil.generateToken(phone);

            jsonResult.put("code", "0");
            jsonResult.put("is_new_user", String.valueOf(is_new_user));
            jsonResult.put("msg", "");
            String returnUrl = String.format(request.getRequestURL().toString().replace(request.getRequestURI(), RETURN_URL), is_new_user, token,channel_no);
            if(request.getRequestURL().toString().indexOf("testapp")==-1){
                returnUrl=returnUrl.replace("http:","https:");
            }
            jsonResult.put("apply_url", returnUrl);
        } catch (Exception e) {
            thirdLog.error("jdqLogin error:", e);
            jsonResult.put("code", "550");
            jsonResult.put("msg", e.getCause());
        }

        return jsonResult;
    }

    /**
     * 现金超人对接
     *
     * @param mobile 手机号码
     * @param source 来源
     * @param sign   签名
     * @return {
     * error_code 正确返回0 ，非0错误
     * error_reason 错误原因
     * sign_url  登陆成功跳转地址
     * }
     */

    @RequestMapping("/xjcrLogin")
    @ResponseBody
    public JSONObject xjcrLogin(String mobile, String source, String sign) throws Exception {
        String fanbeiChannelCode = "xjcr";

        Map<String, String[]> paramsMap = request.getParameterMap();
        TreeMap<String, String> signMap = getSignMap(paramsMap);
        String paramsJsonStr = JSONObject.toJSONString(paramsMap);
        addLogs(fanbeiChannelCode, paramsJsonStr);
        AfUnionLoginChannelDo channelDo = getsetChannel(fanbeiChannelCode, source);
        String localSign = JdqMessageSign.signParams(signMap, "&key=" + channelDo.getSecretKey());
        JSONObject jsonResult = new JSONObject();
        if (!localSign.toUpperCase().equals(sign)) {
            jsonResult.put("error_code", "-2");
            jsonResult.put("error_reason", "签名错误,remote sign:" + sign);
            return jsonResult;
        }
        int is_new_user = getsetUserInfo(mobile, fanbeiChannelCode, paramsJsonStr);

        jsonResult.put("error_code", "0");
        jsonResult.put("result_code", is_new_user == 1 ? 1 : 2);
        jsonResult.put("error_reason", "");
        jsonResult.put("result_reason", is_new_user == 1 ? "新用户" : "老用户");
        afUnionLoginLogService.addLog(fanbeiChannelCode, mobile, paramsJsonStr);
        String token = UserUtil.generateToken(mobile);
        String returnUrl = String.format(request.getRequestURL().toString().replace(request.getRequestURI(), RETURN_URL), is_new_user, token,fanbeiChannelCode);
        if(request.getRequestURL().toString().indexOf("testapp")==-1){
            returnUrl=returnUrl.replace("http:","https:");
        }
        jsonResult.put("apply_url", returnUrl);
        return jsonResult;
    }

    /**
     * 玖富登录
     *
     * @param channel_id    加密渠道id
     * @param dingdang_id   暂不使用
     * @param serial_number 暂不使用
     * @param mobile        手机号
     * @param name          姓名
     * @param cert_id       身份证号码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/jfLogin",produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String jfLogin(String channel_id, String dingdang_id, String serial_number, String mobile, String name, String cert_id) throws Exception {
        try {
            Map<String, String[]> paramsMap = request.getParameterMap();
            String paramsJsonStr = JSONObject.toJSONString(paramsMap);
            String fanbeiChannelCode = "jf";
            //region 查询渠道信息
            AfUnionLoginChannelDo afUnionLoginChannelDo = new AfUnionLoginChannelDo();
            afUnionLoginChannelDo.setCode(fanbeiChannelCode);
            AfUnionLoginChannelDo channelDo = afUnionLoginChannelService.getByCommonCondition(afUnionLoginChannelDo);
            //endregion

            //region 必要信息解密
            String privateKeyStr = channelDo.getValue1();
            String publicKeyStr = channelDo.getValue2();
            PrivateKey privateKey = JFSecret.getPrivateKey(privateKeyStr);
            PublicKey publicKey = JFSecret.getPublicKey(publicKeyStr);
            String channelDec = JFSecret.decrypt(privateKey, channel_id);


            if (StringUtils.isEmpty(channelDo.getThirdChannelCode())) {
                //设置第三方的渠道号码
                channelDo.setThirdChannelCode(channelDec);
                afUnionLoginChannelService.updateById(channelDo);
            }
            String mobileDec = JFSecret.decrypt(privateKey, mobile);
//        String nameDec = JFSecret.decrypt(privateKey, name);
//        String cert_idDec = JFSecret.decrypt(privateKey, cert_id);

            //region 必要信息解密
            logger.info("解密第三方渠道号:" + channelDec + "，解密手机号:" + mobileDec);
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("channel_id", channel_id);
            jsonObject.put("serial_number", serial_number);
            jsonObject.put("regist_code", JFSecret.encrypt(publicKey, "1".getBytes()));
            int is_new_user = getsetUserInfo(mobileDec, fanbeiChannelCode, paramsJsonStr);
            afUnionLoginLogService.addLog(fanbeiChannelCode, mobileDec, paramsJsonStr);
            String token = UserUtil.generateToken(mobileDec);
            String returnUrl = String.format(request.getRequestURL().toString().replace(request.getRequestURI(), RETURN_URL), is_new_user, token,channelDec);
            if(request.getRequestURL().toString().indexOf("testapp")==-1){
                returnUrl=returnUrl.replace("http:","https:");
            }
            JSONObject jsonResultObject = new JSONObject();
            jsonResultObject.put("message", "成功");
            jsonResultObject.put("status", "1");
            jsonResultObject.put("code", "0");

            logger.info("返回 url 地址:" + returnUrl);
            jsonObject.put("url",  returnUrl);
            jsonResultObject.put("data", jsonObject);
            return jsonResultObject.toJSONString();
        }catch (Exception e) {
            JSONObject jsonResultObject = new JSONObject();
            thirdLog.error("jfLogin error:", e);
            jsonResultObject.put("message", "系统异常，请稍后再试！");
            jsonResultObject.put("status", "0");
            jsonResultObject.put("code", "0");
            return jsonResultObject.toJSONString();
        }
    }

    /**
     * 爱上街统一联合登录
     *
     * @param channel 渠道 明文
     * @param phone   手机号 加密
     * @param sign    签名
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fanbeiLogin", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject fanbeiLogin(String phone, String channel, String sign) throws Exception {
        try {
            phone=phone.replace(" ","+");
            Map<String, String[]> paramsMap = request.getParameterMap();
            String paramsJsonStr = JSONObject.toJSONString(paramsMap);
            String fanbeiChannelCode = channel;
            addLogs(fanbeiChannelCode, paramsJsonStr);

            //取非空参数作为签名
            TreeMap<String, String> signMap = getSignMap(paramsMap);
            //endregion

            //region 签名验证
            JSONObject jsonResult = new JSONObject();
            AfUnionLoginChannelDo condition = new AfUnionLoginChannelDo();
            condition.setCode(fanbeiChannelCode);
            AfUnionLoginChannelDo channelDo = afUnionLoginChannelService.getByCommonCondition(condition);
            //签名校验
            String localSign = FanBeiSecret.signParams(channelDo.getSecretKey(), signMap);
            if (!localSign.equals(sign)) {
                thirdLog.error("fanbeiLogin local sign " + sign + ",remote sign" + sign);
                jsonResult.put("code", "1002");
                jsonResult.put("msg", "签名错误");
                return jsonResult;
            }


            //region 必要信息解密

            String phoneStr = "";
            try {
                byte[] phoneDecodeBase64Byte = Base64.decodeBase64(phone);
                byte[] phoneDecodeAesByte = FanBeiSecret.decode(channelDo.getSecretKey(), phoneDecodeBase64Byte);
                phoneStr = new String(phoneDecodeAesByte, "utf-8").trim();

            } catch (Exception e) {
                jsonResult.put("code", "1001");
                jsonResult.put("msg", "解密出错");
                return jsonResult;
            }

            //endregion 必要信息解密

            int is_new_user = getsetUserInfo(phoneStr, fanbeiChannelCode, paramsJsonStr);
            afUnionLoginLogService.addLog(fanbeiChannelCode, phoneStr, paramsJsonStr);
            String token = UserUtil.generateToken(phoneStr);
            String returnUrl = String.format(request.getRequestURL().toString().replace(request.getRequestURI(), RETURN_URL), is_new_user, token,channel);
            JSONObject jsonResultObject = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            jsonResultObject.put("msg", "成功");
            jsonResultObject.put("code", "0");
            jsonObject.put("user_state", is_new_user);
            if(request.getRequestURL().toString().indexOf("testapp")==-1){
                returnUrl=returnUrl.replace("http:","https:");
            }
            jsonObject.put("return_url", returnUrl);
            jsonResultObject.put("data", jsonObject);
            return jsonResultObject;
        } catch (Exception e) {
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("code", "500");
            jsonResult.put("msg", "服务器操作错误");
            thirdLog.error("fanbeiLogin error：", e);
            return jsonResult;
        }

    }

    /**
     * 登陆成功后的欢迎页面
     *
     * @param isNew 是否是新用户
     * @param token 登录令牌，暂时不使用
     * @return 页面
     * @throws Exception
     */
    @RequestMapping("/welcome")
    public String welcome(int isNew, String token,String channel) throws Exception {
        thirdLog.info("union login view："+channel);
        return "/unionlogin/welcome";
    }

    /**
     * 获取/设置用户信息
     *
     * @param mobile        手机号
     * @param channelCode   爱上街提供的渠道信息
     * @param paramsJsonStr 参数原样保存
     * @return 1:新用户 2:当前渠道的老用户 3:其他渠道的老用户
     * @throws Exception
     */
    private int getsetUserInfo(String mobile, String channelCode, String paramsJsonStr) throws Exception {
        int is_new_user = 1;
        AfUserDo userInfo = afUserService.getUserByUserName(mobile);//查询数据库
        if (userInfo != null) {
            //2.查询用户来源，通过 手机号+渠道
            AfUnionLoginRegisterDo condition = new AfUnionLoginRegisterDo();
            condition.setUserId(userInfo.getRid());
            condition.setChannelCode(channelCode);
            AfUnionLoginRegisterDo exist = afUnionLoginRegisterService.getByCommonCondition(condition);
            if (exist != null) {
                is_new_user = 2;
            } else {
                is_new_user = 3;
            }
        } else {
            String password = afUnionLoginRegisterService.register(channelCode, mobile, paramsJsonStr);
            smsUtil.sendDefaultPassword(mobile, password,channelCode);
        }
        return is_new_user;
    }

    /**
     * 获取并设置第三方渠道信息
     *
     * @param fanbeiChannelCode 爱上街的渠道code
     * @param thridChannelCode  第三方渠道code
     * @return 渠道信息
     */
    public AfUnionLoginChannelDo getsetChannel(String fanbeiChannelCode, String thridChannelCode) {
        try {
            AfUnionLoginChannelDo condition = new AfUnionLoginChannelDo();
            condition.setCode(fanbeiChannelCode);
            AfUnionLoginChannelDo channelDo = afUnionLoginChannelService.getByCommonCondition(condition);
            if (StringUtils.isEmpty(channelDo.getThirdChannelCode())) {
                //设置第三方的渠道号码
                channelDo.setThirdChannelCode(thridChannelCode);
                afUnionLoginChannelService.updateById(channelDo);
            }
            return channelDo;
        } catch (Exception e) {
            logger.error("getsetChannel error:", e);
            return null;
        }

    }

    /**
     * 参数进行处理，去除sign和空参数
     *
     * @param paramsMap
     * @return
     */
    public TreeMap<String, String> getSignMap(Map<String, String[]> paramsMap) {
        TreeMap<String, String> signMap = new TreeMap<>();
        for (String key : paramsMap.keySet()) {
            String[] values = paramsMap.get(key);
            if (!key.equals("sign")) {
                for (String value : values) {
                    if (!StringUtils.isEmpty(value)) {
                        signMap.put(key, value.replace(" ","+"));
                    }
                }
            }

        }
        return signMap;
    }

    /**
     * 添加日志
     *
     * @param channel       爱上街渠道号
     * @param paramsJsonStr 参数信息
     */
    private void addLogs(String channel, String paramsJsonStr) {
        thirdLog.info("request from " + channel + ":" + paramsJsonStr);
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        return null;
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
}
