package com.ald.fanbei.api.web.api.category;

import com.ald.fanbei.api.biz.service.AfCategoryOprationService;
import com.ald.fanbei.api.biz.service.AfGoodsCategoryService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.dal.dao.AfGoodsCategoryDao;
import com.ald.fanbei.api.dal.domain.AfCategoryOprationDo;
import com.ald.fanbei.api.dal.domain.AfGoodsCategoryDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.gson.JsonObject;

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
 * @version version 4.1.3 liutengyuan 2018.4.14 为爱尚街添加分类运营位信息
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAllGoodsCategoryApi")
public class GetAllGoodsCategoryApi implements ApiHandle {

    @Resource
    AfGoodsCategoryService afGoodsCategoryService;
    @Resource
    private AfCategoryOprationService afCategoryOprationService;

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
            oneLevelList = new ArrayList<Object>();
            for(int x=0;x<oneList.size();x++){
                secondLevelList = new ArrayList<Object>();
                thirdLevelList = new ArrayList<Object>();
                Map<String,Object> objFirst = new HashMap<String,Object>();
                rid = oneList.get(x).getId();
                name = oneList.get(x).getName();
                // 查询分类运营位配置信息
                AfCategoryOprationDo categoryRunData =  afCategoryOprationService.getByCategoryId(rid);
                secondList = afGoodsCategoryService.selectSecondLevel(rid);
                //查出三级
                if(null != secondList && secondList.size()>0){
                    secondLevelList = new ArrayList<Object>();
                    for(int i=0;i<secondList.size();i++){
                        thirdLevelList = new ArrayList<Object>();
                        Map<String,Object> objSecond = new HashMap<String,Object>();
                        secondRid = secondList.get(i).getId();
                        secondName = secondList.get(i).getName();
                        list = afGoodsCategoryService.selectThirdLevel(secondRid);
                        if(null != list && list.size()>0){
                            thirdLevelList = new ArrayList<Object>();
                            for(int k=0;k<list.size();k++){
                                Map<String,Object> objThird = new HashMap<String,Object>();
                                String thirdName = list.get(k).getName();
                                Long thirdRid = list.get(k).getId();
                                String thirdImgUrl = list.get(k).getImgUrl();
                                objThird.put("name",thirdName);
                                objThird.put("imgUrl",thirdImgUrl);
                                objThird.put("id",thirdRid);
                                thirdLevelList.add(objThird);
                            }
                        }
                        objSecond.put("name",secondName);
                        objSecond.put("thirdLevelList",thirdLevelList);
                        secondLevelList.add(objSecond);
                    }
                }
                objFirst.put("name",name);
                objFirst.put("rid", rid);// 前端统计
                objFirst.put("categoryRunData", categoryRunData);
                objFirst.put("secondLevelList",secondLevelList);
                oneLevelList.add(objFirst);
            }
        }
        data.put("oneLevelList",oneLevelList);
        resp.setResponseData(data);
        return resp;
    }
}
