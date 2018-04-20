/**
 * 
 */
package com.ald.fanbei.api.web.api.pay;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsResendSmsRespBo;
import com.ald.fanbei.api.biz.service.AfTradeCodeInfoService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：快捷支付重新获取验证码
 * @author chenqiwei 2018年3月29日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("quickPaymentResendCodeApi")
public class QuickPaymentResendCodeApi implements ApiHandle {
    @Resource
    UpsUtil upsUtil;
    @Resource
    AfUserBankcardDao afUserBankcardDao;
    @Resource
    AfTradeCodeInfoService afTradeCodeInfoService;

    @Autowired
    GeneratorClusterNo generatorClusterNo;
    
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

	String tradeNo = requestDataVo.getParams().get("tradeNo").toString();
	if (StringUtils.isBlank(tradeNo)) {
	    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
	}
	String orderNo = generatorClusterNo.getRepaymentNo(new Date(), BankPayChannel.KUAIJIE.getCode());
	UpsResendSmsRespBo respBo = upsUtil.quickPayResendSms(tradeNo,orderNo);

	if (!respBo.isSuccess()) {
	    throw new FanbeiException(respBo.getRespDesc());
	}
	resp.addResponseData("data", respBo);

	return resp;
    }

}
