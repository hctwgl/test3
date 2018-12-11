package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.FanbeiBorrowBankInfoBo;
import com.ald.fanbei.api.biz.bo.assetpush.EdspayGetCreditRespBo;
import com.ald.fanbei.api.dal.domain.JsdAssetPackageDetailDo;
import com.ald.fanbei.api.dal.domain.JsdAssetSideInfoDo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Service
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 11:21:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdAssetPackageDetailService extends ParentService<JsdAssetPackageDetailDo, Long>{

    List<EdspayGetCreditRespBo> getXgJsdBatchCreditInfo(FanbeiBorrowBankInfoBo bankInfo, JsdAssetSideInfoDo afAssetSideInfoDo, BigDecimal money, Date startTime, Date endTime, BigDecimal minMoney);


    int batchGiveBackCreditInfo(JsdAssetSideInfoDo afAssetSideInfoDo, List<String> orderNos, Integer debtType);

    int addPackageDetailLoanTime(List<String> orderNos, Date loanTime,Integer debtType);
}
