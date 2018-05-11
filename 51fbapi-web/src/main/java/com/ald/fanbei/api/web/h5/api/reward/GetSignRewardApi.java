package com.ald.fanbei.api.web.h5.api.reward;


import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSignRewardExtService;
import com.ald.fanbei.api.biz.service.AfSignRewardService;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSignRewardDo;
import com.ald.fanbei.api.dal.domain.AfSignRewardExtDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Random;


/**
 * 分享成功 领取 奖励
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("getSignRewardApi")
public class GetSignRewardApi implements H5Handle {

    @Resource
    AfSignRewardService afSignRewardService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    NumberWordFormat numberWordFormat;
    @Resource
    AfSignRewardExtService afSignRewardExtService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        Integer signType = NumberUtil.objToIntDefault(context.getData("signType"),null);
        Long friendUserId = NumberUtil.objToLongDefault(context.getData("friendUserId"),null);
        AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
        afSignRewardDo.setUserId(context.getUserId());
        afSignRewardDo.setGmtCreate(new Date());
        afSignRewardDo.setGmtModified(new Date());
        afSignRewardDo.setType(signType);
        afSignRewardDo.setStatus(0);
        afSignRewardDo.setFriendUserId(friendUserId);
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("SIGN_COEFFICIENT");
        if(afResourceDo == null || numberWordFormat.isNumeric(afResourceDo.getValue())){
            return new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        if(signType == 0){
            if(afSignRewardService.isExist(afSignRewardDo.getUserId())){
                return new H5HandleResponse(context.getId(), FanbeiExceptionCode.USER_SIGN_EXIST);
            }
            if(userSign(afSignRewardDo,afResourceDo)){
                return new H5HandleResponse(context.getId(), FanbeiExceptionCode.USER_SIGN_FAIL);
            }


        }else if(signType == 1){

        }else if(signType == 2){

        }else {
            resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }

        return resp;
    }

    /**
     * 自己签到
     * @param afSignRewardDo
     * @return
     */
    private boolean userSign(AfSignRewardDo afSignRewardDo,AfResourceDo afResourceDo){
        boolean result = true;
        boolean flag = afSignRewardService.checkUserSign(afSignRewardDo.getUserId());
        if(flag){//多次签到
            //判断是当前周期的第几天
            AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(afSignRewardDo.getUserId());



        }else {//第一次签到
            BigDecimal rewardAmount = new BigDecimal(Math.random() * (Double.parseDouble(afResourceDo.getValue1()) - Double.parseDouble(afResourceDo.getValue2())) + afResourceDo.getValue2()).setScale(2, RoundingMode.HALF_EVEN);
            afSignRewardDo.setAmount(rewardAmount);
            if(afSignRewardService.saveRecord(afSignRewardDo)<1 ){
                result = false;
            }
        }

        return result;
    }

    /**
     * 帮签
     * @param afSignRewardDo
     * @return
     */
    private boolean friendSign(AfSignRewardDo afSignRewardDo){

        return true;
    }

    /**
     * 补签
     * @param afSignRewardDo
     * @return
     */
    private boolean supplementSign(AfSignRewardDo afSignRewardDo){
        return true;
    }

}
