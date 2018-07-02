package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponTigerMachineDao;
import com.ald.fanbei.api.dal.domain.AfUserCouponTigerMachineDo;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserCouponTigerMachineService;



/**
 * 老虎机用户领券次数ServiceImpl
 * 
 * @author qiao
 * @version 1.0.0 初始化
 * @date 2018-01-05 16:20:41
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserCouponTigerMachineService")
public class AfUserCouponTigerMachineServiceImpl extends ParentServiceImpl<AfUserCouponTigerMachineDo, Long> implements AfUserCouponTigerMachineService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserCouponTigerMachineServiceImpl.class);
   
    @Resource
    private AfUserCouponTigerMachineDao afUserCouponTigerMachineDao;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfUserCouponService afUserCouponService;
    
		@Override
	public BaseDao<AfUserCouponTigerMachineDo, Long> getDao() {
		return afUserCouponTigerMachineDao;
	}

		@Override
		public int getTotalTimesByUserId(Long userId) {
			
			return afUserCouponTigerMachineDao.getTotalTimesByUserId(userId);
		}

		@Override
		public boolean grandCoupon(final Long couponId,final Long userId) {
			return transactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					Boolean result = false;
					try {
						String log = String.format("afUserCouponTigerMachineService.grandCoupon() params: couponId = %d , userId = %d", couponId,userId);
						logger.info(log);
						//decrease time 
						int result1 = afUserCouponTigerMachineDao.decreaseOnce(userId);
						
						log = log + String.format("afUserCouponTigerMachineDao.decreaseOnce(userId) result = %d ",result1);
						logger.info(log);
						
						//grand coupon
						afUserCouponService.grantCoupon(userId, couponId, "SPRING_FESTIVAL_ACTIVITY", " ");
						if (result1 == 1) {
							result = true;
						}
						
						return result;
						
					} catch (Exception e) {
						status.setRollbackOnly();
						logger.info("afUserCouponTigerMachineService.grandCoupon() error:", e);
						return result;
					}
				
				}
			});
		
			
		}

		@Override
		public int addOneTime(Long userId,String type) {
			AfUserCouponTigerMachineDo machineDo = new AfUserCouponTigerMachineDo();
			machineDo.setUserId(userId);
			AfUserCouponTigerMachineDo resultDo = afUserCouponTigerMachineDao.getByCommonCondition(machineDo);
			if (resultDo != null) {
				if (type.equals("DAILY")) {
					//updateOneTimeDaily
					AfUserCouponTigerMachineDo tigerMachineDo = new AfUserCouponTigerMachineDo();
					tigerMachineDo.setUserId(userId);
					tigerMachineDo = afUserCouponTigerMachineDao.getByCommonCondition(tigerMachineDo);
					tigerMachineDo.setDailyTime(1);
					afUserCouponTigerMachineDao.updateById(tigerMachineDo);
					
				}else{
					//updateOneTimeShop
					AfUserCouponTigerMachineDo tigerMachineDo = new AfUserCouponTigerMachineDo();
					tigerMachineDo.setUserId(userId);
					tigerMachineDo = afUserCouponTigerMachineDao.getByCommonCondition(tigerMachineDo);
					tigerMachineDo.setShopingTime(tigerMachineDo.getShopingTime() + 1);
					afUserCouponTigerMachineDao.updateById(tigerMachineDo);
				}
			}else{
				if (type.equals("DAILY")) {
					//TODO:InsertOneTimeDaily
					AfUserCouponTigerMachineDo tigerMachineDo = new AfUserCouponTigerMachineDo();
					tigerMachineDo.setDailyTime(1);
					tigerMachineDo.setGmtCreate(new Date());
					tigerMachineDo.setGmtModified(new Date());
					tigerMachineDo.setShopingTime(0);
					tigerMachineDo.setUserId(userId);
					afUserCouponTigerMachineDao.saveRecord(tigerMachineDo);
					
				}else{
					//InsertOneTimeShop
					AfUserCouponTigerMachineDo tigerMachineDo = new AfUserCouponTigerMachineDo();
					tigerMachineDo.setDailyTime(1);
					tigerMachineDo.setGmtCreate(new Date());
					tigerMachineDo.setGmtModified(new Date());
					tigerMachineDo.setShopingTime(1);
					tigerMachineDo.setUserId(userId);
					afUserCouponTigerMachineDao.saveRecord(tigerMachineDo);
				}
			}
			return 0;
		}
}