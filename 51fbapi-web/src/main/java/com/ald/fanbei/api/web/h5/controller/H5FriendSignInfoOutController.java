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
@RequestMapping(value = "/friendSignReward/")
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
    @Resource
    AfUserThirdInfoService afUserThirdInfoService;
    @Resource
    AfUserAuthStatusService afUserAuthStatusService;
    /**
     * 朋友帮签(站外)
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/friendSign", method = RequestMethod.POST)
    public String homePage(HttpServletRequest request, HttpServletResponse response) {
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
            AfResourceDo afResource = afResourceService.getWechatConfig();
            String appid = afResource.getValue();
            String secret = afResource.getValue1();
            JSONObject userWxInfo = WxUtil.getUserInfoWithCache(appid, secret, wxCode);
            AfUserDo eUserDo = afUserService.getUserByUserName(moblie);
            if (eUserDo != null) {
                final BigDecimal rewardAmount = randomNum(afResourceDo.getValue1(),afResourceDo.getValue2()).setScale(2, RoundingMode.HALF_UP);
                if(!signReward(request,eUserDo.getRid(),rewardAmount,"old",moblie,userWxInfo)){
                    return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
                }
                data = homeInfo(eUserDo.getRid(),data,push,rewardAmount);
                data.put("rewardAmount",new BigDecimal(data.get("rewardAmount").toString()).add(rewardAmount));
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
            if(!signReward(request,userId,rewardAmount,"new",moblie,userWxInfo)){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
            }
            //首页信息
            data = homeInfo(userId,data,push,rewardAmount);
            data.put("rewardAmount",new BigDecimal(data.get("rewardAmount").toString()).add(rewardAmount).setScale(2, RoundingMode.HALF_UP));
            return H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(),"",data ).toString();
        } catch (FanbeiException e) {
            logger.error("commitRegister fanbei exception" + e.getMessage());
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        } catch (Exception e) {
            logger.error("commitRegister exception", e);
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        }
    }



    @RequestMapping(value = "/friendSignIn", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @Deprecated
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
            //测试用
//        AfUserThirdInfoDo thirdInfo = new AfUserThirdInfoDo();
//        thirdInfo.setUserId(18637963763l);
            Long friendUserId = thirdInfo.getUserId();
            if(StringUtil.equals(friendUserId+"",userId+"")){//已经绑定并且是自己打开
                data = homeInfo(userId,data,push,BigDecimal.ZERO);
                data.put("openType","0");
            } else {//已绑定
                data = homeInfo(friendUserId,data,push,BigDecimal.ZERO);
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
                        if(flag){
                            amount = randomNum(afResource.getValue1(),afResource.getValue2()).setScale(2, RoundingMode.HALF_UP);
                        }else{
                            amount = randomNum(afResource.getValue3(),afResource.getValue4()).setScale(2, RoundingMode.HALF_UP);
                        }
                    }
                    List<AfSignRewardDo> signRewardList = new ArrayList<AfSignRewardDo>();
                    //帮签成功 分享者获取相应的奖励
                    AfSignRewardDo rewardDo = H5SupplementSignInfoOutController.buildSignReward(rewardUserId, SignRewardType.ONE.getCode(),userId,rewardAmount,null);
                    //帮签成功 打开者获取相应的奖励
                    AfSignRewardDo afSignRewardDo = H5SupplementSignInfoOutController.buildSignReward(userId, SignRewardType.FOUR.getCode(),null,amount,null);
                    signRewardList.add(afSignRewardDo);
                    signRewardList.add(rewardDo);
                    afSignRewardService.saveRecordBatch(signRewardList);
                    List<Long> list = new ArrayList<>();
                    list.add(userId);
                    list.add(rewardUserId);
                    List<AfSignRewardExtDo> signList = afSignRewardExtService.selectByUserIds(list);
                    if(signList.size()>0){
                        if(signList.size() == 2){
                            for(AfSignRewardExtDo signRewardExt : signList){
                                if(StringUtil.equals(signRewardExt.getUserId()+"",userId+"")){
                                    signRewardExt.setAmount(amount);
                                }else if(StringUtil.equals(signRewardExt.getUserId()+"",rewardUserId+"")){
                                    signRewardExt.setAmount(rewardAmount);
                                }
                            }
                            afSignRewardExtService.increaseMoneyBtach(signList);
                        }else {
                            if(StringUtil.equals(signList.get(0).getUserId()+"",userId+"")){
                                signList.get(0).setAmount(amount);
                                afSignRewardExtService.increaseMoney(signList.get(0));
                                AfSignRewardExtDo signRewardExt = H5SupplementSignInfoOutController.buildSignRewardExt(rewardUserId,rewardAmount);
                                afSignRewardExtService.saveRecord(signRewardExt);
                            }else{
                                signList.get(0).setAmount(rewardAmount);
                                afSignRewardExtService.increaseMoney(signList.get(0));
                                AfSignRewardExtDo signRewardExt = H5SupplementSignInfoOutController.buildSignRewardExt(userId,amount);
                                afSignRewardExtService.saveRecord(signRewardExt);
                            }
                        }
                    }else{
                        //帮签成功 打开者增加余额
                        AfSignRewardExtDo afSignRewardExt = H5SupplementSignInfoOutController.buildSignRewardExt(userId,amount);
                        //帮签成功 分享者增加余额
                        AfSignRewardExtDo signRewardExt = H5SupplementSignInfoOutController.buildSignRewardExt(rewardUserId,rewardAmount);
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

    private Map<String,Object> homeInfo (Long userId, Map<String,Object> resp,String push,BigDecimal rewardAmount){
        //今天是否签到
        String status = afSignRewardService.isExist(userId)==false?"N":"Y";
        resp.put("rewardStatus",status);
        resp = afSignRewardExtService.getHomeInfo(userId,status,rewardAmount);
        // 正式环境和预发布环境区分
        String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        String homeBanner = AfResourceType.RewardHomeBanner.getCode();
        resp.put("rewardBannerList",afResourceService.rewardBannerList(type,homeBanner));
        //任务列表
        AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
        AfUserAuthStatusDo authStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId,"ONLINE");
        String level = afUserAuthService.signRewardUserLevel(userId,userAuthDo);
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
        data.put("rewardAmount",new BigDecimal(data.get("rewardAmount").toString()).add(rewardAmount).setScale(2, RoundingMode.HALF_UP));
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
                    BigDecimal amount;
                    if(!flag){
                        amount = randomNum(afResource.getValue1(),afResource.getValue2()).setScale(2, RoundingMode.HALF_UP);
                        AfSignRewardExtDo afSignRewardExt = H5SupplementSignInfoOutController.buildSignRewardExt(friendUserId,amount);
                        afSignRewardExtService.saveRecord(afSignRewardExt);
                    }else{
                        amount = randomNum(afResource.getValue3(),afResource.getValue4()).setScale(2, RoundingMode.HALF_UP);
                        AfSignRewardExtDo afSignRewardExt = H5SupplementSignInfoOutController.buildSignRewardExt(friendUserId,amount);
                        afSignRewardExtService.increaseMoney(afSignRewardExt);
                    }
                    AfSignRewardDo rewardDo = H5SupplementSignInfoOutController.buildSignReward(userId, SignRewardType.FOUR.getCode(),null,amount,null);
                    List<AfSignRewardDo> list = new ArrayList<>();
                    list.add(rewardDo);
                    list.add(signRewardDo);
                    afSignRewardService.saveRecordBatch(list);
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
