package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowTotalBillDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;

/**
 * @类现描述：AfBorrowBillDao
 * @author hexin 2017年2月21日 上午11:13:37
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowBillDao {

	/**
	 * 获取某月账单列表
	 * @param query
	 * @return
	 */
	List<AfBorrowBillDo> getMonthBillList(AfBorrowBillQuery query);
	
	/**
	 * 获取所有借款账单记录
	 * @param query
	 * @return
	 */
	List<AfBorrowBillDo> getBillListByBorrowIdAndStatus(@Param("borrowId") Long borrowId, @Param("status") String status);
	
	/**
	 * 获取本期总额
	 * @param userId
	 * @param billYear
	 * @param billMonth
	 * @param status
	 * @return
	 */
	public BigDecimal getMonthlyBillByStatus(@Param("userId")Long userId, @Param("billYear")int billYear,
			@Param("billMonth")int billMonth, @Param("status")String status);
	
	/**
	 * 获取用户全部账单
	 * @param userId
	 * @return
	 */
	public List<AfBorrowTotalBillDo> getUserFullBillList(@Param("userId")
			Long userId);
	
	/**
	 * 获取账单详情信息
	 * @param rid
	 * @return
	 */
	public AfBorrowBillDo getBorrowBillById(@Param("rid")Long rid);
	
	/**
	 * 获取借款账单总额
	 * @param borrowId
	 * @return
	 */
	BigDecimal getBorrowBillByBorrowId(@Param("borrowId")Long borrowId);
	
	/**
	 * 获取借款账单关联信息
	 * @param rid
	 * @return
	 */
	AfBorrowBillDto getBorrowBillDtoById(@Param("rid")Long rid);
	
	/**
	 * 获取用户某期全部账单
	 * @param userId
	 * @param billYear
	 * @param billMonth
	 * @return
	 */
	AfBorrowBillDo getTotalMonthlyBillByUserId(@Param("userId")Long userId,@Param("billYear")int billYear,@Param("billMonth")int billMonth);
	
	/**
	 * 获取账单金额,本金总额
	 * @param ids
	 * @return
	 */
	public AfBorrowBillDo getBillAmountByIds(@Param("ids")List<String> ids);
	
	/**
	 * 变更账单状态
	 * @param ids
	 * @return
	 */
	int updateBorrowBillStatusByIds(@Param("ids")List<String> ids,@Param("status")String status,@Param("repaymentId")Long repaymentId);
	
	/**
	 * 获取未还款账单数量
	 * @param year
	 * @param month
	 * @param userId
	 * @return
	 */
	int getUserMonthlyBillNotpayCount(@Param("year")int year, @Param("month")int month, @Param("userId")Long userId);
	
	/**
	 * 修改总账单状态
	 * @param year
	 * @param month
	 * @param userId
	 * @param status
	 * @return
	 */
	int updateTotalBillStatus(@Param("year")int year, @Param("month")int month, @Param("userId")Long userId,
			@Param("status")String status);
	
	/**
	 * 获取现金借款的账单金额
	 * @param ids
	 * @return
	 */
	AfBorrowBillDo getBillAmountByCashIds(@Param("ids")List<String> ids);
	
	/**
	 * 获取账单数量
	 * @param year
	 * @param month
	 * @param userId
	 * @return
	 */
	int getUserMonthlyBillTotalCount(@Param("year")int year, @Param("month")int month, @Param("userId")Long userId);
	
	int getBorrowBillWithNoPayByUserId(@Param("userId")Long userId);
	
	/**
	 * 统一修改所有账单的状态
	 * @param borrowId
	 * @param status
	 * @return
	 */
	int updateBorrowBillStatusByBorrowId(@Param("borrowId")Long borrowId, @Param("status")String status);
}
