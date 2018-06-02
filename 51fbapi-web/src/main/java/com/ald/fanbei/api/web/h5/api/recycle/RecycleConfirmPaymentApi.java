package com.ald.fanbei.api.web.h5.api.recycle;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.UpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;

/**
 * 
 * @类描述：回收快捷支付确认支付
 * @author yanghailong 2018年5月23日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("recycleConfirmPaymentApi")
public class RecycleConfirmPaymentApi implements H5Handle {

    @Resource
    AfUserBankcardDao afUserBankcardDao;
    
    @Autowired
    @Qualifier("afBorrowRecycleRepaymentService")
    UpsPayKuaijieServiceAbstract afBorrowRecycleRepaymentService;

    @Autowired
    BizCacheUtil bizCacheUtil;

    @Override
	public H5HandleResponse process(Context context) {
    	H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);

		String smsCode = ObjectUtils.toString(context.getData("smsCode"), null);
		String tradeNo = ObjectUtils.toString(context.getData("tradeNo"), null);
	
		if (StringUtils.isBlank(tradeNo) || StringUtils.isBlank(smsCode)) {
		    return new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
	
		Object beanName = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_BEAN_ID + tradeNo);
		if (beanName == null) {
		    // 未获取到缓存数据，支付订单过期
		    throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
		}
	
		Map<String, Object> map = new HashMap<String, Object>();
		switch (beanName.toString()) {
		case "afBorrowRecycleRepaymentService":
		    map = afBorrowRecycleRepaymentService.doUpsPay(tradeNo, smsCode);
		    break;
		default:
		    throw new FanbeiException(FanbeiExceptionCode.UPS_KUAIJIE_NOT_SUPPORT);
		}
	
		resp.setResponseData(map);
	
		return resp;
    }
}
