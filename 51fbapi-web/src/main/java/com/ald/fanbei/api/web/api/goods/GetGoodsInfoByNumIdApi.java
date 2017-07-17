/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
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
import com.taobao.api.domain.XItem;

/**
 * @类描述：
 * 
 * @author suweili 2017年7月7日上午10:54:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getGoodsInfoByNumIdApi")
public class GetGoodsInfoByNumIdApi implements ApiHandle {

	@Resource
	TaobaoApiUtil taobaoApiUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		try {

			Map<String, Object> params = new HashMap<String, Object>();
			Map<String, Object> data = new HashMap<String, Object>();

			String numId = ObjectUtils.toString(requestDataVo.getParams().get("numId"), null);
			params.put("numIid", numId);

			List<XItem> nTbkItemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();

			if (null != nTbkItemList && nTbkItemList.size() > 0) {
				XItem item = nTbkItemList.get(0);
				String goodsType = item.getMall() ? "TMALL" : "TAOBAO";
				String title = item.getTitle();
				String pictUrl = item.getPicUrl();
				BigDecimal saleAmount = NumberUtil.objToBigDecimalDefault(item.getPriceWap(), BigDecimal.ZERO);
				if (saleAmount.compareTo(BigDecimal.ZERO)==0) {
				 saleAmount = NumberUtil.objToBigDecimalDefault(item.getReservePrice(), BigDecimal.ZERO);

				}

				BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(item.getTkRate(), BigDecimal.ZERO)
						.divide(new BigDecimal(100)).multiply(saleAmount).divide(new BigDecimal(100));

				String nick = item.getNick();
				data.put("goodsName", title);
				data.put("goodsIcon", pictUrl);
				data.put("rebateAmount", rebateAmount);
				data.put("saleAmount", saleAmount);
				data.put("shopNick", nick);
				data.put("goodsType", goodsType);

			}
			resp.setResponseData(data);
			return resp;

		} catch (Exception e) {
			logger.error("this numId error_response", e);
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);

		}
	}

}
