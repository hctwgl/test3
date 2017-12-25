package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfTradeCodeInfoDao;
import com.ald.fanbei.api.dal.domain.AfTradeCodeInfoDo;
import com.ald.fanbei.api.biz.service.AfTradeCodeInfoService;



/**
 * 交易响应码配置信息ServiceImpl
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-12-19 17:26:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTradeCodeInfoService")
public class AfTradeCodeInfoServiceImpl extends ParentServiceImpl<AfTradeCodeInfoDo, Long> implements AfTradeCodeInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfTradeCodeInfoServiceImpl.class);
   
    @Resource
    private AfTradeCodeInfoDao afTradeCodeInfoDao;

	@Override
	public BaseDao<AfTradeCodeInfoDo, Long> getDao() {
		return afTradeCodeInfoDao;
	}
		
	/**
	 * 根据交易响应码获取响应配置记录
	 * @param tradeCode
	 * @return
	 */
	@Override
	public String getRecordDescByTradeCode(String tradeCode){
		AfTradeCodeInfoDo afTradeCodeInfoDo = afTradeCodeInfoDao.getRecordByTradeCode(tradeCode);
		if(afTradeCodeInfoDo!=null){
			//配置非空则返回配置描述 
			return afTradeCodeInfoDo.getRespDesc();
		}
		
		afTradeCodeInfoDo = afTradeCodeInfoDao.getRecordByTradeCode(Constants.TRADE_CODE_INFO_DEFAULT_KEY);
		if(afTradeCodeInfoDo!=null){
			//返回默认配置描述 
			return afTradeCodeInfoDo.getRespDesc();
		}
		
		//配置为空，则返回默认描述，交易失败
		return "交易失败";
	}
}