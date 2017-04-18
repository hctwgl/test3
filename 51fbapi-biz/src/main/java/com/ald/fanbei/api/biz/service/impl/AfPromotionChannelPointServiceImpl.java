package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfPromotionChannelPointService;
import com.ald.fanbei.api.dal.dao.AfPromotionChannelPointDao;
import com.ald.fanbei.api.dal.domain.AfPromotionChannelPointDo;

@Service("afPromotionChannelPointService")
public class AfPromotionChannelPointServiceImpl implements AfPromotionChannelPointService {

	@Resource
	AfPromotionChannelPointDao afPromotionChannelPointDao; 
	
	@Override
	public AfPromotionChannelPointDo getPoint(String channelCode, String pointCode) {
		return afPromotionChannelPointDao.getPointByChannelCodeAndPointCode(channelCode,pointCode);
	}

	@Override
	public void addVisit(Long id) {
		
	}

}
