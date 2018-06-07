package com.ald.fanbei.api.web.h5.controller;


import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SignRewardType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
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
import java.text.DecimalFormat;
import java.util.*;


/**
 * 分享成功 领取 奖励
 * @author cfp
 * @类描述：签到领金币
 */
@RestController
@RequestMapping(value = "/mySignInfo/")
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
            map = homeInfo(afUserDo.getRid(),map,push);
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
        List<Integer> level = afUserAuthService.signRewardUserLevel(userId,userAuthDo,authStatusDo);
        resp.put("taskList",afTaskService.getTaskInfo(level,userId,push,userAuthDo,authStatusDo));
        return resp;
    }




}
