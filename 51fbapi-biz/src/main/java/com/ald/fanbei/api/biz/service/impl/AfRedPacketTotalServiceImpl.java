package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRedPacketTotalService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.enums.AccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfRedPacketTotalDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * 拆红包活动，用户总红包ServiceImpl
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afRedPacketTotalService")
public class AfRedPacketTotalServiceImpl extends ParentServiceImpl<AfRedPacketTotalDo, Long>
		implements AfRedPacketTotalService {
	
    @Autowired
    private AfRedPacketTotalDao afRedPacketTotalDao;

    @Autowired
    private AfUserAccountService afUserAccountService;

	@Override
	public int getWithdrawTotalNum() {
		return afRedPacketTotalDao.getWithdrawTotalNum();
	}

	@Override
	public AfRedPacketTotalDo getTheOpening(Long userId, Integer overdueIntervalHour) {
		return afRedPacketTotalDao.getTheOpening(userId, overdueIntervalHour);
	}

	@Override
	public List<AfRedPacketTotalDo> findWithdrawList(Long userId, Integer queryNum) {
		return afRedPacketTotalDao.findWithdrawList(userId, queryNum);
	}

	@Override
	@Transactional
	public void withdraw(Long id, String modifier) {
		AfRedPacketTotalDo redPacketTotalDo = getById(id);
		if (redPacketTotalDo.getIsWithdraw() == 1) return;

		redPacketTotalDo.setIsWithdraw(1);
		redPacketTotalDo.setModifier(modifier);
		redPacketTotalDo.setGmtWithdraw(new Date());
		updateById(redPacketTotalDo);

		// 红包金额提现到返现金额
		AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(redPacketTotalDo.getUserId());
		if (userAccountDo == null) {
			throw new FanbeiException("account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		// 2.1更新用户账户额度
		AfUserAccountDo newUserAccountDo = new AfUserAccountDo();
		newUserAccountDo.setRebateAmount(redPacketTotalDo.getAmount());
		newUserAccountDo.setUserId(userAccountDo.getUserId());
		afUserAccountService.updateUserAccount(newUserAccountDo);
		// 记录数据到账户记录表
		AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
		afUserAccountLogDo.setGmtCreate(new Date());
		afUserAccountLogDo.setUserId(redPacketTotalDo.getUserId());
		afUserAccountLogDo.setAmount(redPacketTotalDo.getAmount());
		afUserAccountLogDo.setType(AccountLogType.OPEN_REDPACKET.getCode());
		afUserAccountLogDo.setRefId("");
		afUserAccountService.addUserAccountLog(afUserAccountLogDo);
	}

	@Override
	public BaseDao<AfRedPacketTotalDo, Long> getDao() {
		return afRedPacketTotalDao;
	}

}