package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBorrowLogDo;

/**
 * 
 * @类描述：借款审核日志表
 * @author hexin 2017年3月15日下午20:42:38
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLogDao {

	/**
    * 增加记录
    * @param afBorrowLogDo
    * @return
    */
    int addBorrowLog(AfBorrowLogDo afBorrowLogDo);
   /**
    * 更新记录
    * @param afBorrowLogDo
    * @return
    */
    int updateBorrowLog(AfBorrowLogDo afBorrowLogDo);
}
