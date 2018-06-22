package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.UpsBankStatusDto;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedUserBankcardDao;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.ald.fanbei.api.biz.service.DsedUserBankcardService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;


/**
 * 都市E贷用户绑定的银行卡ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:51:50
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedUserBankcardService")
public class DsedUserBankcardServiceImpl extends ParentServiceImpl<DsedUserBankcardDo, Long> implements DsedUserBankcardService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedUserBankcardServiceImpl.class);
   
    @Resource
    private DsedUserBankcardDao dsedUserBankcardDao;
    @Resource
	BizCacheUtil bizCacheUtil;

		@Override
	public BaseDao<DsedUserBankcardDo, Long> getDao() {
		return dsedUserBankcardDao;
	}


	/**
	 * 获取用户所选银行卡对应的支付方式(代扣WITHHOLD、快捷支付KUAIJIE(有短信))
	 * @param userId
	 * @param cardNumber
	 * author: chefeipeng
	 * @return
	 */
	@Override
	public HashMap<String,Object> getPayTypeByBankNoAndUserId(Long userId, String cardNumber){
		return dsedUserBankcardDao.getPayTypeByBankNoAndUserId(userId,cardNumber);
	}

	@Override
	public DsedUserBankcardDo getUserMainBankcardByUserId(Long userId) {
		return dsedUserBankcardDao.getUserMainBankcardByUserId(userId);
	}

	@Override
	public String hideCardNumber(String bankcard) {
		return bankcard.substring(bankcard.length() - 4);
	}

	@Override
	public void checkUpsBankLimit(String bankCode, String bankChannel,BigDecimal amount) {
		UpsBankStatusDto upsBankStatusDto = getUpsBankStatus(bankCode, bankChannel);
		if (upsBankStatusDto.getLimitUp().compareTo(amount.doubleValue()) < 0) {
			throw new FanbeiException(String.format("该银行单笔限额%.2f元，请分批还款或使用其他银行卡还款，谢谢！", upsBankStatusDto.getLimitUp()));
		}
	}

	@Override
	public int getUserBankByCardNo(String cardNumber) {
		return dsedUserBankcardDao.getUserBankByCardNo(cardNumber);
	}

	@Override
	public List<DsedUserBankcardDo> getUserBankCardInfoByUserId(Long userId) {
		return dsedUserBankcardDao.getUserBankCardInfoByUserId(userId);
	}

	@Override
	public int addUserBankcard(DsedUserBankcardDo userBankcardDo) {
		return dsedUserBankcardDao.addUserBankcard(userBankcardDo);
	}



	@Override
	public int updateUserBankcard(DsedUserBankcardDo userBankcardDo) {
		return dsedUserBankcardDao.updateUserBankcard(userBankcardDo);
	}


	public UpsBankStatusDto getUpsBankStatus(String bankCode, String bankChannel) {
		String bankStatusKey = "";
		if(BankPayChannel.KUAIJIE.getCode().equals(bankChannel))
		{
			bankStatusKey = "ups_quickPay_" + bankCode;
		}else {
			bankStatusKey ="ups_collect_" + bankCode;
		}
		Object bankStatusValue = bizCacheUtil.getStringObject(bankStatusKey);
		logger.info("getUserBankcardByUserId key:" + bankStatusKey + ",value：" + bankStatusValue);
		if (bankStatusValue != null && StringUtils.isNotBlank(bankStatusValue.toString())) {
			return JSON.parseObject(bankStatusValue.toString(), UpsBankStatusDto.class);
		} else {
			return new UpsBankStatusDto();
		}
	}
}