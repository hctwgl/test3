package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.DsedLoanRateDo;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedLoanProductDao;
import com.ald.fanbei.api.dal.domain.DsedLoanProductDo;
import com.ald.fanbei.api.biz.service.DsedLoanProductService;

import java.util.List;


/**
 * 都市e贷借款产品表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:44:46
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedLoanProductService")
public class DsedLoanProductServiceImpl extends ParentServiceImpl<DsedLoanProductDo, Long> implements DsedLoanProductService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedLoanProductServiceImpl.class);

    @Resource
    private DsedLoanProductDao dsedLoanProductDao;

		@Override
	public BaseDao<DsedLoanProductDo, Long> getDao() {
		return dsedLoanProductDao;
	}

	@Override
	public DsedLoanRateDo getByPrdTypeAndNper(String prdType, String nper) {
		DsedLoanProductDo productDo = dsedLoanProductDao.getByPrdType(prdType);
		String conf = productDo.getConf();
		List<DsedLoanRateDo> list = JSONArray.parseArray(conf, DsedLoanRateDo.class);
		for (DsedLoanRateDo rate:list) {
			if (nper.equals(rate.getBorrowTag())){
				return rate;
			}
		}
		return null;
	}
}