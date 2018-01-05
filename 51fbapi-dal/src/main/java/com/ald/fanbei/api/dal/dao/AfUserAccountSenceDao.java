package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;

/**
 * 额度拆分多场景分期额度记录Dao
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:57:51 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAccountSenceDao extends BaseDao<AfUserAccountSenceDo, Long> {

    int updateUserSceneAuAmount(@Param("scene") String scene, @Param("userId") Long userId, @Param("auAmount") BigDecimal auAmount);
}
