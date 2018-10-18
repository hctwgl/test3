package com.ald.fanbei.api.web.h5.api.jsd;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.JsdProctocolBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.enums.BorrowVersionType;
import com.ald.fanbei.api.common.enums.XgxyProtocolType;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetLoanProtocolParam;
import com.alibaba.fastjson.JSON;


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
    
    @Override
    public JsdH5HandleResponse process(Context context) {
    	JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
        GetLoanProtocolParam param = (GetLoanProtocolParam) context.getParamEntity();
        List<JsdProctocolBo> protocolVos = new ArrayList<>();;
		JsdBorrowCashDo jsdBorrowCashDo = jsdBorrowCashService.getByTradeNoXgxy(param.bizNo);
		String tyingType = "";
		logger.info("param = " + JSON.toJSONString(param) + " , jsdBorrowCashDo = " + JSON.toJSONString(jsdBorrowCashDo));
		if(jsdBorrowCashDo != null){
			tyingType = jsdBorrowCashDo.getVersion();
		}else {
			JSONObject jsonObject = new JSONObject(param.previewParam);
			tyingType = jsonObject.getString("tyingType");
		}
		if(StringUtil.equals(tyingType, BorrowVersionType.SELL.name())){
			if(XgxyProtocolType.BORROW.name().equals(param.type)) {
				protocolVos = jsdBorrowCashService.getBorrowProtocols(param.openId, param.bizNo, param.previewParam);
			}else if (XgxyProtocolType.TYING.name().equals(param.type)){
				protocolVos = jsdBorrowCashService.getAgencyProtocols(param.openId, param.bizNo, param.previewParam);
			}else if(XgxyProtocolType.DELAY.name().equals(param.type)){
				protocolVos = jsdBorrowCashService.getRenewalProtocols(param.openId, param.bizNo, param.previewParam);
			}else {
				logger.warn("Don't support " + param.type + " protocol yet!");
				throw new BizException(BizExceptionCode.PROTOCOL_NOT_SUPPORT_YET);
			}
		}else if(StringUtil.equals(tyingType, BorrowVersionType.BEHEAD.name())){
			if(XgxyProtocolType.BORROW.name().equals(param.type)) {
				protocolVos = jsdBorrowCashService.getBorrowPlusProtocols(param.openId, param.bizNo, param.previewParam);
			}else if(XgxyProtocolType.DELAY.name().equals(param.type)){
				protocolVos = jsdBorrowCashService.getRenewalPlusProtocols(param.openId, param.bizNo, param.previewParam);
			}else {
				logger.warn("Don't support " + param.type + " protocol yet!");
			}
		}

        resp.setData(protocolVos);
        return resp;
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
