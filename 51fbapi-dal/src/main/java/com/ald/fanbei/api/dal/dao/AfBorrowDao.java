package com.ald.fanbei.api.dal.dao;

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
	int addBorrowBill(List<AfBorrowBillDo> borrowBill);
	
	/**
	 * 新增借款详情信息
	 * @param borrowBill
	 * @return
	 */
	int addBorrowBillInfo(AfBorrowBillDo borrowBill);
	
	/**
	 * 通过id获取借款信息
	 * @param id
	 * @return
	 */
	AfBorrowDo getBorrowById(@Param("id")Long id);
	
	/**
	 * 获取最近借款编号
	 * @param current
	 * @return
	 */
	public String getCurrentLastBorrowNo(String orderNoPre);
	
	int updateBorrowStatus(@Param("id")Long id,@Param("status")String status);
	
	/**
     * 通过订单id获取借款信息
     * @param id
     * @return
     */
    AfBorrowDo getBorrowByOrderId(Long orderId);
    
    /**
     * 通过订单id 并且状态获取借款信息
     * @param id
     * @return
     */
    AfBorrowDo getBorrowByOrderIdAndStatus(@Param("orderId")Long orderId, @Param("status")String status);
	/**
	 * 获取总借款次数
	 * @param userId
	 * @return
	 */
	int getBorrowNumByUserId(@Param("userId") Long userId);
	
	/**
	 * 根据借款编号获取借款信息
	 * @param borrowNo
	 * @return
	 */
	AfBorrowDo getBorrowInfoByBorrowNo(String borrowNo);
}
