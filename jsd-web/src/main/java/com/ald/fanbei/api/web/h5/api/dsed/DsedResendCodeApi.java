/**
 * 
 */
package com.ald.fanbei.api.web.h5.api.dsed;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsAuthSignRespBo;
import com.ald.fanbei.api.biz.bo.UpsResendSmsRespBo;
import com.ald.fanbei.api.biz.service.DsedUserBankcardService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.SmsCodeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;

/**
 * 
 * @类描述：快捷支付重新获取验证码
 * @author chefeipeng 2018年3月29日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("dsedResendCodeApi")
public class DsedResendCodeApi implements DsedH5Handle {
    @Resource
    UpsUtil upsUtil;
    @Autowired
    GeneratorClusterNo generatorClusterNo;
	@Resource
	private DsedUserService dsedUserService;

	@Resource
	private DsedUserBankcardService dsedUserBankcardService;


    @Override
    public DsedH5HandleResponse process(Context context) {
    	DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
		String busiFlag = ObjectUtils.toString(context.getData("busiFlag"), null);
		String type = ObjectUtils.toString(context.getData("type"), null);
		String openId = ObjectUtils.toString(context.getData("userId"), null);

		if(StringUtil.equals(SmsCodeType.REPAY.getCode(),type)){
			if (StringUtils.isBlank(busiFlag)) {
				return new DsedH5HandleResponse(9999, "参数错误");
			}
			String orderNo = generatorClusterNo.getLoanNo(new Date());
			UpsResendSmsRespBo respBo = upsUtil.quickPayResendSms(busiFlag,orderNo);

			if (!respBo.isSuccess()) {
				throw new FanbeiException(respBo.getRespDesc());
			}
			resp.setData(respBo);
		}else {
			DsedUserDo user=dsedUserService.getByOpenId(openId);
			//判断是否已经被绑定
			DsedUserBankcardDo userBankcardDo=dsedUserBankcardService.getById(Long.valueOf(busiFlag));
			//默认赋值为借记卡
			String cardType = "00";

			//调用ups
			UpsAuthSignRespBo upsResult = upsUtil.authSign(user.getRid().toString(), user.getRealName(), userBankcardDo.getMobile(), user.getIdNumber(), userBankcardDo.getBankCardNumber(), "02",
					userBankcardDo.getBankCode(),cardType,userBankcardDo.getValidDate(),userBankcardDo.getSafeCode());

			if(!upsResult.isSuccess()){
				return new DsedH5HandleResponse(1542, FanbeiExceptionCode.AUTH_BINDCARD_ERROR.getDesc());
			}else if(!"10".equals(upsResult.getNeedCode())){
				return new DsedH5HandleResponse(1567, FanbeiExceptionCode.AUTH_BINDCARD_SMS_ERROR.getErrorMsg());
			}

		}

		return resp;
    }

}
