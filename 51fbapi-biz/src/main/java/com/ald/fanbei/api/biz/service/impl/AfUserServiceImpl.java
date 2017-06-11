package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAuthDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
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
	BizCacheUtil bizCacheUtil;
	@Override
	public int addUser(final AfUserDo afUserDo) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					afUserDao.addUser(afUserDo);
					AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
					afUserAuthDo.setUserId(afUserDo.getRid());
					afUserAuthDao.addUserAuth(afUserAuthDo);

					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(afUserDo.getRid());
					account.setUserName(afUserDo.getUserName());
					afUserAccountDao.addUserAccount(account);
			        couponSceneRuleEnginerUtil.regist(afUserDo.getRid(),afUserDo.getRecommendId());

					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("addUser error:", e);
					return 0;
				}
			}
		});

	}

	@Override
	public AfUserDo getUserById(Long userId) {
		return afUserDao.getUserById(userId);
	}

	@Override
	public int updateUser(AfUserDo afUserDo) {
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
	public AfUserDo getUserByRecommendCode(String recommendCode) {
		return afUserDao.getUserByRecommendCode(recommendCode);
	}

	@Override
	public List<AfUserInvitationDto> getRecommendUserByRecommendId(Long recommendId, Integer start, Integer end) {
		return afUserDao.getRecommendUserByRecommendId(recommendId, start, end);
	}

}
