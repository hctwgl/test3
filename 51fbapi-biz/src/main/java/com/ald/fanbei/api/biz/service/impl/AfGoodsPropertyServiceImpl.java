package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfGoodsPropertyDao;
import com.ald.fanbei.api.dal.domain.AfGoodsPropertyDo;
import com.ald.fanbei.api.biz.service.AfGoodsPropertyService;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-07-13 20:40:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afGoodsPropertyService")
public class AfGoodsPropertyServiceImpl extends ParentServiceImpl<AfGoodsPropertyDo, Long> implements AfGoodsPropertyService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfGoodsPropertyServiceImpl.class);
   
    @Resource
    private AfGoodsPropertyDao afGoodsPropertyDao;

		@Override
	public BaseDao<AfGoodsPropertyDo, Long> getDao() {
		return afGoodsPropertyDao;
	}
}