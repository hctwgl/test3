package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayInvestorInfoBo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @类描述：
 * @author guoshuaiqiang 2017年10月27日下午2:26:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLegalContractPdfCreateService {

    void protocolLegalInstalment(long userId, BigDecimal borrowAmount, Long orderId);

    void protocolLegalCashLoan(Long borrowId, BigDecimal borrowAmount, long userId);

    void protocolLegalRenewal(long userId, Long borrowId, Long renewalId, int renewalDay, BigDecimal renewalAmount);


}
