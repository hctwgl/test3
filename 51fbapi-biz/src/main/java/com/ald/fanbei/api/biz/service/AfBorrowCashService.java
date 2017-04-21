/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;

/**
 * @类描述：
 * @author suweili 2017年3月24日下午5:02:52
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowCashService {
	  /**
	    * 增加记录
	    * @param afBorrowCashDo
	    * @return
	    */
	    int addBorrowCash(AfBorrowCashDo afBorrowCashDo);
	   /**
	    * 更新记录
	    * @param afBorrowCashDo
	    * @return
	    */
	    int updateBorrowCash(AfBorrowCashDo afBorrowCashDo);
	    /**
	     * 获取最近一次借钱信息
	     * @param userId
	     * @return
	     */
	    AfBorrowCashDo getBorrowCashByUserId(Long userId); 
	    /**
	     * 借钱信息
	     * @param userId
	     * @param 开始条数
	     * @return
	     */
	    List<AfBorrowCashDo> getBorrowCashListByUserId(Long userId,Integer start); 
	    /**
	     * 根据rid获取借款信息
	     * @param rid
	     * @return
	     */
	    AfBorrowCashDo getBorrowCashByrid(Long rid); 
	    /**
		 * 获取最近借款号
		 * @param current
		 * @return
		 */
		String getCurrentLastBorrowNo(Date current);
}
