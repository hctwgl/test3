package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfContractPdfEdspaySealDao;
import com.ald.fanbei.api.dal.domain.AfContractPdfEdspaySealDo;
import com.ald.fanbei.api.biz.service.AfContractPdfEdspaySealService;



/**
 * e都市钱包用户出借信息与协议关联表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-01-22 17:34:26
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afContractPdfEdspaySealService")
public class AfContractPdfEdspaySealServiceImpl extends ParentServiceImpl<AfContractPdfEdspaySealDo, Long> implements AfContractPdfEdspaySealService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfContractPdfEdspaySealServiceImpl.class);
   
    @Resource
    private AfContractPdfEdspaySealDao afContractPdfEdspaySealDao;

		@Override
	public BaseDao<AfContractPdfEdspaySealDo, Long> getDao() {
		return afContractPdfEdspaySealDao;
	}
}