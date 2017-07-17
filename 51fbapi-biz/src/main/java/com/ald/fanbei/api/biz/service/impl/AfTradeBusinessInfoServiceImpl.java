package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.dto.AfTradeBusinessInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfTradeBusinessInfoDao;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessInfoDo;
import com.ald.fanbei.api.biz.service.AfTradeBusinessInfoService;

import java.util.List;


/**
 * 商圈商户信息表ServiceImpl
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTradeBusinessInfoService")
public class AfTradeBusinessInfoServiceImpl extends ParentServiceImpl<AfTradeBusinessInfoDo, Long> implements AfTradeBusinessInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfTradeBusinessInfoServiceImpl.class);
   
    @Resource
    private AfTradeBusinessInfoDao afTradeBusinessInfoDao;

	@Override
	public BaseDao<AfTradeBusinessInfoDo, Long> getDao() {
		return afTradeBusinessInfoDao;
	}

	@Override
	public List<AfTradeBusinessInfoDto> getByOrderId(Long orderId) {
		List<AfTradeBusinessInfoDto> reslut = afTradeBusinessInfoDao.getByOrderId(orderId);
		for(AfTradeBusinessInfoDto dto : reslut) {
			dto.setImageUrl(dto.getImageUrl().split(",")[0]);
		}
		return reslut;
	}
}