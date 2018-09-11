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
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.XgxyProtocolType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetLoanProtocolParam;
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
        GetLoanProtocolParam param = (GetLoanProtocolParam) context.getParamEntity();

        if(StringUtils.isNotBlank(param.bizNo)) { //根据业务流水号获取对应协议
        	if(XgxyProtocolType.BORROW.name().equals(param.type)) {
        		JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(param.bizNo);
        		if(cashDo == null) {
        			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
        		}
        	}else if (XgxyProtocolType.TYING.name().equals(param.type)){

            }else if (XgxyProtocolType.AUTH.name().equals(param.type)){

            }else {
        		logger.warn("Don't support " + param.type + " protocol yet!");
        		throw new FanbeiException(FanbeiExceptionCode.PROTOCOL_NOT_SUPPORT_YET);
        	}
        }else if(XgxyProtocolType.BORROW.name().equals(param.type)){//否则为预览借款协议，只支持BORROW类型
        	JSONObject previewObj = JSONObject.parseObject(param.previewParam);
        	
        }else {
    		throw new FanbeiException(FanbeiExceptionCode.JSD_PARAMS_ERROR);
        }
        
        resp.setData(jsdPrococolVo);
        return resp;
    }
    
    private List<JsdProctocolVo> getBorrowProtocols(){
    	List<JsdResourceDo> ress = jsdResourceService.listByType(ResourceType.PROTOCOL_BORROW.getCode());
    	List<JsdProctocolVo> protocolVos = new ArrayList<>();
    	for(JsdResourceDo resdo: ress) {
    		JsdProctocolVo protocolVo = new JsdProctocolVo();
            if ( ResourceSecType.PROTOCOL_BORROW_ORDER.name().equals(resdo.getSecType())) {//分期服务协议
            	protocolVo.setProtocolName(resdo.getName());
            	protocolVo.setProtocolUrl(getNotifyHost()+"/jsd-web/h5/platformServiceProtocol?openId=" + openId +
                        "&nper=" + nper + "&loanNo=" + param.bizNo + "&borrowAmount=" + borrowAmount + "&totalServiceFee=" + BigDecimal.ZERO);
            } else if (ResourceSecType.PROTOCOL_BORROW_CASH.name().equals(param.type)) {//借钱协议
            	protocolVo.setProtocolName("借钱协议");
                protocolVo.setProtocolUrl(getNotifyHost()+"/jsd-web/h5/loanProtocol?openId=" + openId +
                        "&borrowAmount=" + borrowAmount + "&nper=" + nper + "&borrowId=" + borrowId + "&loanRemark=" + loanRemark +
                        "&repayRemark=" + "");
            } else if ("PROTOCOL_BORROW_DIGITAL CERTIFICATE".equals(param.type)) {//数字证书
            	protocolVo.setProtocolName("数字证书");
            	protocolVo.setProtocolUrl(getNotifyHost()+"/jsd-web/app/numProtocol?openId=" + openId);
            } else if ("PROTOCOL_BORROW_RISK_NOTICE".equals(param.type)) {//风险提示协议
            	protocolVo.setProtocolName("风险提示协议");
            	protocolVo.setProtocolUrl(getNotifyHost()+"/app/sys/riskWarning");
            }
            protocolVos.add(protocolVo);
    	}
    	return protocolVos;
    }
    
    
    private String getNotifyHost(){
        if(notifyHost==null){
            notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);
            return notifyHost;
        }
        return notifyHost;
    }
    
    public static class JsdProctocolVo{
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
