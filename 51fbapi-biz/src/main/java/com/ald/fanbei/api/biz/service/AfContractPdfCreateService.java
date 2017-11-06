package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author guoshuaiqiang 2017年10月27日下午2:26:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfContractPdfCreateService {

    void protocolInstalment(String userName, Integer nper, BigDecimal amount, BigDecimal poundage , Long borrowId, BigDecimal borrowAmount);

    void protocolCashLoan(Long borrowId,BigDecimal borrowAmount,String userName );

    void protocolRenewal(String userName,Long borrowId,Long renewalId,int renewalDay ,BigDecimal renewalAmount);

}
