package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowDto;

import org.apache.ibatis.annotations.Param;

/**
 * 
 * @类描述：
 * 
 * @author hexin 2017年2月09日下午4:41:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowService {

	/**
	 * 获取还款日
	 * 
	 * @param now
	 * @return
	 */
	Date getReyLimitDate(String billType, Date now);

	/**
	 * 获取某年某月的还款日
	 * 
	 * @param now
	 * @return
	 */
	Date getReyLimitDate(int year, int month);

	/**
	 * 现金借款申请
	 * 
	 * @param userDto
	 * @param money
	 * @param cardId
	 * @return
	 */
	public long dealCashApply(final AfUserAccountDo userDto, final BigDecimal money, final Long cardId);

	/**
	 * 消费分期申请
	 * 
	 * @param userDto
	 *            --
	 * @param amount
	 *            --金额
	 * @param cardId
	 *            --银行卡id
	 * @param goodsId
	 *            --商品id
	 * @param openId
	 *            --商品混淆id
	 * @param name
	 *            --借款名称
	 * @param nper
	 *            --分期数
	 * @return
	 */
	public long dealConsumeApply(AfUserAccountDo userDto, BigDecimal amount, Long cardId, Long goodsId, String openId,
			String numId, String name, int nper);

	/**
	 * 
	 * @param userDto
	 *            需要操作的用户
	 * @param amount
	 *            退回可用额度
	 * @param orderId
	 *            订单id
	 * @return
	 */
	public long dealAgentPayClose(AfUserAccountDo userDto, BigDecimal amount, Long orderId);

	
	/**
	 * 获取最近借款号
	 * 
	 * @param current
	 * @return
	 */
	String getCurrentLastBorrowNo(String orderNoPre);

	/**
	 * 获取当前账单 year month
	 * 
	 * @param now
	 *            当前时间
	 * @return
	 */
	Map<String, Object> getCurrentYearAndMonth(Date now,AfUserOutDayDo afUserOutDayDo);

	/**
	 * 获取本期下期年月 year month
	 * 
	 * @param type
	 *            C-本月 N-next
	 * @return
	 */
	Map<String, Integer> getCurrentTermYearAndMonth(String type, Date now);

	/**
	 * 获取利息总天数
	 * 
	 * @param borrowId
	 * @return
	 */
	int getBorrowInterestCountByBorrowId(Long borrowId);

	/**
	 * 通过借款id获取借款信息
	 * 
	 * @param id
	 * @return
	 */
	AfBorrowDo getBorrowById(Long id);

	/**
	 * 通过订单id获取借款信息
	 * 
	 * @param id
	 * @return
	 */
	AfBorrowDo getBorrowByOrderId(Long orderId);

	/**
	 * 通过订单id 并且状态获取借款信息
	 * 
	 * @param id
	 * @return
	 */
	AfBorrowDo getBorrowByOrderIdAndStatus(Long orderId, String status);

	/**
	 * 获取借款商品信息
	 * 
	 * @param borrowId
	 * @return
	 */
	AfBorrowTempDo getBorrowTempByBorrowId(Long borrowId);

	/**
	 * 现金打款，以及生成账单
	 * 
	 * @param borrow
	 * @param userDto
	 * @param bank
	 */

	void cashBillTransfer(AfBorrowDo borrow, AfUserAccountDo userDto);

	/**
	 * 分期打款
	 * 
	 * @param borrow
	 * @param userDto
	 * @param bank
	 */
	void consumeBillTransfer(AfBorrowDo borrow, AfUserAccountDo userDto);

	/**
	 * 修改借款表状态
	 * 
	 * @param id
	 * @param status
	 * @return
	 */
	int updateBorrowStatus(Long id, String status);

	/**
	 * 计算出重新生产账单金额 借款金额+借款金额*退款日利率*（退款日期-借款日期+1）*（0,1）- 退款金额- 已还账单和 + 优惠和
	 * 
	 * @param borrowId
	 *            借款id
	 * @param refundAmount
	 *            退款金额
	 * @param refundByUser
	 *            是否由客户发起退款 如果是平台原因 则不会收取相关手续费
	 * @return
	 */
	BigDecimal calculateBorrowAmount(Long borrowId, BigDecimal refundAmount, boolean refundByUser);
	
	/**
	 * 生成代付借款以及账单
	 * 
	 * @param userId
	 *            --用户id
	 * @param userName
	 *            -用户名          
	 * @param amount
	 *            --账单金额
	 * @param name
	 *            --借款名称
	 * @return
	 */
	Long dealAgentPayBorrowAndBill(AfBorrowDo borrow, Long userId, String userName, BigDecimal amount, String payType,String orderType);

	Long updateBorrowStatus(AfBorrowDo borrow,String userName,long userId);

	/**
	 * 生成代付借款以及账单
	 * 
	 * @param userId
	 *            --用户id
	 * @param userName
	 *            -用户名          
	 * @param amount
	 *            --账单金额
	 * @param name
	 *            --借款名称
	 * @param nper
	 *            --分期数
	 * @param orderId
	 *            订单id
	 * @param orderNo
	 *            订单编号
	 * @param borrowRate
	 *            订单利率
	 * @param interestFreeJson
	 * 			  免息规则
	 * @return
	 */
	Long dealAgentPayBorrowAndBill(Long userId, String userName, BigDecimal amount, String name, Integer nper, Long orderId,
			String orderNo, String borrowRate, String interestFreeJson,boolean isBack);
	
	/**
	 * 获取总借款次数
	 * @param userId
	 * @return
	 */
	int getBorrowNumByUserId(Long userId);
	
	/**
	 * 代付分期
	 * 
	 * @param userDto
	 *            --
	 * @param amount
	 *            --金额
	 * @param cardId
	 *            --银行卡id
	 * @param name
	 *            --借款名称
	 * @param nper
	 *            --分期数
	 * @param orderId
	 *            订单id
	 * @param orderNo
	 *            订单编号
	 * @param totalNper
	 *            原来订单总分期数 为重新生成账单使用，当第一次生成账单 该值为null
	 * @return
	 */
	Long dealAgentPayConsumeRisk(AfUserAccountDo userDto, BigDecimal amount, String goodsName, int nper, Long rid,
			String orderNo, Integer totalNper);
	
	/**
	 * 根据借款编号获取借款信息
	 * @param borrowNo
	 * @return
	 */
	AfBorrowDo getBorrowInfoByBorrowNo(String borrowNo);

	List<AfBorrowBillDo> buildBorrowBillForNewInterest(AfBorrowDo borrow, String payType);

	int addBorrowBill(List<AfBorrowBillDo> billList);

	List<AfBorrowBillDo> getBorrowBillList(String status, Long userId);

	List<AfBorrowBillDo> getBorrowBillListY(Long userId,Integer billYear,Integer billMonth);

	HashMap addHomeBorrow(final Long orderId, final int nper, final Long userId,Date date);

	/**
	 * 获取用户所有未入账账单
	 * @author yuyue
	 * @Time 2017年11月21日 下午4:59:01
	 * @param userId
	 * @return
	 */
	List<AfBorrowDto> getUserNotInBorrow(Long userId);

	/**
	 * 获取用户所有未入账账单数
	 * @author yuyue
	 * @Time 2017年11月29日 下午6:06:41
	 * @param userId
	 * @return
	 */
	int getUserNotInBorrowCount(Long userId);

	/**
	 * 获取用户所有未入账账单金额
	 * @author yuyue
	 * @Time 2017年11月29日 下午6:09:14
	 * @param userId
	 * @return
	 */
	BigDecimal getUserNotInBorrowMoney(Long userId);

	/**
	 * 根据borrowId获取已还款期数
	 * @author yuyue
	 * @Time 2018年1月5日 下午3:11:01
	 * @param rid
	 * @return
	 */
	Integer countNperRepaymentByBorrowId(Long rid);

	/**
	 * 获取用户统计信息
	 * @param userId 用户id
	 * @return 用户信息
	 */
	HashMap getUserSummary(Long userId);

	AfBorrowDto getBorrowInfoById(Long borrowId);

	

	AfBorrowDo getOverdueBorrowInfoByUserId(Long userId);

}