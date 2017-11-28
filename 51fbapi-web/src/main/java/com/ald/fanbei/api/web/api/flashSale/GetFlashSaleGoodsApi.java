package com.ald.fanbei.api.web.api.flashSale;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.query.AfGoodsCategoryQuery;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @类描述：商品分类
 * @author chefeipeng 2017年10月25日下午2:03:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getFlashSaleGoodsApi")
public class GetFlashSaleGoodsApi implements ApiHandle {


    @Resource
    AfResourceService afResourceService;
    @Resource
    AfGoodsService afGoodsService;
    @Resource
    AfSchemeGoodsService afSchemeGoodsService;
    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;
    @Resource
    AfGoodsPriceService afGoodsPriceService;
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfActivityGoodsService afActivityGoodsService;
    private FanbeiContext contextApp;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Map<String,Object> data = new HashMap<String,Object>();
        List<Object> topBannerList = new ArrayList<Object>();
        String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        int count = 0;
        //正式环境和预发布环境区分，banner轮播图展示
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
            //新版,旧版,banner图不一样
            String homeBanner = AfResourceType.LimitedPurchaseBanner.getCode();
            topBannerList = getObjectWithResourceDolist(
                    afResourceService.getResourceHomeListByTypeOrderBy(homeBanner));
        } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ){
            //新版,旧版,banner图不一样
            String homeBanner = AfResourceType.LimitedPurchaseBanner.getCode();
            topBannerList = getObjectWithResourceDolist(
                    afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(homeBanner));
        }
        data.put("BannerList",topBannerList);
        //商品展示
        AfGoodsQuery query = getCheckParam(requestDataVo);
        List<AfEncoreGoodsDto> list = afGoodsService.selectFlashSaleGoods(query);
        List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
        //获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        JSONArray array = JSON.parseArray(resource.getValue());
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }
        Iterator<Object> it = array.iterator();
        while (it.hasNext()) {
            JSONObject json = (JSONObject) it.next();
            if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
                it.remove();
                break;
            }
        }
        for(AfEncoreGoodsDto goodsDo : list) {
            Map<String, Object> goodsInfo = new HashMap<String, Object>();
            goodsInfo.put("goodName",goodsDo.getName());
            goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
            goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
            goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
            goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
            goodsInfo.put("goodsId", goodsDo.getRid());
            goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
            goodsInfo.put("goodsType", "0");
            // 如果是分期免息商品，则计算分期
            Long goodsId = goodsDo.getRid();
            AfActivityGoodsDo afActivityGoodsDo = afActivityGoodsService.getActivityGoodsByGoodsIdAndType(goodsDo.getRid());
            if(null != afActivityGoodsDo){
                count = new Long(afActivityGoodsDo.getInitialCount()).intValue();
            }else{
                count = 0;
            }
            Integer total = afGoodsPriceService.selectSumStock(goodsId);
            goodsInfo.put("total",total+count);
            Integer volume = afOrderService.selectSumCountByGoodsId(goodsId);
            goodsInfo.put("volume",volume+count);
            AfSchemeGoodsDo schemeGoodsDo = null;
            try {
                schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
            } catch(Exception e){
                logger.error(e.toString());
            }
            JSONArray interestFreeArray = null;
            if(schemeGoodsDo != null){
                AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
                String interestFreeJson = interestFreeRulesDo.getRuleJson();
                if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                    interestFreeArray = JSON.parseArray(interestFreeJson);
                }
            }
            List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                    goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
            if(nperList!= null){
                goodsInfo.put("goodsType", "1");
                Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                String isFree = (String)nperMap.get("isFree");
                if(InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
                    nperMap.put("freeAmount", nperMap.get("amount"));
                }
                goodsInfo.put("nperMap", nperMap);
            }

            goodsList.add(goodsInfo);
        }
        data.put("GoodsList",goodsList);
        resp.setResponseData(data);
        return resp;
    }

    private List<Object> getObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
        List<Object> bannerList = new ArrayList<Object>();
        for (AfResourceDo afResourceDo : bannerResclist) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("imageUrl", afResourceDo.getValue());
            data.put("titleName", afResourceDo.getName());
            data.put("type", afResourceDo.getValue1());
            data.put("content", afResourceDo.getValue2());
            data.put("sort", afResourceDo.getSort());
            bannerList.add(data);
        }

        return bannerList;
    }

    private AfGoodsQuery getCheckParam(RequestDataVo requestDataVo){
        Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
        AfGoodsQuery query = new AfGoodsQuery();
        query.setPageNo(pageNo);
        return query;
    }
}
