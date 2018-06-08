package com.ald.fanbei.api.web.h5.controller;


import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SignRewardType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.context.ContextImpl;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.*;


/**
 * 分享成功 领取 奖励
 * @author cfp
 * @类描述：签到领金币
 */
@RestController
@RequestMapping(value = "/mySignInfo/",produces = "application/json;charset=UTF-8")
public class H5MySignInfoOutController extends H5Controller {

    @Resource
    AfUserService afUserService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfResourceService afResourceService;
    @Resource
    NumberWordFormat numberWordFormat;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfSignRewardService afSignRewardService;
    @Resource
    AfSignRewardExtService afSignRewardExtService;
    @Resource
    AfUserCouponService afUserCouponService;
    @Resource
    AfCouponService afCouponService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfUserAuthStatusService afUserAuthStatusService;
    @Resource
    AfTaskService afTaskService;


    /**
     * 自己签到
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/mySign", method = RequestMethod.POST)
    public String homePage(HttpServletRequest request, HttpServletResponse response) {
        Context context = buildContext(request);
        Integer appVersion = context.getAppVersion();
        String userName = ObjectUtils.toString(request.getParameter("userId"),null);
        String push = ObjectUtils.toString(request.getParameter("push"),"N");
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        if(null == afUserDo){
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc()).toString();
        }
        Map<String,Object> map = new HashMap<String,Object>();
        try {
            AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
            afSignRewardDo.setIsDelete(0);
            afSignRewardDo.setUserId(afUserDo.getRid());
            afSignRewardDo.setGmtCreate(new Date());
            afSignRewardDo.setGmtModified(new Date());
            afSignRewardDo.setType(SignRewardType.ZERO.getCode());
            afSignRewardDo.setStatus(0);
            AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("SIGN_COEFFICIENT");
            if(afResourceDo == null || numberWordFormat.isNumeric(afResourceDo.getValue())){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc()).toString();
            }
            if(afSignRewardService.isExist(afSignRewardDo.getUserId())){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_SIGN_EXIST.getDesc()).toString();
            }
            if(!userSign(afSignRewardDo,afResourceDo,map)){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_SIGN_FAIL.getDesc()).toString();
            }
            map = homeInfo(afUserDo.getRid(),map,push,appVersion);
            return H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(),"",map ).toString();
        } catch (FanbeiException e) {
            logger.error("commitRegister fanbei exception" + e.getMessage());
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        } catch (Exception e) {
            logger.error("commitRegister exception", e);
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        }
    }

    /**
     * 自己签到
     * @param afSignRewardDo
     * @return
     */
    private boolean userSign(AfSignRewardDo afSignRewardDo, final AfResourceDo afResourceDo,Map<String,Object> resp){
        boolean flag = afSignRewardService.checkUserSign(afSignRewardDo.getUserId());
        boolean result;
        String status = "" ;
        if(flag){//多次签到
            //判断是当前周期的第几天
            AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(afSignRewardDo.getUserId());
            Map<String,String> days = afSignRewardService.supplementSign(afSignRewardExtDo,0,"N");
            String str[] = days.get("signDays").toString().split(",");
            int count = 0;
            if(StringUtil.equals(days.get("signDays").toString(),"")){
                count = str.length;
            }else{
                count = str.length+1;
            }
            final BigDecimal rewardAmount = randomNum(afResourceDo.getValue3(),afResourceDo.getValue4()).setScale(2,RoundingMode.HALF_EVEN);
            afSignRewardDo.setAmount(rewardAmount);
            final AfSignRewardDo rewardDo = afSignRewardDo;
            if(count == 1){
                afSignRewardExtDo.setAmount(rewardAmount);
                final AfSignRewardExtDo signRewardExtDo = afSignRewardExtDo;
                afSignRewardExtDo.setFirstDayParticipation(new Date());
                status = transactionTemplate.execute(new TransactionCallback<String>() {
                    @Override
                    public String doInTransaction(TransactionStatus status) {
                        try{
                            afSignRewardService.saveRecord(rewardDo);
                            afSignRewardExtService.increaseMoney(signRewardExtDo);
                            return "success";
                        }catch (Exception e){
                            status.setRollbackOnly();
                            return "fail";
                        }
                    }
                });
            }else {
                if(str.length>1){
                    sortStr(str);
                }
                int maxCount = maxCount(str);
                Date before = DateUtil.formatDateToYYYYMMdd(afSignRewardExtDo.getFirstDayParticipation());
                Date after = DateUtil.formatDateToYYYYMMdd(new Date());
                StringBuffer buffer = new StringBuffer(days.get("signDays"));
                if(str.length>1){
                    buffer.append(",").append(DateUtil.getNumberOfDatesBetween(before,after)%10+1);
                }else {
                    buffer.append(DateUtil.getNumberOfDatesBetween(before,after)%10+1);
                }
                String arrayStr[] = buffer.toString().split(",");
                sortStr(arrayStr);
                int newMaxCount = maxCount(arrayStr);
                if(count >= 5 && count < 7){
                    //给予连续5天的奖励
                    if(maxCount < 5 && newMaxCount == 5){
                        status = fiveOrSevenSignDays(afSignRewardExtDo,rewardAmount,rewardDo,afResourceDo);
                    }else{
                        afSignRewardExtDo.setAmount(rewardAmount);
                        status = tenSignDays(rewardDo,afSignRewardExtDo);
                    }
                }else if(count >= 7 && count< 10){
                    //给予连续5天的奖励
                    if(maxCount < 5 && newMaxCount == 5){
                        status = fiveOrSevenSignDays(afSignRewardExtDo,rewardAmount,rewardDo,afResourceDo);
                    }else if(maxCount < 5 && newMaxCount == 7){//给予连续5天和7天的奖励
                        afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(2)).setScale(2, RoundingMode.HALF_EVEN));
                        status = fiveOrSevenSignDays(afSignRewardExtDo,afSignRewardExtDo.getAmount(),rewardDo,afResourceDo);
                    }else if(maxCount >= 5 && newMaxCount == 7){//给予连续7天的奖励
                        afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(2)).setScale(2,RoundingMode.HALF_EVEN));
                        status = tenSignDays(rewardDo,afSignRewardExtDo);
                    }else{
                        afSignRewardExtDo.setAmount(rewardAmount);
                        status = tenSignDays(rewardDo,afSignRewardExtDo);
                    }
                }else if(count == 10){
                    //给予连续7天和10天的奖励
                    if(maxCount < 7){
                        afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(4)).setScale(2,RoundingMode.HALF_EVEN));
                        status = signDays(rewardDo,afSignRewardExtDo,afResourceDo);
                    }else{
                        afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(3)).setScale(2,RoundingMode.HALF_EVEN));
                        status = signDays(rewardDo,afSignRewardExtDo,afResourceDo);
                    }

                }else {//给予普通签到的奖励
                    afSignRewardExtDo.setAmount(rewardAmount);
                    status = tenSignDays(rewardDo,afSignRewardExtDo);
                }
            }
            resp.put("amount",afSignRewardExtDo.getAmount().toString());
        }else {//第一次签到
            final BigDecimal rewardAmount = randomNum(afResourceDo.getValue1(),afResourceDo.getValue2()).setScale(2,RoundingMode.HALF_EVEN);
            afSignRewardDo.setAmount(rewardAmount);
            final AfSignRewardDo rewardDo = afSignRewardDo;
            logger.info("cfp sign_reward" + rewardDo);
            status = transactionTemplate.execute(new TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    try{
                        AfSignRewardExtDo afSignRewardExtDo = new AfSignRewardExtDo();
                        afSignRewardExtDo.setUserId(rewardDo.getUserId());
                        afSignRewardExtDo.setGmtModified(new Date());
                        afSignRewardExtDo.setFirstDayParticipation(new Date());
                        afSignRewardExtDo.setAmount(rewardAmount);
                        afSignRewardExtService.updateSignRewardExt(afSignRewardExtDo);
                        afSignRewardService.saveRecord(rewardDo);
                        return "success";
                    }catch (Exception e){
                        status.setRollbackOnly();
                        return "fail";
                    }
                }
            });
            resp.put("amount",rewardAmount.toString());
        }
        if(StringUtil.equals(status,"success")){
            result =true;
        }else {
            result =false;
        }
        return result;
    }



    public void sortStr(String[] str){
        for (int sx=0; sx<str.length-1; sx++) {
            for (int i=0; i<str.length-1-sx; i++) {
                if (Integer.parseInt(str[i]) > Integer.parseInt(str[i+1]) ) {
                    // 交换数据
                    String temp = str[i];
                    str[i] = str[i+1];
                    str[i+1] = temp;
                }
            }
        }
    }


    private  int maxCount(String[] nums) {
        int count = 0;
        int maxCount = 0;
        for (int i = 0; i < nums.length-1; i++) {
            if(Integer.parseInt(nums[i]) == Integer.parseInt(nums[i+1])-1){
                count++;
            }else {
                if(count > maxCount){
                    maxCount = count;
                }
                count = 0;
            }
        }
        if(count > maxCount){
            maxCount = count;
        }
        return maxCount+1;
    }

    /**
     * 随机获取min 与 max 之间的值
     * @param min
     * @param max
     * @return
     */
    private BigDecimal randomNum(String min,String max){
        Double amount = new BigDecimal(Math.random() * (Double.parseDouble(max) - Double.parseDouble(min)) + Double.parseDouble(min)).doubleValue();
        DecimalFormat dFormat=new DecimalFormat("#.00");
        String yearString=dFormat.format(amount);
        Double temp= Double.valueOf(yearString);
        return new BigDecimal(temp);
    }

    /**
     * 连续5天和7天的奖励 或者 连续5天的奖励
     * @param afSignRewardExtDo
     * @param rewardAmount
     * @param rewardDo
     * @param afResourceDo
     * @return
     */
    private String fiveOrSevenSignDays(AfSignRewardExtDo afSignRewardExtDo ,BigDecimal rewardAmount,final AfSignRewardDo rewardDo,final AfResourceDo afResourceDo){
        afSignRewardExtDo.setAmount(rewardAmount);
        final AfSignRewardExtDo signRewardExtDo = afSignRewardExtDo;
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                    AfUserCouponDo afUserCouponDo = new AfUserCouponDo();
                    AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(afResourceDo.getValue5()==null?"0":afResourceDo.getValue5()));
                    if(afCouponDo!=null){
                        if(StringUtil.equals(afCouponDo.getExpiryType(),"D")){
                            afUserCouponDo.setGmtStart(new Date());
                            afUserCouponDo.setGmtEnd(DateUtil.addDays(new Date(),afCouponDo.getValidDays()));
                        }else if(StringUtil.equals(afCouponDo.getExpiryType(),"R")){
                            afUserCouponDo.setGmtStart(afCouponDo.getGmtStart());
                            afUserCouponDo.setGmtEnd(afCouponDo.getGmtEnd());
                        }
                        afUserCouponDo.setUserId(rewardDo.getUserId());
                        afUserCouponDo.setCouponId(Long.parseLong(afResourceDo.getValue5()));
                        afUserCouponDo.setGmtCreate(new Date());
                        afUserCouponDo.setGmtModified(new Date());
                        afUserCouponDo.setSourceType("SIGN_REWARD");
                        afUserCouponDo.setSourceRef("SYS");
                        afUserCouponDo.setStatus("NOUSE");
                        afUserCouponService.addUserCoupon(afUserCouponDo);
                    }
                    afSignRewardService.saveRecord(rewardDo);
                    afSignRewardExtService.increaseMoney(signRewardExtDo);
                    return "success";
                }catch (Exception e){
                    status.setRollbackOnly();
                    return "fail";
                }
            }
        });
        return status;
    }

    public void addCoupon(final Long couponId,final Long userId){
        AfUserCouponDo afUserCouponDo = new AfUserCouponDo();
        AfCouponDo afCouponDo = afCouponService.getCouponById(couponId);
        if(afCouponDo!=null){
            if(StringUtil.equals(afCouponDo.getExpiryType(),"D")){
                afUserCouponDo.setGmtStart(new Date());
                afUserCouponDo.setGmtEnd(DateUtil.addDays(new Date(),afCouponDo.getValidDays()));
            }else if(StringUtil.equals(afCouponDo.getExpiryType(),"R")){
                afUserCouponDo.setGmtStart(afCouponDo.getGmtStart());
                afUserCouponDo.setGmtEnd(afCouponDo.getGmtEnd());
            }
            afUserCouponDo.setUserId(userId);
            afUserCouponDo.setCouponId(couponId);
            afUserCouponDo.setGmtCreate(new Date());
            afUserCouponDo.setGmtModified(new Date());
            afUserCouponDo.setSourceType("SIGN_REWARD");
            afUserCouponDo.setSourceRef("SYS");
            afUserCouponDo.setStatus("NOUSE");
            afUserCouponService.addUserCoupon(afUserCouponDo);
        }
    }

    /**
     * 签到7天  或者 普通签到的奖励
     * @param rewardDo
     * @param signRewardExtDo
     * @return
     */
    private String tenSignDays(final AfSignRewardDo rewardDo,final AfSignRewardExtDo signRewardExtDo){
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                    afSignRewardService.saveRecord(rewardDo);
                    afSignRewardExtService.increaseMoney(signRewardExtDo);
                    return "success";
                }catch (Exception e){
                    status.setRollbackOnly();
                    return "fail";
                }
            }
        });
        return status;
    }

    /**
     * 签到10天
     * @param rewardDo
     * @param signRewardExtDo
     * @return
     */
    private String signDays(final AfSignRewardDo rewardDo,final AfSignRewardExtDo signRewardExtDo,final AfResourceDo afResourceDo){
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                    Long couponId = Long.parseLong(afResourceDo.getPic1()==null?"0":afResourceDo.getPic1());
                    addCoupon(couponId,rewardDo.getUserId());
                    afSignRewardService.saveRecord(rewardDo);
                    afSignRewardExtService.increaseMoney(signRewardExtDo);
                    return "success";
                }catch (Exception e){
                    status.setRollbackOnly();
                    return "fail";
                }
            }
        });
        return status;
    }


    private Map<String,Object> homeInfo (Long userId, Map<String,Object> resp,String push,Integer appVersion ){
        //今天是否签到
        String status = afSignRewardService.isExist(userId)==false?"N":"Y";
        resp.put("rewardStatus",status);
        resp = afSignRewardExtService.getHomeInfo(userId,status);
        // 正式环境和预发布环境区分
        String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        String homeBanner = AfResourceType.RewardHomeBanner.getCode();
        resp.put("rewardBannerList",afResourceService.rewardBannerList(type,homeBanner));
        //任务列表
        HashMap<String,Object> hashMap = afUserAuthService.getUserAuthInfo(userId);
        List<Integer> level = afUserAuthService.signRewardUserLevel(userId,hashMap);
        resp.put("taskList",afTaskService.getTaskInfo(level,userId,push,hashMap,appVersion));
        return resp;
    }


    public Context buildContext(HttpServletRequest request) {
        ContextImpl.Builder builder = new ContextImpl.Builder();
        String method = request.getRequestURI();
        String appInfo =  request.getParameter("_appInfo");
        if(org.apache.commons.lang3.StringUtils.isEmpty(appInfo)) {
            // 从请求头获取_appInfo
            String referer = request.getHeader("Referer");
            if(org.apache.commons.lang3.StringUtils.isNotBlank(referer)) {
                appInfo = getAppInfo(referer);
            }
        }
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(appInfo)) {
            JSONObject _appInfo = JSONObject.parseObject(appInfo);
            String userName = _appInfo.getString("userName");
            String id = _appInfo.getString("id");
            Integer appVersion = _appInfo.getInteger("appVersion");

            Map<String,Object> systemsMap = (Map) JSON.parse(appInfo);
            AfUserDo userInfo = afUserService.getUserByUserName(userName);
            Long userId = userInfo == null ? null : userInfo.getRid();
            builder.method(method)
                    .userId(userId)
                    .userName(userName)
                    .appVersion(appVersion)
                    .systemsMap(systemsMap)
                    .id(id);
        }

        Map<String,Object> dataMaps = Maps.newHashMap();

        wrapRequest(request,dataMaps);
        builder.dataMap(dataMaps);

        logger.info("request method=>{},params=>{}",method,JSON.toJSONString(dataMaps));

        String clientIp = CommonUtil.getIpAddr(request);
        builder.clientIp(clientIp);
        Context context = builder.build();
        return context;
    }

    private String getAppInfo(String referer) {

        String appInfo = org.apache.commons.lang3.StringUtils.EMPTY;
        try {
            Map<String, List<String>> params = Maps.newHashMap();
            String[] urlParts = referer.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }

                    List<String> values = params.get(key);
                    if (values == null) {
                        values = new ArrayList<String>();
                        params.put(key, values);
                    }
                    values.add(value);
                }
            }
            List<String> _appInfo = params.get("_appInfo");
            if (_appInfo != null && _appInfo.size() > 0) {
                appInfo = _appInfo.get(0);
            }
            return appInfo;
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

    private void wrapRequest(HttpServletRequest request, Map<String, Object> dataMaps) {

        Enumeration<String> paramNames =  request.getParameterNames();
        while(paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if(!org.apache.commons.lang3.StringUtils.equals("_appInfo", paramName)) {
                String objVal = request.getParameter(paramName);
                dataMaps.put(paramName, objVal);
            }
        }
    }


}
