package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdResourceDao;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.alibaba.fastjson.JSONObject;



/**
 * 极速贷资源配置ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-24 10:37:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdResourceService")
public class JsdResourceServiceImpl extends ParentServiceImpl<JsdResourceDo, Long> implements JsdResourceService {
    @Resource
    private JsdResourceDao jsdResourceDao;

		@Override
	public BaseDao<JsdResourceDo, Long> getDao() {
		return jsdResourceDao;
	}

	@Override
	public JsdResourceDo getByTypeAngSecType(String type, String secType) {
		return jsdResourceDao.getByTypeAngSecType(type, secType);
	}
	
	@Override
	public ResourceRateInfoBo getRateInfo(String borrowType) {
        JsdResourceDo rateInfoDo = jsdResourceDao.getByTypeAngSecType(Constants.JSD_CONFIG, Constants.JSD_RATE_INFO);
        JSONObject rateInfoHolder = JSONObject.parseObject(rateInfoDo.getValue());
        return rateInfoHolder.getJSONObject(borrowType).toJavaObject(ResourceRateInfoBo.class);
    }
	
	@Override
	public ResourceRateInfoBo getOrderRateInfo(String borrowType) {
		JsdResourceDo rateInfoDo = jsdResourceDao.getByTypeAngSecType(Constants.JSD_CONFIG, Constants.JSD_RATE_INFO);
		JSONObject rateInfoHolder = JSONObject.parseObject(rateInfoDo.getValue3());
		return rateInfoHolder.getJSONObject(borrowType).toJavaObject(ResourceRateInfoBo.class);
	}
	
	public static class ResourceRateInfoBo {
		public BigDecimal interestRate;
		public BigDecimal serviceRate;
		public BigDecimal overdueRate;
	}

	@Override
	public List<JsdResourceDo> listByType(String type) {
		return jsdResourceDao.listByType(type);
	}
	
}