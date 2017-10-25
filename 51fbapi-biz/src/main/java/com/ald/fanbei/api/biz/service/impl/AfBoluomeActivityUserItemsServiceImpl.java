package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityUserItemsDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserItemsDo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserItemsService;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeActivityUserItemsService")
public class AfBoluomeActivityUserItemsServiceImpl extends ParentServiceImpl<AfBoluomeActivityUserItemsDo, Long> implements AfBoluomeActivityUserItemsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityUserItemsServiceImpl.class);
   
    @Resource
    private AfBoluomeActivityUserItemsDao afBoluomeActivityUserItemsDao;

		@Override
	public BaseDao<AfBoluomeActivityUserItemsDo, Long> getDao() {
		return afBoluomeActivityUserItemsDao;
	}

		@Override
		public List<Long> getItemsByActivityIdUserId(Long activityId, Long userId) {
			return afBoluomeActivityUserItemsDao.getItemsByActivityIdUserId(activityId,userId);
			
		}

		@Override
		public void deleteByRid(Long rid) {
			afBoluomeActivityUserItemsDao.deleteByRid(rid);
			
		}

		@Override
		public Integer geFakeJoin(Long activityId) {
			return afBoluomeActivityUserItemsDao.geFakeJoin(activityId);
		}

		@Override
		public Integer getFakeFinal(Long activityId) {
			return afBoluomeActivityUserItemsDao.getFakeFinal(activityId);
		}

		@Override
		
			public void updateUserItemsStatus(Long userItemsId, String status) throws Exception{
				try{
					// 检测是否有这个userItemsId的卡片，若有，则更新状态
					AfBoluomeActivityUserItemsDo prevousDo = afBoluomeActivityUserItemsDao.getById(userItemsId);
					if (prevousDo != null ) {

						//验证这个用户是否拥有多余1张的此卡片
						AfBoluomeActivityUserItemsDo t = new AfBoluomeActivityUserItemsDo();
						t.setUserId(prevousDo.getUserId());
						t.setBoluomeActivityId(prevousDo.getBoluomeActivityId());
						t.setItemsId(prevousDo.getItemsId());
						t.setStatus("NORMAL");
						List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsDao
								.getListByCommonCondition(t);
						if (userItemsList != null && userItemsList.size() > 0) {
							AfBoluomeActivityUserItemsDo resourceDo = new AfBoluomeActivityUserItemsDo();
							resourceDo.setRid(userItemsId);
							resourceDo.setStatus(status);
							resourceDo.setGmtModified(new Date());
							afBoluomeActivityUserItemsDao.updateById(resourceDo);
						}
					}
				
				}catch (Exception e) {
					logger.error("update userItems status erro");
					e.printStackTrace();
				}
			}
		
}