/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.taobao.api.ApiException;
import com.taobao.api.domain.NTbkItem;

/**
 * @类描述：
 * 
 * @author suweili 2017年5月31日上午9:46:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRecommendGoodsApi")
public class GetRecommendGoodsApi implements ApiHandle {
	@Resource
	TaobaoApiUtil taobaoApiUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);


		try {
			List<Object> list = new ArrayList<Object>();

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("numIid", "524010015574,524009251672");
			
			List<NTbkItem>	nTbkItemList1 = taobaoApiUtil.executeTaeItemRecommendSearch("524010015574").getResults();
			List<NTbkItem>	nTbkItemList2 = taobaoApiUtil.executeTaeItemRecommendSearch("524009251672").getResults();
			List<NTbkItem>	nTbkItemList3 = taobaoApiUtil.executeTaeItemRecommendSearch("533803157219").getResults();
			List<NTbkItem>	nTbkItemList4 = taobaoApiUtil.executeTaeItemRecommendSearch("524776152072").getResults();

			
			goodsInfoWithTbkItemList(nTbkItemList1,list);
			goodsInfoWithTbkItemList(nTbkItemList2,list);
			goodsInfoWithTbkItemList(nTbkItemList3,list);
			goodsInfoWithTbkItemList(nTbkItemList4,list);

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("goodsList", list);
			resp.setResponseData(data);
			return resp;

		} catch (ApiException e) {
			logger.info("getGoodsTkRateFail");
			;
			e.printStackTrace();
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
		}

	}
	
	public void goodsInfoWithTbkItemList(List<NTbkItem> nTbkItemList,List<Object> list) {
		
		if (null != nTbkItemList && nTbkItemList.size() > 0) {
			for (NTbkItem item : nTbkItemList) {
				list.add(goodsInfoWithNTbkItem(item));
			}
			
		}
	}

	public Map<String, Object> goodsInfoWithNTbkItem(NTbkItem item) {
		BigDecimal saleAmount = NumberUtil.objToBigDecimalDefault(item.getZkFinalPrice(), BigDecimal.ZERO);
	    BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(item.getTkRate(), BigDecimal.ZERO).divide(new BigDecimal(100)).multiply(saleAmount);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("numId", item.getNumIid());
		data.put("saleAmount", saleAmount);
		data.put("rebateAmount", rebateAmount);
		data.put("goodsName", item.getTitle());
		data.put("goodsIcon", item.getPictUrl());
		data.put("goodsUrl", item.getItemUrl());

		return data;
	}
}
