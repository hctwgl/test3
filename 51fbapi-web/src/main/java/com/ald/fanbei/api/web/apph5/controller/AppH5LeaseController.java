package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.dto.LeaseGoods;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhourui on 2018年03月03日 13:20
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5LeaseController extends BaseController {
    @Resource
    AfResourceService afResourceService;

    @Resource
    AfGoodsService afGoodsService;
    /**
     *获取租赁首页banner
     */
    @ResponseBody
    @RequestMapping(value = "getHomeLeaseBanner", produces = "text/html;charset=UTF-8",method = RequestMethod.GET)
    public String getHomeLeaseBanner(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            List<Object> bannerList = new ArrayList<Object>();
            String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
            if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
                bannerList = getBannerObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.LEASE_BANNER.getCode()));
            }else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ){
                bannerList = getBannerObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.LEASE_BANNER.getCode()));
            }

            resp = H5CommonResponse.getNewInstance(false,"请求成功", "", bannerList);
            return resp.toString();
        }catch  (Exception e) {
            logger.error("getHomeLeaseBanner", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *获取租赁首页商品
     */
    @ResponseBody
    @RequestMapping(value = "getHomeLeaseGoods", produces = "text/html;charset=UTF-8",method = RequestMethod.GET)
    public String getHomeLeaseGoods(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        List<Map<String, Object>> goodsInfoList = new ArrayList<Map<String, Object>>();
        try{
            Long pageIndex = NumberUtil.objToLongDefault(request.getParameter("pageIndex"), 1);
            Long pageSize = NumberUtil.objToLongDefault(request.getParameter("pageSize"), 50);
            List<LeaseGoods> list = afGoodsService.getHomeLeaseGoods(pageIndex,pageSize);
            for (LeaseGoods item:list) {
                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                goodsInfo.put("id",item.getRid());
                goodsInfo.put("goodsIcon",item.getGoodsIcon());
                goodsInfo.put("name",item.getName());
                goodsInfo.put("priceAmount","0");
                goodsInfo.put("nperAmount","0");
                if(item.getGoodsPrice() != null && item.getGoodsPrice().size() > 0) {
                    BigDecimal priceAmount = new BigDecimal(0);
                    BigDecimal nperAmount = new BigDecimal(0);
                    for (AfGoodsPriceDo afGoodsPriceDo:item.getGoodsPrice()) {
                        if(afGoodsPriceDo.getLeaseParam() != null && afGoodsPriceDo.getLeaseParam() != ""){
                            JSONObject leaseObj = JSON.parseObject(afGoodsPriceDo.getLeaseParam());
                            if(priceAmount.compareTo(leaseObj.getBigDecimal("leaseAmout"))>0 || priceAmount.compareTo(new BigDecimal(0))==0){
                                priceAmount = leaseObj.getBigDecimal("leaseAmout");
                            }
                            JSONArray leaseParam = JSON.parseArray(leaseObj.getString("leaseParam"));
                            for (int i = 0;i < leaseParam.size(); i++){
                                JSONObject obj = leaseParam.getJSONObject(i);
                                if(nperAmount.compareTo(obj.getBigDecimal("monthlyRent"))>0 || nperAmount.compareTo(new BigDecimal(0))==0){
                                    nperAmount = obj.getBigDecimal("monthlyRent");
                                }
                            }
                        }
                    }
                    goodsInfo.put("priceAmount",priceAmount);
                    goodsInfo.put("nperAmount",nperAmount);
                }
                goodsInfoList.add(goodsInfo);
            }
            resp = H5CommonResponse.getNewInstance(false,"请求成功", "", goodsInfoList);
            return resp.toString();
        }catch  (Exception e) {
            logger.error("getHomeLeaseGoods", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    private List<Object> getBannerObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
        List<Object> bannerList = new ArrayList<Object>();
        for (AfResourceDo afResourceDo : bannerResclist) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("imageUrl", afResourceDo.getValue());
            data.put("skipUrl", afResourceDo.getValue1());
            data.put("sort", afResourceDo.getSort());
            bannerList.add(data);
        }
        return bannerList;
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();

            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setId(jsonObj.getString("id"));
            reqVo.setMethod(request.getRequestURI());
            reqVo.setSystem(jsonObj);
            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
}
