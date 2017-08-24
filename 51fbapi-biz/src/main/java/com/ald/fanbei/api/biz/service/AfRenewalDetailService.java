package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

/**
 * @类描述：
 * 
 * @author fumeiai 2017年5月19日 20:02:52
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRenewalDetailService {

	/**
	 * 收取手续费+利息+滞纳金，并创建续期记录
	 * 
	 * @return
	 */
	Map<String, Object> createRenewal(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo);

	public long dealRenewalSucess(String outTradeNo, String tradeNo);

	public long dealRenewalFail(final String outTradeNo, final String tradeNo);

	/**
	 * 续借信息
	 * 
	 * @param borrowId
	 * @param 开始条数
	 * @return
	 */
	public List<AfRenewalDetailDo> getRenewalListByBorrowId(Long borrowId, Integer start);

	/**
	 * 续借详情
	 * 
	 * @param rId
	 * @return
	 */
	public AfRenewalDetailDo getRenewalDetailByRenewalId(Long rId);
	
	/**
	 * 获取最新的续借信息
	 * 
	 * @param borrowId
	 * @return
	 */
	public AfRenewalDetailDo getRenewalDetailByBorrowId(Long borrowId);
	
}
