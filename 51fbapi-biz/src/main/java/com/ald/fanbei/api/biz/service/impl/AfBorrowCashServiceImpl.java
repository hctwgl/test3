/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;

/**
 * @类描述：
 * @author suweili 2017年3月24日下午5:04:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowCashService")
public class AfBorrowCashServiceImpl extends BaseService implements AfBorrowCashService {
	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AfBorrowCashDao afBorrowCashDao;
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfUserBankcardDao afUserBankcardDao;
	@Resource
	RiskUtil riskUtil;
	@Resource
	TransactionTemplate transactionTemplate;
	@Override
	public int addBorrowCash(final AfBorrowCashDo afBorrowCashDo) {
		
		
		
		return transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				
				try {
					
					
					Date currDate = new Date();

					afBorrowCashDo.setBorrowNo(generatorClusterNo.getBorrowCashNo(currDate));
					 Long userId = afBorrowCashDo.getUserId();

					 afBorrowCashDao.addBorrowCash(afBorrowCashDo);
					 Long borrowId = afBorrowCashDo.getRid();
					 RiskVerifyRespBo result = riskUtil.verify(ObjectUtils.toString(userId, "") , "20", afBorrowCashDo.getCardNumber());
					 AfBorrowCashDo borrowCashDo = new AfBorrowCashDo();
					 borrowCashDo.setRid(borrowId);	 
					 if(StringUtils.equals("10", result.getResult())){
						//审核通过
						 borrowCashDo.setGmtArrival(currDate);
						 borrowCashDo.setArrivalAmount(afBorrowCashDo.getArrivalAmount());;

						 borrowCashDo.setStatus(AfBorrowCashStatus.transed.getCode());
						 AfUserAccountDto userDto = afUserAccountDao.getUserAndAccountByUserId(userId);
							AfUserBankcardDo card = afUserBankcardDao.getUserMainBankcardByUserId(userId);

						 //打款
							UpsDelegatePayRespBo upsResult = UpsUtil.delegatePay(afBorrowCashDo.getArrivalAmount(), userDto.getRealName(), afBorrowCashDo.getCardNumber(), afBorrowCashDo.getUserId()+"",
									card.getMobile(), card.getBankName(), card.getBankCode(),Constants.DEFAULT_BORROW_PURPOSE, "02",
									UserAccountLogType.BorrowCash.getCode(),borrowId+"");
							borrowCashDo.setReviewStatus(AfBorrowCashReviewStatus.agree.getCode());
							if(!upsResult.isSuccess()){
								logger.info("upsResult error:"+FanbeiExceptionCode.BANK_CARD_PAY_ERR);
								 borrowCashDo.setStatus(AfBorrowCashStatus.transedfail.getCode()); 
							}
							afBorrowCashDao.updateBorrowCash(borrowCashDo);
						 
					 }else if(StringUtils.equals("30", result.getResult())){
						 borrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
						 borrowCashDo.setReviewStatus(AfBorrowCashReviewStatus.refuse.getCode());
						 borrowCashDo.setReviewDetails(AfBorrowCashReviewStatus.refuse.getName());
					 }else{
						 borrowCashDo.setReviewStatus(AfBorrowCashReviewStatus.waitfbReview.getCode());
					 }
					 afBorrowCashDao.updateBorrowCash(borrowCashDo);
					return 1;
				} catch (Exception e) {
					logger.info("dealWithTransferSuccess error:"+e);
					
					
					status.setRollbackOnly();
					return 0;
				}
			}
		});
	}

	
	@Override
	public int updateBorrowCash(AfBorrowCashDo afBorrowCashDo) {
		return afBorrowCashDao.updateBorrowCash(afBorrowCashDo);
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

}
