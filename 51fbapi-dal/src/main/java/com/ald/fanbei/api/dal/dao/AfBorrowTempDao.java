package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowTempDo;

/**
 * 
 * @类描述：
 * @author hexin 2017年3月3日上午11:24:27
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowTempDao {

	/**
    * 增加记录
    * @param afBorrowTempDo
    * @return
    */
    int addBorrowTemp(AfBorrowTempDo afBorrowTempDo);
    
    AfBorrowTempDo getBorrowTempByBorrowId(@Param("borrowId")Long borrowId);
}
