package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;

import org.apache.ibatis.annotations.Param;
/**
 * 额度拆分多场景认证状体记录Dao
 *
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:58:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAuthStatusDao extends BaseDao<AfUserAuthStatusDo, Long>{
    /**
     * 新增一个用户认证信息场景
     * @param afUserAuthStatusDo
     * @return
     */
    Long addAfUserAuthStatus(AfUserAuthStatusDo afUserAuthStatusDo);

    /**
     * 查询一个用户认证场景
     * @param userId
     * @param scene
     * @return
     */
    AfUserAuthStatusDo selectAfUserAuthStatusByUserIdAndScene(@Param("userId") Long userId, @Param("scene") String scene,@Param("status") String status);


}
