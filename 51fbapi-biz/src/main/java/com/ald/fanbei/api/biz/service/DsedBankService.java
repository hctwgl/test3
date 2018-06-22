package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.DsedBankDo;

/**
 * 信用卡绑定及订单支付Service
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:36:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedBankService extends ParentService<DsedBankDo, Long>{

    DsedBankDo getBankByName(String name);

}
