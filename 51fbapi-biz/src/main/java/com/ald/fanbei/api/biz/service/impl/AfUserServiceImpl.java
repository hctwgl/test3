package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.bo.risk.RiskAuthFactory.RiskEventType;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.CouponActivityType;
import com.ald.fanbei.api.common.enums.CouponCateGoryType;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfRecommendUserDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAuthDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.dao.AfUserRegisterTypeDao;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserOutDayDo;
import com.ald.fanbei.api.dal.domain.AfUserRegisterTypeDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserInvitationDto;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 
 * @author Xiaotianjian 2017年1月19日下午1:52:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserService")
public class AfUserServiceImpl extends BaseService implements AfUserService {
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfUserDao afUserDao;
	@Resource
	AfUserAuthDao afUserAuthDao;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	CouponSceneRuleEnginerUtil couponSceneRuleEnginerUtil;

	@Resource
	AfRecommendUserDao afRecommendUserDao;
	@Resource
	JpushService jpushService;

	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserRegisterTypeDao afUserRegisterTypeDao;
	
	@Resource
	AfUserCouponService afUserCouponService;

	@Resource
	BizCacheUtil bizCacheUtil;

	@Resource
	AfUserOutDayDao afUserOutDayDao;
	
	@Resource
	AfResourceService afResourceService;
	
	
	@Override
	public Long addUser(final AfUserDo afUserDo) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					afUserDao.addUser(afUserDo);
					AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
					afUserAuthDo.setUserId(afUserDo.getRid());
					logger.info(StringUtil.appendStrs("yuyuegetaddUser",afUserDo.getRid()));
					afUserAuthDao.addUserAuth(afUserAuthDo);

					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(afUserDo.getRid());
					account.setUserName(afUserDo.getUserName());
					afUserAccountDao.addUserAccount(account);
			        couponSceneRuleEnginerUtil.regist(afUserDo.getRid(),afUserDo.getRecommendId(),afUserDo);
			        //新人专享送自营商城优惠券
			        sentUserCouponGroup(afUserDo);
			       
			        long recommendId = afUserDo.getRecommendId();
					//#region add by hongzhengpei
			        if(recommendId !=0){
			        	//新增推荐记录表
						AfRecommendUserDo afRecommendUserDo = new AfRecommendUserDo();
						afRecommendUserDo.setUser_id(afUserDo.getRid());
						afRecommendUserDo.setParentId(recommendId);
						logger.info("addShared1 userName:" + afUserDo.getRid() );
						afRecommendUserDao.addRecommendUser(afRecommendUserDo);
					}
			        
					//#endregion

					//处理帐单日，还款日
					addOutDay(afUserDo.getRid());
					long userId = 0L;
					userId = afUserDo.getRid();
					return userId;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("addUser error:", e,afUserDo);
					return 0L;
				}
			}

			
		});

	}
	
	public Long toAddUser(final AfUserDo afUserDo,final String source) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					afUserDao.addUser(afUserDo);
					AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
					afUserAuthDo.setUserId(afUserDo.getRid());
					logger.info(StringUtil.appendStrs("yuyuegetaddUser",afUserDo.getRid()));
                    afUserAuthDao.addUserAuth(afUserAuthDo);

                    logger.info("addUser error regist ");
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(afUserDo.getRid());
					account.setUserName(afUserDo.getUserName());
					afUserAccountDao.addUserAccount(account);
			        couponSceneRuleEnginerUtil.regist(afUserDo.getRid(),afUserDo.getRecommendId(),afUserDo);
			        //新人专享送自营商城优惠券
                    logger.info("addUser error sentUserCouponGroup ");
			        sentUserCouponGroup(afUserDo);
			       
			        long recommendId = afUserDo.getRecommendId();
                    logger.info("addUser error recommendId ");
					//#region add by hongzhengpei
			        if(recommendId !=0){
			        	//新增推荐记录表
			                         //如果是一元活动，开关开启才绑定
			             		logger.info("add user source  = "+source  +"and user = "+JSONObject.toJSONString(afUserDo));
						AfRecommendUserDo afRecommendUserDo = new AfRecommendUserDo();
						afRecommendUserDo.setUser_id(afUserDo.getRid());
						afRecommendUserDo.setParentId(recommendId);
						afRecommendUserDo.setSource(source);
						if(!"oneYuan".equals(source)){
							logger.info("addShared2 userName:" + afUserDo.getRid() );
						        afRecommendUserDao.addRecommendUser(afRecommendUserDo);
						}else if("oneYuan".equals(source)){
						    try{
							AfResourceDo   biddingSwitch =   afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY","BIDDING_SWITCH");
							 String swtich = "";
			                		 String ctype = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
			                		//线上为开启状态
			                		 if (Constants.INVELOMENT_TYPE_ONLINE.equals(ctype) || Constants.INVELOMENT_TYPE_TEST.equals(ctype)) {
			                		     swtich = biddingSwitch.getValue();
			                		 } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(ctype) ){
			                		     swtich = biddingSwitch.getValue1();
			                		 }
							if(StringUtil.isNotBlank(swtich) && "O".equals(swtich) ){
								logger.info("addShared3 userName:" + afRecommendUserDo.getUserId());
							    afRecommendUserDao.addRecommendUser(afRecommendUserDo);
						        }
						     }catch(Exception e){
							logger.error("one yuan activity addUser error:", e,afUserDo);
						    }
						}
					}
			        
					//#endregion

					//处理帐单日，还款日
					addOutDay(afUserDo.getRid());
                    logger.info("addUser error addOutDay "+ JSON.toJSONString(afUserDo));
					long userId = 0L;
					userId = afUserDo.getRid();
                    logger.info("addUser error addOutDay ");
					return userId;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("addUser error ee",e);
                    logger.info("addUser error:={}", e.getCause());
					logger.info("addUser error:", e,afUserDo);
					return 0L;
				}
			}

			
		});

	}
	
	private void sentUserCouponGroup(AfUserDo afUserDo) {
	    // TODO Auto-generated method stub
	        String tag = CouponCateGoryType._FIRST_SINGLE_.getCode();
		String sourceType = CouponActivityType.FIRST_SINGLE.getCode();
		String msg = afUserCouponService.sentUserCouponGroup(afUserDo.getRid(),tag,sourceType);
		logger.info("sent new user couponGroup for first single and  msg = "+msg.toString());
	}

	@Override
	public int addUser(final AfUserDo afUserDo, final String type) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					afUserDao.addUser(afUserDo);
					AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
					afUserAuthDo.setUserId(afUserDo.getRid());
					logger.info(StringUtil.appendStrs("yuyuegetaddUser",afUserDo.getRid()));
					afUserAuthDao.addUserAuth(afUserAuthDo);

					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(afUserDo.getRid());
					account.setUserName(afUserDo.getUserName());
					afUserAccountDao.addUserAccount(account);
					couponSceneRuleEnginerUtil.regist(afUserDo.getRid(),afUserDo.getRecommendId(),afUserDo);
					//      
					  sentUserCouponGroup(afUserDo);
 
					long recommendId = afUserDo.getRecommendId();

					//#region add by hongzhengpei
					if(recommendId !=0){
						//新增推荐记录表
						AfRecommendUserDo afRecommendUserDo = new AfRecommendUserDo();
						afRecommendUserDo.setUser_id(afUserDo.getRid());
						afRecommendUserDo.setParentId(recommendId);
						logger.info("addShared4 userName:" + afRecommendUserDo.getUserId() );
						afRecommendUserDao.addRecommendUser(afRecommendUserDo);
					}
					//#endregion

					//处理帐单日，还款日
					addOutDay(afUserDo.getRid());
					if ("Q".equals(type)){
						AfUserRegisterTypeDo afUserRegisterTypeDo = new AfUserRegisterTypeDo();
						afUserRegisterTypeDo.setUserId(afUserDo.getRid());
						afUserRegisterTypeDo.setType(1);
						afUserRegisterTypeDao.insert(afUserRegisterTypeDo);
					}
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("addUser error:", e,afUserDo);
					return 0;
				}
			}
		});

	}

	/**
	 * 帐单日处理
	 * @param userId
	 */
	private void addOutDay(long userId){
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 7);
			int out_day = calendar.get(Calendar.DAY_OF_MONTH);
			List<Integer> _nodays = new ArrayList<>();
			_nodays.add(19);
			_nodays.add(20);
			List<Integer> _nodays1 = new ArrayList<>();
			_nodays1.add(29);
			_nodays1.add(30);
			_nodays1.add(31);
			AfUserOutDayDo afUserOutDayDo = new AfUserOutDayDo();
			int pay_day = 0;
			if (_nodays.contains(out_day)) {
				out_day = 21;
				pay_day = 1;

			} else if (_nodays1.contains(out_day)) {
				out_day = 1;
				pay_day = 11;
			} else {
				pay_day = out_day + 10;
				if (pay_day > 30) pay_day = pay_day - 30;

			}
			afUserOutDayDo.setUserId(userId);
			afUserOutDayDo.setOutDay(out_day);
			afUserOutDayDo.setPayDay(pay_day);
			afUserOutDayDao.addserOutDay(afUserOutDayDo);
		}
		catch (Exception e){
			logger.info("add out_day error:", e);
			logger.error("add out_day error",e);
		}
	}




	@Override
	public AfUserDo getUserById(Long userId) {
		return afUserDao.getUserById(userId);
	}

	@Override
	public int updateUser(AfUserDo afUserDo) {
		String key = Constants.CACHEKEY_USER_NAME + afUserDo.getUserName();
		bizCacheUtil.delCache(key);
		return afUserDao.updateUser(afUserDo);
	}

	@Override
	public AfUserDo getUserByUserName(String userName) {
		String key = Constants.CACHEKEY_USER_NAME + userName;
		AfUserDo userInfo = (AfUserDo)bizCacheUtil.getObject(key);
		if(userInfo != null){
			return userInfo;
		}
		
		userInfo = afUserDao.getUserByUserName(userName);
		if(userInfo != null){
			bizCacheUtil.saveObject(key, userInfo);
		}
		return userInfo;
	}

	@Override
	public Long getUserByBorrowCashStatus(Long userId) {
		return afUserDao.getUserByBorrowCashStatus(userId);
	}

	@Override
	public AfUserDo getUserByMobile(String mobile) {
		return afUserDao.getUserByMobile(mobile);
	}

	@Override
	public AfUserDo getUserByRecommendCode(String recommendCode) {
		return afUserDao.getUserByRecommendCode(recommendCode);
	}

	@Override
	public List<AfUserInvitationDto> getRecommendUserByRecommendId(Long recommendId, Integer start, Integer end) {
		return afUserDao.getRecommendUserByRecommendId(recommendId, start, end);
	}

	@Override
	public Long getUserIdByMobile(String mobile) {
		return afUserDao.getUserIdByMobile(mobile);
	}

	@Override
	public List<String> getUserNameByUserId(List<String> users) {
		return afUserDao.getUserNameByUserId(users);
	}

	public void updateUserCoreInfo(final Long userId, final String newMobile, final String password) {
		transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				AfUserDo userDo = afUserDao.getUserById(userId);
				AfUserAccountDo userAccountDo = afUserAccountDao.getUserAndAccountByUserId(userId);

				userDo.setUserName(newMobile);
				userDo.setMobile(newMobile);
				userAccountDo.setUserName(newMobile);

				AfUserDo userDoForMod = new AfUserDo();
				userDoForMod.setRid(userDo.getRid());
				userDoForMod.setUserName(newMobile);
				userDoForMod.setMobile(newMobile);
				if(!StringUtils.isBlank(password)) {
					String salt = UserUtil.getSalt();
					userDoForMod.setSalt(salt);
					userDoForMod.setPassword(UserUtil.getPassword(password, salt));
				}
				afUserDao.updateUser(userDoForMod);

				AfUserAccountDo userAccountDoForMod = new AfUserAccountDo();
				userAccountDoForMod.setUserId(userDo.getRid());
				userAccountDoForMod.setUserName(newMobile);
				afUserAccountDao.updateUserAccount(userAccountDoForMod);

				AfUserAuthDo authDo = afUserAuthDao.getUserAuthInfoByUserId(userId);
				if(authDo == null || RiskStatus.A.getCode().equals(authDo.getRiskStatus())) { // 用户还未风控初始化，跳过
					logger.info("don't init risk,skip sync user");
				}else {
					// 更新用户信息 USER
					RiskRespBo riskResp = riskUtil.registerStrongRisk(userId.toString(), RiskEventType.USER.name(), userDo, null, "", "", (AfUserAccountDto)userAccountDo, "", "", "", "");
					if (!riskResp.isSuccess()) {
						throw new FanbeiException(FanbeiExceptionCode.RISK_MODIFY_ERROR);
					}
				}

				return 1;
			}
		});
	}
	@Override
	public AfUserRegisterTypeDo isQuickRegisterUser(Long id) {
		return afUserRegisterTypeDao.selectByUserId(id);
	}

	@Override
	public int addQuickRegisterUser(AfUserRegisterTypeDo afUserRegisterTypeDo) {
		return afUserRegisterTypeDao.insert(afUserRegisterTypeDo);
	}

	public void checkPayPwd(String reqPayPwd, Long userId) {
		AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
		String finalPwd = UserUtil.getPassword(reqPayPwd, userAccount.getSalt());
		if (!StringUtils.equals(finalPwd, userAccount.getPassword())) {
			throw new FanbeiException("Pay Password is error",FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
		}
	}
	@Override
	public AfUserDto getUserInfoByUserId(Long userId) {
		return afUserDao.getUserInfoByUserId(userId);
	}

	@Override
	public Long convertUserNameToUserId(String userName) {
		Long userId = null;
		if (!StringUtil.isBlank(userName)) {
			AfUserDo user = this.getUserByUserName(userName);
			if (user != null) {
				userId = user.getRid();
			}

		}
		return userId;

	}

}
