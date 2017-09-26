package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月24日下午5:04:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowCashService")
public class AfBorrowCashServiceImpl extends BaseService implements AfBorrowCashService {
	@Resource
	UpsUtil upsUtil;
	@Resource
	AfBorrowCashDao afBorrowCashDao;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserBankcardDao afUserBankcardDao;
	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AfRenewalDetailDao afRenewalDetailDao;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;

	@Resource
	AfRecommendUserService afRecommendUserService;


	@Override
	public int addBorrowCash(AfBorrowCashDo afBorrowCashDo) {
		Date currDate = new Date();
		afBorrowCashDo.setBorrowNo(generatorClusterNo.getBorrowCashNo(currDate));
		return afBorrowCashDao.addBorrowCash(afBorrowCashDo);
	}


	/**
	 * 借款成功
	 * @param afBorrowCashDo
	 * @return
	 */
	public int borrowSuccess(final AfBorrowCashDo afBorrowCashDo){
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus transactionStatus) {
				logger.info("borrowSuccess--begin");
				Date currDate = new Date(System.currentTimeMillis());
				afBorrowCashDo.setGmtArrival(currDate);
				Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
				Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(afBorrowCashDo.getGmtArrival());
				Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
				afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
				afBorrowCashDao.updateBorrowCash(afBorrowCashDo);
				
				int rr = afRecommendUserService.updateRecommendByBorrow(afBorrowCashDo.getUserId(), afBorrowCashDo.getGmtCreate());
				logger.info("updateRecommendUser="+ rr);
				logger.info("borrowSuccess--end");
				return 1;
			}
		});
	}


	@Override
	public int updateBorrowCash(final AfBorrowCashDo afBorrowCashDo) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus transactionStatus) {
				 afBorrowCashDao.updateBorrowCash(afBorrowCashDo);
				 return 1;
			}
		});

	}

	@Override
	public AfBorrowCashDo getBorrowCashByUserId(Long userId) {
		return afBorrowCashDao.getBorrowCashByUserId(userId);
	}

	@Override
	public List<AfBorrowCashDo> getBorrowCashListByUserId(Long userId, Integer start) {
		return afBorrowCashDao.getBorrowCashListByUserId(userId, start);
	}

	@Override
	public AfBorrowCashDo getBorrowCashByrid(Long rid) {
		return afBorrowCashDao.getBorrowCashByrid(rid);
	}

	@Override
	public String getCurrentLastBorrowNo(String orderNoPre) {
		return afBorrowCashDao.getCurrentLastBorrowNo(orderNoPre);
	}

	@Override
	public AfBorrowCashDo getBorrowCashByRishOrderNo(String rishOrderNo) {
		return afBorrowCashDao.getBorrowCashByRishOrderNo(rishOrderNo);
	}

	@Override
	public AfBorrowCashDo getUserDayLastBorrowCash(Long userId) {
		Date startTime = DateUtil.getToday();
		Date endTime = DateUtil.getTodayLast();
		return afBorrowCashDao.getUserDayLastBorrowCash(userId, startTime, endTime);
	}

	@Override
	public Integer getSpecBorrowCashNums(Long userId, String reviewStatus, Date startTime) {
		return afBorrowCashDao.getSpecBorrowCashNums(userId, reviewStatus, startTime);
	}

	@Override
	public boolean isCanBorrowCash(Long userId) {
		List<AfBorrowCashDo> list = afBorrowCashDao.getBorrowCashByStatusNotInFinshAndClosed(userId);
		if (list.size() > 0) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public AfBorrowCashDo getNowTransedBorrowCashByUserId(Long userId) {
		return afBorrowCashDao.getNowTransedBorrowCashByUserId(userId);
	}

	@Override
	public int getBorrowNumByUserId(Long userId) {
		return afBorrowCashDao.getBorrowNumByUserId(userId);
	}

	@Override
	public AfBorrowCashDo getNowUnfinishedBorrowCashByUserId(Long userId) {
		return afBorrowCashDao.getNowUnfinishedBorrowCashByUserId(userId);
	}

	@Override
	public AfBorrowCashDo getBorrowCashInfoByBorrowNo(String borrowNo) {
		return afBorrowCashDao.getBorrowCashInfoByBorrowNo(borrowNo);
	}

	@Override
	public List<AfBorrowCashDo> getRiskRefuseBorrowCash(Long userId, Date gmtStart, Date gmtEnd) {
		return afBorrowCashDao.getRiskRefuseBorrowCash(userId, gmtStart, gmtEnd);
	}

	@Override
	public List<String> getBorrowedUserIds() {
		return afBorrowCashDao.getBorrowedUserIds();
	}


	@Override
	public int updateBalancedDate(AfBorrowCashDo afBorrowCashDo) {
		return afBorrowCashDao.updateBalancedDate(afBorrowCashDo);
	}
	
	@Override
	public int getCurrDayTransFailTimes(Long userId){
		return afBorrowCashDao.getCurrDayTransFailTimes(userId);
	}
}
