package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfGoodsPriceDto;
import com.ald.fanbei.api.dal.domain.dto.AfOrderDto;
import com.ald.fanbei.api.web.common.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhourui on 2018年04月09日 10:50
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@RestController
@RequestMapping(value = "/visualH5", produces = "application/json;charset=UTF-8")
public class VisualH5Controller extends BaseController {
    @Resource
    AfVisualH5Service afVisualH5Service;

    @Resource
    AfVisualH5ItemService afVisualH5ItemService;

    @Resource
    AfResourceService afResourceService;

    @Resource
    AfGoodsService afGoodsService;

    @Resource
    AfSchemeGoodsService afSchemeGoodsService;

    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;

    @Resource
    AfActivityGoodsService afActivityGoodsService;

    @Resource
    AfCouponService afCouponService;

    /**
     * 获取H5设置
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getVisualH5", method = RequestMethod.POST)
    public String getVisualH5(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long visualId = NumberUtil.objToLongDefault(request.getParameter("id"), 0);
            if (visualId <= 0) {
                return H5CommonResponse.getNewInstance(false, "请求参数缺失", "", null).toString();
            }
            AfVisualH5Do afVisualH5Do = afVisualH5Service.getById(visualId);
            AfVisualH5ItemDo afVisualH5ItemDo = new AfVisualH5ItemDo();
            afVisualH5ItemDo.setVisualId(visualId);
            List<AfVisualH5ItemDo> list = afVisualH5ItemService.getListByCommonCondition(afVisualH5ItemDo);
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("name", afVisualH5Do.getName());
            data.put("urlName", afVisualH5Do.getUrlName());
            data.put("isSearch", afVisualH5Do.getIsSearch());
            data.put("item", list);
            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (Exception e) {
            logger.error("changeUserAddress", e);
            return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
        }
    }

    /**
     * 获取H5状态
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getVisualH5Status", method = RequestMethod.POST)
    public String getVisualH5Status(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long visualId = NumberUtil.objToLongDefault(request.getParameter("id"), 0);
            if (visualId <= 0) {
                return H5CommonResponse.getNewInstance(false, "请求参数缺失", "", null).toString();
            }
            AfVisualH5Do afVisualH5Do = afVisualH5Service.getById(visualId);
            AfVisualH5ItemDo afVisualH5ItemDo = new AfVisualH5ItemDo();
            afVisualH5ItemDo.setVisualId(visualId);
            List<AfVisualH5ItemDo> list = afVisualH5ItemService.getListByCommonCondition(afVisualH5ItemDo);
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("status", afVisualH5Do.getStatus());
            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (Exception e) {
            logger.error("changeUserAddress", e);
            return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
        }
    }

    /**
     * 获取力推商品
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getPushingGoods", method = RequestMethod.POST)
    public String getPushingGoods(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long id = NumberUtil.objToLongDefault(request.getParameter("id"), 0);
            Integer pageIndex = NumberUtil.objToIntDefault(request.getParameter("pageIndex"), 1);
            Integer pageSize = NumberUtil.objToIntDefault(request.getParameter("pageSize"), 10);
            List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();
            if (id <= 0) {
                return H5CommonResponse.getNewInstance(false, "请求参数缺失", "", null).toString();
            }
            //获取借款分期配置信息
            AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
            JSONArray array = JSON.parseArray(resource.getValue());
            if (array == null) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
            }
            AfVisualH5ItemDo afVisualH5ItemDo = afVisualH5ItemService.getById(id);
            String value = afVisualH5ItemDo.getValue4();
            List<String> selectIds = new ArrayList<>();
            if (StringUtil.isNotBlank(value)) {
                String[] skuIds = value.split(",");
                Integer current = (pageIndex - 1) * pageSize;
                if (current < skuIds.length) {
                    for (int i = current; i < skuIds.length; i++) {
                        if (i - current <= pageSize) {
                            selectIds.add(skuIds[i]);
                        }
                    }
                }
            }
            List<HashMap> list = afGoodsService.getVisualGoodsByGoodsId(selectIds);
            for (HashMap goods : list) {
                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                goodsInfo.put("goodName", goods.get("name"));
                goodsInfo.put("rebateAmount", goods.get("rebate_amount"));
                goodsInfo.put("priceAmount", goods.get("price_amount"));
                goodsInfo.put("saleAmount", goods.get("sale_amount"));
                goodsInfo.put("goodsIcon", goods.get("goods_icon"));
                goodsInfo.put("goodsId", goods.get("id"));
                // 如果是分期免息商品，则计算分期
                Long goodsId = Long.parseLong(goods.get("id").toString());
                HashMap afActivityGoods = afActivityGoodsService.getVisualActivityGoodsByGoodsId(goodsId);
                if (afActivityGoods != null) {
                    goodsInfo.put("saleAmount", afActivityGoods.get("special_price"));
                }
                AfSchemeGoodsDo schemeGoodsDo = null;
                try {
                    schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
                } catch (Exception e) {
                    logger.error(e.toString());
                }
                JSONArray interestFreeArray = null;
                if (schemeGoodsDo != null) {
                    AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
                    String interestFreeJson = interestFreeRulesDo.getRuleJson();
                    if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                        interestFreeArray = JSON.parseArray(interestFreeJson);
                    }
                }
                List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                        new BigDecimal(goodsInfo.get("priceAmount").toString()), resource.getValue1(), resource.getValue2(), goodsId, "0");
                if (nperList != null) {
                    Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                    String isFree = (String) nperMap.get("isFree");
                    if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
                        nperMap.put("freeAmount", nperMap.get("amount"));
                    }
                    goodsInfo.put("nperMap", nperMap);
                }

                goodsList.add(goodsInfo);
            }

            return H5CommonResponse.getNewInstance(true, "", "", goodsList).toString();
        } catch (Exception e) {
            logger.error("changeUserAddress", e);
            return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
        }
    }

    /**
     * 获取分类商品
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getCategoryGoods", method = RequestMethod.POST)
    public String getCategoryGoods(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long id = NumberUtil.objToLongDefault(request.getParameter("id"), 0);
            String categorynName = ObjectUtils.toString(request.getParameter("categorynName"));
            Integer pageIndex = NumberUtil.objToIntDefault(request.getParameter("pageIndex"), 1);
            Integer pageSize = NumberUtil.objToIntDefault(request.getParameter("pageSize"), 10);
            List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();
            if (id <= 0) {
                return H5CommonResponse.getNewInstance(false, "请求参数缺失", "", null).toString();
            }
            //获取借款分期配置信息
            AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
            JSONArray array = JSON.parseArray(resource.getValue());
            if (array == null) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
            }
            AfVisualH5ItemDo afVisualH5ItemDo = afVisualH5ItemService.getById(id);
            JSONArray value = JSON.parseArray(afVisualH5ItemDo.getValue4());
            List<String> selectIds = new ArrayList<>();
            if (array != null) {
                for (int j = 0; j < value.size(); j++) {
                    JSONObject item = value.getJSONObject(j);
                    if (item.getString("categorynName").equals(categorynName)) {
                        selectIds = StringUtil.splitToList(item.getString("skuId"), ",");
                    }
                }
            }
            List<HashMap> list = afGoodsService.getVisualGoodsByGoodsId(selectIds);
            for (HashMap goods : list) {
                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                goodsInfo.put("goodName", goods.get("name"));
                goodsInfo.put("rebateAmount", goods.get("rebate_amount"));
                goodsInfo.put("priceAmount", goods.get("price_amount"));
                goodsInfo.put("saleAmount", goods.get("sale_amount"));
                goodsInfo.put("goodsIcon", goods.get("goods_icon"));
                goodsInfo.put("goodsId", goods.get("id"));
                // 如果是分期免息商品，则计算分期
                Long goodsId = Long.parseLong(goods.get("id").toString());
                HashMap afActivityGoods = afActivityGoodsService.getVisualActivityGoodsByGoodsId(goodsId);
                if (afActivityGoods != null) {
                    goodsInfo.put("saleAmount", afActivityGoods.get("special_price"));
                }
                AfSchemeGoodsDo schemeGoodsDo = null;
                try {
                    schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
                } catch (Exception e) {
                    logger.error(e.toString());
                }
                JSONArray interestFreeArray = null;
                if (schemeGoodsDo != null) {
                    AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
                    String interestFreeJson = interestFreeRulesDo.getRuleJson();
                    if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                        interestFreeArray = JSON.parseArray(interestFreeJson);
                    }
                }
                List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                        new BigDecimal(goodsInfo.get("priceAmount").toString()), resource.getValue1(), resource.getValue2(), goodsId, "0");
                if (nperList != null) {
                    Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                    String isFree = (String) nperMap.get("isFree");
                    if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
                        nperMap.put("freeAmount", nperMap.get("amount"));
                    }
                    goodsInfo.put("nperMap", nperMap);
                }

                goodsList.add(goodsInfo);
            }

            return H5CommonResponse.getNewInstance(true, "", "", goodsList).toString();
        } catch (Exception e) {
            logger.error("changeUserAddress", e);
            return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
        }
    }

    /**
     * 获取优惠券状态
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getCouponStatus", method = RequestMethod.POST)
    public String getCouponStatus(HttpServletRequest request, HttpServletResponse response) {
        try {
           List<Map<String, Object>> data = new ArrayList<>();
            Long visualId = NumberUtil.objToLongDefault(request.getParameter("visualId"), 0);
            if (visualId <= 0) {
                return H5CommonResponse.getNewInstance(false, "请求参数缺失", "", null).toString();
            }
            List<HashMap> list = afVisualH5ItemService.getCouponByVisualId(visualId);
            List<String> couponIds = new ArrayList<>();
            if(list != null && list.size() > 0){
                for (int i = 0; i < list.size(); i++){
                    JSONObject coupon = JSON.parseObject(list.get(i).get("value_4").toString());
                    JSONArray array = coupon.getJSONArray("list");
                    for (int j=0;j<array.size();j++){
                        JSONObject item = array.getJSONObject(i);
                        couponIds.add(item.getString("couponId")) ;
                    }
                }
            }
            if(couponIds.size()>0){
                List<AfCouponDo> couponList = afCouponService.getCouponByIds(couponIds);
                for (AfCouponDo afCouponDo: couponList) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    if(afCouponDo.getQuota() > afCouponDo.getQuotaAlready()){
                        item.put("id",afCouponDo.getRid());
                        item.put("isHas","Y");
                    }
                    else {
                        item.put("id",afCouponDo.getRid());
                        item.put("isHas","N");
                    }
                    if(afCouponDo.getStatus().equals("C")){
                        item.put("isHas","N");
                    }
                    data.add(item);
                }
            }
            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (Exception e) {
            logger.error("changeUserAddress", e);
            return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
        }
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        return null;
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
}