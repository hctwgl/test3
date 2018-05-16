package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo.ReqParam;
import com.ald.fanbei.api.biz.service.*;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DateFormat;
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


	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		//活动规则
		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("REWARD_RULE");
		if(null != afResourceDo){
			resp.addResponseData("rewardRule",afResourceDo.getValue());
		}else {
			resp.addResponseData("rewardRule","");
		}
		Long userId = context.getUserId();
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
			resp.addResponseData("isOpenRemind","N");
			//是否有余额
			resp.addResponseData("rewardAmount",BigDecimal.ZERO);
			//是否有补签
			resp.addResponseData("supplementSignDays",0);
		}else if(null != afSignRewardExtDo){
			//签到提醒
			resp.addResponseData("isOpenRemind",afSignRewardExtDo.getIsOpenRemind()>0?"Y":"N");
			//是否有余额
			resp.addResponseData("rewardAmount",afSignRewardExtDo.getAmount());
			//是否有补签
			int count = afSignRewardService.supplementSign(afSignRewardExtDo,0);
			resp.addResponseData("supplementSignDays",count);
		}

		//banner
		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		List<Object> rewardBannerList = new ArrayList<Object>();
		String homeBanner = AfResourceType.RewardHomeBanner.getCode();
		// 正式环境和预发布环境区分
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
			rewardBannerList = getObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(homeBanner));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
			rewardBannerList = getObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(homeBanner));
		}
		resp.addResponseData("rewardBannerList",rewardBannerList);

		//今天是否签到
		resp.addResponseData("rewardStatus",afSignRewardService.isExist(userId)==false?"N":"Y");

		//任务列表
		String level = afUserAuthService.signRewardUserLevel(userId);
		resp.addResponseData("taskList",afTaskService.getTaskInfo(level,userId));

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

}
