package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SignRewardType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.h5.controller.H5FriendSignInfoOutController;
import com.ald.fanbei.api.web.h5.controller.H5SupplementSignInfoOutController;
import com.ald.fanbei.api.web.validator.Validator;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * 帮签
 * @author cfp
 * @类描述：签到领金币
 */

@Component("friendSignInfoApi")
@Validator("friendSignInfoParam")
public class FriendSignInfoApi implements H5Handle {

    @Resource
    AfSignRewardExtService afSignRewardExtService;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfUserService afUserService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfSignRewardService afSignRewardService;
    @Resource
    AfTaskService afTaskService;
    @Resource
    NumberWordFormat numberWordFormat;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        String userName = ObjectUtils.toString(context.getData("userName"),null);//打开者手机号码
        Long userId = NumberUtil.objToLongDefault(context.getData("userId"),null);//分享用户id
        //判断用户和openId是否在爱上街绑定
        boolean flag = checkInfo(userName);
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        //活动规则
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("REWARD_RULE");
        if(null != afResourceDo){
            resp.addResponseData("rewardRule",afResourceDo.getValue());
        }else {
            resp.addResponseData("rewardRule","");
        }
        if(afUserDo.getRid() == userId){//自己打开
            homeInfo(userId,resp);
            resp.addResponseData("openType","0");
        } else if(flag){//已绑定
            homeInfo(afUserDo.getRid(),resp);
            AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
            afSignRewardDo.setIsDelete(0);
            afSignRewardDo.setUserId(userId);
            afSignRewardDo.setGmtCreate(new Date());
            afSignRewardDo.setGmtModified(new Date());
            afSignRewardDo.setType(SignRewardType.ONE.getCode());
            afSignRewardDo.setStatus(0);
            afSignRewardDo.setFriendUserId(afUserDo.getRid());
            if(friendSign(afSignRewardDo,userId,afUserDo.getRid(),resp)){
                return  new H5HandleResponse(context.getId(),FanbeiExceptionCode.USER_SIGN_FAIL);
            }
            resp.addResponseData("openType","1");
        }else {//未绑定
            resp.addResponseData("openType","2");
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
                    AfSignRewardDo rewardDo = H5SupplementSignInfoOutController.buildSignReward(userId, SignRewardType.FOUR.getCode(),null,amount);
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


    private H5HandleResponse homeInfo (Long userId,H5HandleResponse resp){
        Map<String,Object> map = afSignRewardExtService.getHomeInfo(userId);
        resp.addResponseData("isOpenRemind",map.get("isOpenRemind"));
        resp.addResponseData("rewardAmount",map.get("rewardAmount"));
        resp.addResponseData("supplementSignDays",map.get("supplementSignDays"));

        // 正式环境和预发布环境区分
        String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        String homeBanner = AfResourceType.RewardHomeBanner.getCode();
        resp.addResponseData("rewardBannerList",afResourceService.rewardBannerList(type,homeBanner));

        //任务列表
        String level = afUserAuthService.signRewardUserLevel(userId);
        resp.addResponseData("taskList",afTaskService.getTaskInfo(level,userId));
        return resp;
    }

    private boolean checkInfo(String userName){
        return true;
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



}
