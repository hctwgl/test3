package com.ald.fanbei.api.dal.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;

/**
 * 极速贷用户银行卡信息Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-23 09:40:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdUserBankcardDao extends BaseDao<JsdUserBankcardDo, Long> {

	HashMap<String, Object> getUserBankInfoByBankNo(@Param("bankNo")String bankNo,@Param("userId")Long userId);

	JsdUserBankcardDo getByBankNo(@Param("bankNo")String bankNo,@Param("userId")Long userId);

	HashMap<String, Object> getPayTypeByBankNoAndUserId(@Param("userId") Long userId, @Param("bankNo")String bankNo);

	List<JsdUserBankcardDo> getUserBankCardInfoByUserId(@Param("userId")Long userId);

	int addUserBankcard(JsdUserBankcardDo userBankcardDo);

	int updateUserBankcard(JsdUserBankcardDo userBankcardDo);

	int getUserBankByCardNo(@Param("cardNumber")String cardNumber,@Param("userId")Long userId);

	JsdUserBankcardDo getByBindNo(String bindNo);

	JsdUserBankcardDo getMainBankByUserId(@Param("userId")Long userId);

	List<JsdUserBankcardDo> getUserNoMainBankCardInfoByUserId(@Param("userId")Long userId);
}
