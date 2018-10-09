package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.service.BeheadBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.common.enums.BorrowVersionType;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：获取利润差
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getProfitApi")
public class GetProfitApi implements JsdH5Handle {
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    BeheadBorrowCashService beheadBorrowCashService;
    
    @Override
    public JsdH5HandleResponse process(Context context) {
    	JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");


    	TrialBeforeBorrowBo bo = new TrialBeforeBorrowBo();
    	bo.layerInterestRate = jsdBorrowCashService.getRiskDailyRate(context.getOpenId());
    	bo.userId = context.getUserId();
    	
    	JSONObject dataMap = context.getDataMap();
    	String type = dataMap.getString("type");
    	String tyingType = dataMap.getString("ni");
    	if("BORROW".equals(type)) {
    		bo.req = new TrialBeforeBorrowReq(context.getOpenId(), new BigDecimal(dataMap.getString("amount")),
    				dataMap.getString("term"), dataMap.getString("unit"));
    		
    		if(BorrowVersionType.SELL.name().equals(tyingType)){
        		jsdBorrowCashService.resolve(bo);	// 赊销
        	}else{
        		beheadBorrowCashService.resolve(bo); // 砍头
        	}
        	
        	Map<String,Object> respData = new HashMap<>();
        	respData.put("totalDiffFee", bo.resp.totalDiffFee);
        	resp.setData(respData);
    	}
    	
    	return resp;
    }
}
