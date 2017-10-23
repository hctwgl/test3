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
        List<AfGoodsCategoryDo> secondList = new ArrayList<AfGoodsCategoryDo>();
        List<AfGoodsCategoryDo> list = new ArrayList<AfGoodsCategoryDo>();
        List<Object> oneLevelList = new ArrayList<Object>();
        List<Object> secondLevelList = new ArrayList<Object>();
        List<Object> thirdLevelList = new ArrayList<Object>();
        Map<String,Object> data = new HashMap<String,Object>();
        String name = "";
        Long rid = 0l;
        Long secondRid = 0l;
        String secondName = "";
        //查出一级
        List<AfGoodsCategoryDo> oneList = afGoodsCategoryService.selectOneLevel();
        //查出二级
        if(null != oneList && oneList.size()>0){
            for(int x=0;x<secondList.size();x++){
                List<Object> objFirst = new ArrayList<Object>();
                rid = oneList.get(x).getRid();
                name = oneList.get(x).getName();
                secondList = afGoodsCategoryService.selectSecondLevel(rid);
                //查出三级
                if(null != secondList && secondList.size()>0){
                    for(int i=0;i<secondList.size();i++){
                        List<Object> objSecond = new ArrayList<Object>();
                        secondRid = secondList.get(i).getRid();
                        secondName = secondList.get(i).getName();
                        list = afGoodsCategoryService.selectThirdLevel(secondRid);
                        if(null != list && list.size()>0){
                            for(int k=0;k<secondList.size();k++){
                                List<Object> objThird = new ArrayList<Object>();
                                String thirdName = list.get(k).getName();
                                Long thirdRid = list.get(k).getRid();
                                String thirdImgUrl = list.get(k).getImgUrl();
                                objThird.add(thirdName);
                                objThird.add(thirdImgUrl);
                                objThird.add(thirdRid);
                                thirdLevelList.add(objThird);
                            }
                        }
                        objSecond.add(secondName);
                        objSecond.add(thirdLevelList);
                        secondLevelList.add(objSecond);
                    }
                }
                objFirst.add(name);
                objFirst.add(secondLevelList);
                oneLevelList.add(objFirst);
            }

        }
        data.put("oneLevelList",oneLevelList);
        resp.setResponseData(data);
        return resp;
    }
}
