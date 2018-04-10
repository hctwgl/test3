package com.ald.fanbei.api.web.h5.controller;

import com.ald.fanbei.api.biz.service.AfVisualH5ItemService;
import com.ald.fanbei.api.biz.service.AfVisualH5Service;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfVisualH5Do;
import com.ald.fanbei.api.dal.domain.AfVisualH5ItemDo;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhourui on 2018年04月09日 10:50
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@RestController
@RequestMapping(value = "/visualH5", produces = "application/json;charset=UTF-8")
public class VisualH5Controller {
    @Resource
    AfVisualH5Service afVisualH5Service;

    @Resource
    AfVisualH5ItemService afVisualH5ItemService;

    @RequestMapping(value = "/getVisualH5", method = RequestMethod.POST)
    public String getVisualH5(HttpServletRequest request, HttpServletResponse response) {
        Long visualId = NumberUtil.objToLongDefault(request.getParameter("id"),0);
        if(visualId <= 0){
            return H5CommonResponse.getNewInstance(false, "请求参数缺失","",null).toString();
        }
        AfVisualH5Do afVisualH5Do = afVisualH5Service.getById(visualId);
        AfVisualH5ItemDo afVisualH5ItemDo = new AfVisualH5ItemDo();
        afVisualH5ItemDo.setVisualId(visualId);
        List<AfVisualH5ItemDo> list = afVisualH5ItemService.getListByCommonCondition(afVisualH5ItemDo);
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("name",afVisualH5Do.getName());
        data.put("urlName",afVisualH5Do.getUrlName());
        data.put("isSearch",afVisualH5Do.getIsSearch());
        data.put("item",list);
        return H5CommonResponse.getNewInstance(true, "","",data).toString();
    }

    @RequestMapping(value = "/getPushingGoods", method = RequestMethod.POST)
    public String getPushingGoods(HttpServletRequest request, HttpServletResponse response) {
        Long id = NumberUtil.objToLongDefault(request.getParameter("id"),0);
        Integer pageIndex = NumberUtil.objToIntDefault(request.getParameter("pageIndex"),1);
        Integer pageSize = NumberUtil.objToIntDefault(request.getParameter("pageSize"),10);
        HashMap<String,Object> data = new HashMap<String,Object>();
        if(id <= 0){
            return H5CommonResponse.getNewInstance(false, "请求参数缺失","",null).toString();
        }
        AfVisualH5ItemDo afVisualH5ItemDo = afVisualH5ItemService.getById(id);
        String value = afVisualH5ItemDo.getValue4();
        String[] selectIds = new String[pageSize];
        if(StringUtil.isNotBlank(value)){
            String[] skuIds = value.split(",");
            Integer current = (pageIndex - 1) * pageSize;
            if(current > skuIds.length){
                for (int i = current ; i < skuIds.length; i++){
                    if(i - current <= pageSize){
                        selectIds[i - current] = skuIds[i];
                    }
                }
            }
        }
        return H5CommonResponse.getNewInstance(true, "","",data).toString();
    }
}
