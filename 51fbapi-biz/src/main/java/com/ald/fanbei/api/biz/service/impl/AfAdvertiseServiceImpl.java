package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfAdvertiseDao;
import com.ald.fanbei.api.dal.domain.AfAdvertiseDo;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityMsgIndexDo;
import com.ald.fanbei.api.dal.domain.dto.AfAdvertiseDto;
import com.ald.fanbei.api.biz.service.AfAdvertiseService;



/**
 * 定向广告规则ServiceImpl
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-05-17 22:38:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAdvertiseService")
public class AfAdvertiseServiceImpl extends ParentServiceImpl<AfAdvertiseDo, Long> implements AfAdvertiseService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAdvertiseServiceImpl.class);
   
    @Resource
    private AfAdvertiseDao afAdvertiseDao;

		@Override
	public BaseDao<AfAdvertiseDo, Long> getDao() {
		return afAdvertiseDao;
	}
		
//		public AfAdvertiseDto getDirectionalRecommend(Long userId) {
//		    // TODO Auto-generated method stub
//		return afAdvertiseDao.getDirectionalRecommend(userId);
//	}
//		
		private int getDirectionalRecommendCount(Long userId,String queryConditions) {
		    // TODO Auto-generated method stub
		return afAdvertiseDao.getDirectionalRecommendCount(userId,queryConditions);
	}
	
		public AfAdvertiseDto getDirectionalRecommendInfo(String positionCode,Long userId) {
			
			List<AfAdvertiseDto>  advertiseList = afAdvertiseDao.getDirectionalRecommendInfo(positionCode);
			if(advertiseList != null && advertiseList.size() > 0){
				//循环匹配规则
			
				for(AfAdvertiseDto afAdvertiseDto:advertiseList){
			     int count =  	getDirectionalRecommendCount(userId,afAdvertiseDto.getQueryConditions());
				     if(count > 0){
				    	 //更新次数+1；
				       int clickCount = afAdvertiseDao.updateClickCountById(afAdvertiseDto.getRid());
				       logger.info("getDirectionalRecommendInfo clickCount++ by userId"+userId+"clickCount = "+clickCount);
				       return	 afAdvertiseDto;
				     }
				}
			}
			return null;
		}
		
}