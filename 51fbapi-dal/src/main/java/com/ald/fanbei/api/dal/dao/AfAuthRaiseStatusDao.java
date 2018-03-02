package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfAuthRaiseStatusDo;

/**
 * 贷款业务Dao
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-02-06 17:58:14
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAuthRaiseStatusDao extends BaseDao<AfAuthRaiseStatusDo, Long> {

	AfAuthRaiseStatusDo getByPrdTypeAndAuthType(@Param("prdType")String prdType, @Param("authType")String authType,@Param("userId") Long userId);

    

}
