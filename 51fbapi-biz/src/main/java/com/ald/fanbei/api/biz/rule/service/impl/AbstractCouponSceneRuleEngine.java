package com.ald.fanbei.api.biz.rule.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.biz.service.CouponSceneRuleEnginer;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfCouponDao;
import com.ald.fanbei.api.dal.dao.AfCouponSceneDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfCouponSceneDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.alibaba.druid.util.StringUtils;


/**
 * 
 *@类现描述：优惠券领券场景规则抽象类
 *@author chenjinhu 2017年2月17日 下午8:22:19
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public abstract class AbstractCouponSceneRuleEngine implements CouponSceneRuleEnginer {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected static final String EMPTY_CONDITION = "-1";
	
	@Resource
	private AfCouponDao afCouponDao;
	@Resource
	private AfUserDao afUserDao;
	@Resource
	private AfUserCouponDao afUserCouponDao;
	@Resource
	private AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	private AfUserAccountDao afUserAccountDao;
	@Resource
	protected AfCouponSceneDao afCouponSceneDao;
	@Resource
	protected AfResourceDao afResourceDao;
//	@Resource
//	protected TransactionTemplate transactionTemplate;
//	@Resource
//	protected HoaUserCouponService   hoaUserCouponService;
//	@Resource
//	protected HoaUserService         hoaUserService;
//	@Resource
//	protected HoaCouponService       hoaCouponService;
//	@Resource
//	protected HoaUserRedpacketsDao   hoaUserRedpacketsDao;
//	@Resource
//	protected PushService        pushService;
//	@Resource
//	protected HoaUserAccountDao hoaUserAccountDao;
	
	@Override
	public void executeRule(Map<String,Object> inputData) {
		Long userId = (Long)inputData.get("userId");
		logger.info(StringUtil.appendStrs("userId=",userId,",inputData=",inputData));
		try{
			Date now = new Date();
			AfCouponSceneDo couponSenceDo = getCouponScene(now);
			if(couponSenceDo == null){
				return;
			}
			Map<String,List<CouponSceneRuleBo>> rules =  getRules(now,couponSenceDo);
			logger.info(StringUtil.appendStrs("userId=",userId,",rules=",rules));
			if(rules == null || rules.size() < 0){
				return ;
			}
			Map<String,List<CouponSceneRuleBo>> matchRules = matchRule(now,inputData,rules,couponSenceDo);
			if(matchRules == null || matchRules.size() < 0){
				return ;
			}
			logger.info(StringUtil.appendStrs("userId=",userId,",matchRules=",matchRules));
			exeRule(inputData,matchRules);
		}catch(Exception e){
			logger.error("executeRule error" + StringUtil.appendStrs("userId=",userId,",inputData=",inputData),e);
		}
	}
	
	/**
	 * 获取优惠券场景对象
	 * @param now
	 * @return
	 */
	protected abstract AfCouponSceneDo getCouponScene(Date now);
	
	/**
	 * 获取符合场景的规则列表
	 *@param now
	 *@return
	 */
	protected abstract Map<String,List<CouponSceneRuleBo>> getRules(Date now,AfCouponSceneDo couponScene);
	
	/**
	 * 匹配规则，根据规则对应的条件，结束时间判断是否符合规则
	 *@param now
	 *@param inputData
	 *@param rules
	 *@return
	 */
	protected abstract Map<String,List<CouponSceneRuleBo>> matchRule(Date now,Map<String,Object> inputData,Map<String,List<CouponSceneRuleBo>> rules,AfCouponSceneDo couponScene);
	
	/**
	 * 执行规则
	 */
	protected abstract void exeRule(Map<String,Object> inputData,Map<String,List<CouponSceneRuleBo>> rules);

	/**
	 * 增加用户优惠券
	 * 
	 * @param item
	 * @param userId
	 * @param ruleType
	 * @param sourceRef
	 */
	//TODO 增加事物处理
	protected void addUserCoupon(CouponSceneRuleBo item, Long userId, CouponSenceRuleType ruleType, String sourceRef) {
		if(item.getCouponId()==null&&item.getResourceId()!=null){
			AfResourceDo afResourceDo =	afResourceDao.getResourceByResourceId(item.getResourceId());
			if (afResourceDo == null) {
				return;
			}
			
			AfUserAccountDo userAccountDo =	afUserAccountDao.getUserAccountInfoByUserId(userId);
			
			
			BigDecimal rebateAmount = BigDecimalUtil.add(userAccountDo.getRebateAmount(), new BigDecimal(afResourceDo.getValue()) );
			
			
			// 用户账号金额增加
			AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
			afUserAccountDo.setUserId(userId);
			afUserAccountDo.setRebateAmount(rebateAmount  );
			afUserAccountDao.updateUserAccount(afUserAccountDo);
			
			//增加account变更日志
			AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
			accountLog.setAmount(new BigDecimal(afResourceDo.getValue()));
			accountLog.setUserId(userId);
			accountLog.setRefId(sourceRef == null?"":sourceRef);
			accountLog.setType(ruleType.getCode());
			afUserAccountLogDao.addUserAccountLog(accountLog);
			
		}else if(item.getCouponId()!=null){
			AfCouponDo couponDo = afCouponDao.getCouponById(item.getCouponId());
			if (couponDo == null) {
				return;
			}
			
			Long totalCount = couponDo.getQuota();
			if(totalCount!=0&&totalCount<=couponDo.getQuotaAlready()){
				logger.error("pick coupon over");
				return ;
			}
			
			AfUserCouponDo userCoupon = new AfUserCouponDo();
			userCoupon.setCouponId(item.getCouponId());
			userCoupon.setStatus(CouponStatus.NOUSE.getCode());

			if(StringUtils.equals(couponDo.getExpiryType(), "R")   ){
				userCoupon.setGmtStart(couponDo.getGmtStart());
				userCoupon.setGmtEnd(couponDo.getGmtEnd());
				if(DateUtil.afterDay(new Date(), couponDo.getGmtEnd())){
					userCoupon.setStatus(CouponStatus.EXPIRE.getCode());
				}

			}else{
				userCoupon.setGmtStart(new Date());
				if(couponDo.getValidDays()==-1){
					userCoupon.setGmtEnd(DateUtil.getFinalDate());
				}else{
					userCoupon.setGmtEnd(DateUtil.addDays(new Date(), couponDo.getValidDays()));
				}

			}

			userCoupon.setSourceType(ruleType.getCode());
			userCoupon.setUserId(userId);
		
			afUserCouponDao.addUserCoupon(userCoupon);
			AfCouponDo couponDoT = new AfCouponDo();
			couponDoT.setRid(couponDo.getRid());
			couponDoT.setQuotaAlready(1);
			afCouponDao.updateCouponquotaAlreadyById(couponDoT);
			
		}
		
		

		
	}
	
	
//	
//	/**
//	 * 判断date1 在 date2之后
//	 *@param date1
//	 *@param date2
//	 *@return true:date1在date2之后
//	 */
//	protected boolean checkDate1AfterDate2(String date1,Date date2){
//		boolean result = false;
//		Date endDate = DateUtil.parseDate(date1, DateUtil.DEFAULT_PATTERN_WITH_HYPHEN);
//		endDate = DateUtil.addDays(endDate, 1);
//		result = DateUtil.afterDay(endDate, date2);
//		return result;
//	}
//	
	protected AfCouponSceneDo checkActivity(AfCouponSceneDo couponScene,Date now){
		if(couponScene == null 
				|| (couponScene.getGmtStart() != null && DateUtil.afterDay(couponScene.getGmtStart(), now)) 
				|| (couponScene.getGmtEnd() != null && DateUtil.afterDay(now, DateUtil.addDays(couponScene.getGmtEnd(), 1))) 
				|| StringUtil.isBlank(couponScene.getRuleJson())){
				return null;
		}
		return couponScene;
	}

}
