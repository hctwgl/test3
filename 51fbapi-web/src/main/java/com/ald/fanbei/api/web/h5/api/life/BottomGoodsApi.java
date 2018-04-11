package com.ald.fanbei.api.web.h5.api.life;

import com.ald.fanbei.api.biz.service.AfBottomGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BottomGoodsPageFlag;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfBottomGoodsDto;
import com.ald.fanbei.api.dal.domain.query.AfBottomGoodsQuery;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查找生活首页底部商品
 *
 * @author wangli
 * @date 2018/4/11 8:07
 */
@Component("bottomGoodsApi")
public class BottomGoodsApi implements H5Handle {

    @Autowired
    private AfBottomGoodsService afBottomGoodsService;

    @Autowired
    private AfResourceService afResourceService;

    @Autowired
    private AfSchemeGoodsService afSchemeGoodsService;

    @Autowired
    private AfInterestFreeRulesService afInterestFreeRulesService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("floorImage", getFloorImage());
            data.put("list", findBottomGoods(context));
            resp.setResponseData(data);
            return resp;
        } catch (FanbeiException e) {
            return new H5HandleResponse(context.getId(), e.getErrorCode());
        }
    }

    // 获取生活首页爱分期楼层图
    public String getFloorImage() {
        return null;
    }

    // 获取生活首页底部商品
    private List<Map<String, Object>> findBottomGoods(Context context) {
        List<Map<String, Object>> result = new ArrayList<>();

        AfBottomGoodsQuery query = new AfBottomGoodsQuery();
        query.setPageNo((Integer) context.getData("pageNo"));
        query.setPageFlag(BottomGoodsPageFlag.LIFE.getCode());
        List<AfBottomGoodsDto> goodsDos = afBottomGoodsService.findGoodsByPageFlag(query);
        if (CollectionUtil.isEmpty(goodsDos)) {
            return result;
        }

        result = CollectionConverterUtil.convertToListFromList(goodsDos, new Converter<AfBottomGoodsDto, Map<String, Object>>() {
            @Override
            public Map<String, Object> convert(AfBottomGoodsDto source) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", source.getGoodsDo().getRid());
                map.put("icon", source.getGoodsDo().getGoodsIcon());
                map.put("name", source.getGoodsDo().getName());
                map.put("saleAmount", source.getGoodsDo().getSaleAmount().toString());
                map.put("sort", source.getSort());
                return map;
            }
        });



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
            /*if (nperList != null) {
                Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                String isFree = (String) nperMap.get("isFree");
                if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
                    nperMap.put("freeAmount", nperMap.get("amount"));
                }
                goodsInfo.put("nperMap", nperMap);
            }*/
        }
    }
}
