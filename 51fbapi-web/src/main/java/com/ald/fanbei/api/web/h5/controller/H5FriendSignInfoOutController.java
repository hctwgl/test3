package com.ald.fanbei.api.web.h5.controller;


import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.web.common.H5CommonResponse;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 *签到领金币朋友帮签
 */
@RestController
@RequestMapping(value = "/signReward/")
public class H5FriendSignInfoOutController extends H5Controller {

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

    /**
     * 朋友帮签
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/friendSign", method = RequestMethod.POST)
    public String homePage(HttpServletRequest request, HttpServletResponse response) {
        try {
            String moblie = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
            String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
            String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
            String bsqToken = ObjectUtils.toString(request.getParameter("bsqToken"), "").toString();
            Map<String, Object> data = new HashMap<String, Object>();
            final AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("NEW_FRIEND_USER_SIGN");
            if(afResourceDo == null || numberWordFormat.isNumeric(afResourceDo.getValue())){
                throw new FanbeiException("param error", FanbeiExceptionCode.PARAM_ERROR);
            }
            AfUserDo eUserDo = afUserService.getUserByUserName(moblie);
            if (eUserDo != null) {
                final BigDecimal rewardAmount = randomNum(afResourceDo.getValue1(),afResourceDo.getValue2());
                if(!signReward(request,eUserDo.getRid(),rewardAmount)){
                    return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
                }
                homeInfo(eUserDo.getRid(),data);
                return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",data).toString();
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
            // 更新为已经验证
            afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
            String salt = UserUtil.getSalt();
            AfUserDo userDo = new AfUserDo();
            userDo.setSalt(salt);
            userDo.setUserName(moblie);
            userDo.setMobile(moblie);
            userDo.setNick("");
            userDo.setPassword("");
            userDo.setRecommendId(0l);
            final long userId = afUserService.addUser(userDo);
            //绑定微信的唯一标识open_id

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
            if(!signReward(request,userId,rewardAmount)){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
            }
            //首页信息
            homeInfo(userId,data);
            return H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(),"",data ).toString();
        } catch (FanbeiException e) {
            logger.error("commitRegister fanbei exception" + e.getMessage());
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        } catch (Exception e) {
            logger.error("commitRegister exception", e);
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        }
    }

    private boolean signReward(HttpServletRequest request,final Long userId,final BigDecimal rewardAmount){
        boolean result ;
        final Long rewardUserId = NumberUtil.objToLongDefault(request.getParameter("rewardUserId"),null);
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                    AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
                    afSignRewardDo.setIsDelete(0);
                    afSignRewardDo.setUserId(rewardUserId);
                    afSignRewardDo.setGmtCreate(new Date());
                    afSignRewardDo.setGmtModified(new Date());
                    afSignRewardDo.setType(2);
                    afSignRewardDo.setStatus(0);
                    afSignRewardDo.setFriendUserId(userId);
                    afSignRewardDo.setAmount(rewardAmount);
                    AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(rewardUserId);
                    afSignRewardService.saveRecord(afSignRewardDo);
                    afSignRewardExtDo.setAmount(rewardAmount);
                    afSignRewardExtService.increaseMoney(afSignRewardExtDo);
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
        BigDecimal rewardAmount = new BigDecimal(Math.random() * (Double.parseDouble(max) - Double.parseDouble(min)) + min).setScale(2, RoundingMode.HALF_EVEN);
        return rewardAmount;

    }

    private Map<String,Object> homeInfo (Long userId, Map<String,Object> resp){
        resp = afSignRewardExtService.getHomeInfo(userId);
        // 正式环境和预发布环境区分
        String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        String homeBanner = AfResourceType.RewardHomeBanner.getCode();
        resp.put("rewardBannerList",afResourceService.rewardBannerList(type,homeBanner));
        //任务列表
        String level = afUserAuthService.signRewardUserLevel(userId);
        resp.put("taskList",afTaskService.getTaskInfo(level,userId));
        return resp;
    }



}
