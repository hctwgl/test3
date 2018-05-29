package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SignRewardType;
import com.ald.fanbei.api.common.enums.ThirdType;
import com.ald.fanbei.api.common.enums.UserThirdType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


/**
 * 补签
 * @author cfp
 * @类描述：签到领金币
 */

@Component("supplementSignInfoApi")
@Validator("supplementSignInfoParam")
public class SupplementSignInfoApi implements H5Handle {

    @Resource
    AfSignRewardExtService afSignRewardExtService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfTaskService afTaskService;
    @Resource
    AfUserThirdInfoService afUserThirdInfoService;
    @Resource
    AfUserAuthStatusService afUserAuthStatusService;
    @Resource
    AfSignRewardService afSignRewardService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = NumberUtil.objToLongDefault(context.getData("userId"),null);//分享用户id
        String push = ObjectUtils.toString(context.getData("push"),null);//用户是否打开手机推送权限
        String wxCode = ObjectUtils.toString(context.getData("wxCode"),null);
        //活动规则
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("REWARD_RULE");
        if(null != afResourceDo){
            resp.addResponseData("rewardRule",afResourceDo.getValue());
        }else {
            resp.addResponseData("rewardRule","");
        }
        //判断用户和openId是否在爱上街绑定
        AfUserThirdInfoDo thirdInfo = checkBindOpen(wxCode);
        if(thirdInfo == null){
            resp.addResponseData("openType","2");
            return resp;
        }
        Long firendUserId = thirdInfo.getUserId();
        if(firendUserId == userId){//已经绑定并且是自己打开
            homeInfo(userId,resp,push);
            resp.addResponseData("openType","0");
        } else if(firendUserId != userId ){//已绑定
            homeInfo(firendUserId,resp,push);
            AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
            afSignRewardDo.setIsDelete(0);
            afSignRewardDo.setUserId(userId);
            afSignRewardDo.setGmtCreate(new Date());
            afSignRewardDo.setGmtModified(new Date());
            afSignRewardDo.setType(SignRewardType.THREE.getCode());
            afSignRewardDo.setStatus(0);
            afSignRewardDo.setFriendUserId(firendUserId);
            resp.addResponseData("openType","1");
        }else {//未绑定
            resp.addResponseData("openType","2");
        }
        return resp;
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



    private AfUserThirdInfoDo checkBindOpen(String wxCode){
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







}
