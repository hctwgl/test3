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
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSignRewardExtDo;
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
 * 发起贷款申请
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.ApplyLegalBorrowCashV2Api}
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

	private FanbeiContext contextApp;
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

		//是否有余额
		resp.addResponseData("rewardAmount",afSignRewardService.sumAmount(context.getUserId()));

		//签到提醒
		AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(context.getUserId());
		if(null == afSignRewardExtDo){
			resp.addResponseData("isOpenRemind","false");
		}else if(null != afSignRewardExtDo){
			resp.addResponseData("isOpenRemind",afSignRewardExtDo.getIsOpenRemind()>0?"true":"false");
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
		resp.addResponseData("rewardStatus",afSignRewardService.isExist(context.getUserId())==false?"false":"true");

		//任务列表

		//是否有补签

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

	private int supplementSign(AfSignRewardExtDo afSignRewardExtDo,int num){
		List<Object> supplementSignList = new ArrayList<Object>();
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
			calendar.add(Calendar.DAY_OF_MONTH,(new BigDecimal(num).multiply(new BigDecimal(cycle)).subtract(new BigDecimal(1))).intValue());
			startTime = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH,cycle);
			endTime = calendar.getTime();
			if((startTime.getTime() <= DateUtil.formatDateToYYYYMMdd(new Date()).getTime()) && (endTime.getTime() >= DateUtil.formatDateToYYYYMMdd(new Date()).getTime())){
				flag = false;
				int count = afSignRewardService.sumSignDays(afSignRewardExtDo.getUserId(),startTime);
				Long days = DateUtil.getNumberOfDatesBetween(startTime,new Date());
				if(days.intValue()>count){
					countDays = days.intValue()-count;
				}
			}else{
				supplementSign(afSignRewardExtDo,num);
			}
		}
		return countDays;
	}
	

}
