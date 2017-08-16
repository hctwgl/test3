package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfPromotionChannelService;
import com.ald.fanbei.api.dal.dao.AfPromotionChannelDao;
import com.ald.fanbei.api.dal.domain.AfPromotionChannelDo;

@Service("afPromotionChannelService")
public class AfPromotionChannelServiceImpl implements AfPromotionChannelService {

	@Resource
	AfPromotionChannelDao afPromotionChannelDao;
	
	@Override
	public AfPromotionChannelDo getById(Long id) {
		return afPromotionChannelDao.getById(id);
	}

}
