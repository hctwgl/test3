/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @类描述：
 * @author suweili 2017年3月27日下午9:00:27
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRepaymentBorrowCashService {
	Map<String, Object> createRepaymentYiBao(BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, AfUserCouponDto coupon, BigDecimal rebateAmount,
											 Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo,String bankPayType);

	Map<String,Object> createRepayment(BigDecimal jfbAmount,BigDecimal repaymentAmount,BigDecimal actualAmount,AfUserCouponDto coupon,
			BigDecimal rebateAmount,Long borrow,Long cardId,Long userId,String clientIp,AfUserAccountDo afUserAccountDo,String bankPayType,String majiabaoName);
	public long dealRepaymentSucess(final String outTradeNo, final String tradeNo);

	public long dealRepaymentSucess(final String outTradeNo, final String tradeNo,String majiabaoName);

	public long dealRepaymentFail(final String outTradeNo, final String tradeNo,boolean isNeedMsgNotice,String errorMsg,AfRepaymentBorrowCashDo repayment);

	public long dealRepaymentFail(final String outTradeNo, final String tradeNo,boolean isNeedMsgNotice,String errorMsg,AfRepaymentBorrowCashDo repayment,String majiabaoName);
	/**
	 * 增加记录
	 * 
	 * @param afRepaymentBorrowCashDo
	 * @return
	 */
	int addRepaymentBorrowCash(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo);

	/**
	 * 更新记录
	 * 
	 * @param afRepaymentBorrowCashDo
	 * @return
	 */
	int updateRepaymentBorrowCash(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo);

	/**
	 * 删除还款记录
	 * 
	 * @param rid
	 * @return
	 */
	int deleteRepaymentBorrowCash( Long rid);

	/**
	 * 根据借款id获取还款记录
	 * 
	 * @param borrowCashId
	 * @return
	 */
	List<AfRepaymentBorrowCashDo> getRepaymentBorrowCashByBorrowId( Long borrowCashId);

	/**
	 * 根据rid获取还款信息
	 * 
	 * @param rid
	 * @return
	 */
	AfRepaymentBorrowCashDo getRepaymentBorrowCashByrid( Long rid);

	/**
	 * 更加用户id获取还款列表
	 * 
	 * @param userId
	 * @return
	 */
	List<AfRepaymentBorrowCashDo> getRepaymentBorrowCashListByUserId( Long userId);
	
	AfRepaymentBorrowCashDo getLastRepaymentBorrowCashByBorrowId( Long borrowCashId);
	/**
	 * 已还款金额
	 * @param borrowId
	 * @return
	 */
	BigDecimal getRepaymentAllAmountByBorrowId(Long borrowId);
	/**
	 * 还款处理中金额
	 * @param borrowId
	 * @return
	 */
	BigDecimal getRepayingTotalAmountByBorrowId(Long borrowId);
	/**
	 * 查找还款处理中的数量
	 *
	 * @author wangli
	 * @date 2018/4/14 10:02
	 */
	int getRepayingTotalNumByBorrowId(Long borrowId);
	
	/**
	 * 查询某一天最后一个订单号
	 * @param orderNoPre
	 * @return
	 */
	String getCurrentLastRepayNo(String orderNoPre);

	public String dealOfflineRepaymentSucess(final String repayNo,final String borrowNo,final String repayType,final String repayTime,final BigDecimal repayAmount,final BigDecimal restAmount,final String tradeNo,final String isBalance,String isAdmin);

	int updateRepaymentBorrowCashName(Long refId);

    AfRepaymentBorrowCashDo getRepaymentBorrowCashByTradeNo(Long borrowId, String tradeNo);

	String getProcessingRepayNo(Long userId);
}
