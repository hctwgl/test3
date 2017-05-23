/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月27日下午8:53:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRepaymentBorrowCashDao {
	/**
	 * 增加记录
	 * 
	 * @param afRepaymentBorrowCashDo
	 * @return
	 */
	int addRepaymentBorrowCash(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo);

	/**
	 * 更新记录
	 * 
	 * @param afRepaymentBorrowCashDo
	 * @return
	 */
	int updateRepaymentBorrowCash(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo);

	/**
	 * 删除还款记录
	 * 
	 * @param rid
	 * @return
	 */
	int deleteRepaymentBorrowCash(@Param("rid") Long rid);

	/**
	 * 根据借款id获取还款记录
	 * 
	 * @param borrowCashId
	 * @return
	 */
	List<AfRepaymentBorrowCashDo> getRepaymentBorrowCashByBorrowId(@Param("borrowCashId") Long borrowCashId);

	/**
	 * 根据rid获取还款信息
	 * 
	 * @param rid
	 * @return
	 */
	AfRepaymentBorrowCashDo getRepaymentBorrowCashByrid(@Param("rid") Long rid);

	/**
	 * 更加用户id获取还款列表
	 * 
	 * @param userId
	 * @return
	 */
	List<AfRepaymentBorrowCashDo> getRepaymentBorrowCashListByUserId(@Param("userId") Long userId);
	
	AfRepaymentBorrowCashDo getRepaymentByPayTradeNo(@Param("payTradeNo") String payTradeNo);
	
	/**
	 * 根据rid获取还款信息
	 * 
	 * @param rid
	 * @return
	 */
	AfRepaymentBorrowCashDo getLastRepaymentBorrowCashByBorrowId(@Param("borrowCashId") Long borrowCashId);
	
	/**
	 * 已还款金额
	 * @param borrowId
	 * @return
	 */
	BigDecimal getRepaymentAllAmountByBorrowId(@Param("borrowId") Long borrowId);
	
	/**
	 * 查询某一天最后一个订单号
	 * @param orderNoPre
	 * @return
	 */
	String getCurrentLastRepayNo(String orderNoPre);
}
