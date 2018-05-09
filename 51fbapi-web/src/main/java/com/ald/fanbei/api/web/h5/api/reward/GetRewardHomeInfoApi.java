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
	AfUserAuthStatusService afUserAuthStatusService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfTaskService afTaskService;
	@Resource
	AfTaskUserService afTaskUserService;

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
			resp.addResponseData("isOpenRemind","N");
		}else if(null != afSignRewardExtDo){
			resp.addResponseData("isOpenRemind",afSignRewardExtDo.getIsOpenRemind()>0?"Y":"N");
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
		resp.addResponseData("rewardStatus",afSignRewardService.isExist(context.getUserId())==false?"N":"Y");

		//是否有补签
		int count = supplementSign(afSignRewardExtDo,0);
		resp.addResponseData("supplementSignDays",count);

		//任务列表
		resp.addResponseData("taskList",taskList(context));

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

	private List<AfTaskDto> taskList(Context context){
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
		int count = afOrderService.getFinishOrderCount(context.getUserId());
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
		String onLicneStatus = riskOnline(context.getUserId());

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
			AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(context.getUserId());
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
		List<AfTaskDto> taskList = afTaskService.getTaskListByUserIdAndUserLevel(context.getUserId(),sb.toString());

		for(AfTaskDo afTaskDo : taskList){
			if(afTaskDo.getIsDailyUpdate().equals("1")){
				isDailyList.add(afTaskDo.getRid());
			}else{
				isNotDailyList.add(afTaskDo.getRid());
			}
		}
		if(isDailyList != null){
			isDailyTaskList = afTaskUserService.isDailyTaskList(context.getUserId(),isDailyList);
		}
		if(isNotDailyList != null){
			isNotDailyTaskList = afTaskUserService.isNotDailyTaskList(context.getUserId(),isNotDailyList);
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
	

}
