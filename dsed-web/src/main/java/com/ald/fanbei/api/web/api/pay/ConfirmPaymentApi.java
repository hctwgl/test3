/**
 * 
 */
package com.ald.fanbei.api.web.api.pay;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.UpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：快捷支付确认支付
 * @author chenqiwei 2018年3月29日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("confirmPaymentApi")
public class ConfirmPaymentApi implements ApiHandle {

    @Resource
    AfUserBankcardDao afUserBankcardDao;

    @Autowired
    @Qualifier("afRepaymentService")
    UpsPayKuaijieServiceAbstract afRepaymentAbstract;

    @Autowired
    @Qualifier("afBorrowLegalRepaymentV2Service")
    UpsPayKuaijieServiceAbstract afBorrowLegalRepaymentV2Abstract;

    @Autowired
    @Qualifier("afBorrowLegalRepaymentService")
    UpsPayKuaijieServiceAbstract afBorrowLegalRepaymentService;

    //loanAllRepayDoApi 相同逻辑处理（业务数据不同，从redis获取）
    @Autowired
    @Qualifier("afLoanRepaymentService")
    UpsPayKuaijieServiceAbstract afLoanRepaymentService;

    @Autowired
    @Qualifier("afRenewalLegalDetailV2Service")
    UpsPayKuaijieServiceAbstract afRenewalLegalDetailV2Service;

    @Autowired
    @Qualifier("afOrderService")
    UpsPayKuaijieServiceAbstract afOrderService;

    @Autowired
    @Qualifier("afOrderCombinationPayService")
    UpsPayKuaijieServiceAbstract afOrderCombinationPayService;

    @Autowired
    BizCacheUtil bizCacheUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

	String smsCode = ObjectUtils.toString(requestDataVo.getParams().get("smsCode"), null);
	String tradeNo = ObjectUtils.toString(requestDataVo.getParams().get("tradeNo"), null);

	if (StringUtils.isBlank(tradeNo) || StringUtils.isBlank(smsCode)) {
	    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
	}

	Object beanName = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_BEAN_ID + tradeNo);
	if (beanName == null) {
	    // 未获取到缓存数据，支付订单过期
	    throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
	}

	Map<String, Object> map = new HashMap<String, Object>();
	switch (beanName.toString()) {
	case "afRepaymentService":
	    map = afRepaymentAbstract.doUpsPay(tradeNo, smsCode);
	    break;
	case "afBorrowLegalRepaymentV2Service":
	    map = afBorrowLegalRepaymentV2Abstract.doUpsPay( tradeNo, smsCode);
	    break;
	case "afBorrowLegalRepaymentService":
	    map = afBorrowLegalRepaymentService.doUpsPay(tradeNo, smsCode);
	    break;
	case "afLoanRepaymentService":
	    map = afLoanRepaymentService.doUpsPay(tradeNo, smsCode);
	    break;
	case "afRenewalLegalDetailV2Service":
	    map = afRenewalLegalDetailV2Service.doUpsPay(tradeNo, smsCode);
	    break;
	case "afOrderService":
	    map = afOrderService.doUpsPay(tradeNo, smsCode);
	    break;
	case "afOrderCombinationPayService":
	    map = afOrderCombinationPayService.doUpsPay(tradeNo, smsCode);
	    break;
	default:
	    throw new FanbeiException(FanbeiExceptionCode.UPS_KUAIJIE_NOT_SUPPORT);
	}

	resp.setResponseData(map);

	return resp;
    }
}
