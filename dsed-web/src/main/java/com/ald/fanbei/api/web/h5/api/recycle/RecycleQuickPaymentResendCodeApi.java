/**
 * 
 */
package com.ald.fanbei.api.web.h5.api.recycle;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsResendSmsRespBo;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;

/**
 * 
 * @类描述：回收快捷支付重新获取验证码
 * @author yanghailong 2018年5月23日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("recycleQuickPaymentResendCodeApi")
public class RecycleQuickPaymentResendCodeApi implements H5Handle {
    @Resource
    UpsUtil upsUtil;
    @Autowired
    GeneratorClusterNo generatorClusterNo;
    
    @Override
	public H5HandleResponse process(Context context) {
    	H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
	
		String tradeNo = ObjectUtils.toString(context.getData("tradeNo"), null);
		
		if (StringUtils.isBlank(tradeNo)) {
		    return new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
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
