package com.ald.fanbei.api.biz.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.ActivityRuleBo;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.Source;
import com.ald.fanbei.api.common.enums.UserCouponSource;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.domain.AfActivityDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午1:24:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public abstract class AbstractActiveRuleEngine implements ActiveRuleEnginer {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected static final String EMPTY_CONDITION = "-1";
	protected static final String EMPTY_ENDDATE = "-1";
	
	@Resource
	protected TransactionTemplate transactionTemplate;
	@Resource
	protected AfUserCouponService afUserCouponService;
	@Resource
	protected AfUserService afUserService;
	@Resource
	protected AfCouponService afCouponService;
	@Resource
	protected AfUserAccountDao afUserAccountDao;
	
	@Override
	public void executeRule(Map<String,Object> inputData,Source source) {
		Long userId = (Long)inputData.get("userId");
		logger.info(StringUtil.appendStrs("userId=",userId,",inputData=",inputData));
		try{
			Date now = new Date();
			AfActivityDo activityDo = getActivity(now);
			if(activityDo == null){
				return;
			}
			Map<String,List<ActivityRuleBo>> rules =  getRules(now,activityDo);
			logger.info(StringUtil.appendStrs("userId=",userId,",rules=",rules));
			if(rules == null || rules.size() < 0){
				return ;
			}
			Map<String,List<ActivityRuleBo>> matchRules = matchRule(now,inputData,rules,activityDo);
			if(matchRules == null || matchRules.size() < 0){
				return ;
			}
			logger.info(StringUtil.appendStrs("userId=",userId,",matchRules=",matchRules));
			exeRule(inputData,matchRules,source);
		}catch(Exception e){
			logger.error("executeRule error" + StringUtil.appendStrs("userId=",userId,",inputData=",inputData),e);
		}
//		pushMsg(inputData,matchRules);
	}
	
	/**
	 * 获取活动规则列表
	 *@param now
	 *@return
	 */
	protected abstract Map<String,List<ActivityRuleBo>> getRules(Date now,AfActivityDo activityDo);
	
	/**
	 * 匹配规则，根据规则对应的条件，结束时间判断是否符合规则
	 *@param now
	 *@param inputData
	 *@param rules
	 *@return
	 */
	protected abstract Map<String,List<ActivityRuleBo>> matchRule(Date now,Map<String,Object> inputData,Map<String,List<ActivityRuleBo>> rules,AfActivityDo activityDo);
	
	/**
	 * 执行规则
	 */
	protected abstract void exeRule(Map<String,Object> inputData,Map<String,List<ActivityRuleBo>> rules,Source source);
	
	protected abstract AfActivityDo getActivity(Date now);
	

	/**
	 * 增加用户优惠券
	 *@param item 活动规则bo
	 *@param userId 用户id
	 *@param ruleType 规则类型
	 *@param sourceId 来源id
	 *@param sourceAmount 来源条件金额
	 */
	protected void addUserCoupon(ActivityRuleBo item,Long userId,UserCouponSource ruleType,Long sourceId,Integer sourceAmount,Source source){
		AfCouponDo couponDo = afCouponService.getCouponById(item.getCouponId());
		if(couponDo == null){
			return;
		}
		AfUserDo user = afUserService.getUserById(userId);
		AfUserCouponDo userCoupon = new AfUserCouponDo();
		userCoupon.setUserId(user.getRid());
		userCoupon.setCouponId(item.getCouponId());
		userCoupon.setGmtStart(new Date());
		userCoupon.setGmtEnd(DateUtil.addDays(new Date(), couponDo.getValidDays()));
		userCoupon.setSourceType(ruleType.getCode());
		userCoupon.setStatus(CouponStatus.NOUSE.getCode());
		userCoupon.setUserId(userId);
		afUserCouponService.addUserCoupon(userCoupon);

	}
	
	/**
	 * 判断date1 在 date2之后
	 *@param date1
	 *@param date2
	 *@return true:date1在date2之后
	 */
	protected boolean checkDate1AfterDate2(String date1,Date date2){
		boolean result = false;
		Date endDate = DateUtil.parseDate(date1, DateUtil.DEFAULT_PATTERN_WITH_HYPHEN);
		endDate = DateUtil.addDays(endDate, 1);
		result = DateUtil.afterDay(endDate, date2);
		return result;
	}
	
	protected AfActivityDo checkActivity(AfActivityDo activityDo,Date now){
		if(activityDo == null 
				|| (activityDo.getGmtStart() != null && DateUtil.afterDay(activityDo.getGmtStart(), now)) 
				|| (activityDo.getGmtEnd() != null && DateUtil.afterDay(now, DateUtil.addDays(activityDo.getGmtEnd(), 1))) 
				|| StringUtils.isBlank(activityDo.getRuleJson())){
				return null;
		}
		return activityDo;
	}

}
