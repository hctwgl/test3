package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.TaskSecType;
import com.ald.fanbei.api.common.enums.TaskType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 去完成任务
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("doFinishTaskApi")
public class DoFinishTaskApi implements H5Handle {

    @Resource
    AfGoodsService afGoodsService;
    @Resource
    AfActivityGoodsService afActivityGoodsService;
    @Resource
    AfSchemeGoodsService afSchemeGoodsService;
    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;
    @Resource
    AfResourceService afResourceService;


    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        String taskType = ObjectUtils.toString(context.getData("taskType"),null);
        String taskSecType = ObjectUtils.toString(context.getData("taskSecType"),null);
        String taskCondition = ObjectUtils.toString(context.getData("taskCondition"),null);
        Integer pageNo = NumberUtil.objToPageIntDefault(context.getData("pageNo").toString(), 1);
        Integer pageSize = NumberUtil.objToPageIntDefault(context.getData("pageSize").toString(), 10);
        List<AfGoodsDo> goodsList = new ArrayList<AfGoodsDo>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(StringUtil.equals(taskType, TaskType.browse.getCode()) || StringUtil.equals(taskType, TaskType.shopping.getCode()) ){
            if(StringUtil.equals(taskSecType, TaskSecType.brand.getCode())){
                AfGoodsQuery query = new AfGoodsQuery();
                query.setBrandId(Long.parseLong(taskCondition));
                query.setPageNo(pageNo);
                query.setPageSize(pageSize);
                goodsList = afGoodsService.getAfGoodsListByBrandId(query);
            }else if(StringUtil.equals(taskSecType, TaskSecType.category.getCode())){
                AfGoodsQuery query = new AfGoodsQuery();
                query.setCategoryId(Long.parseLong(taskCondition));
                query.setPageNo(pageNo);
                query.setPageSize(pageSize);
                goodsList = afGoodsService.getGoodsVerifyByCategoryId(query);
            }
        }
        //获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        JSONArray array = JSON.parseArray(resource.getValue());
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }
        for (AfGoodsDo goods : goodsList) {
            Map<String, Object> goodsInfo = new HashMap<String, Object>();
            goodsInfo.put("goodName", goods.getName());
            goodsInfo.put("rebateAmount", goods.getRebateAmount());
            goodsInfo.put("priceAmount", goods.getPriceAmount());
            goodsInfo.put("saleAmount", goods.getPriceAmount());
            goodsInfo.put("goodsIcon", goods.getGoodsIcon());
            goodsInfo.put("goodsId", goods.getRid());
            // 如果是分期免息商品，则计算分期
            Long goodsId = goods.getRid();
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

            list.add(goodsInfo);
        }

        resp.addResponseData("goodsList",list);
        return resp;
    }



}
