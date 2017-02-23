package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowTotalBillDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月21日上午10:55:47
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowBillService {

	/**
	 * 获取用户某月账单列表
	 * @param query
	 * @return
	 */
	List<AfBorrowBillDo> getMonthBillList(AfBorrowBillQuery query);
	
	/**
	 * 
	 * @param userId
	 * @param billYear
	 * @param billMonth
	 * @return
	 */
	BigDecimal getMonthlyBillByStatus(Long userId,int billYear,int billMonth,String status);
	
	/**
	 * 用户全部账单
	 */
	List<AfBorrowTotalBillDo> getUserFullBillList(Long userId);
	
	/**
	 * 获取账单详情信息
	 * @param rid
	 * @return
	 */
	public AfBorrowBillDto getBorrowBillById(Long rid);
	
	/**
	 * 获取用户某期全部账单
	 * @param userId
	 * @param billYear
	 * @param billMonth
	 * @return
	 */
	AfBorrowBillDo getTotalMonthlyBillByUserId(Long userId,int billYear,int billMonth);
	
	/**
	 * 获取账单金额
	 * @param ids
	 * @return
	 */
	AfBorrowBillDo getBillAmountByIds(String ids);
	
	/**
	 * 账单状态变更
	 * @param ids
	 * @return
	 */
	int updateBorrowBillStatusByIds(String ids,String status);
	
	/**
	 * 获取未还款账单数量
	 * @return
	 */
	int getUserMonthlyBillNotpayCount(int year,int month,Long userId);
	
	/**
	 * 修改总账单状态
	 * @param year
	 * @param month
	 * @param userId
	 * @param status
	 * @return
	 */
	int updateTotalBillStatus(int year,int month,Long userId,String status);
	
	/**
	 * 获取现金借款的账单金额
	 */
	AfBorrowBillDo getBillAmountByCashIds(String ids);
}
