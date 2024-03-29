package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;

/**
 * 极速贷资源配置Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-24 10:37:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdResourceService extends ParentService<JsdResourceDo, Long>{

	JsdResourceDo getByTypeAngSecType(String type, String secType);
	
	List<JsdResourceDo> listByType(String type);
	
	ResourceRateInfoBo getRateInfo(String borrowType);

	ResourceRateInfoBo getOrderRateInfo(String borrowType);

	int updateById(JsdResourceDo jsdResourceDo);
}
