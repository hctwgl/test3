package com.ald.fanbei.api.web.h5.api.jsd;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.ProtocolType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetLoanProtocolParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * @author ZJF 2018年09月05日 10:46:23
 * @类描述：获取协议列表接口
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("dsedGetLoanProtocolApi")
@Validator("getLoanProtocolParam")
public class GetLoanProtocolApi implements JsdH5Handle {

	@Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdUserService jsdUserService;

    private static String notifyHost = null;
    
    @Override
    public JsdH5HandleResponse process(Context context) {
    	JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
     // 获取客户端请求参数
        GetLoanProtocolParam param = (GetLoanProtocolParam) context.getParamEntity();
        
        String uid, amount, nper, loanRemark = "tmp";
        if(StringUtils.isNotBlank(param.bizNo)) {
        	if(ProtocolType.BORROW.name().equals(param.type)) {
        		JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(param.bizNo);
        		if(cashDo == null) {
        			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
        		}
        		uid = cashDo.getUserId().toString();
        		amount = cashDo.getAmount().toString();
                nper = "1";
        	}else {
        		logger.warn("Don't support " + param.type + " protocol yet!");
        		throw new FanbeiException(FanbeiExceptionCode.PROTOCOL_NOT_SUPPORT_YET);
        	}
        }else if(StringUtils.isNotBlank(param.previewParam)){
        	String previewParam = param.previewParam;
            JSONObject jsonObject = JSON.parseObject(previewParam);
            String openId = jsonObject.get("openId").toString();
            JsdUserDo jsdUserDo = jsdUserService.getByOpenId(openId);
            if(jsdUserDo == null) {
            	throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }
            
            uid = jsdUserDo.getRid().toString();
            amount = jsonObject.get("amount").toString();
            nper = jsonObject.get("nper").toString();
            loanRemark = jsonObject.get("loanRemark").toString();
        }else {
        	throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
        
        List<JsdResourceDo> jsdResourceDoList = jsdResourceService.listByType("DSED_AGREEMENT");
        List<JsdPrococolVo> jsdPrococolVoList = new ArrayList<>();
        for (JsdResourceDo afResourceDo : jsdResourceDoList) {
        	JsdPrococolVo jsdPrococolVo = new JsdPrococolVo();
            if ("DSED_PLATFORM_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//平台服务协议
                jsdPrococolVo.setProtocolName("平台服务协议");
                jsdPrococolVo.setProtocolUrl(getNotifyHost()+"/dsed-web/h5/dsedLoanPlatformServiceProtocol?userId=" + uid +
                        "&nper=" + nper + "&loanId=" + param.bizNo + "&amount=" + amount + "&totalServiceFee=" + BigDecimal.ZERO);
            } else if ("DSED_LOAN_CONTRACT".equals(afResourceDo.getSecType())) {//借钱协议
                jsdPrococolVo.setProtocolName("借钱协议");
                jsdPrococolVo.setProtocolUrl(getNotifyHost()+"/dsed-web/h5/loanProtocol?userId=" + uid +
                        "&amount=" + amount + "&nper=" + nper + "&loanId=" + param.bizNo + "&loanRemark=" + loanRemark +
                        "&repayRemark=" + "");
            } else if ("DIGITAL_CERTIFICATE_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//数字证书
                jsdPrococolVo.setProtocolName("数字证书");
                jsdPrococolVo.setProtocolUrl(getNotifyHost()+"/dsed-web/app/numProtocol?userId=" + uid);
            } else if ("LETTER_OF_RISK".equals(afResourceDo.getSecType())) {//风险提示协议
                jsdPrococolVo.setProtocolName("风险提示协议");
                jsdPrococolVo.setProtocolUrl(getNotifyHost()+"/app/sys/riskWarning");
            }
            jsdPrococolVoList.add(jsdPrococolVo);
        }
        resp.setData(jsdPrococolVoList);
        return resp;
    }
    
    private static String getNotifyHost(){
        if(notifyHost==null){
            notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);
            return notifyHost;
        }
        return notifyHost;
    }
    
    public static class JsdPrococolVo{
    	private String protocolName;
    	private String protocolUrl;
    	
		public String getProtocolName() {
			return protocolName;
		}
		public void setProtocolName(String protocolName) {
			this.protocolName = protocolName;
		}
		public String getProtocolUrl() {
			return protocolUrl;
		}
		public void setProtocolUrl(String protocolUrl) {
			this.protocolUrl = protocolUrl;
		}
    }
}
