package com.ald.fanbei.api.web.api.agencybuy;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaRespBo;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.VirtualGoodsCateogy;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author suweili 2017年5月29日下午3:50:12
 * @类描述：获取代买分期信息
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAgencyNperInfoApi")
public class GetAgencyNperInfoApi implements ApiHandle {
	@Resource
	RiskUtil riskUtil;
	
	@Resource
	AfUserVirtualAccountService afUserVirtualAccountService;
	
    @Resource
    AfResourceService afResourceService;

    @Resource
    AfGoodsService afGoodsService;

    @Resource
    AfSchemeGoodsService afSchemeGoodsService;
    
    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;

    private JSONArray getInterestFreeArray(String numId,String type){
    	JSONArray interestFreeArray = null;
    	if (StringUtils.isBlank(numId)) {
            return null;
        }
    	Long goodsId = 0L;
    	if(StringUtils.equals(type,OrderType.TAOBAO.getCode())){
    		//获取商品信息
            AfGoodsDo afGoodsDo = afGoodsService.getGoodsByNumId(numId);
            if (null == afGoodsDo) {
            	return null;
            }
            goodsId = afGoodsDo.getRid();
    	}else{
    		goodsId = NumberUtil.objToLongDefault(numId, 0);
    	}
        //通过商品查询免息规则配置
        AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
        if(null == afSchemeGoodsDo){
        	return null;
        }
        Long interestFreeId = afSchemeGoodsDo.getInterestFreeId();
        AfInterestFreeRulesDo afInterestFreeRulesDo = afInterestFreeRulesService.getById(interestFreeId);
        if (null != afInterestFreeRulesDo && StringUtils.isNotBlank(afInterestFreeRulesDo.getRuleJson())) {
            interestFreeArray = JSON.parseArray(afInterestFreeRulesDo.getRuleJson());
        }
    	return interestFreeArray;
    }
    
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        Map<String, Object> params = requestDataVo.getParams();
        BigDecimal amount = NumberUtil.objToBigDecimalDefault(params.get("amount"), BigDecimal.ZERO);
        String goodsName = ObjectUtils.toString(requestDataVo.getParams().get("goodsName"));
        String type = ObjectUtils.toString(requestDataVo.getParams().get("type"), OrderType.TAOBAO.getCode());
        
        JSONArray interestFreeArray = null;
        
        if(params.get("numId") != null) {
            String numId = params.get("numId") + "";
            interestFreeArray = getInterestFreeArray(numId,type);
        }

        //获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        JSONArray array = JSON.parseArray(resource.getValue());
        //删除2分期
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }
        removeSecondNper(array);
        
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
        BigDecimal useableAmount = BigDecimalUtil.subtract(afUserAccountDo.getAuAmount(), afUserAccountDo.getUsedAmount());
        
        //是否是限额类目
		String isQuotaGoods = "N";
        if (!StringUtil.isBlank(goodsName)) {
    		RiskVirtualProductQuotaRespBo quotaBo = riskUtil.virtualProductQuota(userId.toString(), "", goodsName);
    		String data = quotaBo.getData();
    		if (StringUtils.isNotBlank(data)&&!StringUtil.equals(data, "{}")) {
    			JSONObject json = JSONObject.parseObject(data);
    			resp.addResponseData("goodsTotalAmount", json.getBigDecimal("amount"));
    			String virtualCode = json.getString("virtualCode");
    			BigDecimal goodsUseableAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(userId, virtualCode, json.getBigDecimal("amount"));
    			resp.addResponseData("goodsUseableAmount", goodsUseableAmount);
    			VirtualGoodsCateogy virtualGoodsCateogy = VirtualGoodsCateogy.findRoleTypeByCode(virtualCode);
    			resp.addResponseData("categoryName", virtualGoodsCateogy.getName());
    			if (goodsUseableAmount.compareTo(useableAmount) < 0) {
    				useableAmount = goodsUseableAmount;
    				isQuotaGoods = "Y";
    			}
    		}      	
        }
        BigDecimal calculateAmount = amount;
        if (amount.compareTo(useableAmount) > 0) {
        	calculateAmount = useableAmount;
        }
        List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
        		calculateAmount, resource.getValue1(), resource.getValue2());

        resp.addResponseData("instalmentAmount", amount);
        resp.addResponseData("useableAmount", useableAmount);
        resp.addResponseData("isQuotaGoods", isQuotaGoods);
        
        resp.addResponseData("nperList", nperList);

        return resp;
    }

    private void removeSecondNper(JSONArray array) {
        if (array == null) {
            return;
        }
        Iterator<Object> it = array.iterator();
        while (it.hasNext()) {
            JSONObject json = (JSONObject) it.next();
            if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
                it.remove();
                break;
            }
        }

    }
}
