package com.ald.fanbei.api.dal.dao;

import java.util.HashMap;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import org.apache.ibatis.annotations.Param;

/**
 * 极速贷用户银行卡信息Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-23 09:40:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdUserBankcardDao extends BaseDao<JsdUserBankcardDo, Long> {

	HashMap<String, Object> getUserBankInfoByBankNo(String bankNo);

	JsdUserBankcardDo getByBankNo(String bankNo);

	HashMap<String, Object> getBankByBankNoAndUserId(@Param("userId") Long userId, @Param("bankNo")String bankNo);

}