package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import sun.awt.geom.AreaOp;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

	@Resource
	private TransactionTemplate transactionTemplate;

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



	private AfResourceDo getRecommendRecource(){
		List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_MONEY");
		return list.get(0);
	}


	//value1 提交强风控奖历
	//value2 1级奖历
	//value3 2级奖历
	//value4 多少天内借款才有奖历
	private BigDecimal getMoney(AfBorrowCashDo afBorrowCashDo, AfResourceDo afResourceDo,int type){
		if(type ==1){
			return  new  BigDecimal(Double.parseDouble(afResourceDo.getValue1()));
		}
		else if(type ==2){
			BigDecimal t = new  BigDecimal(Double.parseDouble(afResourceDo.getValue2()));
			return afBorrowCashDo.getAmount().multiply(t);
		}
		else if(type ==3){
			BigDecimal t = new  BigDecimal(Double.parseDouble(afResourceDo.getValue3()));
			return afBorrowCashDo.getAmount().multiply(t);
		}
		return null;
	}

	private int getBorrowDay(AfResourceDo afResourceDo){
		return  Integer.parseInt(afResourceDo.getValue4());
	}

	private BigDecimal getBorrowMoney(AfBorrowCashDo afBorrowCashDo, AfResourceDo afResourceDo, int len){
		if(len ==0){
			return getMoney(afBorrowCashDo,afResourceDo,2);
		}
		if(len ==1){
			return getMoney(afBorrowCashDo,afResourceDo,3);
		}
		return BigDecimal.ZERO;
	}



	/**
	 *
	 * @param userId
	 * @param createTime
	 * @return
	 */
	public int updateRecommendByBorrow(long userId,final Date createTime) {
		try {
			logger.info("{updateRecommendByBorrow} userId=" + userId);
			// 不影响原来逻辑，不保持事物一样
			final AfRecommendUserDo afRecommendUserDo = afRecommendUserDao.getARecommendUserByIdAndType(userId,1);
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
				final AfBorrowCashDo afBorrowCashDo = afBorrowCashDao.getBorrowCash(userId);
				final AfResourceDo afResourceDo = getRecommendRecource();
				int borrowDay = getBorrowDay(afResourceDo);
				AfUserDo afUserDo = afUserDao.getUserById(userId);
				Date p = DateUtil.addDays(afUserDo.getGmtCreate(),borrowDay);
				if(p.before(afBorrowCashDo.getGmtCreate())){
					return 1;
				}

				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						try{
							AfRecommendUserDo _afu = new AfRecommendUserDo();
							_afu.setId(afRecommendUserDo.getId());
							_afu.setLoan_time(createTime);
							BigDecimal addMoney = getAddMoney();
							_afu.setPrize_money(addMoney);
							afRecommendUserDao.updateLoanById(afRecommendUserDo);
							// 修改返现金额
							addRecommendBorrowMoney(afResourceDo,afBorrowCashDo,afRecommendUserDo,0);
						}
						catch (Exception e){
							status.setRollbackOnly();
						}
					}
				});


			}
			return 1;
		} catch (Exception e) {
			logger.error("update userBorrowError userId=" + userId);
			return 1;
		}
	}

	String[] userLongType = {"RECOMMEND_CASH","RECOMMEND_CASH_SECOND"};

	int[] recommendMoneyType = {2,3};

	int lenCount = 2;

	private void addRecommendBorrowMoney(AfResourceDo afResourceDo, AfBorrowCashDo afBorrowCashDo, AfRecommendUserDo afRecommendUserDo,int len){
		BigDecimal money = getBorrowMoney(afBorrowCashDo,afResourceDo, len);

		long pid = afRecommendUserDo.getParentId();
		AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
		afUserAccountDo.setUserId(pid);
		afUserAccountDo.setRebateAmount(money);
		afUserAccountDao.updateRebateAmount(afUserAccountDo);

		AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
		afUserAccountLogDo.setAmount(money);
		afUserAccountLogDo.setUserId(pid);
		afUserAccountLogDo.setType(userLongType[len]);
		afUserAccountLogDo.setRefId(String.valueOf(afRecommendUserDo.getId()));
		afUserAccountLogDao.addUserAccountLog(afUserAccountLogDo);

		AfRecommendMoneyDo afRecommendMoneyDo = new AfRecommendMoneyDo();
		afRecommendMoneyDo.setType(recommendMoneyType[len]);
		afRecommendMoneyDo.setMoney(money);
		afRecommendMoneyDo.setUserId(afRecommendUserDo.getUserId());
		afRecommendMoneyDo.setParentId(afRecommendUserDo.getParentId());
		afRecommendUserDao.addRecommendMoney(afRecommendMoneyDo);

		if(len<1){
			len ++;
			AfRecommendUserDo _afRecommendUserDo = afRecommendUserDao.getARecommendUserByIdAndType(afRecommendUserDo.getParentId(),1);
			if(_afRecommendUserDo !=null){
				addRecommendBorrowMoney(afResourceDo, afBorrowCashDo,  _afRecommendUserDo,len);
			}
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
			AfRecommendUserDo afRecommendUserDo = afRecommendUserDao.getARecommendUserByIdAndType(userId,1);
			if (afRecommendUserDo != null) {

				AfResourceDo afResourceDo = getRecommendRecource();
				BigDecimal money = getMoney(null,afResourceDo,1);

				afRecommendUserDo.setLoan_time(null);
//				afRecommendUserDo.setPrize_money(BigDecimal.valueOf(riskMoney));
				afRecommendUserDo.setPrize_money(money);
				afRecommendUserDao.updateLoanById(afRecommendUserDo);

				long pid = afRecommendUserDo.getParentId();
				AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
				afUserAccountDo.setUserId(pid);
//				afUserAccountDo.setRebateAmount(BigDecimal.valueOf(riskMoney));
				afUserAccountDo.setRebateAmount(money);
				afUserAccountDao.updateRebateAmount(afUserAccountDo);

				AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
//				afUserAccountLogDo.setAmount(BigDecimal.valueOf(riskMoney));
				afUserAccountLogDo.setAmount(money);
				afUserAccountLogDo.setUserId(pid);
				afUserAccountLogDo.setType("RECOMMEND_RISK");
				afUserAccountLogDo.setRefId(String.valueOf(afRecommendUserDo.getId()));
				afUserAccountLogDao.addUserAccountLog(afUserAccountLogDo);


				AfRecommendMoneyDo afRecommendMoneyDo = new AfRecommendMoneyDo();
				afRecommendMoneyDo.setType(0);
				afRecommendMoneyDo.setMoney(money);
				afRecommendMoneyDo.setUserId(afRecommendUserDo.getUserId());
				afRecommendMoneyDo.setParentId(afRecommendUserDo.getParentId());
				afRecommendUserDao.addRecommendMoney(afRecommendMoneyDo);
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
