package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderStatisticsDto;
import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfTradeOrderDo;

/**
 * 商圈订单扩展表Dao
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:31 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTradeOrderDao extends BaseDao<AfTradeOrderDo, Long> {

	int updateStatusByIds(@Param("items") List<Long> ids, @Param("status") String status);

	/**
	 * 获取时间点前的可提现总额
	 *
	 * @param businessId
	 *            商户主键
	 * @param canWithDrawDate
	 *            时间点
	 * @return
	 */
	BigDecimal getCanWithDrawMoney(@Param("businessId") Long businessId, @Param("canWithDrawDate") Date canWithDrawDate);

	/**
	 * 获取时间点前的不可提现总额
	 *
	 * @param businessId
	 *            商户主键
	 * @param cannotWithDrawDate
	 *            时间点
	 * @return
	 */
	BigDecimal getCannotWithDrawMoney(@Param("businessId") Long businessId, @Param("cannotWithDrawDate") Date cannotWithDrawDate);
	/**
	 * 查询一段时间的订单合计
	 *
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	AfTradeOrderStatisticsDto payOrderInfo(@Param("businessId") Long businessId, @Param("startOfDate") Date startDate, @Param("endOfDate") Date endDate);
}
