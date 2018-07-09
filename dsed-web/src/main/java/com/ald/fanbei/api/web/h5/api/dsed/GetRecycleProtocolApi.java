package com.ald.fanbei.api.web.h5.api.dsed;


import org.springframework.stereotype.Component;

import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;


/**
 * @author chefeipeng 2017年01月8日 10:46:23
 * @类描述：展示商品代买提示语
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRecycleProtocolApi")
@Validator("getRecycleProtocolParam")
public class GetRecycleProtocolApi implements H5Handle {

	@Override
	public H5HandleResponse process(Context context) {
		return null;
	}
	
}
