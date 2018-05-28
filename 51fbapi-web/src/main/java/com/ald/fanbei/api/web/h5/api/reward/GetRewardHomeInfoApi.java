package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo.ReqParam;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyLoanParam;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 签到领金币
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("getRewardHomeInfoApi")
public class GetRewardHomeInfoApi implements H5Handle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfSignRewardService afSignRewardService;
	@Resource
	AfSignRewardExtService afSignRewardExtService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfTaskService afTaskService;
	@Resource
	AfUserAuthStatusService afUserAuthStatusService;
	@Resource
	BizCacheUtil bizCacheUtil;

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		String push = ObjectUtils.toString(context.getData("push"),"N");
		//活动规则
		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("REWARD_RULE");
		if(null != afResourceDo){
			resp.addResponseData("rewardRule",afResourceDo.getValue());
		}else {
			resp.addResponseData("rewardRule","");
		}
		Long userId = context.getUserId();

		//今天是否签到
		String status = afSignRewardService.isExist(userId)==false?"N":"Y";
		resp.addResponseData("rewardStatus",status);

		Map<String,Object> map = afSignRewardExtService.getHomeInfo(userId,status);
		resp.addResponseData("isOpenRemind",map.get("isOpenRemind"));
		resp.addResponseData("rewardAmount",map.get("rewardAmount"));
		resp.addResponseData("supplementSignDays",map.get("supplementSignDays"));
		resp.addResponseData("signDays",map.get("signDays"));

		//banner
		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		String homeBanner = AfResourceType.RewardHomeBanner.getCode();
		// 正式环境和预发布环境区分
		resp.addResponseData("rewardBannerList",afResourceService.rewardBannerList(type,homeBanner));



		//任务列表
		AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		AfUserAuthStatusDo authStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId,"ONLINE");
		String level = afUserAuthService.signRewardUserLevel(userId,userAuthDo);
		resp.addResponseData("taskList",afTaskService.getTaskInfo(level,userId,push,userAuthDo,authStatusDo));

		return resp;
	}



}
