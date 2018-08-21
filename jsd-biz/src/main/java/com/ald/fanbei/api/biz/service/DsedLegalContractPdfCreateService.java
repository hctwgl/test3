package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.EdspayInvestorInfoBo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author guoshuaiqiang 2017年10月27日下午2:26:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLegalContractPdfCreateService {

    String getProtocalLegalByType(Integer debtType, String orderNo, String protocolUrl, String borrowerName, List<EdspayInvestorInfoBo> investorList) throws IOException;

    String getProtocalLegalWithOutLenderByType(Integer debtType, String orderNo, String protocolUrl, String borrowerName, List<EdspayInvestorInfoBo> investorList) throws IOException;

    String whiteLoanProtocolPdf(Integer debtType, String loanNo) throws IOException;

}