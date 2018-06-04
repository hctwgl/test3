package com.ald.fanbei.api.web.h5.controller;


import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SignRewardType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.enums.UserThirdType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;


/**
 *签到领金币朋友帮签
 */
@RestController
@RequestMapping(value = "/SignRewardInfo/",produces = "application/json;charset=UTF-8")
public class H5SignInfoOutController extends H5Controller {

    @Resource
    AfUserService afUserService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfSmsRecordService afSmsRecordService;
    @Resource
    TongdunUtil tongdunUtil;
    @Resource
    BaiQiShiUtils baiQiShiUtils;
    @Resource
    NumberWordFormat numberWordFormat;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfSignRewardService afSignRewardService;
    @Resource
    AfSignRewardExtService afSignRewardExtService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfTaskService afTaskService;
    @Resource
    AfUserThirdInfoService afUserThirdInfoService;
    @Resource
    AfUserAuthStatusService afUserAuthStatusService;
    @Resource
    AfUserCouponService afUserCouponService;
    /**
     * 朋友帮签(站外)
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/friendSign", method = RequestMethod.POST)
    public String getFriendSign(HttpServletRequest request, HttpServletResponse response) {
        try {
            String moblie = ObjectUtils.toString(request.getParameter("mobile"), "").toString();
            String verifyCode = ObjectUtils.toString(request.getParameter("verifyCode"), "").toString();
            String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
            String bsqToken = ObjectUtils.toString(request.getParameter("bsqToken"), "").toString();
            String push = ObjectUtils.toString(request.getParameter("push"), "").toString();
            String wxCode = ObjectUtils.toString(request.getParameter("wxCode"), "").toString();
            Map<String, Object> data = new HashMap<String, Object>();
            final AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("NEW_FRIEND_USER_SIGN");
            if(afResourceDo == null || numberWordFormat.isNumeric(afResourceDo.getValue())){
                throw new FanbeiException("param error", FanbeiExceptionCode.PARAM_ERROR);
            }

            AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(moblie, SmsType.REGIST.getCode());
            if (smsDo == null) {
                logger.error("sms record is empty");
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc()).toString();
            }
            String realCode = smsDo.getVerifyCode();
            if (!StringUtils.equals(verifyCode, realCode)) {
                logger.error("verifyCode is invalid");
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc()).toString();
            }
            if (smsDo.getIsCheck() == 1) {
                logger.error("verifyCode is already invalid");
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc()).toString();
            }
            // 判断验证码是否过期
            if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc()).toString();
            }
            // 更新为已经验证
            afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
            AfResourceDo afResource = afResourceService.getWechatConfig();
            String appid = afResource.getValue();
            String secret = afResource.getValue1();
            JSONObject userWxInfo = WxUtil.getUserInfoWithCache(appid, secret, wxCode);
            AfUserDo eUserDo = afUserService.getUserByUserName(moblie);
            if (eUserDo != null) {
                final BigDecimal rewardAmount = randomNum(afResourceDo.getValue3(),afResourceDo.getValue4()).setScale(2, RoundingMode.HALF_UP);
                if(!signReward(request,eUserDo.getRid(),rewardAmount,"old",moblie,userWxInfo )){
                    return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
                }
                data = homeInfo(eUserDo.getRid(),data,push);
//                data.put("rewardAmount",new BigDecimal(data.get("rewardAmount").toString()).add(rewardAmount));
                return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",data).toString();
            }
            try {
                tongdunUtil.getPromotionResult(token, null, null, CommonUtil.getIpAddr(request), moblie, moblie, "");
            } catch (Exception e) {
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc()).toString();
            }
            try {
                baiQiShiUtils.getRegistResult("h5",bsqToken,CommonUtil.getIpAddr(request),moblie,"","","","");
            }catch (Exception e){
                logger.error("h5Common commitRegisterLogin baiQiShiUtils getRegistResult error => {}",e.getMessage());
            }

            String salt = UserUtil.getSalt();
            AfUserDo userDo = new AfUserDo();
            userDo.setSalt(salt);
            userDo.setUserName(moblie);
            userDo.setMobile(moblie);
            userDo.setNick("");
            userDo.setPassword("");
            userDo.setRecommendId(0l);
            final long userId = afUserService.addUser(userDo);
            Long invteLong = Constants.INVITE_START_VALUE + userId;
            String inviteCode = Long.toString(invteLong, 36);
            userDo.setRecommendCode(inviteCode);
            afUserService.updateUser(userDo);
            // 获取邀请分享地址
            String appDownLoadUrl = "";
            // save token to cache 记住登录状态
            String  newtoken = UserUtil.generateToken(moblie);
            String tokenKey = Constants.H5_CACHE_USER_TOKEN_COOKIES_KEY + moblie;
            CookieUtil.writeCookie(response, Constants.H5_USER_NAME_COOKIES_KEY, moblie, Constants.SECOND_OF_HALF_HOUR_INT);
            CookieUtil.writeCookie(response, Constants.H5_USER_TOKEN_COOKIES_KEY, token, Constants.SECOND_OF_HALF_HOUR_INT);
            bizCacheUtil.saveObject(tokenKey, newtoken, Constants.SECOND_OF_HALF_HOUR);
            final BigDecimal rewardAmount = randomNum(afResourceDo.getValue1(),afResourceDo.getValue2());
            if(!signReward(request,userId,rewardAmount,"new",moblie,userWxInfo)){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
            }
            //首页信息
            data = homeInfo(userId,data,push);
            return H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(),"",data ).toString();
        } catch (FanbeiException e) {
            logger.error("commitRegister fanbei exception" + e.getMessage());
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        } catch (Exception e) {
            logger.error("commitRegister exception", e);
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        }
    }



    @RequestMapping(value = "/friendSignIn", method = RequestMethod.POST)
    public String getFriendSignIn(HttpServletRequest request, HttpServletResponse response) {
        String resultStr = "";
        try {
            String userName = ObjectUtils.toString(request.getParameter("userName"),null);
            logger.info("userName cfp = "+userName);
            AfUserDo afUserDo = afUserService.getUserByUserName(userName);
            if(null == afUserDo){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc()).toString();
            }
            String push = ObjectUtils.toString(request.getParameter("push"),"N");
            String wxCode = ObjectUtils.toString(request.getParameter("wxCode"),null);
            Map<String,Object> data = new HashMap<String,Object>();
            Long userId = afUserDo.getRid();
            //判断用户和openId是否在爱上街绑定
            AfUserThirdInfoDo thirdInfo = checkBindOpen(wxCode);
            logger.info("thirdInfo = " + thirdInfo);
            if(thirdInfo == null){
                data.put("openType","2");
                return H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(),"",data ).toString();
            }
            Long friendUserId = thirdInfo.getUserId();
            if(StringUtil.equals(friendUserId+"",userId+"")){//已经绑定并且是自己打开
                data = homeInfo(userId,data,push);
                data.put("openType","0");
            } else {//已绑定
                data = homeInfo(friendUserId,data,push);
                AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
                afSignRewardDo.setIsDelete(0);
                afSignRewardDo.setUserId(userId);
                afSignRewardDo.setGmtCreate(new Date());
                afSignRewardDo.setGmtModified(new Date());
                afSignRewardDo.setType(SignRewardType.ONE.getCode());
                afSignRewardDo.setStatus(0);
                afSignRewardDo.setFriendUserId(friendUserId);
                if(afSignRewardService.frienddUserSignCountToDay(userId,friendUserId)){
                    return H5CommonResponse.getNewInstance(false,FanbeiExceptionCode.FRIEND_USER_SIGN_EXIST.getDesc(),"",data ).toString();
                }
                if(!friendSign(afSignRewardDo,userId,friendUserId,data)){
                    return H5CommonResponse.getNewInstance(false,FanbeiExceptionCode.USER_SIGN_FAIL.getDesc(),"",data ).toString();
                }
                data.put("openType","1");
            }
            logger.info("data cfp = "+data);
            return H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(),"",data ).toString();
        } catch (FanbeiException e) {
            resultStr = H5CommonResponse.getNewInstance(false, "getOpenId error", "", e.getErrorCode().getDesc()).toString();
        } catch (Exception e) {
            resultStr = H5CommonResponse.getNewInstance(false, "getOpenId error", "", e).toString();
        }

        return resultStr;
    }


    @RequestMapping(value = "/supplementSign", method = RequestMethod.POST)
    public String getSupplementSign(HttpServletRequest request, HttpServletResponse response) {
        try {
            final String moblie = ObjectUtils.toString(request.getParameter("mobile"), "").toString();
            String verifyCode = ObjectUtils.toString(request.getParameter("verifyCode"), "").toString();
            String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
            String bsqToken = ObjectUtils.toString(request.getParameter("bsqToken"), "").toString();
            String push = ObjectUtils.toString(request.getParameter("push"), "").toString();
            Integer time = NumberUtil.objToIntDefault(request.getParameter("time"),1);
            String wxCode = ObjectUtils.toString(request.getParameter("wxCode"), "").toString();
            String userName = ObjectUtils.toString(request.getParameter("rewardUserId"),null);
            AfUserDo afUserDo = afUserService.getUserByUserName(userName);
            final Long rewardUserId = afUserDo.getRid();//分享者的userId
            AfResourceDo afResource = afResourceService.getWechatConfig();
            String appid = afResource.getValue();
            String secret = afResource.getValue1();
            final JSONObject userWxInfo = WxUtil.getUserInfoWithCache(appid, secret, wxCode);
            Map<String, Object> data = new HashMap<String, Object>();
            AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(moblie, SmsType.REGIST.getCode());
            if (smsDo == null) {
                logger.error("sms record is empty");
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc()).toString();
            }
            String realCode = smsDo.getVerifyCode();
            if (!StringUtils.equals(verifyCode, realCode)) {
                logger.error("verifyCode is invalid");
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc()).toString();
            }
            if (smsDo.getIsCheck() == 1) {
                logger.error("verifyCode is already invalid");
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc()).toString();
            }
            // 判断验证码是否过期
            if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc()).toString();
            }
            // 更新为已经验证
            afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
            final AfUserDo eUserDo = afUserService.getUserByUserName(moblie);
            if (eUserDo != null) {
                String status = transactionTemplate.execute(new TransactionCallback<String>() {
                    @Override
                    public String doInTransaction(TransactionStatus status) {
                        try{
                            //绑定openId
                            String openId = userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID);
                            AfUserThirdInfoDo userThirdInfoDo = new AfUserThirdInfoDo();
                            userThirdInfoDo.setUserId(eUserDo.getRid());
                            userThirdInfoDo.setThirdId(openId);
                            userThirdInfoDo.setThirdType(UserThirdType.WX.getCode());
                            userThirdInfoDo.setCreator(moblie);
                            userThirdInfoDo.setModifier(moblie);
                            userThirdInfoDo.setThirdInfo(userWxInfo.toJSONString());
                            userThirdInfoDo.setUserName(moblie);
                            afUserThirdInfoService.saveRecord(userThirdInfoDo);
                            AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
                            afSignRewardDo.setIsDelete(0);
                            afSignRewardDo.setUserId(rewardUserId);
                            afSignRewardDo.setGmtCreate(new Date());
                            afSignRewardDo.setGmtModified(new Date());
                            afSignRewardDo.setType(SignRewardType.THREE.getCode());
                            afSignRewardDo.setStatus(0);
                            afSignRewardDo.setFriendUserId(eUserDo.getRid());
                            afSignRewardDo.setAmount(BigDecimal.ZERO);
                            afSignRewardService.saveRecord(afSignRewardDo);
                            return "success";
                        }catch (Exception e){
                            status.setRollbackOnly();
                            return "fail";
                        }
                    }
                });
                data = homeInfo(eUserDo.getRid(),data,push);
                data.put("flag","fail");
                if(StringUtil.equals(status,"fail")){
                    return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.WX_BIND_FAIL.getDesc(),"",data).toString();
                }
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SUPPLEMENT_SIGN_FAIL.getDesc(),"",data).toString();
            }
            try {
                tongdunUtil.getPromotionResult(token, null, null, CommonUtil.getIpAddr(request), moblie, moblie, "");
            } catch (Exception e) {
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc()).toString();
            }
            try {
                baiQiShiUtils.getRegistResult("h5",bsqToken,CommonUtil.getIpAddr(request),moblie,"","","","");
            }catch (Exception e){
                logger.error("h5Common commitRegisterLogin baiQiShiUtils getRegistResult error => {}",e.getMessage());
            }
            String salt = UserUtil.getSalt();
            AfUserDo userDo = new AfUserDo();
            userDo.setSalt(salt);
            userDo.setUserName(moblie);
            userDo.setMobile(moblie);
            userDo.setNick("");
            userDo.setPassword("");
            userDo.setRecommendId(0l);
            final long userId = afUserService.addUser(userDo);
            Long invteLong = Constants.INVITE_START_VALUE + userId;
            String inviteCode = Long.toString(invteLong, 36);
            userDo.setRecommendCode(inviteCode);
            afUserService.updateUser(userDo);
            // save token to cache 记住登录状态
            String  newtoken = UserUtil.generateToken(moblie);
            String tokenKey = Constants.H5_CACHE_USER_TOKEN_COOKIES_KEY + moblie;
            CookieUtil.writeCookie(response, Constants.H5_USER_NAME_COOKIES_KEY, moblie, Constants.SECOND_OF_HALF_HOUR_INT);
            CookieUtil.writeCookie(response, Constants.H5_USER_TOKEN_COOKIES_KEY, token, Constants.SECOND_OF_HALF_HOUR_INT);
            bizCacheUtil.saveObject(tokenKey, newtoken, Constants.SECOND_OF_HALF_HOUR);
            final AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("SIGN_COEFFICIENT");
            final BigDecimal amount = randomNum(afResourceDo.getValue1(),afResourceDo.getValue2());
            if(!signRewardSupplement(request,userId,moblie,time,userWxInfo,amount)){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
            }
            data = homeInfo(userId,data,push);
            data.put("flag","success");
//            data.put("rewardAmount",new BigDecimal(data.get("rewardAmount").toString()).add(amount).setScale(2, RoundingMode.HALF_UP));
            return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",data).toString();
        } catch (FanbeiException e) {
            logger.error("commitRegister fanbei exception" + e.getMessage());
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        } catch (Exception e) {
            logger.error("commitRegister exception", e);
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        }
    }

    private boolean signRewardSupplement(HttpServletRequest request,final Long userId,final String moblie,final int time,final JSONObject userWxInfo,final BigDecimal amount){
        boolean result ;
        final AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("NEW_FRIEND_USER_SIGN");

        final Long rewardUserId = NumberUtil.objToLongDefault(request.getParameter("rewardUserId"),null);
        if(afResourceDo == null ||  numberWordFormat.isNumeric(afResourceDo.getValue())){
            throw new FanbeiException("param error", FanbeiExceptionCode.PARAM_ERROR);
        }
        final BigDecimal rewardAmount = randomNum(afResourceDo.getValue1(),afResourceDo.getValue2());

        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                    //绑定openId
                    String openId = userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID);
                    AfUserThirdInfoDo userThirdInfoDo = new AfUserThirdInfoDo();
                    userThirdInfoDo.setUserId(userId);
                    userThirdInfoDo.setThirdId(openId);
                    userThirdInfoDo.setThirdType(UserThirdType.WX.getCode());
                    userThirdInfoDo.setCreator(moblie);
                    userThirdInfoDo.setModifier(moblie);
                    userThirdInfoDo.setThirdInfo(userWxInfo.toJSONString());
                    userThirdInfoDo.setUserName(moblie);
                    afUserThirdInfoService.saveRecord(userThirdInfoDo);
                    AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(rewardUserId);
                    //补签成功 分享者获取相应的奖励
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(afSignRewardExtDo.getFirstDayParticipation());
                    calendar.add(Calendar.DAY_OF_MONTH,time);
                    Date date =calendar.getTime();
                    AfSignRewardDo afSignRewardDo = buildSignReward(rewardUserId,SignRewardType.TWO.getCode(),userId,rewardAmount,date);
                    //补签成功 打开者获取相应的奖励
                    AfSignRewardDo rewardDo = buildSignReward(userId, SignRewardType.FIVE.getCode(),null,amount,null);
                    List<AfSignRewardDo> signList = new ArrayList<>();
                    signList.add(afSignRewardDo);
                    signList.add(rewardDo);
                    afSignRewardService.saveRecordBatch(signList);
                    //补签成功 分享者增加余额
                    Map<String,String> days = afSignRewardService.supplementSign(afSignRewardExtDo,0,"N");
                    String str[] = days.get("signDays").toString().split(",");
                    int count = 0;
                    if(StringUtil.equals(days.get("signDays").toString(),"")){
                        count = str.length;
                    }else{
                        count = str.length+1;
                    }
                    if(count == 1){
                        afSignRewardExtDo.setAmount(rewardAmount);
                        final AfSignRewardExtDo signRewardExtDo = afSignRewardExtDo;
                        afSignRewardExtDo.setFirstDayParticipation(new Date());
                        afSignRewardExtService.increaseMoney(signRewardExtDo);
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
                                fiveOrSevenSignDays(afSignRewardExtDo,rewardAmount,rewardDo,afResourceDo);
                            }
                        }else if(count >= 7 && count< 10){
                            //给予连续5天的奖励
                            if(maxCount < 5 && newMaxCount == 5){
                                fiveOrSevenSignDays(afSignRewardExtDo,rewardAmount,rewardDo,afResourceDo);
                            }else if(maxCount < 5 && newMaxCount == 7){//给予连续5天和7天的奖励
                                afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(2)).setScale(2, RoundingMode.HALF_EVEN));
                                fiveOrSevenSignDays(afSignRewardExtDo,afSignRewardExtDo.getAmount(),rewardDo,afResourceDo);
                            }else if(maxCount >= 5 && newMaxCount == 7){//给予连续7天的奖励
                                afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(2)).setScale(2,RoundingMode.HALF_EVEN));
                                afSignRewardExtService.increaseMoney(afSignRewardExtDo);
                            }
                        }else if(count == 10){
                            //给予连续7天和10天的奖励
                            if(maxCount < 7){
                                afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(4)).setScale(2,RoundingMode.HALF_EVEN));
                                afSignRewardExtService.increaseMoney(afSignRewardExtDo);
                            }
                        }else {//给予普通签到的奖励
                            afSignRewardExtDo.setAmount(rewardAmount);
                            afSignRewardExtService.increaseMoney(afSignRewardExtDo);
                        }
                    }

                    afSignRewardExtDo.setAmount(rewardAmount);
                    afSignRewardExtService.increaseMoney(afSignRewardExtDo);
                    //补签成功 打开者增加余额
                    AfSignRewardExtDo afSignRewardExt = buildSignRewardExt(userId,amount);
                    afSignRewardExtService.saveRecord(afSignRewardExt);
//
                    return "success";
                }catch (Exception e){
                    status.setRollbackOnly();
                    return "fail";
                }
            }
        });

        if(StringUtil.equals(status,"success")){
            result =true;
        }else {
            result =false;
        }
        return result;

    }

    public static AfSignRewardDo buildSignReward(Long userId,Integer type,Long friendUserId,BigDecimal rewardAmount,Date time){
        AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
        afSignRewardDo.setIsDelete(0);
        afSignRewardDo.setUserId(userId);
        afSignRewardDo.setGmtCreate(new Date());
        afSignRewardDo.setGmtModified(new Date());
        afSignRewardDo.setType(type);
        afSignRewardDo.setStatus(0);
        afSignRewardDo.setFriendUserId(friendUserId);
        afSignRewardDo.setAmount(rewardAmount);
        afSignRewardDo.setTime(time);
        return afSignRewardDo;
    }

    public static AfSignRewardExtDo buildSignRewardExt(Long userId,BigDecimal amount){
        AfSignRewardExtDo afSignRewardExtDo = new AfSignRewardExtDo();
        afSignRewardExtDo.setUserId(userId);
        afSignRewardExtDo.setGmtModified(new Date());
        afSignRewardExtDo.setAmount(amount);
        afSignRewardExtDo.setCycleDays(10);
        afSignRewardExtDo.setGmtCreate(new Date());
        afSignRewardExtDo.setIsOpenRemind(0);
        afSignRewardExtDo.setIsDelete(0);
        return afSignRewardExtDo;
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
     * 连续5天和7天的奖励 或者 连续5天的奖励
     * @param afSignRewardExtDo
     * @param rewardAmount
     * @param rewardDo
     * @param afResourceDo
     * @return
     */
    private void fiveOrSevenSignDays(AfSignRewardExtDo afSignRewardExtDo ,BigDecimal rewardAmount,final AfSignRewardDo rewardDo,final AfResourceDo afResourceDo){
        afSignRewardExtDo.setAmount(rewardAmount);
        AfUserCouponDo afUserCouponDo = new AfUserCouponDo();
        afUserCouponDo.setUserId(rewardDo.getUserId());
        afUserCouponDo.setCouponId(Long.parseLong(afResourceDo.getValue5()));
        afUserCouponDo.setGmtCreate(new Date());
        afUserCouponDo.setGmtModified(new Date());
        afUserCouponDo.setSourceType("SIGN_REWARD");
        afUserCouponDo.setSourceRef("SYS");
        afUserCouponDo.setStatus("NOUSE");
        afUserCouponService.addUserCoupon(afUserCouponDo);
        afSignRewardExtService.increaseMoney(afSignRewardExtDo);
    }

    @RequestMapping(value = "/supplementSignIn", method = RequestMethod.POST)
    public String getSupplementSignIn(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String,Object> data = new HashMap<String,Object>();
            String userName = ObjectUtils.toString(request.getParameter("userName"),null);
            logger.info("userName =  supplementSignIn =" + userName);
            AfUserDo afUserDo = afUserService.getUserByUserName(userName);
            if(null == afUserDo){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc()).toString();
            }
            String push = ObjectUtils.toString(request.getParameter("push"),"N");
            String wxCode = ObjectUtils.toString(request.getParameter("wxCode"),null);

            Long userId = afUserDo.getRid();
            //判断用户和openId是否在爱上街绑定
            AfUserThirdInfoDo thirdInfo = checkBindOpen(wxCode);
            logger.info("thirdInfo =  supplementSignIn =" + thirdInfo);
            if(thirdInfo == null){//未绑定
                data.put("openType","2");
                return H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(),"",data ).toString();
            }
            Long firendUserId = thirdInfo.getUserId();
            if(firendUserId == userId){//已经绑定并且是自己打开
                data = homeInfo(userId,data,push);
                data.put("openType","0");
            } else if(firendUserId != userId ){//已绑定
                data = homeInfo(firendUserId,data,push);
                AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
                afSignRewardDo.setIsDelete(0);
                afSignRewardDo.setUserId(userId);
                afSignRewardDo.setGmtCreate(new Date());
                afSignRewardDo.setGmtModified(new Date());
                afSignRewardDo.setType(SignRewardType.THREE.getCode());
                afSignRewardDo.setStatus(0);
                afSignRewardDo.setFriendUserId(firendUserId);
                afSignRewardDo.setAmount(BigDecimal.ZERO);
                afSignRewardService.saveRecord(afSignRewardDo);
                data.put("openType","1");
            }
            logger.info("data =  supplementSignIn =" + data);
            return H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(),"",data ).toString();

        } catch (FanbeiException e) {
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        } catch (Exception e) {
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        }
    }


    private boolean signReward(HttpServletRequest request,final Long userId,final BigDecimal rewardAmount,final String user,final String moblie,final JSONObject userWxInfo){
        boolean result ;
        String userName = ObjectUtils.toString(request.getParameter("rewardUserId"),null);
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        final Long rewardUserId = afUserDo.getRid();//分享者的userId
        final boolean flag = afSignRewardService.checkUserSign(userId);
        final AfResourceDo afResource = afResourceService.getSingleResourceBytype("SIGN_COEFFICIENT");
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                    //绑定openId
                    String openId = userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID);
                    AfUserThirdInfoDo userThirdInfoDo = new AfUserThirdInfoDo();
                    userThirdInfoDo.setUserId(userId);
                    userThirdInfoDo.setThirdId(openId);
                    userThirdInfoDo.setThirdType(UserThirdType.WX.getCode());
                    userThirdInfoDo.setCreator(moblie);
                    userThirdInfoDo.setModifier(moblie);
                    userThirdInfoDo.setThirdInfo(userWxInfo.toJSONString());
                    userThirdInfoDo.setUserName(moblie);
                    afUserThirdInfoService.saveRecord(userThirdInfoDo);
                    //帮签成功 打开者获取相应的奖励金额
                    BigDecimal amount ;
                    if(StringUtil.equals("new",user)){
                        amount = randomNum(afResource.getValue1(),afResource.getValue2()).setScale(2, RoundingMode.HALF_UP);
                    }else{
                        if(!flag){
                            amount = randomNum(afResource.getValue1(),afResource.getValue2()).setScale(2, RoundingMode.HALF_UP);
                        }else{
                            amount = randomNum(afResource.getValue3(),afResource.getValue4()).setScale(2, RoundingMode.HALF_UP);
                        }
                    }
                    //帮签成功 分享者获取相应的奖励
                    AfSignRewardDo rewardDo = buildSignReward(rewardUserId, SignRewardType.ONE.getCode(),userId,rewardAmount,null);
                    //帮签成功 打开者获取相应的奖励
                    AfSignRewardDo afSignRewardDo = buildSignReward(userId, SignRewardType.FOUR.getCode(),null,amount,null);
                    afSignRewardService.saveRecord(rewardDo);
                    afSignRewardService.saveRecord(afSignRewardDo);
                    List<Long> list = new ArrayList<>();
                    list.add(userId);
                    list.add(rewardUserId);
                    List<AfSignRewardExtDo> signList = afSignRewardExtService.selectByUserIds(list);
                    if(signList.size()>0){
                        if(signList.size() == 2){
                            for(AfSignRewardExtDo signRewardExt : signList){
                                if(StringUtil.equals(signRewardExt.getUserId()+"",userId+"")){
                                    signRewardExt.setAmount(amount);
                                    afSignRewardExtService.increaseMoney(signRewardExt);
                                }else if(StringUtil.equals(signRewardExt.getUserId()+"",rewardUserId+"")){
                                    signRewardExt.setAmount(rewardAmount);
                                    afSignRewardExtService.increaseMoney(signRewardExt);
                                }
                            }
                        }else {
                            if(StringUtil.equals(signList.get(0).getUserId()+"",userId+"")){
                                signList.get(0).setAmount(afSignRewardDo.getAmount());
                                afSignRewardExtService.increaseMoney(signList.get(0));
                                AfSignRewardExtDo signRewardExt = buildSignRewardExt(rewardUserId,rewardDo.getAmount());
                                afSignRewardExtService.saveRecord(signRewardExt);
                            }else{
                                signList.get(0).setAmount(rewardDo.getAmount());
                                afSignRewardExtService.increaseMoney(signList.get(0));
                                AfSignRewardExtDo signRewardExt = buildSignRewardExt(userId,afSignRewardDo.getAmount());
                                afSignRewardExtService.saveRecord(signRewardExt);
                            }
                        }
                    }else{
                        //帮签成功 打开者增加余额
                        AfSignRewardExtDo afSignRewardExt = buildSignRewardExt(userId,afSignRewardDo.getAmount());
                        //帮签成功 分享者增加余额
                        AfSignRewardExtDo signRewardExt = buildSignRewardExt(rewardUserId,rewardDo.getAmount());
                        List<AfSignRewardExtDo> signExtlist = new ArrayList<>();
                        signExtlist.add(afSignRewardExt);
                        signExtlist.add(signRewardExt);
                        afSignRewardExtService.saveRecordBatch(signExtlist);
                    }
                    return "success";
                }catch (Exception e){
                    status.setRollbackOnly();
                    return "fail";
                }
            }
        });
        if(StringUtil.equals(status,"success")){
            result =true;
        }else {
            result =false;
        }
        return result;

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

    private Map<String,Object> homeInfo (Long userId, Map<String,Object> resp,String push ){
        //今天是否签到
        String status = afSignRewardService.isExist(userId)==false?"N":"Y";
        resp.put("rewardStatus",status);
        resp = afSignRewardExtService.getHomeInfo(userId,status);
        // 正式环境和预发布环境区分
        String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        String homeBanner = AfResourceType.RewardHomeBanner.getCode();
        resp.put("rewardBannerList",afResourceService.rewardBannerList(type,homeBanner));
        //任务列表
        AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
        AfUserAuthStatusDo authStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId,"ONLINE");
        List<Integer> level = afUserAuthService.signRewardUserLevel(userId,userAuthDo);
        resp.put("taskList",afTaskService.getTaskInfo(level,userId,push,userAuthDo,authStatusDo));
        return resp;
    }


    public AfUserThirdInfoDo checkBindOpen(String wxCode){
        AfResourceDo afResourceDo = afResourceService.getWechatConfig();
        String appid = afResourceDo.getValue();
        String secret = afResourceDo.getValue1();
        JSONObject userWxInfo = WxUtil.getUserInfoWithCache(appid, secret, wxCode);
        AfUserThirdInfoDo thirdInfo = new AfUserThirdInfoDo();
        thirdInfo.setThirdId(userWxInfo.get("openid").toString());
        thirdInfo.setThirdType(UserThirdType.WX.getCode());
        List<AfUserThirdInfoDo> thirdInfos = afUserThirdInfoService.getListByCommonCondition(thirdInfo);
        return  thirdInfos.size() == 0 ? null : thirdInfos.get(0);
    }


    private boolean friendSign(AfSignRewardDo afSignRewardDo,final Long userId, final Long friendUserId,Map<String,Object> data){
        boolean result;
        final AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("NEW_FRIEND_USER_SIGN");
        final AfResourceDo afResource = afResourceService.getSingleResourceBytype("SIGN_COEFFICIENT");
        if(afResourceDo == null || afResource == null || numberWordFormat.isNumeric(afResourceDo.getValue())){
            throw new FanbeiException("param error", FanbeiExceptionCode.PARAM_ERROR);
        }
        //帮签次数
        final boolean flag = afSignRewardService.checkUserSign(friendUserId);
        int count = afSignRewardService.frienddUserSignCount(userId,friendUserId);
        BigDecimal rewardAmount ;
        if(count<1){//第一次帮签
            rewardAmount = randomNum(afResourceDo.getValue3(),afResourceDo.getValue4());
        }else{//多次帮签
            rewardAmount = randomNum(afResourceDo.getPic2(),afResourceDo.getPic1());
        }
        final BigDecimal resultAmount = rewardAmount.setScale(2, RoundingMode.HALF_UP);

        BigDecimal amount;
        if(!flag){
            amount =  randomNum(afResource.getValue1(),afResource.getValue2()).setScale(2, RoundingMode.HALF_UP);
        }else{
            amount = randomNum(afResource.getValue3(),afResource.getValue4()).setScale(2, RoundingMode.HALF_UP);
        }
        final BigDecimal friendAmount = amount.setScale(2, RoundingMode.HALF_UP);
        data.put("rewardAmount",new BigDecimal(data.get("rewardAmount").toString()).add(amount).setScale(2, RoundingMode.HALF_UP));
        afSignRewardDo.setAmount(resultAmount);
        final AfSignRewardDo signRewardDo = afSignRewardDo;
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                    //好友帮签成功 分享者获取相应的奖励
                    AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(userId);
                    afSignRewardExtDo.setAmount(resultAmount);
                    afSignRewardExtService.increaseMoney(afSignRewardExtDo);
                    //打开者 帮签成功 获取相应的奖励

                    AfSignRewardExtDo afSignRewardExt = buildSignRewardExt(friendUserId,friendAmount);
                    afSignRewardExtService.increaseMoney(afSignRewardExt);

                    AfSignRewardDo rewardDo = buildSignReward(friendUserId, SignRewardType.FOUR.getCode(),null,friendAmount,null);
                    afSignRewardService.saveRecord(signRewardDo);
                    afSignRewardService.saveRecord(rewardDo);
                    return "success";
                }catch (Exception e){
                    status.setRollbackOnly();
                    return "fail";
                }
            }
        });
        if(StringUtil.equals(status,"success")){
            result =true;
        }else {
            result =false;
        }
        return result;
    }




}
