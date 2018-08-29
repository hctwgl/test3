/**
 * 
 */
package com.ald.fanbei.api.web.h5.api.jsd;

import com.ald.fanbei.api.biz.bo.UpsResendSmsRespBo;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.SmsCodeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 
 * @类描述：快捷支付重新获取验证码
 * @author chenqiwei 2018年3月29日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("quickPaymentResendCodeApi")
public class SendSmsCodeApi implements DsedH5Handle {
    @Resource
    UpsUtil upsUtil;


    @Autowired
    GeneratorClusterNo generatorClusterNo;
    
    @Override
    public DsedH5HandleResponse process(Context context) {

	DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
	String busiFlag = ObjectUtils.toString(context.getData("busiFlag"), null);
	String type = ObjectUtils.toString(context.getData("type"), null);
	Long userId=context.getUserId();
	UpsResendSmsRespBo respBo=null;

	if (StringUtils.isBlank(busiFlag)) {
		return new DsedH5HandleResponse(3001, FanbeiExceptionCode.JSD_PARAMS_ERROR.getErrorMsg());
	}
	if(SmsCodeType.REPAY.getCode().equals(type)){
		String orderNo = generatorClusterNo.getRepaymentNo(new Date(), BankPayChannel.KUAIJIE.getCode());
		respBo = upsUtil.quickPayResendSms(busiFlag,orderNo);
		if (!respBo.isSuccess()) {
			throw new FanbeiException(respBo.getRespDesc());
		}

	}



	resp.setData(respBo);

	return resp;
    }

}
