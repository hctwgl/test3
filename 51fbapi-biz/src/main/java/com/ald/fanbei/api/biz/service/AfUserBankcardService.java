package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;

/**
 * @类现描述：
 * @author hexin 2017年2月18日 下午17:23:47
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserBankcardService {

	/**
	 * 获取用户主卡信息
	 * @param userId
	 * @return
	 */
	AfUserBankcardDo getUserMainBankcardByUserId(Long userId);
	
	
	/**
	 * 获取银行卡列表
	 * @param userId
	 * @return
	 */
	List<AfBankUserBankDto> getUserBankcardByUserId(Long userId);
	
	/**
	 * 删除银行卡
	 * @param userId
	 * @param rid
	 * @return
	 */
	int deleteUserBankcardByIdAndUserId(Long userId, Long rid);
	
	/**
	 * 获取用户银行卡信息
	 * @param userId
	 * @return
	 */
	AfUserBankcardDo getUserBankcardById(Long id);
	/**
	 * 修改银行卡信息
	 * @param afUserBankcardDo
	 * @return
	 */
	int updateUserBankcard(AfUserBankcardDo afUserBankcardDo);
	/**
	 * 获取银行卡个数
	 * @param userId
	 * @return
	 */
	int getUserBankcardCountByUserId(Long userId);

	/**
	 * 获取银行身份信息
	 */
	AfUserBankDto getUserBankInfo(Long bankId);
}
