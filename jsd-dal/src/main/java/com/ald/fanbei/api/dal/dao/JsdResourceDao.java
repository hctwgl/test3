package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.JsdResourceDo;

/**
 * 极速贷资源配置Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-24 10:37:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdResourceDao extends BaseDao<JsdResourceDo, Long> {

	JsdResourceDo getByTypeAngSecType(@Param("type")String type, @Param("secType")String secType);
	
	List<JsdResourceDo> listByType(@Param("type")String type);
}
