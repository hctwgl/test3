package com.ald.fanbei.api.web.api.category;

import com.ald.fanbei.api.biz.service.AfGoodsCategoryService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsCategoryDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("getGoodsCategoryApi")
public class GetGoodsCategoryApi implements ApiHandle {

    @Resource
    AfGoodsCategoryService afGoodsCategoryService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long rid = NumberUtil.objToPageLongDefault(requestDataVo.getParams().get("id"), 1L);
        Map<String,Object> data = new HashMap<String,Object>();
        List<AfGoodsCategoryDo> secondLevelList = afGoodsCategoryService.selectSecondLevel(rid);
        List<AfGoodsCategoryDo> goodslist = new ArrayList<AfGoodsCategoryDo>();
        List<List<AfGoodsCategoryDo>> thirdLevelList = new ArrayList<List<AfGoodsCategoryDo>>();
        if (null != secondLevelList && secondLevelList.size()>0) {
            for(int i=0;i<secondLevelList.size();i++){
                Long newRid = secondLevelList.get(i).getRid();
                goodslist = afGoodsCategoryService.selectThirdLevel(newRid);
                thirdLevelList.add(goodslist);
            }
        }
        data.put("secondLevelList",secondLevelList);
        data.put("thirdLevelList",thirdLevelList);
        resp.setResponseData(data);
        return  resp;
    }
}
