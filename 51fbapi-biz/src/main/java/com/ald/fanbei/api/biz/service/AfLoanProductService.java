package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfLoanProductDo;
import com.ald.fanbei.api.dal.domain.AfLoanRateDo;

/**
 * 贷款业务Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanProductService extends ParentService<AfLoanProductDo, Long>{

	String getNameByPrdType(String prdType);

	AfLoanRateDo getByPrdTypeAndNper(String prdType, String nper);
	
	List<AfLoanRateDo> listByPrdType(String prdType);

}
