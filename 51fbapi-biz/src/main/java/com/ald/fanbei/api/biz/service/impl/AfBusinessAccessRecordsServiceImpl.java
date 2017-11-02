package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBusinessAccessRecordsRefType;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBusinessAccessRecordsDao;
import com.ald.fanbei.api.dal.domain.AfBusinessAccessRecordsDo;
import com.ald.fanbei.api.dal.domain.AfGameDo;
import com.ald.fanbei.api.dal.domain.query.AfBusinessAccessRecordQuery;
import com.ald.fanbei.api.biz.service.AfBusinessAccessRecordsService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;



/**
 * 业务访问记录ServiceImpl
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-07-19 16:26:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBusinessAccessRecordsService")
public class AfBusinessAccessRecordsServiceImpl extends ParentServiceImpl<AfBusinessAccessRecordsDo, Long> implements AfBusinessAccessRecordsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBusinessAccessRecordsServiceImpl.class);
   
    @Resource
    private AfBusinessAccessRecordsDao afBusinessAccessRecordsDao;
    
    @Resource
	BizCacheUtil bizCacheUtil;

	@Override
	public BaseDao<AfBusinessAccessRecordsDo, Long> getDao() {
		return afBusinessAccessRecordsDao;
	}

	@Override
	public boolean checkIsSignToday(Long userId) {
		
		int count = afBusinessAccessRecordsDao.getSignCountToday(userId);
		return count==4?true:false;
	}

	@Override
	public void doSign(AfGameDo gameDo, Long userId) {
		
		String LOCK_KEY = Constants.CACHEKEY_LOAN_SUPERMARKET_SIGN_LOCK+userId;
		//是否需要设置签到 
		AfBusinessAccessRecordQuery query = new AfBusinessAccessRecordQuery();
		
		query.setBeginTime(gameDo.getGmtStart());
		query.setEndTime(gameDo.getGmtEnd());
		query.setUserId(userId);
		query.setRefType(AfBusinessAccessRecordsRefType.LOANSUPERMARKET.getCode());
		List<String> signedMarketIds = afBusinessAccessRecordsDao.getSignedSuperMarketList(query);
		
		int count = afBusinessAccessRecordsDao.getVisitCountToday(signedMarketIds, userId);
		if(count>=4){
			boolean locked = bizCacheUtil.getLockAFewMinutes(LOCK_KEY, "1",2);
			try{
				if(locked){
					//再判断今天是否签过到
					boolean signed = checkIsSignToday(userId);
					if(signed){
						return; //今天签到过
					}
					List<String> marketsId = afBusinessAccessRecordsDao.getFourMarketIds(signedMarketIds,userId);
					//获取4个需要设置为签到的
					List<String> ids = afBusinessAccessRecordsDao.getNeedSignIds(marketsId, userId);
					afBusinessAccessRecordsDao.batchSetSignByIds(ids);
					
				}
			}finally{
				if(locked){
					bizCacheUtil.delCache(LOCK_KEY);
				}
			}
			
		}
		
	}

	@Override
	public int getSignDays(AfBusinessAccessRecordQuery query) {
		int totalCount = afBusinessAccessRecordsDao.getTotalSignCount(query);
		return (int)(totalCount/4);
	}

	@Override
	public int getSignCountToday(Long userId) {
		return afBusinessAccessRecordsDao.getSignCountToday(userId);
	}

}