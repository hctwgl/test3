package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfFacescoreImgDao;
import com.ald.fanbei.api.dal.domain.AfFacescoreImgDo;
import com.ald.fanbei.api.biz.service.AfFacescoreImgService;



/**
 * 颜值测试图片表ServiceImpl
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-22 19:20:19
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afFacescoreImgService")
public class AfFacescoreImgServiceImpl extends ParentServiceImpl<AfFacescoreImgDo, Long> implements AfFacescoreImgService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfFacescoreImgServiceImpl.class);
   
    @Resource
    private AfFacescoreImgDao afFacescoreImgDao;

		@Override
	public BaseDao<AfFacescoreImgDo, Long> getDao() {
		return afFacescoreImgDao;
	}
}