/**
 * 
 */
package com.ald.fanbei.api.web.api.agencybuy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：获取代买分期信息
 * @author suweili 2017年5月29日下午3:50:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAgencyNperInfoApi")
public class GetAgencyNperInfoApi implements ApiHandle {
	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		
		
		Map<String, Object> params = requestDataVo.getParams();
		BigDecimal amount = NumberUtil.objToBigDecimalDefault(params.get("amount"), BigDecimal.ZERO);

		//获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
		JSONArray array  = JSON.parseArray(resource.getValue());
		//删除2分期
		if(array == null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}
		removeSecondNper(array);
		
		List<Map<String,Object>> nperList = getConsumeList(array, 1, amount, resource);
		
		resp.addResponseData("nperList", nperList);
		
		return resp;
	}
	private void removeSecondNper(JSONArray array) {
		if (array == null) {
			return;
		}
		Iterator<Object> it = array.iterator();
		while(it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
				it.remove();
				break;
			}
		}
		
	}
	private List<Map<String,Object>> getConsumeList(JSONArray array,int goodsNum,BigDecimal goodsAmount,AfResourceDo resource){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (int i=0;i<array.size();i++) {
			JSONObject obj = array.getJSONObject(i);
			Map<String, Object> attrs = new HashMap<String, Object>();
			String key = obj.getString(Constants.DEFAULT_NPER);
			String value = obj.getString(Constants.DEFAULT_RATE);
			if (value != null && !"".equals(value.trim())) {
				BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
				BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
				String[] range = StringUtil.split(resource.getValue2(), ",");
				if(null != range && range.length==2){
					rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
					rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
				}
				//每期利息+手续费
				BigDecimal amount =  BigDecimalUtil.getConsumeAmount(goodsAmount.multiply(new BigDecimal(goodsNum)), Integer.parseInt(key), 
						new BigDecimal(value).divide(new BigDecimal(Constants.MONTH_OF_YEAR),8,BigDecimal.ROUND_HALF_UP), 
						BigDecimalUtil.getTotalPoundage(goodsAmount.multiply(new BigDecimal(goodsNum)), 
								Integer.parseInt(key),new BigDecimal(resource.getValue1()), rangeBegin, rangeEnd));
				BigDecimal totalAmount = amount.multiply(new BigDecimal(key));
				attrs.put("nper", key);
				attrs.put("amount", amount);
				attrs.put("poundageAmount", totalAmount.subtract(goodsAmount));
				attrs.put("totalAmount", totalAmount);
				attrs.put("freeMonth", "");//免息月份
				attrs.put("freeAmount", 0);//免息月份

				list.add(attrs);
			}
		}
		return list;
	}
}
