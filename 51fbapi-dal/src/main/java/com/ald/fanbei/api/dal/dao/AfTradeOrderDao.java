package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderDto;
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
	 * @param businessId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	AfTradeOrderStatisticsDto payOrderInfo(@Param("businessId") Long businessId, @Param("startOfDate") Date startDate, @Param("endOfDate") Date endDate);

	/**
	 * 分页查询商圈订单
	 *
	 * @param businessId
	 * @param offset
	 * @param limit
	 * @param startOfDate
	 * @param endOfDate
	 * @param orderStatus
	 * @param withDrawStatus
	 * @return
	 */
	List<AfTradeOrderDto> orderGrid(@Param("businessId") Long businessId, @Param("offset") Integer offset, @Param("limit") Integer limit, @Param("startOfDate") Date startOfDate,
									@Param("endOfDate") Date endOfDate, @Param("orderStatus") String orderStatus, @Param("withDrawStatus") String withDrawStatus);

	/**
	 * 分页查询商圈订单总条数
	 *
	 * @param businessId
	 * @param startOfDate
	 * @param endOfDate
	 * @param orderStatus
	 * @param withDrawStatus
	 * @return
	 */
	Long orderGridTotal(@Param("businessId") Long businessId, @Param("startOfDate") Date startOfDate, @Param("endOfDate") Date endOfDate, @Param("orderStatus") String orderStatus,
						@Param("withDrawStatus") String withDrawStatus);

	/**
	 * 分页查询商圈退款明细订单
	 *
	 * @param businessId
	 * @param offset
	 * @param limit
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<AfTradeOrderDto> refundGrid(@Param("businessId") Long businessId, @Param("offset") Integer offset, @Param("limit") Integer limit, @Param("startDate") Date startDate,
									 @Param("endDate") Date endDate);

	/**
	 * 分页查询商圈退款明细订单总条数
	 *
	 * @param businessId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Long refundGridTotal(@Param("businessId") Long businessId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	/**
	 * 获取所有可提现商户订单
	 *
	 * @param businessId
	 * @param canWithDrawDate
	 * @return
	 */
	List<AfTradeOrderDto> getCanWithDrawList(@Param("businessId") Long businessId, @Param("canWithDrawDate") Date canWithDrawDate);

}
