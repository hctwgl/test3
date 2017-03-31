/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;

/**
 * @类描述：
 * @author suweili 2017年3月27日下午9:00:27
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRepaymentBorrowCashService {
	Map<String,Object> createRepayment(BigDecimal repaymentAmount,BigDecimal actualAmount,AfUserCouponDto coupon,
			BigDecimal rebateAmount,Long borrow,Long cardId,Long userId,String clientIp,AfUserAccountDo afUserAccountDo);
	public long dealRepaymentSucess(final String outTradeNo, final String tradeNo);
	
	public long dealRepaymentFail(final String outTradeNo, final String tradeNo);
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

}
