/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBorrowCashOverdueDo;

/**
 * @类描述：
 * @author suweili 2017年3月28日下午5:39:59
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowCashOverdueService {
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
	    List<AfBorrowCashOverdueDo> getBorrowCashOverdueListByBorrowId(Long borrowId);
	    
	    /**
	     * 根据用户id获取逾期信息列表
	     * @param userId
	     * @return
	     */
	    List<AfBorrowCashOverdueDo> getBorrowCashOverdueListByUserId(Long userId);
	    /**
	     * 根据rid获取逾期信息列表
	     * @param rid
	     * @return
	     */
	    AfBorrowCashOverdueDo getBorrowCashOverdueByrid(Long rid);
}
