package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetLoanDecreasedAmountParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：计算提前还款可以减免的金额
 * @author chefeipeng 2018年5月23日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getDsedLoanDecreasedAmountApi")
@Validator("GetLoanDecreasedAmountParam")
public class GetLoanDecreasedAmountApi implements H5Handle {
    
    @Resource
	DsedLoanRepaymentService dsedLoanRepaymentService;


    @Override
	public H5HandleResponse process(Context context) {
    	H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> map = new HashMap<String, Object>();
		GetLoanDecreasedAmountParam param = (GetLoanDecreasedAmountParam)context.getParamEntity();
		String borrowNo = param.borrowNo;
		Long userId = param.userId;
		BigDecimal shouldRepayAmount = dsedLoanRepaymentService.getDecreasedAmount(borrowNo,userId);
		map.put("decreasedAmount",shouldRepayAmount);
		resp.setResponseData(map);
		return resp;
    }
}
