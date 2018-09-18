package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.JsdBankDo;

/**
 * 极速贷银行卡信息Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-23 09:40:17
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBankDao extends BaseDao<JsdBankDo, Long> {

    String getBankNameByCardNumAndUserId(@Param("bankNo") String bankNo, @Param("userId")Long userId);

    JsdBankDo getBankByCode(String bankCode);

}
