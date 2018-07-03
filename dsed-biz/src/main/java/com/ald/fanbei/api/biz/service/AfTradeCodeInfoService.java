package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTradeCodeInfoDo;

/**
 * 交易响应码配置信息Service
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-12-19 17:26:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTradeCodeInfoService extends ParentService<AfTradeCodeInfoDo, Long>{
	/**
	 * 根据交易响应码获取响应配置记录
	 * @param tradeCode
	 * @return
	 */
	String getRecordDescByTradeCode(String tradeCode);
}