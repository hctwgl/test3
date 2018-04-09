/**
 * 
 */
package com.ald.fanbei.api.web.api.pay;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.UpsPayKuaijieServiceAbstract;
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
    UpsPayKuaijieServiceAbstract kuaijieRepaymentServiceAbstract;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

	String smsCode = ObjectUtils.toString(requestDataVo.getParams().get("smsCode"), null);
	String tradeNo = ObjectUtils.toString(requestDataVo.getParams().get("tradeNo"), null);

	if (tradeNo == null || smsCode == null) {
	    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
	}

	Map<String, Object> map = new HashMap<String, Object>();
	kuaijieRepaymentServiceAbstract.doUpsPay(map, tradeNo, smsCode);

	if (map.get("resp") == null ||
		(map.get("resp") != null && !((UpsCollectRespBo) map.get("resp")).isSuccess())) {
	    throw new FanbeiException(FanbeiExceptionCode.UPS_COLLECT_ERROR);
	}
	
	resp.addResponseData("data", map);

	return resp;
    }

}
