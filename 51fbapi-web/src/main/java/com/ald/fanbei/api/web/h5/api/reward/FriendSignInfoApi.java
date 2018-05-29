package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SignRewardType;
import com.ald.fanbei.api.common.enums.ThirdType;
import com.ald.fanbei.api.common.enums.UserThirdType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.h5.controller.H5FriendSignInfoOutController;
import com.ald.fanbei.api.web.h5.controller.H5SupplementSignInfoOutController;
import com.ald.fanbei.api.web.validator.Validator;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;


/**
 * 帮签
 * @author cfp
 * @类描述：签到领金币
 */

@Component("friendSignInfoApi")
public class FriendSignInfoApi implements H5Handle {

    @Resource
    AfSignRewardExtService afSignRewardExtService;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfSignRewardService afSignRewardService;
    @Resource
    AfTaskService afTaskService;
    @Resource
    NumberWordFormat numberWordFormat;
    @Resource
    AfUserThirdInfoService afUserThirdInfoService;
    @Resource
    AfUserAuthStatusService afUserAuthStatusService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = NumberUtil.objToLongDefault(context.getData("userId"),null);//分享用户id
        String push = ObjectUtils.toString(context.getData("push"),"N");
        String wxCode = ObjectUtils.toString(context.getData("wxCode"),null);
        //活动规则
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("REWARD_RULE");
        if(null != afResourceDo){
            resp.addResponseData("rewardRule",afResourceDo.getValue());
        }else {
            resp.addResponseData("rewardRule","");
        }
        //判断用户和openId是否在爱上街绑定
        AfResourceDo afResource = afResourceService.getWechatConfig();
        String appid = afResource.getValue();
        String secret = afResource.getValue1();
        JSONObject userWxInfo = WxUtil.getUserInfoWithCache(appid, secret, wxCode);
        resp.addResponseData("userWxInfo",userWxInfo.toJSONString());
        AfUserThirdInfoDo thirdInfo = checkBindOpen(wxCode);
        if(thirdInfo == null){
            resp.addResponseData("openType","2");
            return resp;
        }
        Long firendUserId = thirdInfo.getUserId();
        if(firendUserId == userId){//已经绑定并且是自己打开
            homeInfo(userId,resp,push);
            resp.addResponseData("openType","0");
        } else {//已绑定
            homeInfo(firendUserId,resp,push);
            AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
            afSignRewardDo.setIsDelete(0);
            afSignRewardDo.setUserId(userId);
            afSignRewardDo.setGmtCreate(new Date());
            afSignRewardDo.setGmtModified(new Date());
            afSignRewardDo.setType(SignRewardType.ONE.getCode());
            afSignRewardDo.setStatus(0);
            afSignRewardDo.setFriendUserId(firendUserId);
            if(friendSign(afSignRewardDo,userId,firendUserId,resp)){
                return  new H5HandleResponse(context.getId(),FanbeiExceptionCode.USER_SIGN_FAIL);
            }
            resp.addResponseData("openType","1");
        }
        return resp;
    }


    private boolean friendSign(AfSignRewardDo afSignRewardDo,final Long userId, final Long friendUserId,H5HandleResponse resp){
        boolean result;
        final AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("NEW_FRIEND_USER_SIGN");
        final AfResourceDo afResource = afResourceService.getSingleResourceBytype("SIGN_COEFFICIENT");
        if(afResourceDo == null || afResource == null || numberWordFormat.isNumeric(afResourceDo.getValue())){
            throw new FanbeiException("param error", FanbeiExceptionCode.PARAM_ERROR);
        }
        //帮签次数
        if(afSignRewardService.frienddUserSignCountToDay(userId,friendUserId)){
            throw new FanbeiException("friend user sign exist", FanbeiExceptionCode.FRIEND_USER_SIGN_EXIST);
        }
        final boolean flag = afSignRewardService.checkUserSign(friendUserId);
        int count = afSignRewardService.frienddUserSignCount(userId,friendUserId);
        BigDecimal rewardAmount ;
        if(count<1){//第一次帮签
            rewardAmount = randomNum(afResourceDo.getValue3(),afResourceDo.getValue4());
        }else{//多次帮签
            rewardAmount = randomNum(afResourceDo.getPic2(),afResourceDo.getPic1());
        }
        final BigDecimal resultAmount = rewardAmount;
        resp.addResponseData("rewardAmount",rewardAmount);
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
                    if(flag){
                        amount = randomNum(afResource.getValue1(),afResource.getValue2());
                        AfSignRewardExtDo afSignRewardExt = H5SupplementSignInfoOutController.buildSignRewardExt(userId,amount);
                        afSignRewardExtService.saveRecord(afSignRewardExt);
                    }else{
                        amount = randomNum(afResource.getValue3(),afResource.getValue4());
                        AfSignRewardExtDo afSignRewardExt = H5SupplementSignInfoOutController.buildSignRewardExt(userId,amount);
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


    private H5HandleResponse homeInfo (Long userId,H5HandleResponse resp,String push){
        //今天是否签到
        String status = afSignRewardService.isExist(userId)==false?"N":"Y";
        resp.addResponseData("rewardStatus",status);
        Map<String,Object> map = afSignRewardExtService.getHomeInfo(userId,status);
        resp.addResponseData("isOpenRemind",map.get("isOpenRemind"));
        resp.addResponseData("rewardAmount",map.get("rewardAmount"));
        resp.addResponseData("supplementSignDays",map.get("supplementSignDays"));
        resp.addResponseData("signDays",map.get("signDays"));

        // 正式环境和预发布环境区分
        String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        String homeBanner = AfResourceType.RewardHomeBanner.getCode();
        resp.addResponseData("rewardBannerList",afResourceService.rewardBannerList(type,homeBanner));

        //任务列表
        AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
        AfUserAuthStatusDo authStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId,"ONLINE");
        String level = afUserAuthService.signRewardUserLevel(userId,userAuthDo);
        resp.addResponseData("taskList",afTaskService.getTaskInfo(level,userId,push,userAuthDo,authStatusDo));
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



}
