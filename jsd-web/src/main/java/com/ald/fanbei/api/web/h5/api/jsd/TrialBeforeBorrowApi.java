package com.ald.fanbei.api.web.h5.api.jsd;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.alibaba.fastjson.JSON;

/**
 * @类描述：试算接口
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("trialBeforeBorrowApi")
public class TrialBeforeBorrowApi implements JsdH5Handle {
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    
    @Override
    public JsdH5HandleResponse process(Context context) {
    	JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
    	
    	TrialBeforeBorrowBo bo = new TrialBeforeBorrowBo();
    	bo.req = JSON.parseObject(JSON.toJSONString(context.getDataMap()), TrialBeforeBorrowBo.Req.class);
    	jsdBorrowCashService.resolve(bo);
    	
    	resp.setData(bo.resp);
    	return resp;
    }
}
