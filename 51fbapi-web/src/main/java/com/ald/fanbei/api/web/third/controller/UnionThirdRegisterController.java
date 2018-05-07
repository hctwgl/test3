package com.ald.fanbei.api.web.third.controller;

import com.ald.fanbei.api.biz.service.AfUnionThirdRegisterLogService;
import com.ald.fanbei.api.biz.service.AfUnionThirdRegisterService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.unionlogin.FanBeiSecret;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfLoanSupermarketDao;
import com.ald.fanbei.api.dal.dao.AfUnionThirdRegisterDao;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;
import com.ald.fanbei.api.dal.domain.AfUnionThirdRegisterDo;
import com.ald.fanbei.api.dal.domain.AfUnionThirdRegisterLogDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static com.ald.fanbei.api.common.unionlogin.FanBeiSecret.encode;

@Controller
@RequestMapping("/unionRegister")
public class UnionThirdRegisterController extends BaseController {

    @Autowired
    HttpServletRequest request;
    @Resource
    private AfLoanSupermarketDao afLoanSupermarketDao;
    @Resource
    AfUnionThirdRegisterDao afUnionThirdRegisterDao;
    @Resource
    private AfUnionThirdRegisterService afUnionThirdRegisterService;
    @Resource
    private AfUnionThirdRegisterLogService afUnionThirdRegisterLogService;
    /**
     * 爱上街统一联合注册
     *
     * @param lsmNo 渠道 明文
     * @param phone   手机号 明文
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/third/register", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject fanbeiLogin(String phone, String lsmNo) throws Exception {
         try {
            Map<String, String[]> paramsMap = request.getParameterMap();
            String paramsJsonStr = JSONObject.toJSONString(paramsMap);
            String channelCode = lsmNo;
            addLogs(channelCode, paramsJsonStr);
            JSONObject jsonResult = new JSONObject();
            String result = "";
            long request_time = 0;
            if(StringUtil.isNotEmpty(phone)&&StringUtil.isNotEmpty(channelCode)){
                //得到借款超市信息
                AfLoanSupermarketDo afLoanSupermarketDo = afLoanSupermarketDao.getLoanSupermarketByLsmNo(lsmNo);
                if(afLoanSupermarketDo!=null){
                    String secret_key = afLoanSupermarketDo.getLsmSecretKey(); //加密签名通用秘钥
                    String register_url = afLoanSupermarketDo.getRegisterUrl();//第三方接口url
                    byte[] phoneAesEncodeByte = encode(secret_key, phone);
                    String phoneBase64EncodeStr = Base64.encodeBase64String(phoneAesEncodeByte); //注意：用于后面加签
                    //签名
                    TreeMap<String,String> params=new TreeMap<>();
                    params.put("phone",phoneBase64EncodeStr);
                    params.put("channel",channelCode);
                    String sign= FanBeiSecret.signParams(secret_key, params);
                    params.put("sign",sign);
                    String paramsStr = paramTreeMapToString(params);
                    Date beforDT = new Date();
                    long time1 = beforDT.getTime();
                    result= HttpUtil.doHttpPost(register_url,
                            paramsStr.replace("+","%2B"));
                    Date afterDT = new Date();
                    long time2 = afterDT.getTime();
                    request_time = time2 - time1;
                    System.out.println(result);
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    System.out.println(jsonObject.toJSONString());
                    String code= jsonObject.getString("code");
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    if(StringUtil.isNotEmpty(code)&&StringUtil.equals("0",code)){
                        String url = jsonData.getString("return_url");
                        String userState = jsonData.getString("user_state");
                        jsonResult.put("code", "1000");
                        jsonResult.put("msg", "成功");
                        JSONObject data = new JSONObject();
                        data.put("user_state", userState);
                        data.put("return_url",url);
                        jsonResult.put("data", data);
                        if(afUnionThirdRegisterDao.getIsRegister(phone,lsmNo)==0){
                            AfUnionThirdRegisterDo rdo = new AfUnionThirdRegisterDo();
                            rdo.setChannelCode(channelCode);
                            rdo.setPhone(phone);
                            rdo.setReturnUrl(url);
                            rdo.setRequestInfo(result);
                            rdo.setGmtCreate(new Date());
                            if(StringUtil.isNotEmpty(userState)){
                                int state = Integer.parseInt(userState);
                                rdo.setUserState(state);
                            }
                            afUnionThirdRegisterService.saveRecord(rdo);
                        }
                    }else{
                        jsonResult.put("code", "1001");
                        jsonResult.put("msg", jsonObject.getString("msg"));
                    }

                }else{
                    jsonResult.put("code", "1002");
                    jsonResult.put("msg", "相应的借款超市不存在");
                }
            }else{
                jsonResult.put("code", "1003");
                jsonResult.put("msg", "参数为空");
            }
            AfUnionThirdRegisterLogDo ldo = new AfUnionThirdRegisterLogDo();
            ldo.setChannelCode(channelCode);
            ldo.setGmtModified(new Date());
            ldo.setPhone(phone);
            ldo.setReturnInfo(jsonResult.toJSONString());
            ldo.setReturnThirdInfo(result);
            ldo.setRequestTime(request_time);
            afUnionThirdRegisterLogService.saveRecord(ldo);
            return jsonResult;
        } catch (Exception e) {
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("code", "500");
            jsonResult.put("msg", "服务器操作错误");
            thirdLog.error("fanbeiLogin error：", e);
            return jsonResult;
        }
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

    /**
     * 拼接参数字符串
     *
     * @param paramMap
     * @return
     */
    public static String paramTreeMapToString(TreeMap<String, String> paramMap) {
        StringBuilder paramStrBuilder = new StringBuilder();

        Iterator<Map.Entry<String, String>> it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entrySet = it.next();
            paramStrBuilder.append(entrySet.getKey()).append('=').append(entrySet.getValue()).append('&');
        }
        return paramStrBuilder.substring(0, paramStrBuilder.length() - 1);
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
