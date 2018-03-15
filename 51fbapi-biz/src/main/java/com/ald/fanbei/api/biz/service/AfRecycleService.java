package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfRecycleDo;
import com.ald.fanbei.api.dal.domain.AfRecycleRatioDo;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import com.ald.fanbei.api.dal.domain.query.AfRecycleRatioQuery;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 
 * @类描述： 有得卖  回收业务
 * @author weiqingeng 2018年2月27日上午9:55:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRecycleService {

	Integer addRecycleOrder(AfRecycleQuery afRecycleQuery);

	AfRecycleDo getRecycleOrder(AfRecycleQuery afRecycleQuery);

	Map<String,Object> addExchange(Long uid, Integer exchangeAmount, BigDecimal remainAmount);//添加兑换记录

}
