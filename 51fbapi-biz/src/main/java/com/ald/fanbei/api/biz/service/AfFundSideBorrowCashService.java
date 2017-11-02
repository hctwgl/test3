package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfFundSideBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfFundSideInfoDo;

/**
 * 资金方放款与用户借款记录关联表Service
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-09-29 13:55:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfFundSideBorrowCashService extends ParentService<AfFundSideBorrowCashDo, Long>{

	/**
	 * 用户借款打款成功，和资金方关联处理添加
	 * @param rid
	 * @return
	 */
	public boolean matchFundAndBorrowCash(Long rid);

	/**
	 * 根据借款id获取资金方，如果没有关联资金方，则返回空串
	 * @param borrowId
	 * @return
	 */
	public AfFundSideInfoDo getLenderInfoByBorrowCashId(Long borrowId);

}
