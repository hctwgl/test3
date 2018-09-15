
/**
 * 
 */
package com.ald.fanbei.api.web.h5.api.jsd;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.ups.UpsAuthSignRespBo;
import com.ald.fanbei.api.biz.bo.ups.UpsResendSmsRespBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderRepaymentService;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.SmsCodeType;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;

/**
 * 
 * @类描述：快捷支付重新获取验证码
 * @author chenqiwei 2018年3月29日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("sendSmsCodeApi")
public class SendSmsCodeApi implements JsdH5Handle {
    @Resource
    UpsUtil upsUtil;


    @Autowired
    GeneratorClusterNo generatorClusterNo;

    @Resource
	private JsdUserBankcardService jsdUserBankcardService;

    @Resource
	private JsdUserService jsdUserService;
    @Resource
    private JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    private JsdBorrowLegalOrderRepaymentService jsdBorrowLegalOrderRepaymentService;
    
    @Override
    public JsdH5HandleResponse process(Context context) {

    	JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
	String busiFlag = ObjectUtils.toString(context.getData("busiFlag"), null);
	String type = ObjectUtils.toString(context.getData("type"), null);
	Long userId=context.getUserId();
	UpsResendSmsRespBo respBo=null;

	if (StringUtils.isBlank(busiFlag)) {
		return new JsdH5HandleResponse(3001, BizExceptionCode.JSD_PARAMS_ERROR.getErrorMsg());
	}
	if(SmsCodeType.REPAY.getCode().equals(type)){
		JsdBorrowCashRepaymentDo repaymentDo=jsdBorrowCashRepaymentService.getByTradeNoXgxy(busiFlag);
		JsdBorrowLegalOrderRepaymentDo legalOrderRepaymentDo=jsdBorrowLegalOrderRepaymentService.getByTradeNoXgxy(busiFlag);
		if(repaymentDo!=null){
			busiFlag=repaymentDo.getTradeNo();
		}else {
			busiFlag=legalOrderRepaymentDo.getTradeNo();
		}
		String orderNo = generatorClusterNo.getRepaymentBorrowCashNo(BankPayChannel.KUAIJIE.getCode());
		respBo = upsUtil.quickPayResendSms(busiFlag,orderNo);
		if (!respBo.isSuccess()) {
			throw new BizException(respBo.getRespDesc());
		}

	}else if(SmsCodeType.BIND.getCode().equals(type)){


		JsdUserBankcardDo userBankcardDo=jsdUserBankcardService.getByBindNo(busiFlag);
		//默认赋值为借记卡
		String cardType = "00";
		JsdUserDo userDo=jsdUserService.getById(userId);
		//调用ups
		UpsAuthSignRespBo upsResult = upsUtil.authSign(userId.toString(), userDo.getRealName(), userBankcardDo.getMobile(), userDo.getIdNumber(), userBankcardDo.getBankCardNumber(), "02",
				userBankcardDo.getBankCode(),cardType,userBankcardDo.getValidDate(),userBankcardDo.getSafeCode());

		if(!upsResult.isSuccess()){
			return new JsdH5HandleResponse(1542, BizExceptionCode.AUTH_BINDCARD_ERROR.getDesc());
		}else if(!"10".equals(upsResult.getNeedCode())){
			return new JsdH5HandleResponse(1567, BizExceptionCode.AUTH_BINDCARD_SMS_ERROR.getErrorMsg());
		}
	}else if(SmsCodeType.DELAY.getCode().equals(type)){
		String orderNo = generatorClusterNo.getJsdRenewalNo();
		JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalService.getByTradeNoXgxy(busiFlag);
		respBo = upsUtil.quickPayResendSms(renewalDo.getTradeNo(),orderNo);
		if (!respBo.isSuccess()) {
			throw new BizException(respBo.getRespDesc());
		}
	}


	resp.setData(respBo);

	return resp;
    }

}
