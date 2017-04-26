/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;

/**
 * @类描述：
 * @author suweili 2017年3月24日下午4:02:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowCashDao {
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
	    AfBorrowCashDo getBorrowCashByUserId(@Param("userId")Long userId); 
	    /**
	     * 借钱信息
	     * @param userId
	     * @param 开始条数
	     * @return
	     */
	    List<AfBorrowCashDo> getBorrowCashListByUserId(@Param("userId")Long userId,@Param("start")Integer start); 
	    /**
	     * 根据rid获取借款信息
	     * @param rid
	     * @return
	     */
	    AfBorrowCashDo getBorrowCashByrid(@Param("rid")Long rid); 

	    /**
	     * 获取当前最大的借款编号
	     * @param orderNoPre
	     * @return
	     */
	    String getCurrentLastBorrowNo(String orderNoPre);
	    
	    /**
	     * 根据rishOrderNo获取借款信息
	     * @param rishOrderNo
	     * @return
	     */
	    public AfBorrowCashDo getBorrowCashByRishOrderNo(String rishOrderNo);
}
