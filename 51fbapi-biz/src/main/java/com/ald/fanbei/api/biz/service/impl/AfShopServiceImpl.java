package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.dal.dao.AfShopDao;
import com.ald.fanbei.api.dal.domain.AfShopDo;

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

}
