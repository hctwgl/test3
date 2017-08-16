package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfPromotionLogsService;
import com.ald.fanbei.api.dal.dao.AfPromotionLogsDao;
import com.ald.fanbei.api.dal.domain.AfPromotionLogsDo;

@Service("afPromotionLogsService")
public class AfPromotionLogsServiceImpl implements AfPromotionLogsService {

	@Resource
	AfPromotionLogsDao afPromotionLogsDao;
	
	@Override
	public void addAfPromotionLogs(AfPromotionLogsDo afPromotionLogsDo) {
		afPromotionLogsDao.addPromotionLogs(afPromotionLogsDo);
	}

}
