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
	public AfBorrowBillDto getBorrowBillById(@Param("rid")Long rid);
}
