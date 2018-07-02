package com.ald.fanbei.api.web.api.instalments;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfAdInstalmentsDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *
 *@类描述：GetGoodsDetailInfoApi
 *@author 何鑫 2017年3月3日  11:41:32
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getInstalmentsAdApi")
public class GetInstalmentsAdApi implements ApiHandle {

    @Resource
    AfAdInstalmentsService afAdInstalmentsService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo,
                                     FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        AfAdInstalmentsDo adInfo = afAdInstalmentsService.getAdInfo();
        if(adInfo==null){
            resp.setResponseData(new JSONObject());
        }else{
            resp.setResponseData(adInfo);
        }
        return resp;
    }

}
