package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BottomGoodsPageFlag;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfBottomGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5Dto;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5ItemDto;
import com.ald.fanbei.api.dal.domain.query.AfBottomGoodsQuery;
import com.ald.fanbei.api.dal.domain.query.AfShopQuery;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.vo.AfShopVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    // 商城默认返回数量
    private static final int SHOP_NUM = 10;

    // 优惠位置标识  对应H5资源中的sort
    private static final int POSITION_FLAG_DISCOUNTS = 2;

    private static final int PAGE_SIZE = 30;

    private static final int POSITION_FALG_STAGE = 4;

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

    @RequestMapping(value = "/categoryAndBanner", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getCategoryAndBanner(HttpServletRequest request) {
        try {
            doWebCheck(request, false);

            Map<String, Object> data = new HashMap<>();
            data.put("shopList", findShopList());
            data.put("bannerList", findBannerList());

            return H5CommonResponse.getNewInstance(true, "成功","",data).toString();
        } catch (Exception e) {
            logger.error("/fanbei-web/life/categoryAndBanner error = {}", e.getStackTrace());
            return H5CommonResponse.getNewInstance(false, "获取资源失败", null, "").toString();
        }
    }

    @RequestMapping(value = "/h5ResourceList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String findH5ResourceList(HttpServletRequest request) {
        try {
            doWebCheck(request, false);

            List<AfResourceH5Dto> resourceH5Dtos = afResourceH5Service
                    .selectByStatus(BottomGoodsPageFlag.LIFE.getCode());
            if (CollectionUtil.isEmpty(resourceH5Dtos)) {
                logger.error("Cannot find tag:" + BottomGoodsPageFlag.LIFE.getCode() + " , please check H5Resource config.");
                return H5CommonResponse.getNewInstance(false, "页面走丢了").toString();
            }
            return H5CommonResponse
                    .getNewInstance(true, "成功", "", buildResourceList(resourceH5Dtos)).toString();
        } catch (Exception e) {
            logger.error("/fanbei-web/life/h5ResourceList error={}", e.getStackTrace());
            return H5CommonResponse.getNewInstance(false, "获取资源失败", null, "").toString();
        }
    }

    @RequestMapping(value = "/goods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String findBottomGoods(HttpServletRequest request, @RequestParam("pageNo") Integer pageNo) {
        try {
            doWebCheck(request, false);

            Map<String, Object> data = new HashMap<>();
            data.put("floorImage", getFloorImage());
            data.put("list", doFindBottomGoods(pageNo));
            data.put("pageNo", pageNo);
            return H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
        } catch (Exception e) {
            logger.error("/fanbei-web/life/goods error={}", e.getStackTrace());
            return H5CommonResponse.getNewInstance(false, "获取资源失败", null, "").toString();
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
    private List<Map<String, Object>>  buildResourceList(List<AfResourceH5Dto> resourceH5Dtos) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (AfResourceH5Dto resource : resourceH5Dtos) {
            final List<AfResourceH5ItemDto> itemList = afResourceH5ItemService.selectByModelId(resource.getId());

            if (!configIsEffective(resource, itemList)) {
                continue;
            }

            Map<String, Object> e = new HashMap<>();
            // 从列表中移除楼层图
            AfResourceH5ItemDto floorImageResource = itemList.remove(0);
            e.put("floorImage", floorImageResource.getValue3());
            e.put("type", resource.getSort());
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
        // 根据约定，生活页面H5资源的第一个项应为楼层图
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
                /*String isFree = (String) nperMap.get("isFree");
                if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
                    nperMap.put("freeAmount", nperMap.get("amount"));
                }*/
                e.put("price", nperMap.get("amount"));
                e.put("nperNum", nperMap.get("nper"));
            }
        }
    }
}
