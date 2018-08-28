/**
 * 
 */
package com.ald.fanbei.api.web.h5.api.jsd;

import com.ald.fanbei.api.biz.service.DsedUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.dao.JsdUserBankcardDao;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：快捷支付确认支付
 * @author chenqiwei 2018年3月29日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("confirmPaymentApi")
public class ConfirmPaymentApi implements DsedH5Handle {


	@Autowired
	@Qualifier("jsdBorrowCashRepaymentService")
	DsedUpsPayKuaijieServiceAbstract jsdBorrowCashRepaymentService;



    @Autowired
    BizCacheUtil bizCacheUtil;

    @Override
    public DsedH5HandleResponse process(Context context) {
		DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");

		String tradeNo = ObjectUtils.toString(context.getData("tradeNo"), null);
		String smsCode = ObjectUtils.toString(context.getData("smsCode"), null);
		if (StringUtils.isBlank(tradeNo) || StringUtils.isBlank(smsCode)) {
			return new DsedH5HandleResponse(3001, FanbeiExceptionCode.JSD_PARAMS_ERROR.getErrorMsg());
		}

		Object beanName = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_BEAN_ID + tradeNo);
		if (beanName == null) {
			// 未获取到缓存数据，支付订单过期
			throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		switch (beanName.toString()) {
			case "jsdBorrowCashRepaymentService":
				map = jsdBorrowCashRepaymentService.doUpsPay(tradeNo, smsCode);
				break;
			default:
				throw new FanbeiException("ups kuaijie not support", FanbeiExceptionCode.UPS_KUAIJIE_NOT_SUPPORT);
		}

		resp.setData(map);

		return resp;
    }
}
