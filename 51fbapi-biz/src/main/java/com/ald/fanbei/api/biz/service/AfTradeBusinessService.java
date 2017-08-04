package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTradeBusinessDo;

/**
 * 商圈商户表Service
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:40:39 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTradeBusinessService extends ParentService<AfTradeBusinessDo, Long> {

	/**
	 * 根据商户用户用查询
	 * 
	 * @param username
	 * @return
	 */
	AfTradeBusinessDo getByName(String username);

}
