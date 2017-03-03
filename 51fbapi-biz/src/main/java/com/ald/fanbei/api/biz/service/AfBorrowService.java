package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfBorrowTempDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月09日下午4:41:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowService {

	/**
	 * 获取还款日
	 * @param now
	 * @return
	 */
	Date getReyLimitDate(Date now);
	
	/**
	 * 获取某年某月的还款日
	 * @param now
	 * @return
	 */
	Date getReyLimitDate(int year,int month);
	
	/**
	 * 现金借款申请
	 * @param userDto
	 * @param money
	 * @param cardId
	 * @return
	 */
	public long dealCashApply(final AfUserAccountDto userDto,final BigDecimal money,final Long cardId);
	
	/**
	 * 消费分期申请
	 * @param userDto --
	 * @param amount --金额
	 * @param cardId --银行卡id
	 * @param goodsId --商品id
	 * @param openId --商品混淆id
	 * @param name --借款名称
	 * @param nper --分期数
	 * @return
	 */
	public long dealConsumeApply(AfUserAccountDto userDto,BigDecimal amount,Long cardId,Long goodsId,String openId,String numId,String name,int nper);
	
	/**
	 * 获取最近借款号
	 * @param current
	 * @return
	 */
	String getCurrentLastBorrowNo(Date current);
	
	/**
	 * 获取本月账单 year month 
	 * @param type C-本月 N-next
	 * @return
	 */
	Map<String,Integer> getCurrentYearAndMonth(String type,Date now);
	
	/**
     * 获取利息总天数
     * @param borrowId
     * @return
     */
    int getBorrowInterestCountByBorrowId(Long borrowId);
    
    /**
     * 通过借款id获取借款信息
     * @param id
     * @return
     */
    AfBorrowDo getBorrowById(Long id);
    
    /**
     * 获取借款商品信息
     * @param borrowId
     * @return
     */
    AfBorrowTempDo getBorrowTempByBorrowId(Long borrowId);
}
