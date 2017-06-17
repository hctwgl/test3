/**
 * 
 */
package com.ald.fanbei.api.web.api.agencybuy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSubjectGoodsService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfSubjectGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 * @类描述：代买订单可使用优惠券列表获取
 * @author suweili 2017年6月12日上午11:47:27
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAgencyCouponListApi")
public class GetAgencyCouponListApi implements ApiHandle {
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfSubjectGoodsService afSubjectGoodsService;
	

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("actualAmount"),BigDecimal.ZERO);
		Long goodsId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("goodsId"), 0);
		
		List<AfSubjectGoodsDo> subjectGoods = afSubjectGoodsService.getSubjectGoodsByGoodsId(goodsId);
		if(CollectionUtil.isEmpty(subjectGoods)){
			return resp;
		}
		
		Long userId = context.getUserId();
		List<AfUserCouponDto>  list = afUserCouponService.getUserAcgencyCouponByAmount(userId,actualAmount);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("couponList", JSON.toJSON(list));
		data.put("pageNo", 1);
		data.put("totalCount", list.size());
		
		resp.setResponseData(data);
		
		return resp;
	}

}
