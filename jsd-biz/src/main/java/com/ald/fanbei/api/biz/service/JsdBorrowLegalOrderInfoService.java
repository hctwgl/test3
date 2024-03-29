package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderInfoDo;


/**
 * Service
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-19 14:09:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowLegalOrderInfoService extends ParentService<JsdBorrowLegalOrderInfoDo, Long>{

    JsdBorrowLegalOrderInfoDo getByBorrowId(Long borrowId);
}
