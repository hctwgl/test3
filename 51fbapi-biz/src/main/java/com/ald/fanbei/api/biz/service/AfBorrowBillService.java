package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.ald.fanbei.api.biz.bo.barlyClearance.AllBarlyClearanceBo;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowTotalBillDo;
import com.ald.fanbei.api.dal.domain.AfInterimAuDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.dto.AfOverdueOrderDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQueryNoPage;

import org.apache.ibatis.annotations.Param;

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

	BigDecimal getMonthlyBillByStatusNew(Long userId, int billYear, int billMonth, String status);

	BigDecimal getMonthlyBillByStatusNewV1(Long userId, String status);

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
	 * 根据借款ID获取该借款账单的最大逾期天数
	 * @param borrowId
	 * @return
	 */
	int getMaxOverdueCountByBorrowId( Long borrowId);
	
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
	 * @param billId
	 * @return
	 */
	AfBorrowBillDo getOverduedAndNotRepayBill(Long borrowId, Long billId);


	List<AfBorrowBillDo> getAllBorrowNoPayByUserId(long userId);
	List<HashMap> getBorrowBillNoPaySumByUserId( long userId);

	AfBorrowBillDo getTotalMonthlyBillByIds(@Param("userId") Long userId,@Param("ids") List<Long > ids );
	/**
	 * 根据billId查询数据，消费分期推送风控使用
	 * @author yuyue
	 * @Time 2017年10月10日 下午4:45:04
	 * @param billIds
	 * @return
	 */
	List<AfOverdueOrderDto> getOverdueDataToRiskByBillIds(List<Long> billIds);

	/**
	 * 根据userId查询数据，消费分期推送风控使用
	 * @author yuyue
	 * @Time 2017年10月10日 下午4:45:07
	 * @param consumerNo
	 * @return
	 */
	List<AfOverdueOrderDto> getOverdueDataToRiskByConsumerNo(Long consumerNo);

	/**
	 * 获取用户未付款的逾期账单月数
	 * @author yuyue
	 * @Time 2017年11月10日 下午1:21:21
	 * @param userId
	 * @return
	 */
	int getOverduedMonthByUserId(Long userId);

	/**
	 * 获取未逾期用户最后还款日
	 * @author yuyue
	 * @Time 2017年11月13日 下午3:42:33
	 * @param userId
	 * @return
	 */
	Date getLastPayDayByUserId(Long userId);

	/**
	 * 根据条件获取用户账单金额
	 * @author yuyue
	 * @Time 2017年11月15日 下午3:55:05
	 * @param query
	 * @return
	 */
	BigDecimal getUserBillMoneyByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 根据条件获取用户月账单金额
	 * @author yuyue
	 * @Time 2017年11月15日 下午4:56:00
	 * @param query
	 * @return
	 */
	List<AfBorrowBillDo> getUserBillListByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 根据条件查询子账单个数
	 * @author yuyue
	 * @Time 2017年11月20日 下午4:32:52
	 * @param query
	 * @return
	 */
	int countBillByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 根据条件查询子账单详情
	 * @author yuyue
	 * @Time 2017年11月21日 上午11:55:03
	 * @param query
	 * @return
	 */
	List<AfBorrowBillDto> getBillListByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 根据条件查询月账单逾期利息
	 * @author yuyue
	 * @Time 2017年11月21日 下午2:34:22
	 * @param query
	 * @return
	 */
	BigDecimal getUserOverdeuInterestByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 根据borrowId查询账单利息
	 * @author yuyue
	 * @Time 2017年11月23日 下午1:55:35
	 * @param borrowId
	 * @return
	 */
	BigDecimal getInterestByBorrowId(Long borrowId);

	/**
	 * 根据borrowId查询账单逾期利息
	 * @author yuyue
	 * @Time 2017年11月23日 下午2:13:27
	 * @param rid
	 * @return
	 */
	BigDecimal getOverdueInterestByBorrowId(Long borrowId);

	/**
	 * 查询用户历史账单(已还清)
	 * @author yuyue
	 * @Time 2017年11月24日 下午1:30:14
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<AfBorrowBillDo> getUserAllMonthBill(Long userId, int page, int pageSize);

	List<AllBarlyClearanceBo> getAllClear(Long userId,Long billId);

	

	/**
	 * 根据用户ID获取未还的逾期数
	 * @author yuyue
	 * @Time 2017年11月6日 下午3:34:04
	 * @param rid
	 * @return
	 */
	int countNotPayOverdueBill(Long userId);

	/**
	 * 新增用户账单日的方法
	 * @author yuyue
	 * @Time 2017年11月10日 下午4:27:32
	 * @param rid
	 * @param outDay
	 * @param payDay
	 */
	int addUserOutDay(long userId, int outDay, int payDay);

	/**
	 * 修改账单日的方法
	 * @author yuyue
	 * @Time 2017年11月10日 下午4:28:05
	 * @param rid
	 * @param outDay
	 * @param payDay
	 */
	int updateUserOutDay(long userId, int outDay, int payDay);


	String getBillIdsByUserId(Long userId);

	int updateBorrowBillLockById(String billId);

	int updateBorrowBillUnLockByIds(String billIds);

	/**
	 * 获取用户还款日
	 * @author yuyue
	 * @Time 2017年12月4日 下午3:18:58
	 * @param userId
	 * @param billYear
	 * @param billMonth
	 * @return
	 */
	Date getPayDayByYearAndMonth(Long userId, int billYear, int billMonth);

	/**
	 * 获取用户的临时额度
	 * @param userId
	 * @return
	 */
	AfInterimAuDo selectInterimAmountByUserId(Long userId);

	/**
	 * 根据条件查询billId
	 * @author yuyue
	 * @Time 2017年12月19日 下午3:40:38
	 * @param query
	 * @return
	 */
	List<Long> getBillIdListByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 获取当前用户还款中的数量
	 * @param userId
	 * @return
	 */
	int getOnRepaymentCountByUserId(Long userId);

	AfBorrowBillDo getOverdueBorrowBillInfoByUserId(Long userId);

	int updateBorrowBillFaildWhenNotY(List<Long> billIdList);
}
