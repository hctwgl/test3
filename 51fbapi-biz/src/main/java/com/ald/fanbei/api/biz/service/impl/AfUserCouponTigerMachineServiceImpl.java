package com.ald.fanbei.api.biz.service.impl;

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
						String log = String.format("afUserCouponTigerMachineService.grandCoupon() params: couponId = %L , userId = %L", couponId,userId);
						logger.info(log);
						//decrease time 
						
						//grand coupon
						afUserCouponService.grantCoupon(userId, couponId, "SPRING_FESTIVAL_ACTIVITY", " ");
						
						result = true;
						return result;
						
					} catch (Exception e) {
						status.setRollbackOnly();
						logger.info("afUserCouponTigerMachineService.grandCoupon() error:", e);
						return result;
					}
				
				}
			});
		
			
		}
}