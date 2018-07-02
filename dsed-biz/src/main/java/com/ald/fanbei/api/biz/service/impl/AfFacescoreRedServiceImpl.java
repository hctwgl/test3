package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.AfUserAndRedRelationDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfFacescoreRedDao;
import com.ald.fanbei.api.dal.domain.AfFacescoreImgDo;
import com.ald.fanbei.api.dal.domain.AfFacescoreRedDo;
import com.ald.fanbei.api.dal.domain.AfUserAndRedRelationDo;
import com.ald.fanbei.api.biz.service.AfFacescoreRedService;



/**
 * 颜值测试红包表实体类ServiceImpl
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-12 16:37:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afFacescoreRedService")
public class AfFacescoreRedServiceImpl extends ParentServiceImpl<AfFacescoreRedDo, Long> implements AfFacescoreRedService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfFacescoreRedServiceImpl.class);
   
    @Resource
    private AfFacescoreRedDao afFacescoreRedDao;
    
    @Resource
    private AfUserAndRedRelationDao afUserAndRedRelationDao;

		@Override
	public BaseDao<AfFacescoreRedDo, Long> getDao() {
		return afFacescoreRedDao;
	}

		@Override
		public int addUserAndRedRecord(
				AfUserAndRedRelationDo afUserAndRedRelationDo) {
			return afUserAndRedRelationDao.addUserAndRedRelation(afUserAndRedRelationDo);
		}
		/**
		 * 查询用户提现的次数
		 */
		@Override
		public int findUserAndRedRelationRecordByUserId(Long userId) {
			return afUserAndRedRelationDao.findUserAndRedRelationRecordByUserId(userId);
		}

		@Override
		public AfFacescoreRedDo getImageUrlByUserId(Long userId) {
			// TODO Auto-generated method stub
			return afFacescoreRedDao.getImageUrlByUserId(userId);
		}
		/**
		 * 添加红包记录的方法
		 */
		@Override
		public int addRed(AfFacescoreRedDo redDo) {
			return afFacescoreRedDao.addRed(redDo);
		}
		/**
		 * 查询红包是否已经被提现的方法
		 */
		@Override
		public int findUserAndRedRelationRecordByRedId(long redId) {
			return afUserAndRedRelationDao.findUserAndRedRelationRecordByRedId(redId);
		}

		@Override
		public List<AfFacescoreImgDo> findRedImg() {
			return afFacescoreRedDao.getAllRedImg();
		}
}