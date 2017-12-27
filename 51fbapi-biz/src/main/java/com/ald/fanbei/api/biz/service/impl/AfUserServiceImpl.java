package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.bo.risk.RiskAuthFactory.RiskEventType;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserInvitationDto;

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
	BizCacheUtil bizCacheUtil;

	@Resource
	AfUserOutDayDao afUserOutDayDao;
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

			        long recommendId = afUserDo.getRecommendId();
					//#region add by hongzhengpei
			        if(recommendId !=0){
			        	//新增推荐记录表
						AfRecommendUserDo afRecommendUserDo = new AfRecommendUserDo();
						afRecommendUserDo.setUser_id(afUserDo.getRid());
						afRecommendUserDo.setParentId(recommendId);
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


					long recommendId = afUserDo.getRecommendId();

					//#region add by hongzhengpei
					if(recommendId !=0){
						//新增推荐记录表
						AfRecommendUserDo afRecommendUserDo = new AfRecommendUserDo();
						afRecommendUserDo.setUser_id(afUserDo.getRid());
						afRecommendUserDo.setParentId(recommendId);
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
					RiskRespBo riskResp = riskUtil.registerStrongRisk(userId.toString(), RiskEventType.USER.name(), userDo, null, "", "", (AfUserAccountDto)userAccountDo, "", "", "");
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


}
