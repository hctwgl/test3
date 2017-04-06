package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;

/**
 * @类现描述：
 * 
 * @author hexin 2017年2月18日 下午17:27:22
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserBankcardDao {

	/**
	 * 获取用户主卡信息
	 * 
	 * @param userId
	 * @return
	 */
	AfUserBankcardDo getUserMainBankcardByUserId(@Param("userId") Long userId);

	/**
	 * 获取银行卡列表
	 * 
	 * @param userId
	 * @return
	 */
	List<AfBankUserBankDto> getUserBankcardByUserId(@Param("userId") Long userId);

	/**
	 * 删除银行卡
	 * @param userId
	 * @param rid
	 * @return
	 */
	int deleteUserBankcardByIdAndUserId(@Param("userId") Long userId, @Param("rid") Long rid);
	
	/**
	 * 获取银行卡信息
	 * @param id
	 * @return
	 */
	public AfUserBankcardDo getUserBankcardById(@Param("rid")Long id);
	
	/**
	 * 修改银行卡
	 * @param afUserBankcardDo
	 * @return
	 */
	int updateUserBankcard(AfUserBankcardDo afUserBankcardDo);
	
	int getUserBankcardCountByUserId(@Param("userId") Long userId);
	
	/**
	 * 新增银行卡
	 * @param afUserBankcardDo
	 * @return
	 */
	int addUserBankcard(AfUserBankcardDo afUserBankcardDo);
	
	/**
	 * 获取银行身份信息
	 */
	AfUserBankDto getUserBankInfo(@Param("bankId")Long bankId);
	
	/**
	 * 银行查询信息
	 * @param bankId
	 * @return
	 */
	AfBankUserBankDto getUserBankcardByBankId(@Param("bankId")Long bankId);
	
	/**
	 * 查询银行卡是否已被人绑定
	 * @param cardNo
	 * @return
	 */
	int getUserBankByCardNo(@Param("cardNumber")String cardNumber);
}
