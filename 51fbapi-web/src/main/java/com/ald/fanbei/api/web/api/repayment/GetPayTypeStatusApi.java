package com.ald.fanbei.api.web.api.repayment;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @author chenqiwwi 2018/3/22 16:35
 * @类描述：还款支付类型状态
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getPayTypeStatusApi")
public class GetPayTypeStatusApi  implements ApiHandle{

    @Resource
    private AfResourceService afResourceService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        long userId = context.getUserId();
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String sceneType = ObjectUtils.toString(requestDataVo.getParams().get("sceneType"),"");
		if(StringUtils.isEmpty(sceneType)){
		    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
        HashMap ret = new HashMap();
        HashMap data = new HashMap();
        HashMap wXdata = new HashMap();
        HashMap zFbdata = new HashMap();
        HashMap xXhkdata = new HashMap();
        
        ArrayList<Object>  payTypeStatusList = new ArrayList<Object>();
        AfResourceDo  afResourceDo =  new AfResourceDo();
        afResourceDo =   afResourceService.getConfigByTypesAndSecType("THIRD_PAY_CONTROL", sceneType);
    	wXdata.put("payType", "WX");
    	zFbdata.put("payType", "ZFB");
    	xXhkdata.put("payType", "XXHK");
    	String value = "";
    	String value1 = "";
    	String value2 = "";
        if(afResourceDo != null){
        	value = afResourceDo.getValue();
         	value1 = afResourceDo.getValue1();
        	value2 = afResourceDo.getValue2();
        	wXdata.put("status", value == null || value == "" ? 0 : Integer.parseInt(value));
        	zFbdata.put("status", value1 == null || value1 == "" ? 0 : Integer.parseInt(value1));
        	xXhkdata.put("status", value2 == null || value2 == "" ? 0 : Integer.parseInt(value2));
        }else{
         	wXdata.put("status", 0);
        	zFbdata.put("status", 0);
        	xXhkdata.put("status", 0);
        	 //return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.RESOURCE_NOT_FOUND_CONFIGURATION);
        }
     
    	payTypeStatusList.add(wXdata);
    	payTypeStatusList.add(zFbdata);
    	payTypeStatusList.add(xXhkdata);
        
        ret.put("payTypeStatusList",payTypeStatusList);
        resp.setResponseData(ret);
        return resp;
    }


}
