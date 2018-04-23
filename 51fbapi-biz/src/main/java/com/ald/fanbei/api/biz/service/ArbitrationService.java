package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.ArbitrationRespBo;
import com.ald.fanbei.api.dal.domain.AfArbitrationDo;

/**
 * @类描述：在线仲裁系统Service
 * @author fanmanfu
 * @version 创建时间：2018年4月13日 上午11:05:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public interface ArbitrationService {

	
	/**
	 * 在线仲裁系统
	 * 3.1获取案件订单信息【GETORDERINFO】
	 * @param borrowNo
	 * 
	 * @return 
	 * 
	 * **/
	ArbitrationRespBo getOrderInfo(String borrowNo);
	
	
	/**
	 * 在线仲裁系统
	 * 3.2获取案件订单相关金额【GETFUNDINFO】
	 * @param borrowNo
	 * 
	 * @return 
	 * 
	 * **/
	ArbitrationRespBo getFundInfo(String borrowNo);
	
	/**
	 * 在线仲裁系统
	 * 3.5获取案件订单相关当事人信息【GETLITIGANTS】
	 * @param borrowNo
	 * 
	 * @return 
	 * 
	 * **/
	ArbitrationRespBo getLitiGants(String borrowNo, String type);
	
	/**
	 * 在线仲裁系统
	 * 3.6获取案件订单相关借款协议【GETCREDITAGREEMENT】
	 * @param borrowNo
	 * 
	 * @return 
	 * 
	 * **/
	ArbitrationRespBo getCreditAgreement(String borrowNo);
	
	
	/**
	 * 在线仲裁系统
	 * 3.7获取案件订单相关借款信息【GETCREDITINFO】
	 * @param borrowNo
	 * 
	 * @return 
	 * 
	 * **/
	ArbitrationRespBo getCreditInfo(String borrowNo);
	
	
	/**
	 * 在线仲裁系统
	 * 3.8获取案件订单相关还款信息【GETREFUNDINFO】
	 * @param borrowNo
	 * 
	 * @return 
	 * 
	 * **/
	ArbitrationRespBo getRefundInfo(String borrowNo);

	/**
	 * 在线仲裁系统
	 * 3.9	获取案件订单相关打款凭证【GETPAYVOUCHER】
	 * @param loanBillNo
	 * @return
	 */
	ArbitrationRespBo getPayVoucher(String loanBillNo);
	
	int saveRecord(AfArbitrationDo afArbitrationDo);
	
	 int updateByloanBillNo(AfArbitrationDo afArbitrationDo);

	AfArbitrationDo getByBorrowNo(String borrowNo);
}
