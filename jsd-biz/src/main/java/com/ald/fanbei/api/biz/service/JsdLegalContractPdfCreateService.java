package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.EdspayInvestorInfoBo;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;

/**
 * 
 * @类描述：
 * @author guoshuaiqiang 2017年10月27日下午2:26:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdLegalContractPdfCreateService {

    void platformServiceSellProtocol(String tradeNoXgxy) throws IOException, DocumentException;

    void platformServiceBeheadProtocol(String tradeNoXgxy) throws IOException, DocumentException;


}
