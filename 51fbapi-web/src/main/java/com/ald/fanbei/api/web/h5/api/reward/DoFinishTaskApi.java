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
    AfInterestFreeRulesService afInterestFreeRulesService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfIdNumberService afIdNumberService;


    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        String taskType = ObjectUtils.toString(context.getData("taskType"),null);
        String taskSecType = ObjectUtils.toString(context.getData("taskSecType"),null);
        String taskCondition = ObjectUtils.toString(context.getData("taskCondition"),null);
        Integer pageNo = NumberUtil.objToPageIntDefault(context.getData("pageNo").toString(), 1);
        Integer pageSize = NumberUtil.objToPageIntDefault(context.getData("pageSize").toString(), 10);
        Long userId = context.getUserId();
        List<HashMap> goodsList = new ArrayList<HashMap>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(StringUtil.equals(taskType, TaskType.browse.getCode()) || StringUtil.equals(taskType, TaskType.shopping.getCode()) ){
            AfGoodsQuery query = new AfGoodsQuery();
            query.setPageNo(pageNo);
            query.setPageSize(pageSize);
            if(StringUtil.equals(taskSecType, TaskSecType.brand.getCode())){
                query.setBrandId(Long.parseLong(taskCondition));
                goodsList = afGoodsService.getTaskGoodsList(query);
            }else if(StringUtil.equals(taskSecType, TaskSecType.category.getCode())){
                query.setCategoryId(Long.parseLong(taskCondition));
                goodsList = afGoodsService.getTaskGoodsList(query);
            }
            //获取借款分期配置信息
            AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
            JSONArray array = JSON.parseArray(resource.getValue());
            if (array == null) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
            }
            for (HashMap goods : goodsList) {
                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                goodsInfo.put("goodName", goods.get("name"));
                goodsInfo.put("rebateAmount", goods.get("rebate_amount"));
                goodsInfo.put("priceAmount", goods.get("price_amount"));
                goodsInfo.put("saleAmount", goods.get("sale_amount"));
                goodsInfo.put("goodsIcon", goods.get("goods_icon"));
                goodsInfo.put("goodsId", goods.get("Rid"));
                // 如果是分期免息商品，则计算分期
                Long goodsId = Long.parseLong(goods.get("Rid").toString());
                if(null != goods.get("special_price")){
                    if(Double.parseDouble(goods.get("special_price")+"") < Double.parseDouble(goods.get("sale_amount")+"")){
                        goodsInfo.put("saleAmount", goods.get("special_price"));
                    }
                }
                JSONArray interestFreeArray = null;
                if (goods.get("interest_free_id") != null) {
                    Long interestFreeId= Long.parseLong(goods.get("interest_free_id").toString());
                    AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(interestFreeId);
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
        }else if(StringUtil.equals(taskType, TaskType.strong_risk.getCode())){
            AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
            if(StringUtil.equals(afUserAuthDo.getRealnameStatus(),"Y")){
                resp.addResponseData("realnameStatus","Y");
                AfIdNumberDo afIdNumberDo = afIdNumberService.getIdNumberInfoByUserId(userId);
                if(null == afIdNumberDo){
                    resp.addResponseData("realName","");
                }else {
                    resp.addResponseData("realName",afIdNumberDo.getName());
                }
            }else {
                resp.addResponseData("realnameStatus","N");
                resp.addResponseData("realName","");
            }
            if(StringUtil.equals(afUserAuthDo.getBankcardStatus(),"Y")){
                resp.addResponseData("bankcardStatus","Y");
            }else {
                resp.addResponseData("bankcardStatus","N");
            }
        }

        return resp;
    }



}
