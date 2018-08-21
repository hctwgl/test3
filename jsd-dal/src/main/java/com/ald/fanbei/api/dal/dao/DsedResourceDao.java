package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.DsedResourceDo;

import java.util.List;

/**
 * 资源配置表Dao
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-22 10:49:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedResourceDao extends BaseDao<DsedResourceDo, Long> {

    /**
     * 获取type类型的配置信息
     *
     * @param type
     * @return
     */

    DsedResourceDo getConfigByTypesAndSecType(@Param("type") String type, @Param("secType") String secType);

    /**
     * 获取type类型的配置信息
     *
     * @param type
     * @return
     */
    List<DsedResourceDo> getConfigByTypes(String type);
}
