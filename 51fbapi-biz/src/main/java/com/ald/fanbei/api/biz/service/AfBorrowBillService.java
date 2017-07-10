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
	 * 
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
	BigDecimal getMonthlyBillByStatus(Long userId, int billYear, int billMonth, String status);

	/**
	 * 用户全部账单
	 */
	List<AfBorrowTotalBillDo> getUserFullBillList(Long userId);

	/**
	 * 获取账单详情信息
	 * 
	 * @param rid
	 * @return
	 */
	public AfBorrowBillDo getBorrowBillById(Long rid);

	/**
	 * 获取借款账单关联信息
	 * 
	 * @param rid
	 * @return
	 */
	AfBorrowBillDto getBorrowBillDtoById(Long rid);

	/**
	 * 获取用户某期全部账单
	 * 
	 * @param userId
	 * @param billYear
	 * @param billMonth
	 * @return
	 */
	AfBorrowBillDo getTotalMonthlyBillByUserId(Long userId, int billYear, int billMonth);

	/**
	 * 获取账单金额
	 * 
	 * @param ids
	 * @return
	 */
	AfBorrowBillDo getBillAmountByIds(String ids);

	/**
	 * 账单状态变更
	 * 
	 * @param ids
	 * @return
	 */
	int updateBorrowBillStatusByIds(String ids, String status, Long repaymentId, BigDecimal couponAmount, BigDecimal jfbAmount, BigDecimal rebateAmount);

	/**
	 * 获取未还款账单数量
	 * 
	 * @return
	 */
	int getUserMonthlyBillNotpayCount(int year, int month, Long userId);

	/**
	 * 获取账单总数量
	 * 
	 * @return
	 */
	int getUserMonthlyBillTotalCount(int year, int month, Long userId);

	/**
	 * 修改总账单状态
	 * 
	 * @param year
	 * @param month
	 * @param userId
	 * @param status
	 * @return
	 */
	int updateTotalBillStatus(int year, int month, Long userId, String status);

	/**
	 * 获取现金借款的账单金额
	 */
	AfBorrowBillDo getBillAmountByCashIds(String ids);

	/**
	 * 借款账单总额
	 * 
	 * @param borrowId
	 * @return
	 */
	BigDecimal getBorrowBillByBorrowId(Long borrowId);

	/**
	 * 获取当前借款所有账单
	 * @param borrowId
	 * @return
	 */
	List<AfBorrowBillDo> getAllBorrowBillByBorrowId(Long borrowId);

	int getBorrowBillWithNoPayByUserId(Long userId);
	/**
	 * 根据借款ID获取已还账单总数
	 * @param borrowId
	 * @return
	 */
	int getPaidBillNumByBorrowId(Long borrowId);
	/**
	 * 根据借款Id统计总收益（账单利息，逾期利息，账单手续费，逾期手续费）
	 */
	BigDecimal getSumIncomeByBorrowId(Long borrowId);
	/**
	 * 获取总逾期天数
	 * @param borrowId
	 * @return
	 */
	Long getSumOverdueDayByBorrowId(Long borrowId);
	/**
	 * 根据借款ID获取该借款的总逾期次数
	 * @param borrowId
	 * @return
	 */
	int getSumOverdueCountByBorrowId(Long borrowId);
	
	/**
	 * 修改账单状态
	 * @param billIds 账单ids
	 * @param status 状态
	 * @return
	 */
	int updateBorrowBillStatusByBillIdsAndStatus(List<Long> billIds, String status);
	
	/**
	 * 根据账单id获取账单列表
	 * 
	 * @param ids
	 * @return
	 */
	List<AfBorrowBillDo> getBorrowBillByIds(List<Long> billIdList);
	
	/**
	 * 判断是否存在当月正在还款中的账单
	 * 
	 * @param query
	 * @return
	 */
	boolean existMonthRepayingBill(Long userId, Integer billYear, Integer billMonth);
	
	/**
	 * 找到逾期并且未还账单Id
	 * @param borrowId
	 * @return
	 */
	Long getOverduedAndNotRepayBillId(Long borrowId);
	
	/**
	 * 找到逾期并且未还账单Id
	 * @param borrowId
	 * @return
	 */
	AfBorrowBillDo getOverduedAndNotRepayBill(Long borrowId);
	
}
