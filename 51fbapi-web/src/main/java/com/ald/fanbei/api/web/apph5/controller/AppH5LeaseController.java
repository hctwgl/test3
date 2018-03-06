package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfGoodsSource;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.LeaseGoods;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
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
@RequestMapping("/fanbei-web/lease/")
public class AppH5LeaseController extends BaseController {
    @Resource
    AfResourceService afResourceService;

    @Resource
    AfGoodsService afGoodsService;

    @Resource
    AfGoodsPriceService afGoodsPriceService;

    @Resource
    AfGoodsPropertyService afGoodsPropertyService;

    @Resource
    AfPropertyValueService afPropertyValueService;

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
                            if(priceAmount.compareTo(afGoodsPriceDo.getLeaseAmount())>0 || priceAmount.compareTo(new BigDecimal(0))==0){
                                priceAmount = afGoodsPriceDo.getLeaseAmount();
                            }
                            JSONArray leaseParam = JSON.parseArray(afGoodsPriceDo.getLeaseParam());
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

    /**
     *获取租赁商品详情
     */
    @ResponseBody
    @RequestMapping(value = "getLeaseGoodsDetail", produces = "text/html;charset=UTF-8",method = RequestMethod.GET)
    public String getLeaseGoodsDetail(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> goodsInfoList = new ArrayList<Map<String, Object>>();
        try{
            Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("goodsId")), 0l);
            AfGoodsDo goods = afGoodsService.getGoodsById(goodsId);
            data = getGoodsVo(goods);

            List<AfLeaseGoodsPriceVo> priceData = new ArrayList<>();
            List<AfPropertyVo> propertyData = new ArrayList<>();
            AfGoodsPriceDo goodsPriceDo = new AfGoodsPriceDo();
            AfGoodsPropertyDo propertyDo = new AfGoodsPropertyDo();

            goodsPriceDo.setGoodsId(goodsId);
            List<AfGoodsPriceDo> priceDos = afGoodsPriceService.getListByCommonCondition(goodsPriceDo);
            priceData = convertListForPrice(priceDos);
            data.put("priceData",priceData);

            propertyDo.setGoodsId(goodsId);
            List<AfGoodsPropertyDo> propertyDos = afGoodsPropertyService.getListByCommonCondition(propertyDo);
            if (propertyDos != null && propertyDos.size() > 0) {
                for (AfGoodsPropertyDo pdo : propertyDos) {
                    AfPropertyVo propertyVo = new AfPropertyVo();

                    List<AfPropertyValueDo> valueDos = new ArrayList<>();
                    List<AfpropertyValueVo> propertyValues = new ArrayList<>();
                    AfPropertyValueDo valueDo = new AfPropertyValueDo();
                    valueDo.setPropertyId(pdo.getRid());
                    valueDos = afPropertyValueService.getListByCommonCondition(valueDo);

                    if (valueDos != null && valueDos.size() > 0) {
                        for (AfPropertyValueDo pvd : valueDos) {
                            AfpropertyValueVo pValueVo = new AfpropertyValueVo();
                            pValueVo.setPropertyValueId(pvd.getRid());
                            pValueVo.setPropertyValueName(pvd.getValue());
                            propertyValues.add(pValueVo);
                        }
                    }
                    propertyVo.setPropertyId(pdo.getRid());
                    propertyVo.setPropertyName(pdo.getName());
                    propertyVo.setPropertyValues(propertyValues);
                    propertyData.add(propertyVo);
                }
            }
            data.put("propertyData",propertyData);
            resp = H5CommonResponse.getNewInstance(false,"请求成功", "", data);
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

    private Map<String, Object> getGoodsVo(AfGoodsDo goods){
        Map<String, Object> data = new HashMap<String, Object>();
        List<String> goodsPics = new ArrayList<String>();
        List<GoodsDetailPicInfoVo> goodsDetail = new ArrayList<GoodsDetailPicInfoVo>();
        data.put("goodsId",goods.getRid());
        data.put("goodsIcon",goods.getGoodsIcon());
        data.put("goodsName",goods.getName());
        data.put("goodsUrl",goods.getGoodsUrl());
        data.put("realAmount",goods.getRealAmount());
        data.put("rebateAmount",goods.getRebateAmount());
        /* 商品详情图：多图，用英文逗号隔开  by weiqingeng
         * 1: 图片没有尺寸 http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg,http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg
         * 2: 图片有尺寸 http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg;1024;760,http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg;1024;760
         */
        if(StringUtil.isNotBlank(goods.getGoodsDetail())){
            String[] details = goods.getGoodsDetail().split(",");
            if(null != details && details.length > 0){
                for(String detail : details){
                    String[] gdArray = detail.split(";");
                    if(gdArray != null && gdArray.length > 0){
                        if(gdArray.length == Constants.GOODSDETAIL_PIC_PARTS){
                            GoodsDetailPicInfoVo goodsDetailPicInfoVo = new GoodsDetailPicInfoVo(gdArray[0], gdArray[1], gdArray[2]);
                            goodsDetail.add(goodsDetailPicInfoVo);
                        }else{
                            GoodsDetailPicInfoVo goodsDetailPicInfoVo = new GoodsDetailPicInfoVo(gdArray[0], null, null);
                            goodsDetail.add(goodsDetailPicInfoVo);
                        }
                    }
                }
            }
        }
        data.put("goodsDetail",goodsDetail);
        //商品图片汇总处理
        if(StringUtil.isNotBlank(goods.getGoodsPic1())){
            goodsPics.add(goods.getGoodsPic1());
        }
        if(StringUtil.isNotBlank(goods.getGoodsPic2())){
            goodsPics.add(goods.getGoodsPic2());
        }
        if(StringUtil.isNotBlank(goods.getGoodsPic3())){
            goodsPics.add(goods.getGoodsPic3());
        }
        if(StringUtil.isNotBlank(goods.getGoodsPic4())){
            goodsPics.add(goods.getGoodsPic4());
        }
        data.put("goodsPics",goodsPics);
        return data;
    }

    private List<AfLeaseGoodsPriceVo> convertListForPrice(List<AfGoodsPriceDo> priceDos) {
        List<AfLeaseGoodsPriceVo> propertyData = new ArrayList<>();
        if (priceDos != null && priceDos.size() > 0) {
            for (AfGoodsPriceDo priceDo : priceDos) {
                AfLeaseGoodsPriceVo goodsPriceVo = new AfLeaseGoodsPriceVo();

                goodsPriceVo.setStock(priceDo.getStock());
                goodsPriceVo.setIsSale(priceDo.getLeaseSale());
                goodsPriceVo.setPriceId(priceDo.getRid());
                goodsPriceVo.setPropertyValueIds(priceDo.getPropertyValueIds());
                goodsPriceVo.setPropertyValueNames(priceDo.getPropertyValueNames());
                goodsPriceVo.setPriceAmount(priceDo.getLeaseAmount());
                goodsPriceVo.setLeaseParam(priceDo.getLeaseParam());
                propertyData.add(goodsPriceVo);
            }
        }
        return propertyData;
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
