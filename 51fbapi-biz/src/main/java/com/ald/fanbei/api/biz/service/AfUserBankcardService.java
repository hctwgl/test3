package com.ald.fanbei.api.biz.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;

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
	List<AfUserBankcardDo> getUserBankcardByUserId(Long userId);
	
	/**
	 * 删除银行卡
	 * @param userId
	 * @param rid
	 * @return
	 */
	int deleteUserBankcardByIdAndUserId(Long userId, Long rid);
}
