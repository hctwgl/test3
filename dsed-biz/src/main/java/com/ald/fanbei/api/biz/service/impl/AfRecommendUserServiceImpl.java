package com.ald.fanbei.api.biz.service.impl;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfCouponDao;
import com.ald.fanbei.api.dal.dao.AfRecommendUserDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserAuthDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfRecommendMoneyDo;
import com.ald.fanbei.api.dal.domain.AfRecommendShareDo;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfRecommendUserDto;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;

import sun.awt.geom.AreaOp;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Service("afRecommendUserService")
public class AfRecommendUserServiceImpl implements AfRecommendUserService {

	@Resource
	AfRecommendUserDao afRecommendUserDao;
	@Resource
	AfUserAccountDao afUserAccountDao;


	@Resource
	AfResourceDao afResourceDao;

	@Resource
	AfBorrowCashDao afBorrowCashDao;

	@Resource
	AfUserAccountLogDao afUserAccountLogDao;

	@Resource
	AfUserDao afUserDao;
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfUserCouponDao  afUserCouponDao;
	@Resource
	AfCouponDao afCouponDao;
	@Resource
	AfUserAuthDao afUserAuthDao;
	
	@Resource
	AfIdNumberService afIdNumberService;
	@Resource
	AfTaskUserService afTaskUserService;
	
	@Resource
	private TransactionTemplate transactionTemplate;

	private BigDecimal getAddMoney() {
		List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_MONEY");
		if (list != null && list.size() > 0) {
			try {
				return new BigDecimal(Double.parseDouble(list.get(0).getValue()));
			} catch (Exception e) {

			}
		}
		return new BigDecimal(50);
	}

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());



//	private AfResourceDo getRecommendRecource(){
//		List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_MONEY");
//		return list.get(0);
//	}
	
	private AfResourceDo getRecommendRecourceForBorrow(){
		List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_BORROW");
		return list.get(0);
	}
	
	private AfResourceDo getRecommendRecourceForStrongWind(){
		List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_STRONG_WIND");
		return list.get(0);
	}


	//value1 提交强风控奖历
	//value2 1级奖历
	//value3 2级奖历
	//value4 多少天内借款才有奖历
	private BigDecimal getMoney(AfBorrowCashDo afBorrowCashDo, AfResourceDo afResourceDo,int type){
		if(type ==1){
			return  new  BigDecimal(Double.parseDouble(afResourceDo.getValue()));
		}
		else if(type ==2){
			BigDecimal t = new  BigDecimal(Double.parseDouble(afResourceDo.getValue1()));
			String firstMoney = afResourceDo.getValue3();
			if(firstMoney == null){
			       firstMoney = "150.00";
			}
			BigDecimal limit = new BigDecimal(firstMoney);
			if(t.compareTo(limit)== 1){
			    t =  new BigDecimal(firstMoney);
		        }
			
			return afBorrowCashDo.getAmount().multiply(t);
		}
		else if(type ==3){
			BigDecimal t = new  BigDecimal(Double.parseDouble(afResourceDo.getValue2()));
			String secondMoney = afResourceDo.getValue4();
			if(secondMoney == null){
			    secondMoney = "25.00";
			}
			BigDecimal limit = new BigDecimal(secondMoney);
			if(t.compareTo(limit)== 1){
			    t =  new BigDecimal(secondMoney);
		        }
			
			return afBorrowCashDo.getAmount().multiply(t);
		}
		return null;
	}

	private int registOfDistance(AfResourceDo afResourceDo){
		return  Integer.parseInt(afResourceDo.getPic1());
	}

	private BigDecimal getBorrowMoney(AfBorrowCashDo afBorrowCashDo, AfResourceDo afResourceDo, int len){
		if(len ==0){
			return getMoney(afBorrowCashDo,afResourceDo,2);
		}
		if(len ==1){
			return getMoney(afBorrowCashDo,afResourceDo,3);
		}
		return BigDecimal.ZERO;
	}



	/**
	 *
	 * @param userId
	 * @param createTime
	 * @return
	 */
	public int updateRecommendByBorrow(long userId,final Date createTime) {
		try {
			logger.info("{updateRecommendByBorrow} userId=" + userId);
			// 不影响原来逻辑，不保持事物一样
			final AfRecommendUserDo afRecommendUserDo = afRecommendUserDao.getARecommendUserByIdAndType(userId,1);
			if (afRecommendUserDo != null && !afRecommendUserDo.isIs_loan()) {
				Long count = 0L;
				HashMap map = afBorrowCashDao.getBorrowCashByRemcommend(userId);
				logger.info("{update begin} userId=" + userId);
				try {
					count = (Long) map.get("count");
					logger.info("{update begin count} count="+count+"userId=" + userId);
					if (count > 1)
						return 1;
				} catch (Exception e) {
					logger.error("{update userBorrowError} userId=" + userId);
				}
				try {
					//若被邀请者在活动时间之前注册的，即使或者之后完成任务，也不给邀请者发放奖励
				  if (afRecommendUserDo != null) {
				    String  activityTime = null;
				    Date acTime = new Date();
				    AfResourceDo activityStart = new AfResourceDo();
					   List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_START_TIME");
					   activityStart = list.get(0);
					    if(activityStart !=null){
						 activityTime = activityStart.getValue();
						 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						 acTime = sdf.parse(activityTime);
					
					    }
					    
				     if(afRecommendUserDo.getGmt_create().before(acTime)){
					 return 1;
				       }
				  }
				} catch (Exception e) {
					logger.error("{update userBorrowError for activityTime} userId=" + userId);
				}
				
				/*//给该用户送优惠券（还款券）
				String tag = "_FIRST_LOAN_";
				String sourceType = CouponSenceRuleType.FIRST_LOAN.getCode();
			        sentUserCoupon(userId,tag,sourceType);*/
			        
				final AfBorrowCashDo afBorrowCashDo = afBorrowCashDao.getBorrowCash(userId);
				final AfResourceDo afResourceDo = getRecommendRecourceForBorrow();
				int borrowDay = registOfDistance(afResourceDo);
				logger.info("{updateLoanById begin } afBorrowCashDo=,afResourceDo="+afBorrowCashDo.toString(),afResourceDo.toString());
				if(borrowDay >0){
        				AfUserDo afUserDo = afUserDao.getUserById(userId);
        				Date p = DateUtil.addDays(afUserDo.getGmtCreate(),borrowDay);
        				if(p.before(afBorrowCashDo.getGmtCreate())){
        					return 1;
        				}
				}
				
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						try{
							AfRecommendUserDo _afu = new AfRecommendUserDo();
							_afu.setId(afRecommendUserDo.getId());
							_afu.setLoan_time(createTime);
							BigDecimal addMoney = getAddMoney();
							_afu.setPrize_money(addMoney);
							
							afRecommendUserDao.updateLoanById(_afu);
							// 修改返现金额
							addRecommendBorrowMoney(afResourceDo,afBorrowCashDo,afRecommendUserDo,0,afRecommendUserDo.getUserId());
						}
						catch (Exception e){
							status.setRollbackOnly();
						}
					}
				});


			}
			return 1;
		} catch (Exception e) {
			logger.error("update userBorrowError userId=" + userId);
			return 1;
		}
	}

	String[] userLongType = {"RECOMMEND_CASH","RECOMMEND_CASH_SECOND"};

	int[] recommendMoneyType = {2,3};

	int lenCount = 2;

	private void addRecommendBorrowMoney(AfResourceDo afResourceDo, AfBorrowCashDo afBorrowCashDo, AfRecommendUserDo afRecommendUserDo,int len,long userId){
		BigDecimal money = getBorrowMoney(afBorrowCashDo,afResourceDo, len);

		long pid = afRecommendUserDo.getParentId();
		AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
		afUserAccountDo.setUserId(pid);
		afUserAccountDo.setRebateAmount(money);
		afUserAccountDao.updateRebateAmount(afUserAccountDo);

		AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
		afUserAccountLogDo.setAmount(money);
		afUserAccountLogDo.setUserId(pid);
		afUserAccountLogDo.setType(userLongType[len]);
		afUserAccountLogDo.setRefId(String.valueOf(userId));
		afUserAccountLogDao.addUserAccountLog(afUserAccountLogDo);

		// add by luoxiao for 边逛边赚，增加零钱明细
		afTaskUserService.addTaskUser(pid, UserAccountLogType.RECOMMEND_USER.getName(), money);
		// end by luoxiao

		AfRecommendMoneyDo afRecommendMoneyDo = new AfRecommendMoneyDo();
		afRecommendMoneyDo.setType(recommendMoneyType[len]);
		afRecommendMoneyDo.setMoney(money);
		afRecommendMoneyDo.setUserId(userId);
		afRecommendMoneyDo.setParentId(afRecommendUserDo.getParentId());
		logger.info("addShared5 userName:" +afRecommendMoneyDo.getUserId() );
		afRecommendUserDao.addRecommendMoney(afRecommendMoneyDo);

		if(len<1){
			len = len +1;
			AfRecommendUserDo _afRecommendUserDo = afRecommendUserDao.getARecommendUserByIdAndType(afRecommendUserDo.getParentId(),1);
			if(_afRecommendUserDo !=null){
				addRecommendBorrowMoney(afResourceDo, afBorrowCashDo,  _afRecommendUserDo,len,userId);
			}
		}

	}




	int riskMoney = 2;
	/**
	 * 通过强风控就给推荐人加5块钱
	 * 
	 * @param userId
	 * @return
	 */
	public int updateRecommendCash(long userId) {
		try {   
		        AfResourceDo afResourceDo = getRecommendRecourceForStrongWind();
			int distanceDay = registOfDistance(afResourceDo);
			
        		AfUserDo afUserDo = afUserDao.getUserById(userId);
        		Date p = DateUtil.addDays(afUserDo.getGmtCreate(),distanceDay);
        	        if(p.before(new Date())){
        			return 1;
        	        }
			
        	        
			AfRecommendUserDo afRecommendUserDo = afRecommendUserDao.getARecommendUserByIdAndType(userId,1);
			if (afRecommendUserDo != null) {
			    	//若被邀请者在活动时间之前注册的，即使或者之后完成任务，也不给邀请者发放奖励
			    try{
			    String  activityTime = null;
			    Date acTime = new Date();
			    AfResourceDo activityStart = new AfResourceDo();
				   List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_START_TIME");
				   activityStart = list.get(0);
				    if(activityStart !=null){
					 activityTime = activityStart.getValue();
					 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					 acTime = sdf.parse(activityTime);
				
				    }
				    
			     if(afRecommendUserDo.getGmt_create().before(acTime)){
				 return 1;
			     }
			
			   }catch(Exception e){
			    logger.error("updateRecommendCash error for activity time"+e);
		         }
			         

				/********临时逻辑：被邀请人未满20周岁时不给邀请人发放奖励***************/
				AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
				if (idNumberDo == null || idNumberDo.getRid() == null) {
					logger.error("updateRecommendCash error : idNumberDo is null");
					return 1;
				}
				if (StringUtil.isEmpty(idNumberDo.getBirthday())) {
					logger.error("updateRecommendCash error : birthday is null");
					return 1;
				}
				String[] birthdayStrings = idNumberDo.getBirthday().split("\\.");
				Integer year = Integer.valueOf(birthdayStrings[0]);
				Integer month = Integer.valueOf(birthdayStrings[1]);
				Integer day = Integer.valueOf(birthdayStrings[2]);
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month - 1, day);
				Date time = calendar.getTime();
				Date falg = DateUtil.addMonths(new Date(), -240);
				Date falg2 = DateUtil.addMonths(new Date(), -540);
				if (falg.getTime() < time.getTime() || falg2.getTime() > time.getTime()) {
					// 说明小于20周岁，大于45岁,不发放奖励
					return 1;
				}
				/********临时逻辑：被邀请人未满20周岁时不给邀请人发放奖励***************/
				//AfResourceDo afResourceDo = getRecommendRecource();

				BigDecimal money = getMoney(null,afResourceDo,1);

				afRecommendUserDo.setLoan_time(null);
//				afRecommendUserDo.setPrize_money(BigDecimal.valueOf(riskMoney));
				afRecommendUserDo.setPrize_money(money);
				afRecommendUserDao.updateLoanById(afRecommendUserDo);

				long pid = afRecommendUserDo.getParentId();
				    //单个用户邀请用户提交信用审核的数量，单日超出限制后，不予发放5元强风控奖励。
				if(afResourceDo.getValue3()!= null){
				     
				    int limitNum =  NumberUtil.objToInteger(afResourceDo.getValue3());
				    //count单日提交强风控的数量
				    int  submitNum = afRecommendUserDao.getSumSubmitRealname(pid);
				  //等于0无限制
				    if( limitNum >0 && submitNum > limitNum){
					return 1;
				    }
				}
				        
				    
        				AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
        				afUserAccountDo.setUserId(pid);
        //				afUserAccountDo.setRebateAmount(BigDecimal.valueOf(riskMoney));
        				afUserAccountDo.setRebateAmount(money);
        				afUserAccountDao.updateRebateAmount(afUserAccountDo);
        
        				AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
        //				afUserAccountLogDo.setAmount(BigDecimal.valueOf(riskMoney));
        				afUserAccountLogDo.setAmount(money);
        				afUserAccountLogDo.setUserId(pid);
        				afUserAccountLogDo.setType("RECOMMEND_RISK");
        				afUserAccountLogDo.setRefId(String.valueOf(afRecommendUserDo.getId()));
        				afUserAccountLogDao.addUserAccountLog(afUserAccountLogDo);

						// add by luoxiao for 边逛边赚，增加零钱明细
						afTaskUserService.addTaskUser(pid, UserAccountLogType.RECOMMEND_USER.getName(), money);
						// end by luoxiao
        
        
        				AfRecommendMoneyDo afRecommendMoneyDo = new AfRecommendMoneyDo();
        				afRecommendMoneyDo.setType(1);
        				afRecommendMoneyDo.setMoney(money);
        				afRecommendMoneyDo.setUserId(afRecommendUserDo.getUserId());
        				afRecommendMoneyDo.setParentId(afRecommendUserDo.getParentId());
						logger.info("addShared6 userName:" + afRecommendMoneyDo.getUserId() );
        				afRecommendUserDao.addRecommendMoney(afRecommendMoneyDo);
				}
				
			
			return 1;
		} catch (Exception e) {
			logger.error("update updateRecommendCash userId=" + userId, e);
			return 1;
		}
	}

	
	
	@Override
	public List<String> getActivityRule(String type) {
		return afResourceDao.getActivityRule(type);
	}

	@Override
	public String getUserRecommendCode(long userId) {
		return afUserDao.getUserRecommendCode(userId);
	}

	@Override
	public double getSumPrizeMoney(long userId,String activityTime) {
		return afRecommendUserDao.getSumPrizeMoney(userId,activityTime);
	}

	@Override
	public List<AfRecommendUserDto> rewardQuery(long userId, String type,Integer currentPage, Integer pageSize) {
		List<AfRecommendUserDto> listData= new ArrayList<>();
		if(pageSize==null){
			pageSize=5;
		}
		String  activityTime = null;
		    AfResourceDo activityStart = new AfResourceDo();
			   List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_START_TIME");
			   activityStart = list.get(0);
			    if(activityStart !=null){
				activityTime = activityStart.getValue();
			    }
		
		
		long pageNo=(currentPage-1)*pageSize;
		if("1".equals(type)){
			listData =afRecommendUserDao.firstRewardQuery(userId,pageNo,pageSize,activityTime);
		}else if("2".equals(type)){
			listData = afRecommendUserDao.twoLevelRewardQuery(userId,pageNo,pageSize,activityTime);
		}
		if(listData!=null){
			for (AfRecommendUserDto af: listData) {
				//加上状态
				AfRecommendUserDto afRecommendUserDto =afRecommendUserDao.getARecommendUserByIdAndType(af.getUserId(),1);
				
				
//				if(afRecommendUserDo.isIs_loan()){
//					af.setStatus("已借款");
//					af.setColor("1");
//				}else{
//					if("1".equals(type)){
//						int compare=afRecommendUserDo.getPrize_money().compareTo(BigDecimal.ZERO);
//						if(compare==1){
//							af.setStatus("提交信用审核");
//							af.setColor("1");
//						}else{
//							af.setStatus("已注册");
//							af.setColor("0");
//						}
//					}else{
//						af.setStatus("已注册");
//						af.setColor("0");
//					}
//
//				}
				//加上userName
				AfUserDo afUserDo =afUserDao.getUserById(af.getUserId());
				AfUserAuthDo afUseAuthrDo = afUserAuthDao.getUserAuthInfoByUserId(af.getUserId());
				af.setUserName(afUserDo.getUserName());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String createTime = sdf.format(afUserDo.getGmtCreate());
				af.setCreateTime(createTime);
				af.setIsloan(afRecommendUserDto.getIsloan());
				af.setRiskStatus(afUseAuthrDo.getRiskStatus());
				af.setIsSelfsupportRebate(afRecommendUserDto.getIsSelfsupportRebate());
			}
		}
		return listData;
	}

	@Override
	public int rewardQueryCount(long userId, String type) {
		int i =0;
		if("1".equals(type)){
			 i =afRecommendUserDao.firstRewardQueryCount(userId);
		}else if("2".equals(type)){
			i = afRecommendUserDao.twoLevelRewardQueryCount(userId);
		}
		return i;
	}
	
	
	
        private void sentUserCoupon(Long userId,String tag,String sourceType){
	//给该用户送优惠券（还款券）
	AfCouponCategoryDo  couponCategory  = afCouponCategoryService.getCouponCategoryByTag(tag);
	if(couponCategory != null){
	    	String coupons = couponCategory.getCoupons();
		JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
		for (int i = 0; i < couponsArray.size(); i++) {
			String couponId = (String) couponsArray.getString(i);
			AfCouponDo couponDo = afCouponService.getCouponById(Long.parseLong(couponId));
			if (couponDo != null) {
			    //赠送优惠券
			    Integer limitCount = couponDo.getLimitCount();
				Integer myCount = afUserCouponDao.getUserCouponByUserIdAndCouponId(userId,
						NumberUtil.objToLongDefault(couponId, 1l));
				if (limitCount <= myCount) {
				    continue;
				}
				Long totalCount = couponDo.getQuota();
				if (totalCount != -1 && totalCount != 0 && totalCount <= couponDo.getQuotaAlready()) {
				    continue;
				}

				AfUserCouponDo userCoupon = new AfUserCouponDo();
				userCoupon.setCouponId(NumberUtil.objToLongDefault(couponId, 1l));
				userCoupon.setGmtStart(new Date());
				if (StringUtils.equals(couponDo.getExpiryType(), "R")) {
					userCoupon.setGmtStart(couponDo.getGmtStart());
					userCoupon.setGmtEnd(couponDo.getGmtEnd());
					if (DateUtil.afterDay(new Date(), couponDo.getGmtEnd())) {
						userCoupon.setStatus(CouponStatus.EXPIRE.getCode());
					}
				} else {
					userCoupon.setGmtStart(new Date());
					if (couponDo.getValidDays() == -1) {
						userCoupon.setGmtEnd(DateUtil.getFinalDate());
					} else {
						userCoupon.setGmtEnd(DateUtil.addDays(new Date(), couponDo.getValidDays()));
					}
				}
				userCoupon.setSourceType(sourceType);
				userCoupon.setStatus(CouponStatus.NOUSE.getCode());
				userCoupon.setUserId(userId);
				afUserCouponDao.addUserCoupon(userCoupon);
				AfCouponDo couponDoT = new AfCouponDo();
				couponDoT.setRid(couponDo.getRid());
				couponDoT.setQuotaAlready(1);
				afCouponService.updateCouponquotaAlreadyById(couponDoT);
		       }
		  }
	    }
        }
	
	@Override
	public int insertShareWithData(String uuid, long userId, Integer type, String invitationCode) {
		logger.info("addShared7 userName:" + userId );
		return afRecommendUserDao.insertShareWithData(uuid,userId,type,invitationCode);
	}

	public HashMap getRecommedData(long userId) {
		return afRecommendUserDao.getRecommedData(userId);
	}

	public List<HashMap> getRecommendListByUserId(long userId, int pageIndex, int pageSize) {
		pageIndex = pageSize * pageIndex;
		return afRecommendUserDao.getRecommendListByUserId(userId, pageIndex, pageSize);
	}

	public List<HashMap> getRecommendListSort(Date startTime, Date endTime) {
		return afRecommendUserDao.getRecommendListSort(startTime, endTime);
	}

	/**
	 * 获取活动对应的图片或奖品
	 * 
	 * @param type
	 *            RECOMMEND_IMG||RECOMMEND_PRIZE
	 * @return
	 */
	public List<AfResourceDo> getActivieResourceByType(String type) {
		return afResourceDao.getActivieResourceByType(type);
	}

	/**
	 * 获取中奖名单
	 * 
	 * @param datamonth
	 *            月份
	 * @return
	 */
	public List<HashMap> getPrizeUser(String datamonth) {
		return afRecommendUserDao.getPrizeUser(datamonth);
	}

	public int addRecommendShared(AfRecommendShareDo afRecommendShareDo) {
		logger.info("addShared8 userName:" + afRecommendShareDo.getUser_id() );
		return afRecommendUserDao.addRecommendShared(afRecommendShareDo);
	}

	public HashMap getRecommendSharedById(String id) {
		return afRecommendUserDao.getRecommendSharedById(id);
	}


	@Override
	public List<AfRecommendUserDo> getListByParentIdAndType(AfRecommendUserDo queryRecommendUser) {
	    // TODO Auto-generated method stub
	    	return afRecommendUserDao.getListByParentIdAndType(queryRecommendUser);
	}


	@Override
	public Long findRefUserId(AfRecommendUserDo queryRecommendUser) {
	    // TODO Auto-generated method stub
	    	return afRecommendUserDao.findRefUserId(queryRecommendUser);
	}

	@Override
	public int updateRecommendUserById(AfRecommendUserDo afRecommendUserDo) {
	      return afRecommendUserDao.updateRecommendUserById(afRecommendUserDo);
	    // TODO Auto-generated method stub
	    
	}

	@Override
	public AfRecommendUserDo getARecommendUserById(Long userId) {
	    // TODO Auto-generated method stub
	    return afRecommendUserDao.getARecommendUserById(userId);
	}

	@Override
	public int getTodayShareTimes(Long userId){
		return afRecommendUserDao.getTodayShareTimes(userId);
	}

}
