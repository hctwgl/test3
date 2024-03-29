package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdCollectionRepaymentDo;

/**
 * Service
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdCollectionRepaymentService extends ParentService<JsdCollectionRepaymentDo, Long>{

    JsdCollectionRepaymentDo getByRepayNo(String repayNo);

}
