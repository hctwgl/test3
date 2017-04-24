package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月22日下午13:52:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRepaymentService {

	/**
	 * 新增还款记录
	 * @param repaymentAmount
	 * @param actualAmount
	 * @param couponId
	 * @param couponAmount
	 * @param rebateAmount
	 * @param billIds
	 * @param cardId
	 * @return
	 */
	Map<String,Object> createRepayment(BigDecimal repaymentAmount,BigDecimal actualAmount,AfUserCouponDto coupon,
			BigDecimal rebateAmount,String billIds,Long cardId,Long userId,AfBorrowBillDo billDo,String clientIp,AfUserAccountDo afUserAccountDo);
	
	long dealRepaymentSucess(String outTradeNo,String tradeNo);
	
	/**
	 * 获取最近还款编号
	 * @param current
	 * @return
	 */
	String getCurrentLastRepayNo(String orderNoPre);
	
	/**
     * 通过id获取详情
     * @param rid
     * @return
     */
    AfRepaymentDo getRepaymentById(Long rid);
}
