package com.ald.fanbei.api.web.h5.api.jsd;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：试算接口
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getProfitApi")
public class GetProfitApi implements JsdH5Handle {
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    
    @Override
    public JsdH5HandleResponse process(Context context) {
    	JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
    	
    	TrialBeforeBorrowBo bo = new TrialBeforeBorrowBo();
    	TrialBeforeBorrowReq req = bo.req = new TrialBeforeBorrowReq();
    	
    	JSONObject dataMap = context.getDataMap();
    	
    	String type = dataMap.getString("type");
    	if("BORROW".equals(type)) {
    		req.openId = dataMap.getString("openId");
        	req.amount = dataMap.getString("amount");
        	req.term = dataMap.getString("term");
        	req.unit = dataMap.getString("unit");
        	jsdBorrowCashService.resolve(bo);
        	
        	Map<String,Object> respData = new HashMap<>();
        	respData.put("totalDiffFee", bo.resp.totalDiffFee);
        	resp.setData(respData);
    	}
    	
    	return resp;
    }
}
