package com.ald.fanbei.api.web.third.controller;

import com.ald.fanbei.api.biz.service.AfUnionLoginLogService;
import com.ald.fanbei.api.biz.service.AfUnionLoginRegisterService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.unionlogin.Codec;
import com.ald.fanbei.api.common.unionlogin.JdqMessageSign;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUnionLoginLogDo;
import com.ald.fanbei.api.dal.domain.AfUnionLoginRegisterDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
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
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static com.ald.fanbei.api.common.unionlogin.JdqMessageSign.SECRET_KEY;

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

    public static final String RETURN_URL = "/unionlogin/welcome?isNew=%s&token=%s";

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
    @RequestMapping(value = "/jdqLogin",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject jdqLogin(String apply_no, String channel_no, String apply_info, String user_attribute, String timestamp, String sign) throws Exception {

        //region 无论结果如何,记录loger日志

        String channel = "jdq";
        Map<String, String[]> paramsMap = request.getParameterMap();
        TreeMap<String, String> signMap = getSignMap(paramsMap);
        String paramsJsonStr = JSONObject.toJSONString(paramsMap);
        addLogs(channel, paramsJsonStr);
        //endregion

        //region 签名验证
        JSONObject jsonResult = new JSONObject();
        //签名校验
        String localSign = JdqMessageSign.signParams(SECRET_KEY, signMap);


        if (!localSign.equals(sign)) {
            jsonResult.put("code", "451");
            jsonResult.put("msg", "local sign:" + localSign + ",remote sign:" + sign);
            return jsonResult;
        }
        //endregion

        //region 数据解析
        if (StringUtils.isEmpty(apply_info) || StringUtils.isEmpty(user_attribute)) {
            jsonResult.put("code", "456");
            jsonResult.put("msg", "参数错误，不能被解析");
            return jsonResult;
        }
        JSONObject applyInfoJson = (JSONObject) JSONObject.parse(Codec.base64StrDecode(SECRET_KEY, apply_info).trim());
        JSONObject userAttributeJson = (JSONObject) JSONObject.parse(Codec.base64StrDecode(SECRET_KEY, user_attribute).trim());
        //endregion

        String phone = userAttributeJson.getString("mobilephone");
        if (StringUtils.isEmpty(phone)) {
            jsonResult.put("code", "456");
            jsonResult.put("msg", "参数错误，不能被解析");
            return jsonResult;
        }
        //数据库验证用户是否登录
        //1、查询用户信息

        AfUserDo userInfo = afUserService.getUserByUserName(phone);//查询数据库
        int is_new_user = 1;
        if (userInfo != null) {
            //2.查询用户来源，通过 手机号+渠道
            AfUnionLoginRegisterDo condition=new AfUnionLoginRegisterDo();
            condition.setUserId(userInfo.getRid());
            AfUnionLoginRegisterDo exist= afUnionLoginRegisterService.getByCommonCondition(condition);
            if(exist!=null){
                is_new_user = 2;
            }else{
                is_new_user=3;
            }
        } else {
            String password= afUnionLoginRegisterService.register(channel,phone,paramsJsonStr);
            smsUtil.sendDefaultPassword(phone,password) ;
        }

        afUnionLoginLogService.addLog(channel,phone,paramsJsonStr);
        String token = UserUtil.generateToken(phone);

        jsonResult.put("code", "0");
        jsonResult.put("is_new_user", String.valueOf(is_new_user));
        jsonResult.put("msg", "");
        String returnUrl = String.format(request.getRequestURL().toString().replace(request.getRequestURI(), RETURN_URL), is_new_user, token);
        jsonResult.put("apply_url", returnUrl);
        return jsonResult;
    }


    @RequestMapping("/welcome")
    public String welcome(int isNew,String token,HttpServletResponse response) throws Exception{
        return "/unionlogin/welcome";
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
                        signMap.put(key, value);
                    }
                }
            }

        }
        return signMap;
    }

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
