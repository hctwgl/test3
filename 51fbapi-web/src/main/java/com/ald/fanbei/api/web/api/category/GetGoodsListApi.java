package com.ald.fanbei.api.web.api.category;

import com.ald.fanbei.api.biz.service.AfGoodsTbkService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


public class GetGoodsListApi implements ApiHandle {

    @Resource
    AfGoodsTbkService afGoodsTbkService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String level = ObjectUtils.toString(requestDataVo.getParams().get("level"),null);
        Long id = NumberUtil.objToLongDefault(requestDataVo.getParams().get("id"),0l);







        return resp;
    }

}
