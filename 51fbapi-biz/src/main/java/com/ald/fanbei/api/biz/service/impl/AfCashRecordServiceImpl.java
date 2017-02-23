/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfCashRecordService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.common.enums.CashRecordType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.dao.AfCashRecordDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfCashRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

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
	public int addCashRecord(final AfCashRecordDo afCashRecordDo) {

//		return 
		return transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					AfUserAccountDo afUserAccountDo = afUserAccountDao.getUserAccountInfoByUserId(afCashRecordDo.getUserId());
					AfUserAccountDo newAccountDo = new AfUserAccountDo();
					newAccountDo.setRid(afUserAccountDo.getRid());
					
					if(StringUtils.equals(afCashRecordDo.getType(), CashRecordType.JIFENBAO.getCode())){
//						BigDecimal aumont = BigDecimalUtil.
					}
					
					afCashRecordDao.addCashRecord(afCashRecordDo);
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
