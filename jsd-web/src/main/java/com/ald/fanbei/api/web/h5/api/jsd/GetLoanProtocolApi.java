package com.ald.fanbei.api.web.h5.api.jsd;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.util.NumberUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

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
@Component("getLoanProtocolApi")
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
        JsdPrococolVo jsdPrococolVo = new JsdPrococolVo();
        if(StringUtils.isBlank(param.previewParam)){
            throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
        JSONObject jsonObject = JSONObject.parseObject(param.previewParam);
        String loanRemark = "tmp";
        String openId = ObjectUtils.toString(jsonObject.get("openId"), "").toString();
        String dayType = ObjectUtils.toString(jsonObject.get("type"), "").toString();
        Long borrowId = NumberUtil.objToLongDefault(jsonObject.get("borrowId"), 0l);
        Long renewalId = NumberUtil.objToLongDefault(jsonObject.get("renewalId"), 0l);
        int renewalDay = NumberUtil.objToIntDefault(jsonObject.get("renewalDay"), 0);
        String nper = ObjectUtils.toString(jsonObject.get("nper"), "");
        BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(jsonObject.get("renewalAmount"), BigDecimal.ZERO);
        String borrowAmount = ObjectUtils.toString(jsonObject.get("borrowAmount"), "");
        BigDecimal poundage = NumberUtil.objToBigDecimalDefault(jsonObject.get("poundage"), BigDecimal.ZERO);

        if(StringUtils.isNotBlank(param.bizNo)) {
        	if(ProtocolType.BORROW.name().equals(param.type)) {
        		JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(param.bizNo);
        		if(cashDo == null) {
        			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
        		}
                borrowAmount = cashDo.getAmount().toString();
                nper = "1";
                borrowId = cashDo.getRid();
        	}else if (ProtocolType.TYING.name().equals(param.type)){

            }else if (ProtocolType.AUTH.name().equals(param.type)){

            }else {
        		logger.warn("Don't support " + param.type + " protocol yet!");
        		throw new FanbeiException(FanbeiExceptionCode.PROTOCOL_NOT_SUPPORT_YET);
        	}
        }

        
        if ("PROTOCOL_BORROW_PLATFORM".equals(param.type)) {//平台服务协议
            jsdPrococolVo.setProtocolName("平台服务协议");
            jsdPrococolVo.setProtocolUrl(getNotifyHost()+"/jsd-web/h5/platformServiceProtocol?openId=" + openId +
                    "&nper=" + nper + "&loanNo=" + param.bizNo + "&borrowAmount=" + borrowAmount + "&totalServiceFee=" + BigDecimal.ZERO);
        } else if ("PROTOCOL_BORROW_CASH".equals(param.type)) {//借钱协议
            jsdPrococolVo.setProtocolName("借钱协议");
            jsdPrococolVo.setProtocolUrl(getNotifyHost()+"/jsd-web/h5/loanProtocol?openId=" + openId +
                    "&borrowAmount=" + borrowAmount + "&nper=" + nper + "&borrowId=" + borrowId + "&loanRemark=" + loanRemark +
                    "&repayRemark=" + "");
        } else if ("PROTOCOL_BORROW_DIGITAL CERTIFICATE".equals(param.type)) {//数字证书
            jsdPrococolVo.setProtocolName("数字证书");
            jsdPrococolVo.setProtocolUrl(getNotifyHost()+"/jsd-web/app/numProtocol?openId=" + openId);
        } else if ("PROTOCOL_BORROW_RISK_NOTICE".equals(param.type)) {//风险提示协议
            jsdPrococolVo.setProtocolName("风险提示协议");
            jsdPrococolVo.setProtocolUrl(getNotifyHost()+"/app/sys/riskWarning");
        } else if ("PROTOCOL_BORROW_ORDER".equals(param.type)) {//风险提示协议
            jsdPrococolVo.setProtocolName("分期协议");
            jsdPrococolVo.setProtocolUrl(getNotifyHost()+"/jsd-web/h5/loanProtocol");
        }
        resp.setData(jsdPrococolVo);
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
