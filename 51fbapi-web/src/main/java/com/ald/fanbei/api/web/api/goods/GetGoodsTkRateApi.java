/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.taobao.api.ApiException;
import com.taobao.api.domain.XItem;

/**
 * @类描述：
 * 
 * @author suweili 2017年5月29日下午8:26:46
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getGoodsTkRateApi")
public class GetGoodsTkRateApi implements ApiHandle {

	@Resource
	TaobaoApiUtil taobaoApiUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		String numId = ObjectUtils.toString(requestDataVo.getParams().get("numId"));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("numIid", numId);
		List<XItem> nTbkItemList;
		try {
			String tkRate = "0%";
			nTbkItemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();
			if (null != nTbkItemList && nTbkItemList.size() > 0) {
				
				tkRate =NumberUtil.objToIntDefault(nTbkItemList.get(0).getTkRate(), 0)/100+"%";
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("tkRate", tkRate);
			data.put("tkRateUrl", "");
			resp.setResponseData(data);
			return resp;

		} catch (ApiException e) {
			logger.info("getGoodsTkRateFail");
			;
			e.printStackTrace();
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
		}

	}

}
