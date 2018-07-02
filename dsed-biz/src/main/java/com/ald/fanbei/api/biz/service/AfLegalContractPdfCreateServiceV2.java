package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayInvestorInfoBo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author guoshuaiqiang 2017年10月27日下午2:26:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLegalContractPdfCreateServiceV2 {

    void protocolLegalRenewal(long userId, Long borrowId, Long renewalId, int renewalDay, BigDecimal renewalAmount);

    void platformServiceProtocol(Long borrowId, String type, BigDecimal poundage, Long userId);

    void goodsInstalmentProtocol(Long borrowId, String type, Long userId);

    String getProtocalLegalByType(Integer debtType, String orderNo, String protocolUrl, String borrowerName, List<EdspayInvestorInfoBo> investorList) throws IOException;

    String getProtocalLegalWithOutLenderByType(Integer debtType, String orderNo, String protocolUrl, String borrowerName, List<EdspayInvestorInfoBo> investorList) throws IOException;

    String getProtocalLegalByTypeWithoutSeal(Integer debtType, String orderNo) throws IOException;

    String whiteLoanProtocolPdf(Integer debtType, String loanNo) throws IOException;

    String leaseProtocolPdf(Map<String,Object> data,Long userId ,Long orderId) throws IOException;

    String receptProtocolPdf(Map<String,Object> data)throws IOException;

}
