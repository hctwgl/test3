package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowInterestDo;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月21日上午9:39:32
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowInterestDao {

	/**
    * 增加记录
    * @param afBorrowInterestDo
    * @return
    */
    int addBorrowInterest(AfBorrowInterestDo afBorrowInterestDo);
    
    /**
     * 获取利息总天数
     * @param borrowId
     * @return
     */
    int getBorrowInterestCountByBorrowId(@Param("billId")Long billId);
}
