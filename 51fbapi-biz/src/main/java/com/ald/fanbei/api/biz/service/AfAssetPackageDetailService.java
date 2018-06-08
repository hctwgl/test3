package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetPlatUserInfoRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.FanbeiBorrowBankInfoBo;
import com.ald.fanbei.api.dal.domain.AfAssetPackageDetailDo;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;

/**
 * 资产包与债权记录关系Service
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAssetPackageDetailService extends ParentService<AfAssetPackageDetailDo, Long>{

	/**
	 * 批量债权包明细撤回操作
	 * @param orderNos
	 * @param debtType 
	 */
	int batchGiveBackCreditInfo(AfAssetSideInfoDo afAssetSideInfoDo,List<String> orderNos, Integer debtType);

	/**
	 * 非实时债权，更新放款时间
	 * @param orderNos
	 * @param loanTime
	 * @return
	 */
	int addPackageDetailLoanTime(List<String> orderNos, Date loanTime,Integer debtType);

	/**
	 * 单个债权包明细撤回操作
	 * @param orderNo
	 */
	int giveBackCreditInfo(AfAssetSideInfoDo afAssetSideInfoDo,String orderNo);
	
	/**
	 * 根据资产方要求,获取资产方对应的现金贷债权信息
	 * @param edspayGetCreditReqBo
	 * @return
	 */
	List<EdspayGetCreditRespBo> getBorrowCashBatchCreditInfo(FanbeiBorrowBankInfoBo bankInfo,AfAssetSideInfoDo afAssetSideInfoDo,BigDecimal money,Date startTime,Date endTime,BigDecimal minMoney);

	/**
	 * 根据资产方对应债权订单号,获取对应用户信息
	 * @param afAssetSideInfoDo
	 * @param orderNos
	 * @return
	 */
	List<EdspayGetPlatUserInfoRespBo> getBatchPlatUserInfo(AfAssetSideInfoDo afAssetSideInfoDo, List<String> orderNos);

	/**
	 * 根据资产方要求,获取资产方对应的消费分期债权信息
	 * @param edspayGetCreditReqBo
	 * @return
	 */
	List<EdspayGetCreditRespBo> getBorrowBatchCreditInfo(FanbeiBorrowBankInfoBo bankInfo,AfAssetSideInfoDo afAssetSideInfoDo, BigDecimal money,Date startTime, Date endTime);

	List<EdspayGetCreditRespBo> getLoanBatchCreditInfo(FanbeiBorrowBankInfoBo bankInfo,AfAssetSideInfoDo afAssetSideInfoDo, BigDecimal money,Date startTime, Date endTime);

}
