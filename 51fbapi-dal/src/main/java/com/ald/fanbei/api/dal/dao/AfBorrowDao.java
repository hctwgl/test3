package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月9日下午2:03:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowDao {

	/**
	 * 新增借款信息
	 * @param borrow
	 * @return
	 */
	int addBorrow(AfBorrowDo borrow);
	
	/**
	 * 新增借款详情信息
	 * @param borrowBill
	 * @return
	 */
	int addBorrowBill(@Param("billList")List<AfBorrowBillDo> borrowBill);
	
	/**
	 * 通过id获取借款信息
	 * @param id
	 * @return
	 */
	AfBorrowDo getBorrowById(@Param("id")Long id);
	
	/**
	 * 修改借款信息
	 * @param id
	 * @param totalInterest
	 * @param totalPoundage
	 * @return
	 */
	int updateBorrow(@Param("id")Long id,@Param("totalInterest")BigDecimal totalInterest,@Param("totalPoundage")BigDecimal totalPoundage);
	
	/**
	 * 获取最近借款编号
	 * @param current
	 * @return
	 */
	public String getCurrentLastBorrowNo(Date current);
}
