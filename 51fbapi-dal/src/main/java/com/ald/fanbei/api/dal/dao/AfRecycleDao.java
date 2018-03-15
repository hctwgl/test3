package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfRecycleDo;
import com.ald.fanbei.api.dal.domain.AfRecycleRatioDo;
import com.ald.fanbei.api.dal.domain.query.AfRecycleExchangeQuery;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import com.ald.fanbei.api.dal.domain.query.AfRecycleRatioQuery;

import java.math.BigDecimal;
import java.util.List;

/**
 * @类描述：有得卖 回收业务
 * @author weiqingeng  2016年2月27日上午9:49:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRecycleDao {

	Integer addRecycleOrder(AfRecycleQuery afRecycleQuery);

	Integer updateRecycleOrder(AfRecycleQuery afRecycleQuery);

	AfRecycleDo getRecycleOrder(AfRecycleQuery afRecycleQuery);

	AfRecycleRatioDo getRecycleReturnRatio();//获取返现系统系数

	List<AfRecycleRatioDo> getRecycleExchangeRatio();//获取翻倍兑换系统系数
}
