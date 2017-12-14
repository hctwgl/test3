package com.ald.fanbei.api.biz.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBoluomeRebateService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.dao.AfBoluomeRebateDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketRelationDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketThresholdDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeRebateDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeRedpacketDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeRedpacketThresholdDo;
import com.ald.fanbei.api.dal.domain.AfRebateDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;

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
	@Resource
	AfUserDao afUserDao;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;

	@Override
	public BaseDao<AfBoluomeRebateDo, Long> getDao() {
		return afBoluomeRebateDao;
	}

	@Resource
	JpushService jpushService;
	@Resource
	BizCacheUtil bizCacheUtil;

	/**
	 * 
	 * @Title: addRedPacket @author qiao @date 2017年11月17日
	 *         下午3:59:57 @Description: the second time light activity some
	 *         logics during the order is finished . @param orderId @param
	 *         userId @throws Exception @throws
	 */
	@Override
	public void addRedPacket(Long orderId, Long userId) throws Exception {

		String key = Constants.GG_SURPRISE_LOCK + ":" + userId + ":" + orderId;
		boolean lock = bizCacheUtil.getLockTryTimes(key, "1", 100);
		try {
			if (lock) {

				// check if this orderId has already been rebated
				int isHave = afBoluomeRebateDao.getRebateNumByOrderId(orderId);
				if (isHave == 0) {

					String log = String.format("addRedPacket || params : orderId = %s , userId = %s", orderId, userId);
					logger.info(log);
					AfBoluomeRebateDo rebateDo = new AfBoluomeRebateDo();

					rebateDo.setOrderId(orderId);
					rebateDo.setUserId(userId);
					// check if its the first time for one specific channel
					int orderTimes = afOrderDao.findFirstOrder(orderId, userId);
					log = log + String.format("Middle business params : orderTimes = %s ", orderTimes);
					logger.info(log);
					if (orderTimes == 0) {
						rebateDo.setFirstOrder(1);

						// check if the order times for red packet
						int redOrderTimes = afBoluomeRebateDao.checkOrderTimes(userId);
						log = log + String.format("redOrderTimes = %s ", redOrderTimes);
						logger.info(log);

						redOrderTimes += 1;
						// check the red packet amount
						boolean flag = this.getAmountAndName(rebateDo, redOrderTimes);

						log = log + String.format("flag = %s ", flag);
						logger.info(log);
						if (flag) {
							// insert the table af_boluome_redpacket
							rebateDo.setGmtCreate(new Date());
							rebateDo.setGmtModified(new Date());
							int saveResult = afBoluomeRebateDao.saveRecord(rebateDo);

							log = log + String.format("saveResult = %s ", saveResult);
							logger.info(log);
							if (saveResult > 0) {

								// update the table af_user_account
								AfUserAccountDo accountDo = new AfUserAccountDo();
								accountDo.setUserId(userId);
								accountDo.setRebateAmount(rebateDo.getRebateAmount());
								int updateResult = afUserAccountDao.updateRebateAmount(accountDo);

								log = log + String.format("updateResult = %s ", updateResult);
								logger.info(log);

								if (updateResult > 0) {
									AfUserAccountLogDo logDo = new AfUserAccountLogDo();
									logDo.setAmount(rebateDo.getRebateAmount());
									logDo.setType("REBATE");
									logDo.setGmtCreate(new Date());
									logDo.setUserId(userId);
									logDo.setRefId(orderId.toString());
									int saveLogResult = afUserAccountLogDao.addUserAccountLog(logDo);

									log = log + String.format("saveLogResult = %s ", saveLogResult);
									logger.info(log);
								}

								// call Jpush for rebate
								String userName = convertToUserName(userId);
								log = log + String.format("userName = %s , rebateAmount = %s", userName,
										rebateDo.getRebateAmount());
								logger.info(log);
								if (userName != null) {
									String scence = afBoluomeRebateDao.getScence(orderId);
									log = log + String.format(" rebateAmount = %s", rebateDo.getRebateAmount());
									logger.info(log);
									jpushService.sendRebateMsg(userName, scence, rebateDo.getRebateAmount());
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("afBoluomeRebateService.addRedPacket() error :", e);
			throw new Exception();
		} finally {
			bizCacheUtil.delCache(key);
		}

	}

	private String convertToUserName(Long userId) {
		AfUserDo userDo = afUserDao.getUserById(userId);
		String userName = "";
		if (userDo != null) {
			userName = userDo.getUserName();
		}
		return userName;
	}

	/**
	 * 
	 * @Title: getAmountAndName @author qiao @date 2017年11月15日
	 *         下午5:13:00 @Description: @param rebateDo @param
	 *         redOrderTimes @return void @throws
	 */
	boolean getAmountAndName(AfBoluomeRebateDo rebateDo, int redOrderTimes) {
		boolean result = false;
		List<AfBoluomeRedpacketThresholdDo> thresholdList = thresholdDao.getAllThreadholds();
		if (thresholdList != null && thresholdList.size() > 0) {
			Long thresholdId = 0L;

			for (AfBoluomeRedpacketThresholdDo thresholdDo : thresholdList) {
				if (redOrderTimes >= thresholdDo.getStart() && redOrderTimes <= thresholdDo.getEnd()) {
					thresholdId = thresholdDo.getRid();
					break;
				}
			}

			Long redpacketId = 0L;
			// get the relationDo
			List<Long> redpacketIdList = relationDao.getRedpacketIdListByThreshold(thresholdId);
			if (redpacketIdList != null && redpacketIdList.size() > 0) {
				int length = redpacketIdList.size();
				int index = new Random().nextInt(length) % (length - 1 + 1) + 1;
				redpacketId = redpacketIdList.get(index - 1);
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

	/**
	 * 
	 * @Title: getListByUserId @author qiao @date 2017年11月17日
	 *         下午12:59:03 @Description: 根据用户查所有返利 @param userId @return @throws
	 */
	@Override
	public List<AfBoluomeRebateDo> getListByUserId(Long userId) {

		return afBoluomeRebateDao.getListByUserId(userId);
	}

	/**
	 * 
	 * @Title: getLightShopId @author qiao @date 2017年11月17日
	 *         下午3:59:24 @Description: @param orderId @return @throws
	 */
	@Override
	public Long getLightShopId(Long orderId) {
		return afBoluomeRebateDao.getLightShopId(orderId);
	}

	/**
	 * 
	 * @Title: getRebateList @author qiao @date 2017年11月17日
	 *         下午3:59:30 @Description: @param userId @return @throws
	 */
	@Override
	public List<AfRebateDo> getRebateList(Long userId, String startTime) {

		List<AfRebateDo> listRebateDo = afBoluomeRebateDao.getRebateList(userId, startTime);

		for (AfRebateDo do1 : listRebateDo) {
			String date = "";
			try {
				date = StringdateToString(do1.getConsumeTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			do1.setConsumeTime(date);
		}
		return listRebateDo;
	}

	public static String StringdateToString(String time) throws ParseException {
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		String ctime = formatter.format(formatter.parse(time));

		return ctime;
	}

	@Override
	public AfBoluomeRebateDo getLastUserRebateByUserId(Long userId) {
		// TODO Auto-generated method stub
		return afBoluomeRebateDao.getLastUserRebateByUserId(userId);
	}

	@Override
	public int getRebateCount(Long shopId, Long userId) {
		return afBoluomeRebateDao.getRebateCount(shopId, userId);

	}

	@Override
	public AfBoluomeRebateDo getMaxUserRebateByStartIdAndEndIdAndUserId(Long startId, Long endId, Long userId) {
		// TODO Auto-generated method stub
		return afBoluomeRebateDao.getMaxUserRebateByStartIdAndEndIdAndUserId(startId, endId, userId);
	}

}