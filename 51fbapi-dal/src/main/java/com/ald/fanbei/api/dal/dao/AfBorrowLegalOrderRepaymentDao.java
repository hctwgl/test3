package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderRepaymentDo;

/**
 * Dao
 * 
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:26:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalOrderRepaymentDao extends BaseDao<AfBorrowLegalOrderRepaymentDo, Long> {
	
	/**
	 * 增加记录
	 * @param afBorrowLegalOrderRepaymentDo
	 * @return
	 */
    int addBorrowLegalOrderRepayment(AfBorrowLegalOrderRepaymentDo afBorrowLegalOrderRepaymentDo);
    /**
	 * 更新记录
	 * @param afBorrowLegalOrderRepaymentDo
	 * @return
	 */
    int updateBorrowLegalOrderRepayment(AfBorrowLegalOrderRepaymentDo afBorrowLegalOrderRepaymentDo);
    
    AfBorrowLegalOrderRepaymentDo getLastByOrderId(Long orderId);
}
