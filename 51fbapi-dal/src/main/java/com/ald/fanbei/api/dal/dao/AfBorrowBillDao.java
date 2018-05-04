package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfInterimAuDo;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowTotalBillDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.dto.AfOverdueBillDto;
import com.ald.fanbei.api.dal.domain.dto.AfOverdueOrderDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQueryNoPage;

/**
 * @类现描述：AfBorrowBillDao
 * @author hexin 2017年2月21日 上午11:13:37
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowBillDao {

	/**
	 * 获取某月账单列表
	 * 
	 * @param query
	 * @return
	 */
	List<AfBorrowBillDo> getMonthBillList(AfBorrowBillQuery query);
	
	/**
	 * 判断是否存在当月正在还款中的订单
	 * 
	 * @param query
	 * @return
	 */
	int existMonthRepayingBill(@Param("userId") Long userId, @Param("billYear") int billYear, @Param("billMonth") int billMonth);

	/**
	 * 获取所有借款账单记录
	 * 
	 * @param query
	 * @return
	 */
	List<AfBorrowBillDo> getBillListByBorrowIdAndStatus(@Param("borrowId") Long borrowId, @Param("status") String status);

	/**
	 * 获取所有借款账单记录
	 * 
	 * @param query
	 * @return
	 */
	List<AfBorrowBillDo> getBillListByIds(@Param("items") List<Long> billIds);

	/**
	 * 获取本期总额
	 * 
	 * @param userId
	 * @param billYear
	 * @param billMonth
	 * @param status
	 * @return
	 */
	public BigDecimal getMonthlyBillByStatus(@Param("userId") Long userId, @Param("billYear") int billYear, @Param("billMonth") int billMonth, @Param("status") String status);

	BigDecimal getMonthlyBillByStatusNew(@Param("userId") Long userId, @Param("billYear") int billYear, @Param("billMonth") int billMonth, @Param("status") String status);

	BigDecimal getMonthlyBillByStatusNewV1(@Param("userId") Long userId,  @Param("status") String status);

	/**
	 * 获取用户全部账单
	 * 
	 * @param userId
	 * @return
	 */
	public List<AfBorrowTotalBillDo> getUserFullBillList(@Param("userId") Long userId);

	/**
	 * 获取账单详情信息
	 * 
	 * @param rid
	 * @return
	 */
	public AfBorrowBillDo getBorrowBillById(@Param("rid") Long rid);

	/**
	 * 获取借款账单总额
	 * 
	 * @param borrowId
	 * @return
	 */
	BigDecimal getBorrowBillByBorrowId(@Param("borrowId") Long borrowId);

	/**
	 * 获取借款账单关联信息
	 * 
	 * @param rid
	 * @return
	 */
	AfBorrowBillDto getBorrowBillDtoById(@Param("rid") Long rid);

	/**
	 * 获取用户某期全部账单
	 * 
	 * @param userId
	 * @param billYear
	 * @param billMonth
	 * @return
	 */
	AfBorrowBillDo getTotalMonthlyBillByUserId(@Param("userId") Long userId, @Param("billYear") int billYear, @Param("billMonth") int billMonth);

	/**
	 * 获取账单金额,本金总额
	 * 
	 * @param ids
	 * @return
	 */
	public AfBorrowBillDo getBillAmountByIds(@Param("ids") List<String> ids);

	/**
	 * 获取培训账单金额,本金总额
	 *
	 * @param ids
	 * @return
	 */
	AfBorrowBillDo getBillTrainAmountByIds(@Param("ids") List<String> ids);

	AfBorrowBillDo getBillHouseAmountByIds(@Param("ids") List<String> ids);

	/**
	 * 变更账单状态
	 * 
	 * @param ids
	 * @return
	 */
	int updateBorrowBillStatusByIds(@Param("ids") List<String> ids, @Param("status") String status, @Param("repaymentId") Long repaymentId);
	
	

	/**
	 * 变更账单状态
	 * 
	 * @param id
	 * @return
	 */
	int updateBorrowBillStatusById(@Param("id") String id, @Param("status") String status, @Param("repaymentId") Long repaymentId, @Param("couponAmount") BigDecimal couponAmount, @Param("jfbAmount") BigDecimal jfbAmountAvg, @Param("rebateAmount") BigDecimal rebateAmountAvg);
	
	/**
	 * 修改账单状态
	 * @param billIds 账单ids
	 * @param status 状态
	 * @return
	 */
	int updateBorrowBillStatusByBillIdsAndStatus(@Param("items")List<Long> billIds, @Param("status") String status);

	/**
	 * 获取未还款账单数量
	 * 
	 * @param year
	 * @param month
	 * @param userId
	 * @return
	 */
	int getUserMonthlyBillNotpayCount(@Param("year") int year, @Param("month") int month, @Param("userId") Long userId);

	/**
	 * 修改总账单状态
	 * 
	 * @param year
	 * @param month
	 * @param userId
	 * @param status
	 * @return
	 */
	int updateTotalBillStatus(@Param("year") int year, @Param("month") int month, @Param("userId") Long userId, @Param("status") String status);

	/**
	 * 获取现金借款的账单金额
	 * 
	 * @param ids
	 * @return
	 */
	AfBorrowBillDo getBillAmountByCashIds(@Param("ids") List<String> ids);

	/**
	 * 获取账单数量
	 * 
	 * @param year
	 * @param month
	 * @param userId
	 * @return
	 */
	int getUserMonthlyBillTotalCount(@Param("year") int year, @Param("month") int month, @Param("userId") Long userId);

	int getBorrowBillWithNoPayByUserId(@Param("userId") Long userId);

	/**
	 * 统一修改所有账单的状态
	 * 
	 * @param borrowId
	 * @param status
	 * @return
	 */
	int updateBorrowBillStatusByBorrowId(@Param("borrowId") Long borrowId, @Param("status") String status);

	/**
	 * 更改未还款账单状态
	 * 
	 * @param borrowId
	 * @param status
	 * @return
	 */
	int updateNotRepayedBillStatus(@Param("borrowId") Long borrowId, @Param("status") String status);

	/**
	 * 获取当前借款所有账单
	 * @param borrowId
	 * @return
	 */
	List<AfBorrowBillDo> getAllBorrowBillByBorrowId(@Param("borrowId") Long borrowId);
	
	/**
	 * 根据借款ID获取已还账单总数
	 * @param borrowId
	 * @return
	 */
	int getPaidBillNumByBorrowId(@Param("borrowId") Long borrowId);
	
	/**
	 * 根据借款Id统计总收益（账单利息，逾期利息，账单手续费，逾期手续费）
	 */
	BigDecimal getSumIncomeByBorrowId(@Param("borrowId") Long borrowId);
	
	/**
	 * 获取总逾期天数
	 * @param borrowId
	 * @return
	 */
	Long getSumOverdueDayByBorrowId(@Param("borrowId") Long borrowId);
	
	/**
	 * 根据借款ID获取该借款的总逾期次数
	 * @param borrowId
	 * @return
	 */
	int getSumOverdueCountByBorrowId(@Param("borrowId") Long borrowId);
	
	/**
	 * 根据借款ID获取该借款账单的最大逾期时间
	 * @author gaojb
	 * @Time 2018年1月5日 下午2:12:16
	 * @param borrowId
	 * @return
	 */
	int getMaxOverdueCountByBorrowId(@Param("borrowId") Long borrowId);
	
	/**
	 * 根据借款id找出逾期未还且逾期天数最大的借款ID
	 * 
	 * @param borrowId
	 * @return
	 */
	Long getOverduedAndNotRepayBillId(@Param("borrowId") Long borrowId);
	
	/**
	 * 根据借款id找出逾期未还且逾期天数最大的借款,并且账单id不为billId
	 * 
	 * @param borrowId
	 * @return
	 */
	AfBorrowBillDo getOverduedAndNotRepayBill(@Param("borrowId") Long borrowId, @Param("billId")Long billId);

	List<AfBorrowBillDo> getNoPayBorrowBillByUserId(@Param("userId") long userId);


	List<AfBorrowBillDo> getAllBorrowNoPayByUserId(@Param("userId") long userId);
	List<HashMap> getBorrowBillNoPaySumByUserId(@Param("userId") long userId);

	List<AfBorrowBillDo> getBorrowBillList(@Param("status") String status,@Param("userId")Long userId);

	AfBorrowBillDo getTotalMonthlyBillByIds(@Param("userId") Long userId,@Param("ids") List<Long > ids );

	List<AfBorrowBillDo> getBorrowBillListY(@Param("userId") Long userId,@Param("billYear") Integer billYear,@Param("billMonth") Integer billMonth);

	AfBorrowBillDo getLastOutBill(@Param("userId")Long userId);

	AfBorrowTotalBillDo getBorrowBillTotalNow(@Param("userId")Long userId,@Param("billYear") Integer billYear,@Param("billMonth") Integer billMonth);
	/**
	 * 查询消费分期逾期order数据，消费分期推送风控使用
	 * @author yuyue
	 * @Time 2017年10月10日 下午4:58:52
	 * @param billIds
	 * @return
	 */
	List<AfOverdueOrderDto> getOverdueDataToRiskByBillIds(@Param("items")List<Long> billIds);

	/**
	 * 查询消费分期逾期bill数据，消费分期推送风控使用
	 * @author yuyue
	 * @Time 2017年10月10日 下午4:59:37
	 * @param billIds
	 * @param orderId
	 * @return
	 */
	List<AfOverdueBillDto> getAfOverdueBillDtoByBillIds(@Param("items")List<Long> billIds, @Param("orderId")Long orderId);

	/**
	 * 根据userId查询消费分期未完成的order数据，消费分期推送风控使用
	 * @author yuyue
	 * @Time 2017年10月10日 下午4:59:53
	 * @param consumerNo
	 * @return
	 */
	List<AfOverdueOrderDto> getOverdueDataToRiskByConsumerNo(@Param("consumerNo")Long consumerNo);

	/**
	 * 查询消费分期未完成的bill数据，消费分期推送风控使用
	 * @author yuyue
	 * @Time 2017年10月10日 下午5:00:14
	 * @param orderId
	 * @return
	 */
	List<AfOverdueBillDto> getAfOverdueBillDtoByConsumerNo(@Param("orderId")Long orderId);

	/**
	 * 根据用户ID查询未还逾期账单总数
	 * @author yuyue
	 * @Time 2017年11月6日 下午4:00:16
	 * @param userId
	 * @return
	 */
	int countNotPayOverdueBill(@Param("userId")Long userId);

	List<AfBorrowBillDo> getNoPayBillByUserId(@Param("userId")Long userId,@Param("gmt_out_day") Date gmt_out_day);

	/**
	 * 更改出账日
	 * @author yuyue
	 * @Time 2017年11月7日 下午1:48:42
	 * @param rid
	 * @param date
	 * @param date2
	 * @param billYear
	 * @param billMonth
	 */
	void updateBillOutDay(@Param("id")Long id,@Param("gmtOutDay")Date gmtOutDay,@Param("gmtPayTime") Date gmtPayTime,@Param("billYear") int billYear,@Param("billMonth") int billMonth);

	String getBillIdsByUserId(@Param("userId")Long userId);

	int updateBorrowBillLockById(@Param("billId")String billId);

	int updateBorrowBillUnLockByIds(@Param("billIds") List<String> billIds);

	/**
	 * 获取用户未付款的逾期账单月数
	 * @author yuyue
	 * @Time 2017年11月10日 下午1:23:26
	 * @param userId
	 * @return
	 */
	int getOverduedMonthByUserId(@Param("userId")Long userId);

	/**
	 * 获取未逾期用户最后还款日
	 * @author yuyue
	 * @Time 2017年11月13日 下午3:42:33
	 * @param userId
	 * @return
	 */
	Date getLastPayDayByUserId(@Param("userId")Long userId);

	/**
	 * 根据条件获取用户账单金额
	 * @author yuyue
	 * @Time 2017年11月15日 下午3:56:51
	 * @param query
	 * @return
	 */
	BigDecimal getUserBillMoneyByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 根据条件获取用户月账单金额
	 * @author yuyue
	 * @Time 2017年11月15日 下午4:56:52
	 * @param query
	 * @return
	 */
	List<AfBorrowBillDo> getUserBillListByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 根据条件查询子账单个数
	 * @author yuyue
	 * @Time 2017年11月20日 下午4:33:48
	 * @param query
	 * @return
	 */
	int countBillByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 根据条件查询子账单详情
	 * @author yuyue
	 * @Time 2017年11月21日 下午1:18:44
	 * @param query
	 * @return
	 */
	List<AfBorrowBillDto> getBillListByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 根据条件查询月账单逾期利息
	 * @author yuyue
	 * @Time 2017年11月21日 下午2:38:14
	 * @param query
	 * @return
	 */
	BigDecimal getUserOverdeuInterestByQuery(AfBorrowBillQueryNoPage query);

	/**
	 * 根据borrowId查询账单利息
	 * @author yuyue
	 * @Time 2017年11月23日 下午2:05:34
	 * @param borrowId
	 * @return
	 */
	BigDecimal getInterestByBorrowId(@Param("borrowId")Long borrowId);

	/**
	 * 根据borrowId查询账单逾期利息
	 * @author yuyue
	 * @Time 2017年11月23日 下午2:14:21
	 * @param borrowId
	 * @return
	 */
	BigDecimal getOverdueInterestByBorrowId(@Param("borrowId")Long borrowId);

	/**
	 * 查询用户历史账单
	 * @author yuyue
	 * @Time 2017年11月24日 下午1:32:02
	 * @param userId
	 * @param begin
	 * @param pageSize
	 * @return
	 */
	List<AfBorrowBillDo> getUserAllMonthBill(@Param("userId")Long userId, @Param("begin")int begin, @Param("pageSize")int pageSize);

	/**
	 * 获取用户还款日
	 * @author yuyue
	 * @Time 2017年12月4日 下午3:22:27
	 * @param userId
	 * @param billYear
	 * @param billMonth
	 * @return
	 */
	Date getPayDayByYearAndMonth(@Param("userId")Long userId,@Param("billYear")int billYear, @Param("billMonth")int billMonth);

	/**
	 * 获取用户临时额度
	 * @param userId
	 * @return
	 */
	AfInterimAuDo selectInterimAmountByUserId(@Param("userId")Long userId);

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
	int getOnRepaymentCountByUserId(@Param("userId")Long userId);

	/**
	 * 获取租赁账单
	 *
	 * @param ids
	 * @return
	 */
	AfBorrowBillDo getBillLeaseAmountByIds(@Param("ids") List<String> ids);


	List<AfBorrowBillDo> getBorrowBillListByStatus(@Param("userId") Long userId,@Param("billYear") Integer billYear,@Param("billMonth") Integer billMonth);

	AfBorrowBillDo getOverdueBorrowBillInfoByUserId(@Param("userId")Long userId);

    int updateBorrowBillFaildWhenNotY(@Param("items")List<Long> billIds);
}
