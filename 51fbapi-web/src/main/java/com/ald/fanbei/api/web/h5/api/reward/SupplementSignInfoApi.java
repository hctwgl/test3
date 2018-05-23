package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SignRewardType;
import com.ald.fanbei.api.common.enums.ThirdType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
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
    AfUserService afUserService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfTaskService afTaskService;
    @Resource
    AfUserThirdInfoService afUserThirdInfoService;
    @Resource
    AfUserAuthStatusService afUserAuthStatusService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        String userName = ObjectUtils.toString(context.getData("userName"),null);//打开者手机号码
        Long userId = NumberUtil.objToLongDefault(context.getData("userId"),null);//分享用户id
        String thirdId = ObjectUtils.toString(context.getData("third_id"),null);//打开者的微信openId
        String thirdInfo = ObjectUtils.toString(context.getData("third_info"),null);//打开者的微信信息
        String push = ObjectUtils.toString(context.getData("push"),null);//用户是否打开手机推送权限
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
            if(!flag){
                saveUserThirdInfo(userName,thirdId,thirdInfo,afUserDo);
            }
            homeInfo(userId,resp,push);
            resp.addResponseData("openType","0");
        } else if(flag){//已绑定
            homeInfo(afUserDo.getRid(),resp,push);
            AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
            afSignRewardDo.setIsDelete(0);
            afSignRewardDo.setUserId(userId);
            afSignRewardDo.setGmtCreate(new Date());
            afSignRewardDo.setGmtModified(new Date());
            afSignRewardDo.setType(SignRewardType.THREE.getCode());
            afSignRewardDo.setStatus(0);
            afSignRewardDo.setFriendUserId(afUserDo.getRid());
            resp.addResponseData("openType","1");
        }else {//未绑定
            resp.addResponseData("openType","2");
            saveUserThirdInfo(userName,thirdId,thirdInfo,afUserDo);
        }
        return resp;
    }

    private H5HandleResponse homeInfo (Long userId,H5HandleResponse resp,String push){
        Map<String,Object> map = afSignRewardExtService.getHomeInfo(userId);
        resp.addResponseData("isOpenRemind",map.get("isOpenRemind"));
        resp.addResponseData("rewardAmount",map.get("rewardAmount"));
        resp.addResponseData("supplementSignDays",map.get("supplementSignDays"));

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

    private boolean saveUserThirdInfo(String userName,String thirdId,String thirdInfo ,AfUserDo eUserDo) {
        AfUserThirdInfoDo afUserThirdInfoDo = new AfUserThirdInfoDo();
        afUserThirdInfoDo.setCreator(userName);
        afUserThirdInfoDo.setGmtCreate(new Date());
        afUserThirdInfoDo.setGmtModified(new Date());
        afUserThirdInfoDo.setModifier(userName);
        afUserThirdInfoDo.setThirdId(thirdId);
        afUserThirdInfoDo.setThirdType(ThirdType.WX.getCode());
        afUserThirdInfoDo.setThirdInfo(thirdInfo);
        if(null != eUserDo){
            afUserThirdInfoDo.setUserId(eUserDo.getRid());
        }
        return afUserThirdInfoService.saveRecord(afUserThirdInfoDo)>0 ?true:false;
    }


    private boolean checkInfo(String userName){
        return true;
    }







}
