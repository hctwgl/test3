package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.BankCardType;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.UpsBankStatusDto;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @类现描述：
 * @author hexin 2017年2月18日 下午17:25:37
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserBankcardService")
public class AfUserBankcardServiceImpl implements AfUserBankcardService {

    @Resource
    BizCacheUtil bizCacheUtil;
    @Autowired
    AfResourceService afResourceService;

    @Resource
    private AfUserBankcardDao afUserBankcardDao;

    Logger logger = LoggerFactory.getLogger(AfUserBankcardServiceImpl.class);

    @Override
    public AfUserBankcardDo getUserMainBankcardByUserId(Long userId) {
	return afUserBankcardDao.getUserMainBankcardByUserId(userId);
    }

    @Override
    public List<AfBankUserBankDto> getUserBankcardByUserId(Long userId, Integer appVersion,String cardType) {
		List<AfBankUserBankDto> list = afUserBankcardDao.getUserBankcardByUserId(userId, cardType);
		if (CollectionUtil.isNotEmpty(list)) {
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("CASHIER", "AP_NAME");
			for (AfBankUserBankDto item : list) {
				UpsBankStatusDto bankStatus;
				if(BankCardType.CREDIT.getCode().equals(item.getCardType()))
				{
					bankStatus = getUpsBankStatus(item.getBankCode(), BankPayChannel.KUAIJIE.getCode());
				}
				else {
					bankStatus = getUpsBankStatus(item.getBankCode(), item.getBankChannel());
				}
				item.setBankStatus(bankStatus);
				if (bankStatus.getIsMaintain() == 1) {
					item.setMessage(afResourceDo.getValue1());
					item.setIsValid("N");
				}
				if (appVersion < 412) {
					if (BankPayChannel.KUAIJIE.getCode().equals(item.getBankChannel())) {
						item.setMessage(afResourceDo.getValue1());
						item.setIsValid("N");
					}
					if(BankCardType.CREDIT.getCode().equals(item.getCardType()))
					{//老版本信用卡显示维护中（不可用，还未支持快捷支付）
						item.setMessage(afResourceDo.getValue1());
						item.setIsValid("N");
						item.setCreditRate(BigDecimal.valueOf(Double.parseDouble(afResourceDo.getValue4())));
					}
				}
				else
				{
					if(BankCardType.CREDIT.getCode().equals(item.getCardType()))
					{//信用卡为快捷支付
						item.setBankChannel(BankPayChannel.KUAIJIE.getCode());
						item.setCreditRate(BigDecimal.valueOf(Double.parseDouble(afResourceDo.getValue4())));
					}
				}
			}

			// 集合重新排序可用状态在前不可用状态在后
			List<AfBankUserBankDto> listMaintain = new ArrayList<AfBankUserBankDto>();
			Iterator<AfBankUserBankDto> iterator = list.iterator();
			while (iterator.hasNext()) {
				AfBankUserBankDto afBankUserBankDto = iterator.next();
				if ("N".equals(afBankUserBankDto.getIsValid())) {
					// 移除维护状态的银行卡，循环结束后重新添加到集合的尾部
					iterator.remove();
					listMaintain.add(afBankUserBankDto);
				}
			}
			if (listMaintain.size() > 0) {
				// 重新添加维护状态的银行卡到集合的尾部
				for (AfBankUserBankDto item : listMaintain) {
					list.add(item);
				}
			}
		}

		return list;
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

    @Override
    public String hideCardNumber(String bankcard) {
	return bankcard.substring(bankcard.length() - 4);
    }

    @Override
    public int updateMainBankCard(Long userId) {
	return afUserBankcardDao.updateMainBankCard(userId);
    }

    @Override
    public int updateViceBankCard(String cardNumber, Long userId) {
	return afUserBankcardDao.updateViceBankCard(cardNumber, userId);
    }

    @Override
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

//    @Override
//    public UpsBankStatusDto getUpsBankStatus(Long cardId) {
//
//	AfUserBankcardDo afUserBankcardDo = getUserBankcardById(cardId);
//	return getUpsBankStatus(afUserBankcardDo.getBankCode());
//    }

    @Override
    public void checkUpsBankLimit(String bankCode, String bankChannel,BigDecimal amount) {
	UpsBankStatusDto upsBankStatusDto = getUpsBankStatus(bankCode, bankChannel);

	if (upsBankStatusDto.getLimitUp().compareTo(amount.doubleValue()) < 0) {
	    throw new FanbeiException(String.format("该银行单笔限额%.2f元，请分批还款或使用其他银行卡还款，谢谢！", upsBankStatusDto.getLimitUp()));
	}
    }
}
