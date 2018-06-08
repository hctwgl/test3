package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BottomGoodsPageFlag;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfBottomGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5Dto;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5ItemDto;
import com.ald.fanbei.api.dal.domain.query.AfBottomGoodsQuery;
import com.ald.fanbei.api.dal.domain.query.AfShopQuery;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.vo.AfShopVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生活controller
 *
 * @author wangli
 * @date 2018/4/11 14:24
 */
@Controller
@RequestMapping("/fanbei-web/life")
public class AppH5LifeController extends BaseController {

    Logger logger = LoggerFactory.getLogger(AppH5LifeController.class);

    // 商城默认返回数量
    private static final int SHOP_NUM = 10;

    // 优惠位置标识  对应H5资源中的sort
    private static final int POSITION_FLAG_DISCOUNTS = 2;

    // 分期未知标识
    private static final int POSITION_FALG_STAGE = 4;

    // 生活页底部商品默认每页显示30个
    private static final int PAGE_SIZE = 30;

    @Autowired
    private AfShopService afShopService;

    @Autowired
    private AfResourceService afResourceService;

    @Autowired
    private AfResourceH5Service afResourceH5Service;

    @Autowired
    private AfResourceH5ItemService afResourceH5ItemService;

    @Autowired
    private AfBottomGoodsService afBottomGoodsService;

    @Autowired
    private AfSchemeGoodsService afSchemeGoodsService;

    @Autowired
    private AfInterestFreeRulesService afInterestFreeRulesService;

    @Autowired
    private BizCacheUtil bizCacheUtil;

    @Resource
    private AfUserService afUserService;

    @Resource
    private AfBorrowRecycleOrderService borrowRecycleOrderService;

    @Resource
    private AfBorrowCashService borrowCashService;

    @RequestMapping(value = "/categoryAndBanner", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getCategoryAndBanner(HttpServletRequest request) {
        try {
            //doWebCheck(request, false);
            String cacheKey = "life:categoryAndBanner";
            Object cacheResult = bizCacheUtil.getObject(cacheKey);
            String result = "";
            if (cacheResult != null) {
                result = cacheResult.toString();
            }
            if (StringUtils.isBlank(result)) {
                Map<String, Object> data = new HashMap<>();
                List<AfShopVo> shopList = findShopList();
                data.put("shopList", shopList);
                List<Map<String, Object>> bannerList = findBannerList();
                data.put("bannerList", findBannerList());

                result = H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
                if (shopList.size() > 0 || bannerList.size() > 0)
                    bizCacheUtil.saveObject(cacheKey, result, 180);
            }
            return result;
        } catch (Exception e) {
            logger.error("/fanbei-web/life/categoryAndBanner error：", e);
            return H5CommonResponse.getNewInstance(false, "获取资源失败", null, "").toString();
        }
    }

    @RequestMapping(value = "/h5ResourceList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String findH5ResourceList(HttpServletRequest request) {
        try {
            //doWebCheck(request, false);
            FanbeiWebContext context = doWebCheck(request, false);
            boolean isNeedRecycle = false;
            logger.info("h5ResourceList context"+context+",isNeedRecycle="+isNeedRecycle);
            if(context.isLogin()){
                AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
                AfBorrowCashDo borrowCashDo = borrowCashService.getDealingCashByUserId(afUser.getRid());
                logger.info("h5ResourceList borrowCashDo"+JSON.toJSONString(borrowCashDo)+",isNeedRecycle="+isNeedRecycle);
                if (borrowCashDo!= null){
                    AfBorrowRecycleOrderDo afBorrowRecycleOrderDo = borrowRecycleOrderService.getBorrowRecycleOrderByBorrowId(borrowCashDo.getRid());
                    if (afBorrowRecycleOrderDo != null){
                        isNeedRecycle = true;
                    }
                    logger.info("h5ResourceList afBorrowRecycleOrderDo"+JSON.toJSONString(afBorrowRecycleOrderDo)+",isNeedRecycle="+isNeedRecycle);
                }
            }
            logger.info("h5ResourceList isNeedRecycle end"+isNeedRecycle);
            //保留回收中的展示入口，非回收隐藏回收接口
//            String cacheKey = "life:h5ResourceList";
//            Object cacheResult = bizCacheUtil.getObject(cacheKey);
            String result = "";
//            if (cacheResult != null) {
//                result = cacheResult.toString();
//            }
            if (StringUtils.isBlank(result)) {
                List<AfResourceH5Dto> resourceH5Dtos = afResourceH5Service
                        .selectByStatus(BottomGoodsPageFlag.LIFE.getCode());
                if (CollectionUtil.isEmpty(resourceH5Dtos)) {
                    logger.error("Cannot find tag:" + BottomGoodsPageFlag.LIFE.getCode() + " , please check H5Resource config.");
                    return H5CommonResponse.getNewInstance(false, "页面走丢了").toString();
                }

                List<Map<String, Object>> data = buildResourceList(resourceH5Dtos,isNeedRecycle);
                result = H5CommonResponse
                        .getNewInstance(true, "成功", "", data).toString();

//                if (data.size() > 0) {
//                    bizCacheUtil.saveObject(cacheKey, result, 180);
//                }
            }
            logger.info("h5ResourceList result ="+result);
            return result;
        } catch (Exception e) {
            logger.error("/fanbei-web/life/h5ResourceList error：", e);
            return H5CommonResponse.getNewInstance(false, "获取资源失败", null, "").toString();
        }
    }

    @RequestMapping(value = "/goods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String findBottomGoods(HttpServletRequest request, @RequestParam("pageNo") Integer pageNo) {
        try {
            //doWebCheck(request, false);
            String cacheKey = "life:goods:" + pageNo;
            Object cacheResult = bizCacheUtil.getObject(cacheKey);
            String result = "";
            if (cacheResult != null) {
                result = cacheResult.toString();
            }
            if (StringUtils.isBlank(result)) {
                Map<String, Object> data = new HashMap<>();
                data.put("floorImage", getFloorImage());
                data.put("list", doFindBottomGoods(pageNo));
                data.put("pageNo", pageNo);
                result = H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
                bizCacheUtil.saveObject(cacheKey, result, 120);
            }

            return result;
        } catch (Exception e) {
            logger.error("/fanbei-web/life/goods error：", e);
            return H5CommonResponse.getNewInstance(false, "获取资源失败", null, "").toString();
        }
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

    // 查找分类列表
    private List<AfShopVo> findShopList() {
        List<AfShopVo> result = new ArrayList<>();

        AfShopQuery query = new AfShopQuery();
        query.setPageNo(1);
        query.setPageSize(SHOP_NUM);
        List<AfShopDo> shopList = afShopService.getShopList(query);

        if (CollectionUtil.isNotEmpty(shopList)) {
            result = CollectionConverterUtil.convertToListFromList(shopList, new Converter<AfShopDo, AfShopVo>() {
                @Override
                public AfShopVo convert(AfShopDo source) {
                    AfShopVo vo = new AfShopVo();
                    vo.setRid(source.getRid());
                    vo.setName(source.getName());
                    vo.setType(source.getType());
                    vo.setShopUrl(source.getShopUrl());
                    vo.setIcon(source.getNewIcon());
                    return vo;
                }
            });
        }

        return result;
    }

    // 查找banner列表
    private List<Map<String, Object>> findBannerList() {
        List<Map<String, Object>> result = new ArrayList<>();

        List<AfResourceDo> bannerList = null;
        String invelomentType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(invelomentType)
                || Constants.INVELOMENT_TYPE_TEST.equals(invelomentType)) {
            bannerList = afResourceService
                    .getResourceHomeListByTypeOrderBy(AfResourceType.GGHomeTopBanner.getCode());
        } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(invelomentType)){
            bannerList = afResourceService
                    .getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.GGHomeTopBanner.getCode());
        }

        if (CollectionUtil.isNotEmpty(bannerList)) {
            result = CollectionConverterUtil.convertToListFromList(bannerList,
                    new Converter<AfResourceDo, Map<String, Object>>() {
                        @Override
                        public Map<String, Object> convert(AfResourceDo source) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("imageUrl", source.getValue());
                            map.put("type", source.getValue1());
                            map.put("content", source.getValue2());
                            map.put("sort", source.getSort());
                            return map;
                        }
                    });
        }

        return result;
    }

    // 构建结果数据
    private List<Map<String, Object>> buildResourceList(List<AfResourceH5Dto> resourceH5Dtos,boolean isNeedRecycle) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (AfResourceH5Dto resource : resourceH5Dtos) {
            final List<AfResourceH5ItemDto> itemList = afResourceH5ItemService.selectByModelId(resource.getRid());

            if (!configIsEffective(resource, itemList)) {
                continue;
            }

            Map<String, Object> e = new HashMap<>();
            // 从列表中移除楼层图
            AfResourceH5ItemDto floorImageResource = itemList.remove(0);
            e.put("floorImage", floorImageResource.getValue3());
            e.put("type", resource.getSort());
            e.put("isNeedRecycle",isNeedRecycle);
            logger.info("buildResourceList e="+JSON.toJSONString(e)+",isNeedRecycle="+isNeedRecycle);
            List<Map<String, Object>> itemMapList = CollectionConverterUtil.convertToListFromList(itemList,
                    new Converter<AfResourceH5ItemDto, Map<String, Object>>() {
                        @Override
                        public Map<String, Object> convert(AfResourceH5ItemDto source) {
                            Map<String, Object> itemMap = new HashMap<>();
                            itemMap.put("imgUrl", source.getValue3());
                            itemMap.put("skipUrl", source.getValue1());
                            itemMap.put("sort", source.getSort());
                            return itemMap;
                        }
                    });
            e.put("list", itemMapList);
            result.add(e);
        }

        return result;
    }

    // 检查资源配置是否有效
    private boolean configIsEffective(AfResourceH5Dto resource, List<AfResourceH5ItemDto> itemList) {
        // 根据约定，生活页面H5资源的第一个项应为楼层图，配置不完整则不显示整个模块
        if (CollectionUtil.isEmpty(itemList) || itemList.size() == 1) {
            return false;
        }
        // 爱优惠布局为1+2，维护不完整则不展示
        if (POSITION_FLAG_DISCOUNTS == resource.getSort()) {
            if (itemList.size() != 4) {
                return false;
            }
        }

        return true;
    }

    // 获取生活首页爱分期楼层图
    private String getFloorImage() {
        List<AfResourceH5ItemDo> itemList = afResourceH5ItemService
                .findListByModelTagAndSort(BottomGoodsPageFlag.LIFE.getCode(), POSITION_FALG_STAGE);
        return CollectionUtil.isEmpty(itemList) ? "" : itemList.get(itemList.size() - 1).getValue3();
    }

    // 获取生活首页底部商品
    private List<Map<String, Object>> doFindBottomGoods(Integer pageNo) {
        List<Map<String, Object>> result = new ArrayList<>();

        AfBottomGoodsQuery query = new AfBottomGoodsQuery();
        query.setPageNo(pageNo);
        query.setPageSize(PAGE_SIZE);
        query.setPageFlag(BottomGoodsPageFlag.LIFE.getCode());
        List<AfBottomGoodsDto> goodsDos = afBottomGoodsService.findGoodsByPageFlag(query);
        if (CollectionUtil.isEmpty(goodsDos)) {
            return result;
        }

        result = CollectionConverterUtil.convertToListFromList(goodsDos, new Converter<AfBottomGoodsDto, Map<String, Object>>() {
            @Override
            public Map<String, Object> convert(AfBottomGoodsDto source) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", source.getGoodsDo().getRid().toString());
                map.put("icon", source.getGoodsDo().getGoodsIcon());
                map.put("name", source.getGoodsDo().getName());
                map.put("saleAmount", source.getGoodsDo().getSaleAmount().toString());
                map.put("sort", source.getSort());
                return map;
            }
        });

        fillNperInfo(result);

        return result;
    }

    // 填充分期信息
    private void fillNperInfo(List<Map<String, Object>> result) {
        // 获取借款分期配置信息
        AfResourceDo borrowConfig = afResourceService
                .getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,  Constants.RES_BORROW_CONSUME);
        JSONArray array = JSON.parseArray(borrowConfig.getValue());
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }

        for (Map<String, Object> e : result) {
            // 如果是分期免息商品，则计算分期
            Long goodsId = Long.valueOf((String) e.get("id"));
            BigDecimal saleAmount = new BigDecimal((String) e.get("saleAmount"));
            AfSchemeGoodsDo schemeGoodsDo = null;
            try {
                schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
            } catch (Exception ex) {
                logger.error(ex.toString());
            }
            JSONArray interestFreeArray = null;
            if (schemeGoodsDo != null) {
                AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService
                        .getById(schemeGoodsDo.getInterestFreeId());
                String interestFreeJson = interestFreeRulesDo.getRuleJson();
                if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                    interestFreeArray = JSON.parseArray(interestFreeJson);
                }
            }
            List<Map<String, Object>> nperList = InterestFreeUitl
                    .getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                            saleAmount, borrowConfig.getValue1(), borrowConfig.getValue2(),
                            goodsId,"0");
            if (nperList != null) {
                Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                String isFree = (String) nperMap.get("isFree");
                if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
                    //不影响其他业务，此处加
                    Object oAmount =  nperMap.get("amount");
                    String amount = "";
                    if(oAmount != null){
                        amount = oAmount.toString();
                    }
                    nperMap.put("amount",substringAmount(amount));
                    nperMap.put("freeAmount",substringAmount(amount));
                }
                e.put("nperMap", nperMap);
            }
        }
    }

    private  BigDecimal substringAmount(String amount) {
        BigDecimal substringAmount = new BigDecimal(0);
        try{
            //判断小数点后面的是否是00,10.分别去掉两位，一位。去掉之后大于等于8位，则截取前8位。
            String tempNumber = "0";
            String afterNumber =  amount.substring(amount.indexOf(".")+1,amount.length());
            if("00".equals(afterNumber)){
                tempNumber = amount.substring(0,amount.length()-3);
            } else if("10".equals(afterNumber)){
                tempNumber = amount.substring(0,amount.length()-1);
            }else{
                tempNumber = amount;
            }
            if(tempNumber.length() >8 ){
                tempNumber =  tempNumber.substring(0,8);
                String t = tempNumber.substring(tempNumber.length()-1, tempNumber.length());
                if(".".equals(t)){
                    tempNumber =  tempNumber.substring(0,tempNumber.length()-1);
                }
            }
            substringAmount = new BigDecimal(tempNumber);

        }catch(Exception e){
            logger.error("substringAmount error"+e);
        }
        return substringAmount;
    }
}
