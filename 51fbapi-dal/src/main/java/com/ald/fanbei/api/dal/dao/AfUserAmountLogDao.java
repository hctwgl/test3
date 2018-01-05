package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserAmountDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountLogDo;
import com.ald.fanbei.api.dal.domain.query.AfUserAmountQuery;

/**
 * @author honghzengpei 2017/11/21 15:05
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAmountLogDao {
    /**
     * 增加记录
     * @param afUserAmountLogDo
     * @return
     */
    int addUserAmountLog(AfUserAmountLogDo afUserAmountLogDo);

	/**
	 * 根据source_id查询amount_log
	 * @author yuyue
	 * @Time 2017年11月29日 下午4:39:27
	 * @param amountId
	 * @return
	 */
	List<AfUserAmountLogDo> getAmountLogByAmountId(@Param("sourceId")Long sourceId);

}
