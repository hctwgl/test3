package com.ald.fanbei.api.web.third.controller;

import com.ald.fanbei.api.biz.service.AfUserToutiaoService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserToutiaoDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author huangqiujun 2017/9/4 下午1:46
 * @类描述: ToutiaoController
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Controller
@RequestMapping("/third/toutiao")
public class ToutiaoController extends BaseController {

    @Resource
    AfUserToutiaoService afUserToutiaoService;

    //private static String secreat_key = "ffbba6e3-9edc-456d-8de2-bdffdbee22d7";

    @RequestMapping(value = {"/getUser"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getUser( HttpServletRequest request, HttpServletResponse response) throws Exception{
        JSONObject returnjson = new JSONObject();
        try{
            StringBuilder sb = new StringBuilder();
            sb.append("---kdnotify begin:");
            Map<String, String[]> paramMap = request.getParameterMap();
            for (String key : paramMap.keySet()) {
                String[] values = paramMap.get(key);
                for (String value : values) {
                    sb.append("键:" + key + ",值:" + value);
                }
            }
            sb.append("---kdnotify end");

            thirdLog.info(sb.toString());
            //String mac = ObjectUtils.toString(request.getParameter("mac"), null);
            String imei = ObjectUtils.toString(request.getParameter("imei"), null);
            String androidid = ObjectUtils.toString(request.getParameter("androidid1"), null);
            String idfa = ObjectUtils.toString(request.getParameter("idfa"), null);
            //String udid = ObjectUtils.toString(request.getParameter("udid"), null);
            //String os = ObjectUtils.toString(request.getParameter("os"), null);
            //String callbackUrl = ObjectUtils.toString(request.getParameter("callback_url"), null);

            if(StringUtil.isNotEmpty(imei)||StringUtil.isNotEmpty(androidid)||StringUtil.isNotEmpty(idfa)){
                AfUserToutiaoDo tdo = afUserToutiaoService.getUser(imei,androidid,idfa);
                if(tdo==null){
                    Map<String, Object> map = buildParamMap(request);
                    tdo = (AfUserToutiaoDo)map.get("afUserToutiaoDo");
                    afUserToutiaoService.creatUser(tdo);
                    logger.error("toutiaoresult:creat success:"+tdo.toString());
                }else{
                    //int active = tdo.getActive();
                    Long rid = tdo.getRid();
                    afUserToutiaoService.uptUser(rid);
                    logger.error("toutiaoresult:update success"+tdo.toString());
                    /*if(active!=0){
                        String result= HttpUtil.doGet(callbackUrl,20);
                        logger.error("toutiaoresult:update success,active=1,callbacr_url="+callbackUrl+",result="+result);
                    }else{
                        logger.error("toutiaoresult:update success,active=0,id="+rid);
                    }*/
                }
            }else{
                returnjson.put("ret",-1);
                returnjson.put("msg","missing params");
                logger.error("toutiaoresult:all used params are null");
                return returnjson;
            }
        } catch (Exception e) {
            returnjson.put("ret",-1);
            returnjson.put("msg","missing params");
            logger.error("toutiaoresult:catch error",e.getMessage());
            return returnjson;
        }

        returnjson.put("ret",0);
        returnjson.put("msg","success");
        return returnjson;
    }


    private Map<String, Object> buildParamMap( HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        String aid = ObjectUtils.toString(request.getParameter("adid"), null);
        String cid = ObjectUtils.toString(request.getParameter("cid"), null);
        String cSite = ObjectUtils.toString(request.getParameter("csite"), null);
        Integer cType = NumberUtil.objToIntDefault(request.getParameter("ctype"), null);
        String idfa = ObjectUtils.toString(request.getParameter("idfa"), null);
        String mac = ObjectUtils.toString(request.getParameter("mac"), null);
        Integer os = NumberUtil.objToIntDefault(request.getParameter("os"), null);
        String androidId = ObjectUtils.toString(request.getParameter("androidid"), null);
        String androidId1 = ObjectUtils.toString(request.getParameter("androidid1"), null);
        String imei = ObjectUtils.toString(request.getParameter("imei"), null);
        String uuid = ObjectUtils.toString(request.getParameter("uuid"), null);
        String openudId = ObjectUtils.toString(request.getParameter("openudid"), null);
        String udid = ObjectUtils.toString(request.getParameter("udid"), null);
        String ip = ObjectUtils.toString(request.getParameter("ip"), null);
        String timeStamp = ObjectUtils.toString(request.getParameter("timestamp"), null);
        String callbackUrl = ObjectUtils.toString(request.getParameter("callback_url"), null);
        if(StringUtil.isNotEmpty(callbackUrl)){
            /*if(callbackUrl.indexOf("&event_type")==-1){
                callbackUrl+="&event_type=1";
            }*/
            if(os!=null){
                if(callbackUrl.indexOf("&os")==-1){
                    callbackUrl = callbackUrl + "&os=" + os;
                }
            }
        }
        AfUserToutiaoDo afUserToutiaoDo = new AfUserToutiaoDo();
        afUserToutiaoDo.setAid(aid);
        afUserToutiaoDo.setCid(cid);
        afUserToutiaoDo.setCsite(cSite);
        afUserToutiaoDo.setCtype(cType);
        afUserToutiaoDo.setIdfa(idfa);
        afUserToutiaoDo.setAndroidid(androidId);
        afUserToutiaoDo.setAndroidid1(androidId1);
        afUserToutiaoDo.setCallbackUrl(callbackUrl);
        afUserToutiaoDo.setImei(imei);
        afUserToutiaoDo.setIp(ip);
        afUserToutiaoDo.setMac(mac);
        afUserToutiaoDo.setOpenudid(openudId);
        afUserToutiaoDo.setOs(os);
        afUserToutiaoDo.setUdid(udid);
        afUserToutiaoDo.setTs(timeStamp);
        afUserToutiaoDo.setUuid(uuid);
        map.put("afUserToutiaoDo",afUserToutiaoDo);
        return map;
    }

    private static String md5(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.toString().getBytes());
        return bytesToHex(md.digest());
    }

    private static String bytesToHex(byte[] ch) {
        StringBuffer ret = new StringBuffer("");
        for (int i = 0; i < ch.length; i++)
            ret.append(byteToHex(ch[i]));
        return ret.toString();
    }

    /**
     * 字节转换为16进制字符串
     */
    private static String byteToHex(byte ch) {
        String str[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
        return str[ch >> 4 & 0xF] + str[ch & 0xF];
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
