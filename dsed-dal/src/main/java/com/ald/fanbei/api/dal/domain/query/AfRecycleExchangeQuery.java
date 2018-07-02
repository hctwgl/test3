package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfRecycleDo;

import java.math.BigDecimal;

/**
 * 
 *@类描述：有得卖 回收业务  兑换
 *@author weiqingeng 2018年2月27日  09:47:57
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfRecycleExchangeQuery extends Page<AfRecycleDo>{

	private static final long serialVersionUID = -722303985401230132L;
	
	private Integer rid;
	private Long uid;//用户id
	private String refOrderId;//有得卖对应的订单号
	private Integer payType;//支付方式
	private BigDecimal settlePrice;//结算价格
	private Integer status;//订单状态 1：待确认 2：待上门 3：待检测 6：待发货 7：待收货 8：待支付 66：已完成 98：终止退回 99：已终止
	private String partnerId;//合作id

}
