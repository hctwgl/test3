package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.AfTaskCoinChangeProportionDao;
import com.ald.fanbei.api.dal.domain.AfTaskCoinChangeProportionDo;
import com.ald.fanbei.api.biz.service.AfTaskCoinChangeProportionService;

import java.math.BigDecimal;


/**
 * 金币兑换余额比例ServiceImpl
 * 
 * @author luoxiao
 * @version 1.0.0 初始化
 * @date 2018-05-21 17:54:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTaskCoinChangeProportionService")
public class AfTaskCoinChangeProportionServiceImpl implements AfTaskCoinChangeProportionService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfTaskCoinChangeProportionServiceImpl.class);
   
    @Resource
    private AfTaskCoinChangeProportionDao afTaskCoinChangeProportionDao;

    @Resource
    private BizCacheUtil bizCacheUtil;


	@Override
	public BigDecimal getYesterdayProportion() {
		BigDecimal exchangeProportion = (BigDecimal) bizCacheUtil.getObject(CacheConstants.CACHE_KEY_COIN_EXCHANGE_PROPORTION);
		if(null == exchangeProportion){
			AfTaskCoinChangeProportionDo changeProportionDo = afTaskCoinChangeProportionDao.getYesterdayProportion();
			if(null == changeProportionDo){
				return new BigDecimal(0);
			}
			exchangeProportion = changeProportionDo.getChangeProportion();
			bizCacheUtil.saveObject(CacheConstants.CACHE_KEY_COIN_EXCHANGE_PROPORTION, exchangeProportion, Constants.SECOND_OF_AN_HOUR);
		}

		return exchangeProportion;
	}

}