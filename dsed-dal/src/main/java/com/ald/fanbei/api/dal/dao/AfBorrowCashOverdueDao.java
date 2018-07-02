/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowCashOverdueDo;

/**
 * @类描述：
 * @author suweili 2017年3月28日下午5:28:16
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowCashOverdueDao {
	  /**
	    * 增加记录
	    * @param afBorrowCashOverdueDo
	    * @return
	    */
	    int addBorrowCashOverdue(AfBorrowCashOverdueDo afBorrowCashOverdueDo);
	   /**
	    * 更新记录
	    * @param afBorrowCashOverdueDo
	    * @return
	    */
	    int updateBorrowCashOverdue(AfBorrowCashOverdueDo afBorrowCashOverdueDo);
	    /**
	     * 根据借钱id获取逾期信息列表
	     * @param borrowId
	     * @return
	     */
	    List<AfBorrowCashOverdueDo> getBorrowCashOverdueListByBorrowId(@Param("borrowId")Long borrowId);
	    
	    /**
	     * 根据用户id获取逾期信息列表
	     * @param userId
	     * @return
	     */
	    List<AfBorrowCashOverdueDo> getBorrowCashOverdueListByUserId(@Param("userId")Long userId);
	    /**
	     * 根据rid获取逾期信息列表
	     * @param rid
	     * @return
	     */
	    AfBorrowCashOverdueDo getBorrowCashOverdueByrid(@Param("rid")Long rid);

	    /**
	     * 根据天数查询逾期数据
	     * 
	     * @param borrowId
	     * @return 
	     * **/
	    BigDecimal getAfBorrowCashOverdueDoByBorrowId(@Param("borrowId")long borrowId,@Param("days")long days);
	    
	    
	    /**
	     * 根据borrowId查询逾期金額
	     * 
	     * @param borrowId
	     * @return 
	     * **/
	    BigDecimal getOverdueAmountByBorrowId(@Param("borrowId")long borrowId);
	    
} 
