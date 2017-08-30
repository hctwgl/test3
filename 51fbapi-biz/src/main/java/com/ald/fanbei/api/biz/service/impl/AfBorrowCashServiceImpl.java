package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.util.DateUtil;
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
	AfUserService afUserService;
	@Resource
	AfRecommendUserService afRecommendUserService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	SmsUtil smsUtil;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	JpushService jpushService;
	
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
				//fmf 借款金额加入缓存
				BigDecimal amount = (BigDecimal) bizCacheUtil.getObject("BorrowCash_Sum_Amount");
				if(amount.compareTo(new BigDecimal(1000000000)) == -1 || amount.compareTo(new BigDecimal(1000000000)) == 0) {
					amount=amount.add(afBorrowCashDo.getAmount());
					if(amount.compareTo(new BigDecimal(1000000000)) == 1){
						List<String> users=new ArrayList<String>();
						users.add(afBorrowCashDo.getUserId()+"");
						List<String> userName = afUserService.getUserNameByUserId(users);
						//发送短信
						try{
							smsUtil.sendBorrowCashActivitys(userName.get(0),"恭喜成为最幸运“破十亿”用户，10000元现金红包已发放至您的账户，快去查收惊喜吧。 回T退订");
						}catch(Exception e){
							logger.info("sendBorrowCashActivitys is fail,"+e);
						}
						//推送消息
						jpushService.pushBorrowCashActivitys(userName.get(0), "10000","One");
						// 给用户账号打钱
						afUserAccountService.updateBorrowCashActivity(10000, users);
						//保存破十亿中奖用户
						bizCacheUtil.saveObject("Billion_Win_User", userName.get(0), 60*60*24*7);
					}
					bizCacheUtil.saveObject("BorrowCash_Sum_Amount", amount, 60);
				}
				
				afBorrowCashDao.updateBorrowCash(afBorrowCashDo);
				logger.info("user_id="+ afBorrowCashDo.getUserId());
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
	public BigDecimal getBorrowCashSumAmount() {
		return afBorrowCashDao.getBorrowCashSumAmount();
	}
	
	@Override
	public List<String> getRandomUser() {
		return afBorrowCashDao.getRandomUser();
	}

	@Override
	public List<String> getNotRandomUser(List<String> userId) {
		return afBorrowCashDao.getNotRandomUser(userId);
	}

}
