/**
 *
 */
package com.ald.fanbei.api.web.api.agencybuy;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author suweili 2017年5月29日下午3:50:12
 * @类描述：获取代买分期信息
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("GetTradeNperInfoApi")
public class GetTradeNperInfoApi implements ApiHandle {
    @Resource
    AfResourceService afResourceService;

//    @Resource
//    AfGoodsService afGoodsService;
//
//    @Resource
//    AfSchemeGoodsService afSchemeGoodsService;
//
//    @Resource
//    AfInterestFreeRulesService afInterestFreeRulesService;
//
//    private JSONArray getInterestFreeArray(String numId,String type){
//    	JSONArray interestFreeArray = null;
//    	if (StringUtils.isBlank(numId)) {
//            return null;
//        }
//    	Long goodsId = 0L;
//    	if(StringUtils.equals(type,OrderType.TAOBAO.getCode())){
//    		//获取商品信息
//            AfGoodsDo afGoodsDo = afGoodsService.getGoodsByNumId(numId);
//            if (null == afGoodsDo) {
//            	return null;
//            }
//            goodsId = afGoodsDo.getRid();
//    	}else{
//    		goodsId = NumberUtil.objToLongDefault(numId, 0);
//    	}
//        //通过商品查询免息规则配置
//        AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
//        if(null == afSchemeGoodsDo){
//        	return null;
//        }
//        Long interestFreeId = afSchemeGoodsDo.getInterestFreeId();
//        AfInterestFreeRulesDo afInterestFreeRulesDo = afInterestFreeRulesService.getById(interestFreeId);
//        if (null != afInterestFreeRulesDo && StringUtils.isNotBlank(afInterestFreeRulesDo.getRuleJson())) {
//            interestFreeArray = JSON.parseArray(afInterestFreeRulesDo.getRuleJson());
//        }
//    	return interestFreeArray;
//    }
    
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

        Map<String, Object> params = requestDataVo.getParams();
        BigDecimal amount = NumberUtil.objToBigDecimalDefault(params.get("amount"), BigDecimal.ZERO);

        //获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_TRADE);
        JSONArray array = JSON.parseArray(resource.getValue());
        List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, null, BigDecimal.ONE.intValue(),
                amount, resource.getValue1(), resource.getValue2());

        resp.addResponseData("nperList", nperList);

        return resp;
    }

}
