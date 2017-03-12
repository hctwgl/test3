/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfCashRecordService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfCashRecordDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
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
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	
	@Override
	public int addCashRecord(final AfCashRecordDo afCashRecordDo,final AfUserBankcardDo card) {

		return transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				
				try {
					AfUserAccountDo afUserAccountDo = afUserAccountDao.getUserAccountInfoByUserId(afCashRecordDo.getUserId());

					AfUserAccountDo updateAccountDo = new AfUserAccountDo();
					updateAccountDo.setRid(afUserAccountDo.getRid());
					updateAccountDo.setUserId(afCashRecordDo.getUserId());
					BigDecimal amount =afCashRecordDo.getAmount();

					if(StringUtils.equals(afCashRecordDo.getType(), UserAccountLogType.JIFENBAO.getCode())){
						updateAccountDo.setJfbAmount(amount.multiply(new BigDecimal(-1)));

					}else{
						updateAccountDo.setRebateAmount(amount.multiply(new BigDecimal(-1)));
					}
					if(afUserAccountDao.updateUserAccount(updateAccountDo)==0){
						return 0;
					}
					if(null == card){//集分宝提现
						
					}else{//银行卡提现
						UpsDelegatePayRespBo upsResult = UpsUtil.delegatePay(amount, afUserAccountDo.getRealName(), card.getCardNumber(), afUserAccountDo.getUserId()+"",
								card.getMobile(), card.getBankName(), card.getBankCode(),Constants.DEFAULT_CASH_PURPOSE, "02");
						if(!upsResult.isSuccess()){
							throw new FanbeiException("bank card pay error",FanbeiExceptionCode.BANK_CARD_PAY_ERR);
						}
					}
					afCashRecordDao.addCashRecord(afCashRecordDo);
					AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
					afUserAccountLogDo.setRefId( afCashRecordDo.getRid()+"");
					afUserAccountLogDo.setType(afCashRecordDo.getType());
					afUserAccountLogDo.setUserId(afCashRecordDo.getUserId());
					afUserAccountLogDo.setAmount(amount);
					afUserAccountLogDao.addUserAccountLog(afUserAccountLogDo);
					return 1;
				} catch (Exception e) {
					logger.info("dealWithTransferSuccess error:"+e);
					
					
					status.setRollbackOnly();
					return 0;
				}
			}
		});
	}

}
