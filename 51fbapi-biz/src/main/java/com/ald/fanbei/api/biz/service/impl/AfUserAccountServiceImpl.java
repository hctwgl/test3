package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.common.enums.InterestType;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午4:05:08
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserAccountService")
public class AfUserAccountServiceImpl implements AfUserAccountService {

	@Resource
	AfUserAccountDao afUserAccountDao;
	
	@Resource
	private TransactionTemplate transactionTemplate;
	
	@Resource
	AfBorrowDao afBorrowDao;
	
	@Override
	public AfUserAccountDo getUserAccountByUserId(Long userId) {
		return afUserAccountDao.getUserAccountInfoByUserId(userId);
	}

	@Override
	public int addUserAccount(AfUserAccountDo accountDo) {
		return afUserAccountDao.addUserAccount(accountDo);
	}

	@Override
	public AfUserAccountDto getUserAndAccountByUserId(Long userId) {
		return afUserAccountDao.getUserAndAccountByUserId(userId);
	}

	@Override
	public int dealCashApply(final AfUserAccountDto userDto,final BigDecimal money) {
		
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					//修改用户账户信息
					AfUserAccountDo account = new AfUserAccountDo();
//					account.setCcAmount(userDto.getCcAmount().subtract(money));//可取现金额=可取现金额-申请取现金额
					account.setUcAmount(userDto.getUcAmount().add(money));//已取现金额=已取现金额+申请取现金额
					account.setUsedAmount(userDto.getUsedAmount().add(money));//授信已使用金额=授信已使用金额+申请取现金额
//					account.setRemainingAmount(userDto.getRemainingAmount().subtract(money));//授信剩余金额=授信剩余金额-申请取现金额
					afUserAccountDao.updateUserAccount(account);					
					AfBorrowDo borrow =  buildBorrow(userDto.getUserId(), money);
					afBorrowDao.addBorrow(borrow);
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					return 0;
				}
			}
		});
	}

	/**
	 * 
	 * @param userId
	 * @param money -- 借款金额
	 * @return
	 */
	private AfBorrowDo buildBorrow(Long userId,BigDecimal money){
		AfBorrowDo borrow = new AfBorrowDo();
		borrow.setAmount(money);
		borrow.setType(BorrowType.CASH.getCode());
		borrow.setStatus(BorrowStatus.APPLY.getCode());
		borrow.setName("取现");
		borrow.setUserId(userId);
		borrow.setNper(1);
		borrow.setPerAmount(money);
		borrow.setNperRepaid(0);
		borrow.setInterestType(InterestType.CHARGES.getCode());
		return borrow;
	}
	
}
