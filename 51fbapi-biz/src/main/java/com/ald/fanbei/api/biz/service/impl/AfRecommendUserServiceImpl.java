package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.awt.geom.AreaOp;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("afRecommendUserService")
public class AfRecommendUserServiceImpl implements AfRecommendUserService {

	@Resource
	AfRecommendUserDao afRecommendUserDao;
	@Resource
	AfUserAccountDao afUserAccountDao;

	@Resource
	AfResourceDao afResourceDao;

	@Resource
	AfBorrowCashDao afBorrowCashDao;

	@Resource
	AfUserAccountLogDao afUserAccountLogDao;

	@Resource
	AfUserDao afUserDao;

	private BigDecimal getAddMoney() {
		List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_MONEY");
		if (list != null && list.size() > 0) {
			try {
				return new BigDecimal(Double.parseDouble(list.get(0).getValue()));
			} catch (Exception e) {

			}
		}
		return new BigDecimal(50);
	}

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 *
	 * @param userId
	 * @param createTime
	 * @return
	 */
	public int updateRecommendByBorrow(long userId, Date createTime) {
		try {
			logger.info("{updateRecommendByBorrow} userId=" + userId);
			// 不影响原来逻辑，不保持事物一样
			AfRecommendUserDo afRecommendUserDo = afRecommendUserDao.getARecommendUserById(userId);
			if (afRecommendUserDo != null && !afRecommendUserDo.isIs_loan()) {
				Long count = 0L;
				HashMap map = afBorrowCashDao.getBorrowCashByRemcommend(userId);
				logger.info("{update begin} userId=" + userId);
				try {
					count = (Long) map.get("count");
					if (count > 1)
						return 1;
				} catch (Exception e) {
					logger.error("{update userBorrowError} userId=" + userId);
				}

				afRecommendUserDo.setLoan_time(createTime);
				BigDecimal addMoney = getAddMoney();
				afRecommendUserDo.setPrize_money(addMoney);
				afRecommendUserDao.updateLoanById(afRecommendUserDo);
				// 修改返现金额
				long pid = afRecommendUserDo.getParentId();
				AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
				afUserAccountDo.setUserId(pid);
				afUserAccountDo.setRebateAmount(addMoney);
				afUserAccountDao.updateRebateAmount(afUserAccountDo);

				AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
				afUserAccountLogDo.setAmount(addMoney);
				afUserAccountLogDo.setUserId(pid);
				afUserAccountLogDo.setType("RECOMMEND_CASH");
				afUserAccountLogDo.setRefId(String.valueOf(afRecommendUserDo.getId()));
				afUserAccountLogDao.addUserAccountLog(afUserAccountLogDo);

			}
			return 1;
		} catch (Exception e) {
			logger.error("update userBorrowError userId=" + userId);
			return 1;
		}
	}

	int riskMoney = 2;
	/**
	 * 通过强风控就给推荐人加10块钱
	 * 
	 * @param userId
	 * @return
	 */
	public int updateRecommendCash(long userId) {
		try {
			AfRecommendUserDo afRecommendUserDo = afRecommendUserDao.getARecommendUserById(userId);
			if (afRecommendUserDo != null) {

				afRecommendUserDo.setLoan_time(null);
				afRecommendUserDo.setPrize_money(BigDecimal.valueOf(riskMoney));
				afRecommendUserDao.updateLoanById(afRecommendUserDo);

				long pid = afRecommendUserDo.getParentId();
				AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
				afUserAccountDo.setUserId(pid);
				afUserAccountDo.setRebateAmount(BigDecimal.valueOf(riskMoney));
				afUserAccountDao.updateRebateAmount(afUserAccountDo);

				AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
				afUserAccountLogDo.setAmount(BigDecimal.valueOf(riskMoney));
				afUserAccountLogDo.setUserId(pid);
				afUserAccountLogDo.setType("RECOMMEND_RISK");
				afUserAccountLogDo.setRefId(String.valueOf(afRecommendUserDo.getId()));
				afUserAccountLogDao.addUserAccountLog(afUserAccountLogDo);
			}
			return 1;
		} catch (Exception e) {
			logger.error("update updateRecommendCash userId=" + userId, e);
			return 1;
		}
	}

	@Override
	public List<String> getActivityRule(String type) {
		return afResourceDao.getActivityRule(type);
	}

	@Override
	public String getUserRecommendCode(long userId) {
		return afUserDao.getUserRecommendCode(userId);
	}

	@Override
	public double getSumPrizeMoney(long userId) {
		return afRecommendUserDao.getSumPrizeMoney(userId);
	}

	@Override
	public List<AfRecommendUserDo> rewardQuery(long userId, String type) {
		List<AfRecommendUserDo> listData= new ArrayList<>();
		if("1".equals(type)){
			listData =afRecommendUserDao.firstRewardQuery(userId);
		}else if("2".equals(type)){
			listData = afRecommendUserDao.twoLevelRewardQuery(userId);
		}
		if(listData!=null){
			for (AfRecommendUserDo af: listData) {
				//加上状态
				AfRecommendUserDo afRecommendUserDo =afRecommendUserDao.getARecommendUserById(af.getUser_id());
				if(afRecommendUserDo.isIs_loan()){
					af.setStatus("已借款");
				}else{
					int compare=afRecommendUserDo.getPrize_money().compareTo(BigDecimal.ZERO);
					if(compare==1){
						af.setStatus("提交信用审核");
					}else{
						af.setStatus("已注册");
					}
				}
				//加上userName
				AfUserDo afUserDo =afUserDao.getUserById(af.getUser_id());
				af.setUserName(afUserDo.getUserName());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
				String createTime = sdf.format(afUserDo.getGmtCreate());
				af.setCreateTime(createTime);
			}
		}
		return listData;
	}

	public HashMap getRecommedData(long userId) {
		return afRecommendUserDao.getRecommedData(userId);
	}

	public List<HashMap> getRecommendListByUserId(long userId, int pageIndex, int pageSize) {
		pageIndex = pageSize * pageIndex;
		return afRecommendUserDao.getRecommendListByUserId(userId, pageIndex, pageSize);
	}

	public List<HashMap> getRecommendListSort(Date startTime, Date endTime) {
		return afRecommendUserDao.getRecommendListSort(startTime, endTime);
	}

	/**
	 * 获取活动对应的图片或奖品
	 * 
	 * @param type
	 *            RECOMMEND_IMG||RECOMMEND_PRIZE
	 * @return
	 */
	public List<AfResourceDo> getActivieResourceByType(String type) {
		return afResourceDao.getActivieResourceByType(type);
	}

	/**
	 * 获取中奖名单
	 * 
	 * @param datamonth
	 *            月份
	 * @return
	 */
	public List<HashMap> getPrizeUser(String datamonth) {
		return afRecommendUserDao.getPrizeUser(datamonth);
	}

	public int addRecommendShared(AfRecommendShareDo afRecommendShareDo) {
		return afRecommendUserDao.addRecommendShared(afRecommendShareDo);
	}

	public HashMap getRecommendSharedById(String id) {
		return afRecommendUserDao.getRecommendSharedById(id);
	}

}
