package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfSupplierOrderSettlementDo;



/**
 *
 * @类描述：自营商城 订单结算
 * @author weiqingeng 2017年12月12日下午10:22:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSupplierOrderSettlementService {

	void dealPayCallback(AfSupplierOrderSettlementDo afSupplierOrderSettlementDo, String tradeState);//处理划账异步回调


}
