package com.ald.fanbei.api.web.api.legalborrowV2;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowLegalOrderQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author chefeipeng 2018年1月5日下午1:06:18
 * @类描述：查询用户订单记录
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowLegalOrderInfoV2Api")
public class GetBorrowLegalOrderInfoV2Api implements ApiHandle {

	@Resource
	AfBorrowLegalOrderService afBorrowLegalOrderService;

	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

	@Resource
	AfGoodsService afGoodsService;

	@Resource
	AfBorrowCashService afBorrowCashService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = Maps.newHashMap();
		resp.setResponseData(data);

		// 判断用户是否登录
		Long userId = context.getUserId();
		if (userId == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
		}
		// 获取查询页码
		int pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), 0);


		AfBorrowLegalOrderQuery query = new AfBorrowLegalOrderQuery();
		query.setUserId(userId);
		query.setPageNo(pageNo);
		// 查询用户订单记录
		List<AfBorrowLegalOrderDo> borrowLegalOrdersList = afBorrowLegalOrderService.getUserBorrowLegalOrderList(query);
		List<Map<String, Object>> orderList = Lists.newArrayList();
		for (AfBorrowLegalOrderDo borrowLegalOrderDo : borrowLegalOrdersList) {
			Map<String, Object> orderInfoMap = Maps.newHashMap();
			Long orderId = borrowLegalOrderDo.getRid();
			orderInfoMap.put("orderId", orderId);
			orderInfoMap.put("goodsName", borrowLegalOrderDo.getGoodsName());
			orderInfoMap.put("status", borrowLegalOrderDo.getStatus());
			orderInfoMap.put("gmtCreate", borrowLegalOrderDo.getGmtCreate());
			orderInfoMap.put("amount", borrowLegalOrderDo.getPriceAmount());
			Long goodsId = borrowLegalOrderDo.getGoodsId();

			AfGoodsDo goodsDo = afGoodsService.getGoodsById(goodsId);
			if (goodsDo != null) {
				orderInfoMap.put("goodsIcon", goodsDo.getGoodsIcon());
			}

			AfBorrowLegalOrderCashDo legalOrderCash = afBorrowLegalOrderCashService
					.getBorrowLegalOrderCashByBorrowLegalOrderId(orderId);
			if( legalOrderCash != null) {//有cash 就是V1版本的
				orderInfoMap.put("borrowStatus", legalOrderCash.getStatus());
				orderInfoMap.put("source","V1");
				orderList.add(orderInfoMap);
			}else{//没有cash就是V2版本的
				orderInfoMap.put("source","V2");
				orderList.add(orderInfoMap);
			}

		}
//		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
//		if(afBorrowCashDo != null){
//			if(StringUtils.equals(AfBorrowCashStatus.finsh.getCode(),afBorrowCashDo.getStatus()) || StringUtils.equals(AfBorrowCashStatus.closed.getCode(),afBorrowCashDo.getStatus())){
//				data.put("orderList", orderListV2);
//			}else {
//				AfBorrowLegalOrderCashDo legalOrderCash = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowIdNoStatus(afBorrowCashDo.getRid());
//				if(legalOrderCash != null){
//					data.put("orderList", orderListV1);
//				}else {
//					data.put("orderList", orderListV2);
//				}
//			}
//		}
		data.put("orderList", orderList);
		resp.setResponseData(data);
		return resp;
	}

}
