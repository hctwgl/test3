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
import com.ald.fanbei.api.web.common.H5HandleResponse;
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
public class H5SupplementSignInfoOutController extends H5Controller {

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
    AfUserAuthStatusService afUserAuthStatusService;
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfTaskUserService afTaskUserService;
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
        String resultStr = H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc() ).toString();
        try {
            String moblie = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
            String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
            String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
            String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
            String bsqToken = ObjectUtils.toString(request.getParameter("bsqToken"), "").toString();
            final Long rewardUserId = NumberUtil.objToLongDefault(request.getParameter("rewardUserId"),null);
            Map<String, Object> data = new HashMap<String, Object>();
            AfUserDo eUserDo = afUserService.getUserByUserName(moblie);
            if (eUserDo != null) {
                AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
                afSignRewardDo.setIsDelete(0);
                afSignRewardDo.setUserId(rewardUserId);
                afSignRewardDo.setGmtCreate(new Date());
                afSignRewardDo.setGmtModified(new Date());
                afSignRewardDo.setType(3);
                afSignRewardDo.setStatus(0);
                afSignRewardDo.setFriendUserId(eUserDo.getRid());
                afSignRewardDo.setAmount(BigDecimal.ZERO);
                afSignRewardService.saveRecord(afSignRewardDo);
                homeInfo(eUserDo.getRid(),data);
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SUPPLEMENT_SIGN_FAIL.getDesc(),"",data).toString();
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
            // modify by luoxiao 避免passwordSrc 为空出现NullPointerException异常
            String password = "";
            if(StringUtils.isNotEmpty(passwordSrc)){
                password = UserUtil.getPassword(passwordSrc, salt);
            }
            // end by luoxiao
            AfUserDo userDo = new AfUserDo();
            userDo.setSalt(salt);
            userDo.setUserName(moblie);
            userDo.setMobile(moblie);
            userDo.setNick("");
            userDo.setPassword(password);
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
            if(!signReward(request,userId)){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
            }
            homeInfo(userId,data);
            return resultStr;
        } catch (FanbeiException e) {
            logger.error("commitRegister fanbei exception" + e.getMessage());
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        } catch (Exception e) {
            logger.error("commitRegister exception", e);
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc()).toString();
        }
    }

    private boolean signReward(HttpServletRequest request,final Long userId){
        boolean result ;
        final Integer type = NumberUtil.objToIntDefault(request.getParameter("type"),null);
        final Long rewardUserId = NumberUtil.objToLongDefault(request.getParameter("rewardUserId"),null);
        final AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("NEW_FRIEND_USER_SIGN");
        if(afResourceDo == null || numberWordFormat.isNumeric(afResourceDo.getValue())){
            throw new FanbeiException("param error", FanbeiExceptionCode.PARAM_ERROR);
        }
        final BigDecimal rewardAmount = randomNum(afResourceDo.getValue1(),afResourceDo.getValue2());
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                    AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
                    afSignRewardDo.setIsDelete(0);
                    afSignRewardDo.setUserId(rewardUserId);
                    afSignRewardDo.setGmtCreate(new Date());
                    afSignRewardDo.setGmtModified(new Date());
                    afSignRewardDo.setType(type);
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
        AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(userId);
        if(null == afSignRewardExtDo){
            //新增SignRewardExt
            afSignRewardExtDo.setIsOpenRemind(0);
            afSignRewardExtDo.setUserId(userId);
            afSignRewardExtDo.setGmtModified(new Date());
            afSignRewardExtDo.setGmtCreate(new Date());
            afSignRewardExtDo.setAmount(BigDecimal.ZERO);
            afSignRewardExtDo.setCycleDays(10);
            afSignRewardExtDo.setFirstDayParticipation(null);
            afSignRewardExtService.saveRecord(afSignRewardExtDo);
            //签到提醒
            resp.put("isOpenRemind","N");
            //是否有余额
            resp.put("rewardAmount",BigDecimal.ZERO);
            //是否有补签
            resp.put("supplementSignDays",0);
        }else if(null != afSignRewardExtDo){
            //签到提醒
            resp.put("isOpenRemind",afSignRewardExtDo.getIsOpenRemind()>0?"Y":"N");
            //是否有余额
            resp.put("rewardAmount",afSignRewardExtDo.getAmount());
            //是否有补签
            int count = supplementSign(afSignRewardExtDo,0);
            resp.put("supplementSignDays",count);
        }
        // 正式环境和预发布环境区分
        String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        List<Object> rewardBannerList = new ArrayList<Object>();
        String homeBanner = AfResourceType.RewardHomeBanner.getCode();
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
            rewardBannerList = getObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(homeBanner));
        } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
            rewardBannerList = getObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(homeBanner));
        }
        resp.put("rewardBannerList",rewardBannerList);
        //任务列表
        resp.put("taskList",taskList(userId));
        return resp;
    }

    private List<Object> getObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
        List<Object> bannerList = new ArrayList<Object>();
        for (AfResourceDo afResourceDo : bannerResclist) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("imageUrl", afResourceDo.getValue());
            data.put("titleName", afResourceDo.getName());
            data.put("type", afResourceDo.getSecType());
            data.put("content", afResourceDo.getValue2());
            data.put("sort", afResourceDo.getSort());
            bannerList.add(data);
        }
        return bannerList;
    }

    private List<AfTaskDto> taskList(Long userId){
        List<AfTaskUserDo> isDailyTaskList = new ArrayList<AfTaskUserDo>();
        List<AfTaskUserDo> isNotDailyTaskList =	new ArrayList<AfTaskUserDo>();
        List<Long> isDailyList = new ArrayList<Long>();
        List<Long> isNotDailyList = new ArrayList<Long>();
        List<Long> finishedList = new ArrayList<Long>();
        List<Long> notFinishedList = new ArrayList<Long>();
        List<AfTaskDto> finalTaskList = new ArrayList<AfTaskDto>();
        AfTaskDto taskDto = new AfTaskDto();
        String loyalUsers;
        String ordinaryUser;
        String specialUser;
        String newUser;
        //用户层级
        int count = afOrderService.getFinishOrderCount(userId);
        //是否是忠实用户(count超过二次)
        if(count>1){
            loyalUsers = "Y";
        }else{
            loyalUsers = "N";
        }
        //是否是购物一次用户(count等于1次)
        if(count==1){
            ordinaryUser = "Y";
        }else{
            ordinaryUser = "N";
        }

        //消费分期强风控是否通过用户
        String onLicneStatus = riskOnline(userId);

        //消费分期强风控是否通过用户而且未购物
        if(StringUtil.equals("Y",onLicneStatus) && count == 0){
            specialUser = "Y";
        }else{
            specialUser = "N";
        }

        //是否是新用户
        if(count>0 || StringUtil.equals("Y",onLicneStatus)){
            newUser = "N";
        }else{
            AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
            if(userAuthDo != null){
                if(userAuthDo.getGmtFaces() == null && StringUtil.equals("N",userAuthDo.getBankcardStatus())
                        && userAuthDo.getGmtRealname() == null && StringUtil.equals("N",userAuthDo.getRealnameStatus())
                        && StringUtil.equals("N",userAuthDo.getFacesStatus()) ){
                    newUser = "Y";
                }else {
                    newUser = "N";
                }
            }else {
                newUser = "N";
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append("'").append("0").append("',");
        if(newUser.equals("Y")){
            sb.append("'").append("1").append("',");
        }
        if(onLicneStatus.equals("Y")){
            sb.append("'").append("2").append("',");
        }
        if(ordinaryUser.equals("Y")){
            sb.append("'").append("3").append("',");
        }
        if(loyalUsers.equals("Y")){
            sb.append("'").append("4").append("',");
        }
        if(specialUser.equals("Y")){
            sb.append("'").append("5").append("',");
        }
        sb.deleteCharAt(sb.length()-1);
        List<AfTaskDto> taskList = afTaskService.getTaskListByUserIdAndUserLevel(sb.toString());

        for(AfTaskDo afTaskDo : taskList){
            if(afTaskDo.getIsDailyUpdate().equals("1")){
                isDailyList.add(afTaskDo.getRid());
            }else{
                isNotDailyList.add(afTaskDo.getRid());
            }
        }
        if(isDailyList != null){
            isDailyTaskList = afTaskUserService.isDailyTaskList(userId,isDailyList);
        }
        if(isNotDailyList != null){
            isNotDailyTaskList = afTaskUserService.isNotDailyTaskList(userId,isNotDailyList);
        }
        isDailyTaskList.addAll(isNotDailyTaskList);
        for(AfTaskUserDo taskUserDo : isDailyTaskList){
            if(StringUtil.isBlank(taskUserDo.getCashAmount().toString()) && StringUtil.isBlank(taskUserDo.getCoinAmount().toString())
                    && StringUtil.isBlank(taskUserDo.getCouponId().toString())){
                notFinishedList.add(taskUserDo.getTaskId());
            }else{
                finishedList.add(taskUserDo.getTaskId());
            }
        }
        for(Long id : notFinishedList){
            for(AfTaskDto afTaskDo : taskList){
                if(id == afTaskDo.getRid()){
                    taskDto.setReceiveReward("N");
                    finalTaskList.add(afTaskDo);
                }
                break;
            }
        }
        for(AfTaskDto afTaskDo : taskList){
            boolean flag = true;
            boolean taskFlag = true;
            for(AfTaskUserDo afTaskUserDo : isDailyTaskList){
                if(afTaskUserDo.getTaskId() == afTaskDo.getRid()
                        || (StringUtil.equals(afTaskDo.getIsOpen().toString(),"1") && StringUtil.equals(afTaskDo.getIsDelete(),"0"))){
                    flag = false;
                }
                break;
            }
            for(Long id : notFinishedList){
                if(id == afTaskDo.getRid()
                        || (StringUtil.equals(afTaskDo.getIsOpen().toString(),"1") && StringUtil.equals(afTaskDo.getIsDelete(),"0"))){
                    taskFlag = false;
                }
                break;
            }
            if(flag && taskFlag){
                finalTaskList.add(afTaskDo);
            }
        }

        return finalTaskList;
    }

    private String riskOnline(Long userId){
        String flag ;
        AfUserAuthStatusDo authStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId,"ONLINE");
        if(authStatusDo != null){
            if(authStatusDo.getStatus().equals("Y")){
                flag = "Y";
            }else{
                flag = "N";
            }
        }else{
            flag = "N";
        }
        return flag;
    }

    private int supplementSign(AfSignRewardExtDo afSignRewardExtDo,int num){
        int countDays = 0;
        boolean flag = true;
        Date date = afSignRewardExtDo.getFirstDayParticipation();
        int cycle = afSignRewardExtDo.getCycleDays();
        Date startTime;
        Date endTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.formatDateToYYYYMMdd(date));
        while(flag){
            num ++;
            calendar.add(Calendar.DAY_OF_MONTH,(new BigDecimal(num-1).multiply(new BigDecimal(cycle))).intValue());
            startTime = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH,cycle-1);
            endTime = calendar.getTime();
            if((startTime.getTime() <= DateUtil.formatDateToYYYYMMdd(new Date()).getTime()) && (endTime.getTime() >= DateUtil.formatDateToYYYYMMdd(new Date()).getTime())){
                flag = false;
                int count = afSignRewardService.sumSignDays(afSignRewardExtDo.getUserId(),startTime);
                Long days = DateUtil.getNumberOfDatesBetween(startTime,new Date());
                if(days.intValue()>=count){
                    countDays = days.intValue()-count;
                }
            }else{
                supplementSign(afSignRewardExtDo,num);
            }
        }
        return countDays;
    }
}
