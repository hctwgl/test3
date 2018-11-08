package com.ald.fanbei.api.web.h5.api.jsd;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.third.enums.XgxyBorrowNotifyStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;

/**
 * @类描述：试算接口
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowStatusApi")
public class GetBorrowStatusApi implements JsdH5Handle {
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    
    @Override
    public JsdH5HandleResponse process(Context context) {
    	JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
    	
    	Object o = context.getData("borrowNo");
    	if(o == null) throw new BizException(BizExceptionCode.JSD_PARAMS_ERROR);
    	String borrowNo = o.toString();
    	
    	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(borrowNo);
    	if(cashDo == null) throw new BizException(BizExceptionCode.BORROW_DETAIL_NOT_EXIST_ERROR);
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("borrowNo", borrowNo);
    	String status;
    	if(JsdBorrowCashStatus.FINISHED.name().equals(cashDo.getStatus())) {
    		status = XgxyBorrowNotifyStatus.FINISHED.name();
    	}else if(JsdBorrowCashStatus.TRANSFERRED.name().equals(cashDo.getStatus())){
    		status = XgxyBorrowNotifyStatus.SUCCESS.name();
    	}else if(JsdBorrowCashStatus.APPLY.name().equals(cashDo.getStatus()) || JsdBorrowCashStatus.TRANSFERING.name().equals(cashDo.getStatus())){
			status = JsdBorrowCashStatus.TRANSFERING.name();
		} else {
    		status = XgxyBorrowNotifyStatus.FAILED.name();
    	}
    	map.put("status", status);
    	map.put("reason", cashDo.getRemark());
    	map.put("tradeNo", cashDo.getTradeNoUps());
    	
    	resp.setData(map);
    	return resp;
    }
}

