package com.ald.fanbei.api.biz.service.impl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRebateDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketRelationDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketThresholdDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeRebateDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeRedpacketDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeRedpacketRelationDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeRedpacketThresholdDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.timevale.esign.sdk.tech.impl.model.GetAccountInfoModel;
import com.ald.fanbei.api.biz.service.AfBoluomeRebateService;

/**
 * 点亮活动新版ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:25 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afBoluomeRebateService")
public class AfBoluomeRebateServiceImpl extends ParentServiceImpl<AfBoluomeRebateDo, Long>
		implements AfBoluomeRebateService {

	private static final Logger logger = LoggerFactory.getLogger(AfBoluomeRebateServiceImpl.class);

	@Resource
	private AfBoluomeRebateDao afBoluomeRebateDao;
	@Resource
	AfOrderDao afOrderDao;
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfBoluomeRedpacketThresholdDao thresholdDao;
	@Resource
	AfBoluomeRedpacketDao dao;
	@Resource
	AfBoluomeRedpacketRelationDao relationDao;
	@Override
	public BaseDao<AfBoluomeRebateDo, Long> getDao() {
		return afBoluomeRebateDao;
	}

	/**
	 * mqp 2017-11-15 14:58:13 ：the second time light activity some logics
	 * during the order is finished .
	 */
	@Override
	public void addRedPacket(Long orderId, Long userId) throws Exception {
		try {
			AfBoluomeRebateDo rebateDo = new AfBoluomeRebateDo();

			rebateDo.setOrderId(orderId);
			rebateDo.setUserId(userId);
			// check if its the first time for one specific channel
			int orderTimes = afOrderDao.findFirstOrder(orderId);
			if (orderTimes == 0) {
				rebateDo.setFirstOrder(1);
			} else {
				rebateDo.setFirstOrder(0);
			}
			// check if the order times for red packet
			int redOrderTimes = afBoluomeRebateDao.checkOrderTimes(userId);
			redOrderTimes += 1;
			// check the red packet amount
			boolean flag = this.getAmountAndName(rebateDo, redOrderTimes);
			if (flag) {
				// insert the table af_boluome_redpacket
				rebateDo.setGmtCreate(new Date());
				rebateDo.setGmtModified(new Date());
				afBoluomeRebateDao.saveRecord(rebateDo);
				
				// update the table af_user_account
				AfUserAccountDo accountDo = new AfUserAccountDo();
				accountDo.setUserId(userId);
				accountDo.setRebateAmount(rebateDo.getRebateAmount());
				afUserAccountDao.updateRebateAmount(accountDo);
				
				//call Jpush for rebate
			}
		} catch (Exception e) {
			logger.error("afBoluomeRebateService.addRedPacket() error :", e);
			throw new Exception();
		}

	}

	/**
	 * 
	 * @Title: getAmountAndName @author qiao @date 2017年11月15日
	 * 下午5:13:00 @Description: @param rebateDo @param redOrderTimes @return
	 * void @throws
	 */
	boolean getAmountAndName(AfBoluomeRebateDo rebateDo, int redOrderTimes) {
		boolean result = false;
		List<AfBoluomeRedpacketThresholdDo> thresholdList = thresholdDao.getAllThreadholds();
		if (thresholdList != null && thresholdList.size() > 0) {
			Long thresholdId = 0L;
			
			for(AfBoluomeRedpacketThresholdDo thresholdDo : thresholdList){
				if (redOrderTimes >= thresholdDo.getStart() && redOrderTimes <= thresholdDo.getEnd()) {
					thresholdId = thresholdDo.getRid();
					break;
				}
			}
			
			Long redpacketId = 0L;
			//get the relationDo
			List<Long> redpacketIdList = relationDao.getRedpacketIdListByThreshold(thresholdId);
			if (redpacketIdList != null && redpacketIdList.size() > 0) {
				int length = redpacketIdList.size();
				int index = (int)Math.random()*(length - 1);
				redpacketId = redpacketIdList.get(index);
			}
			
			if (redpacketId != 0L) {
				AfBoluomeRedpacketDo redpacketDo = dao.getById(redpacketId);
				if (redpacketDo != null) {
					rebateDo.setRebateAmount(redpacketDo.getAmount());
					result = true;
				}
			}
			
		}
		return result;
	}

	@Override
	public AfBoluomeRebateDo getLastUserRebateByUserId(Long userId) {
	    // TODO Auto-generated method stub
	    return afBoluomeRebateDao.getLastUserRebateByUserId(userId);
	}


	
}