package com.ald.fanbei.api.web.api.user;


import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.ContractPdfThreadPool;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.EviDoc;
import com.ald.fanbei.api.common.EsignPublicInit;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfContractPdfDao;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;


/**
 * @author guoshuaiqiang 2017年10月27日下午1:49:07
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowCashProtocolApi")
public class GetBorrowCashProtocolApi implements ApiHandle {

    @Resource
    AfUserService afUserService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfESdkService afESdkService;
    @Resource
    AfRescourceLogService afRescourceLogService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfRenewalDetailDao afRenewalDetailDao;
    @Resource
    OssFileUploadService ossFileUploadService;
    @Resource
    AfContractPdfDao afContractPdfDao;
    @Resource
    EviDoc eviDoc;
    @Resource
    private EsignPublicInit esignPublicInit;
    @Resource
    private ContractPdfThreadPool contractPdfThreadPool;

    private static final String src = "F:/doc/";

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String protocolCashType = ObjectUtils.toString(requestDataVo.getParams().get("protocolCashType"), "").toString();
        if (null != protocolCashType && !"".equals(protocolCashType)) {
            try {
                /*if ("1".equals(protocolCashType)) {//借款协议
                    protocolCashLoan(requestDataVo, resp);
                } else if ("2".equals(protocolCashType)) {//分期服务协议
                    protocolFenqiService(requestDataVo, resp);
                } else if ("3".equals(protocolCashType)) {//续借协议
                    protocolRenewal(requestDataVo, resp);
                }*/
                contractPdfThreadPool.protocolCashLoanPdf(531l, BigDecimal.valueOf(500), 97l);
            } catch (Exception e) {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
            }
        }
        return resp;
    }
}
