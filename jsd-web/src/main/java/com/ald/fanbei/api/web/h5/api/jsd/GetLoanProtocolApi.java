package com.ald.fanbei.api.web.h5.api.jsd;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetLoanProtocolParam;


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
        List<JsdProctocolVo> protocolVos = new ArrayList<>();;
    	if(XgxyProtocolType.BORROW.name().equals(param.type)) {
			protocolVos = getBorrowProtocols(param.openId, param.bizNo, param.previewParam);
    	}else if (XgxyProtocolType.TYING.name().equals(param.type)){
    		protocolVos = getOrderProtocols(param.openId, param.bizNo, param.previewParam);
        }else if (XgxyProtocolType.AUTH.name().equals(param.type)){
        	
        }else {
    		logger.warn("Don't support " + param.type + " protocol yet!");
    		throw new FanbeiException(FanbeiExceptionCode.PROTOCOL_NOT_SUPPORT_YET);
    	}
        resp.setData(protocolVos);
        return resp;
    }
    
    private List<JsdProctocolVo> getBorrowProtocols(String openId, String tradeNoXgxy, String previewJsonStr){
    	List<JsdResourceDo> ress = jsdResourceService.listByType(ResourceType.PROTOCOL_BORROW.getCode());
    	List<JsdProctocolVo> protocolVos = new ArrayList<>();
    	for(JsdResourceDo resdo: ress) {
    		if( ResourceSecType.PROTOCOL_BORROW_ORDER.name().equals(resdo.getSecType())) {
    			continue;
    		}
    		
    		JsdProctocolVo protocolVo = new JsdProctocolVo();
        	protocolVo.setProtocolName(resdo.getName());
        	String urlPrefix = getNotifyHost()+resdo.getValue();
			try {
				String urlParams = "?openId=" + openId  + "&tradeNoXgxy=" + (tradeNoXgxy == null?"":tradeNoXgxy) + "&preview=" + URLEncoder.encode(previewJsonStr, "UTF-8");
				protocolVo.setProtocolUrl(urlPrefix + urlParams);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	
            protocolVos.add(protocolVo);
    	}
    	return protocolVos;
    }
    
    private List<JsdProctocolVo> getOrderProtocols(String openId, String tradeNoXgxy, String previewJsonStr){
    	JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_ORDER.name());
    	List<JsdProctocolVo> protocolVos = new ArrayList<>();
    		
		JsdProctocolVo protocolVo = new JsdProctocolVo();
    	protocolVo.setProtocolName(resdo.getName());
    	String urlPrefix = getNotifyHost()+resdo.getValue();
		try {
			String urlParams = "?openId=" + openId  + "&tradeNoXgxy=" + tradeNoXgxy + "&preview=" + URLEncoder.encode(previewJsonStr, "UTF-8");
			protocolVo.setProtocolUrl(urlPrefix + urlParams);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
        protocolVos.add(protocolVo);
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
