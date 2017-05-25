package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.dao.AfShopDao;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.query.AfShopQuery;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月23日下午2:16:08
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afShopService")
public class AfShopServiceImpl implements AfShopService {
	
	@Resource
	AfShopDao afShopDao;
	
	@Override
	public AfShopDo getShopById(Long shopId) {
		return afShopDao.getShopById(shopId);
	}

	@Override
	public AfShopDo getShopByPlantNameAndTypeAndServiceProvider(String platformName, String type, String serviceProvider) {
		return afShopDao.getShopByPlantNameAndTypeAndServiceProvider(platformName, type, serviceProvider);
	}

	@Override
	public List<AfShopDo> getShopList(AfShopQuery query) {
		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
			return afShopDao.getShopList(query);
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ){
			return afShopDao.getPreEnvShopList(query);
		}
		return null;
	}

}
