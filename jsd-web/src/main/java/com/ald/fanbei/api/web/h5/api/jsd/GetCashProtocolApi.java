package com.ald.fanbei.api.web.h5.api.jsd;


import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;


/**
 * @author chefeipeng 2017年01月8日 10:46:23
 * @类描述：展示商品代买提示语
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCashProtocolApi")
@Validator("getCashProtocolParam")
public class GetCashProtocolApi implements DsedH5Handle {

    @Resource
    JsdResourceService jsdResourceService;

    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
        return resp;
    }


}
