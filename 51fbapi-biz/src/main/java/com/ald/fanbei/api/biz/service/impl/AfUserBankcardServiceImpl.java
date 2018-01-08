package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;

/**
 *@类现描述：
 *@author hexin 2017年2月18日 下午17:25:37
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserBankcardService")
public class AfUserBankcardServiceImpl implements AfUserBankcardService{

	@Resource
	private AfUserBankcardDao afUserBankcardDao;
	
	@Override
	public AfUserBankcardDo getUserMainBankcardByUserId(Long userId) {
		return afUserBankcardDao.getUserMainBankcardByUserId(userId);
	}

	@Override
	public List<AfBankUserBankDto> getUserBankcardByUserId(Long userId) {
		return afUserBankcardDao.getUserBankcardByUserId(userId);
	}

	@Override
	public int deleteUserBankcardByIdAndUserId(Long userId, Long rid) {
		return afUserBankcardDao.deleteUserBankcardByIdAndUserId(userId, rid);
	}

	@Override
	public AfUserBankcardDo getUserBankcardById(Long id) {
		return afUserBankcardDao.getUserBankcardById(id);
	}

	
	@Override
	public int updateUserBankcard(AfUserBankcardDo afUserBankcardDo) {
		return afUserBankcardDao.updateUserBankcard(afUserBankcardDo);
	}

	
	@Override
	public int getUserBankcardCountByUserId(Long userId) {
		return afUserBankcardDao.getUserBankcardCountByUserId(userId);
	}

	@Override
	public AfUserBankDto getUserBankInfo(Long bankId) {
		return afUserBankcardDao.getUserBankInfo(bankId);
	}

	@Override
	public List<AfUserBankcardDo> getAfUserBankcardDoList(long userId) {
		return afUserBankcardDao.getAfUserBankcardDoList(userId);
	}

	@Override
	public AfUserBankcardDo getUserBankcardIdByCardNumber(String cardNumber) {
		return afUserBankcardDao.getUserBankcardIdByCardNumber(cardNumber);
	}

	@Override
	public String getAfUserBankcardList(long userId) {
		return afUserBankcardDao.getAfUserBankcardList(userId);
	}

	@Override
	public AfUserBankcardDo getUserBankcardByIdAndStatus(Long cardId) {
		return afUserBankcardDao.getUserBankcardByIdAndStatus(cardId);
	}

}
