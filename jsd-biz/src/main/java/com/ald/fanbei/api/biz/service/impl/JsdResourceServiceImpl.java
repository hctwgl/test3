package com.ald.fanbei.api.biz.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdResourceDao;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;



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
	
	public Map<String, Object> getRateInfo(String borrowRate, String borrowType, String tag) {
        JsdResourceDo rateInfoDo = jsdResourceDao.getByTypeAngSecType(Constants.JSD_CONFIG, Constants.JSD_RATE_INFO);
        String oneDay = "";
        String twoDay = "";
        if (null != rateInfoDo) {
            oneDay = rateInfoDo.getTypeDesc().split(",")[0];
            twoDay = rateInfoDo.getTypeDesc().split(",")[1];
        }
        Map<String, Object> rateInfo = Maps.newHashMap();
        double serviceRate = 0;
        double interestRate = 0;
        JSONArray array = JSONObject.parseArray(borrowRate);
        double totalRate = 0;
        for (int i = 0; i < array.size(); i++) {
            JSONObject info = array.getJSONObject(i);
            String borrowTag = info.getString(tag + "Tag");
            if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, borrowType)) {
                    interestRate = info.getDouble(tag + "FirstType");
                    totalRate += interestRate;
                } else if (StringUtils.equals(twoDay, borrowType)) {
                    interestRate = info.getDouble(tag + "SecondType");
                    totalRate += interestRate;
                }
            }
            if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, borrowType)) {
                    serviceRate = info.getDouble(tag + "FirstType");
                    totalRate += serviceRate;
                } else if (StringUtils.equals(twoDay, borrowType)) {
                    serviceRate = info.getDouble(tag + "SecondType");
                    totalRate += serviceRate;
                }
            }

        }
        rateInfo.put("serviceRate", serviceRate);
        rateInfo.put("interestRate", interestRate);
        rateInfo.put("totalRate", totalRate);
        return rateInfo;
    }
	
}