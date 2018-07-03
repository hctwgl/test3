/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.AfTaskUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfCashRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfCashLogDao;
import com.ald.fanbei.api.dal.dao.AfCashRecordDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfCashLogDo;
import com.ald.fanbei.api.dal.domain.AfCashRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;

/**
 * @类描述：
 * @author suweili 2017年2月23日上午11:30:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afCashRecordService")
public class AfCashRecordServiceImpl extends BaseService implements AfCashRecordService {

	@Resource
	AfCashRecordDao afCashRecordDao;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	AfTaskUserService afTaskUserService;
	
	@Resource
	AfCashLogDao afCashLogDao;
	@Resource
	UpsUtil upsUtil;
	
	@Override
	public int addCashRecord(final AfCashRecordDo afCashRecordDo,final AfUserBankcardDo card) {
		final AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(afCashRecordDo.getUserId());
		final BigDecimal amount =afCashRecordDo.getAmount();
		
		int result =  transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					AfUserAccountDo updateAccountDo = new AfUserAccountDo();
					updateAccountDo.setRid(afUserAccountDo.getRid());
					updateAccountDo.setUserId(afCashRecordDo.getUserId());

					if(StringUtils.equals(afCashRecordDo.getType(), UserAccountLogType.JIFENBAO.getCode())){
						updateAccountDo.setJfbAmount(amount.multiply(new BigDecimal(-1)));
					}else{
						updateAccountDo.setRebateAmount(amount.multiply(new BigDecimal(-1)));
					}
					if(afUserAccountService.updateUserAccount(updateAccountDo)==0){
						return 0;
					}
					
					afCashRecordDao.addCashRecord(afCashRecordDo);
					AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
					afUserAccountLogDo.setRefId( afCashRecordDo.getRid()+"");
					afUserAccountLogDo.setType(afCashRecordDo.getType());
					afUserAccountLogDo.setUserId(afCashRecordDo.getUserId());
					afUserAccountLogDo.setAmount(amount);
					afUserAccountLogDao.addUserAccountLog(afUserAccountLogDo);

					// add by luoxiao for 边逛边赚，增加零钱明细
					afTaskUserService.addTaskUser(afCashRecordDo.getUserId(),UserAccountLogType.CASH.getName(), amount.multiply(new BigDecimal(-1)));
					// end by luoxiao

					if(null == card){//集分宝提现
						AfCashLogDo cashLog = new AfCashLogDo();
						cashLog.setCashRecordId(afCashRecordDo.getRid());
						cashLog.setStatus(BorrowStatus.APPLY.getCode());
						cashLog.setUserId(afCashRecordDo.getUserId());
						afCashLogDao.addCashLog(cashLog);
					}
					return 1;
				} catch (Exception e) {
					logger.info("dealWithTransferSuccess error:"+e);
					status.setRollbackOnly();
					return 0;
				}
			}
		});
		
		if(result==1){
			//基础处理成功，待调用 三方进行打款操作
			if(null != card){//银行卡提现
				UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(amount, afUserAccountDo.getRealName(), card.getCardNumber(), afUserAccountDo.getUserId()+"",
						card.getMobile(), card.getBankName(), card.getBankCode(),Constants.DEFAULT_CASH_PURPOSE, "02",UserAccountLogType.REBATE_CASH.getCode(),afCashRecordDo.getRid()+"");
				if(!upsResult.isSuccess()){
					//代付失败处理
					afUserAccountService.dealUserDelegatePayError(UserAccountLogType.REBATE_CASH.getCode(), afCashRecordDo.getRid());
					throw new FanbeiException("bank card pay error",FanbeiExceptionCode.BANK_CARD_PAY_ERR);
				}
			}
		}
		return result;
	}

	@Override
	public AfCashRecordDo getCashRecordById(Long id){
		return afCashRecordDao.getCashRecordById(id);
	}

}