package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBrandDao;
import com.ald.fanbei.api.dal.domain.AfBrandDo;
import com.ald.fanbei.api.dal.domain.dto.AfBrandDto;
import com.ald.fanbei.api.biz.service.AfBrandService;



/**
 * 品牌ServiceImpl
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-04-10 19:29:42
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBrandService")
public class AfBrandServiceImpl extends ParentServiceImpl<AfBrandDo, Long> implements AfBrandService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBrandServiceImpl.class);
   
    @Resource
    private AfBrandDao afBrandDao;

		@Override
	public BaseDao<AfBrandDo, Long> getDao() {
		return afBrandDao;
	}

		@Override
		public List<AfBrandDto> getAllAndNameSort() {
			return afBrandDao.getAllAndNameSort();
		}

		@Override
		public List<AfBrandDo> getHotBrands(String[] brandIds) {
			return afBrandDao.getHotBrands(brandIds);
		}
}