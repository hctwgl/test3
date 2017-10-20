package com.ald.fanbei.api.web.api.category;

import com.ald.fanbei.api.biz.service.AfGoodsCategoryService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfGoodsCategoryDao;
import com.ald.fanbei.api.dal.domain.AfGoodsCategoryDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类描述：商品分类
 * @author chefeipeng 2017年10月25日下午2:03:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAllGoodsCategoryApi")
public class GetAllGoodsCategoryApi implements ApiHandle {

    @Resource
    AfGoodsCategoryService afGoodsCategoryService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        List<AfGoodsCategoryDo> secondLevelList = new ArrayList<AfGoodsCategoryDo>();
        List<AfGoodsCategoryDo> list = new ArrayList<AfGoodsCategoryDo>();
        List<List<AfGoodsCategoryDo>> thirdLevelList = new ArrayList<List<AfGoodsCategoryDo>>();
        Map<String,Object> data = new HashMap<String,Object>();
        //查出一级
        List<AfGoodsCategoryDo> oneLevelList = afGoodsCategoryService.selectOneLevel();
        //查出二级
        if(null != oneLevelList && oneLevelList.size()>0){
            Long rid = oneLevelList.get(0).getRid();
            secondLevelList = afGoodsCategoryService.selectSecondLevel(rid);
            //查出三级
            if(null != secondLevelList && secondLevelList.size()>0){
                for(int i=0;i<secondLevelList.size();i++){
                    Long newRid = secondLevelList.get(i).getRid();
                    list = afGoodsCategoryService.selectThirdLevel(newRid);
                    thirdLevelList.add(list);
                }
            }
        }
        data.put("oneLevelList",oneLevelList);
        data.put("secondLevelList",secondLevelList);
        data.put("thirdLevelList",thirdLevelList);
        resp.setResponseData(data);
        return resp;
    }
}
