/**
 * 
 */
package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAddressService;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAddressDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：自营商品下单（oppr11） @author suweili 2017年6月16日下午3:44:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("buySelfGoodsApi")
public class BuySelfGoodsApi implements ApiHandle {

	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAddressService afUserAddressService;
	@Resource
	private GeneratorClusterNo generatorClusterNo;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId"), ""),
				0l);
		Long addressId = NumberUtil
				.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("addressId"), ""), 0l);
		String invoiceHeader = ObjectUtils.toString(requestDataVo.getParams().get("invoiceHeader"));
		String payType = ObjectUtils.toString(requestDataVo.getParams().get("payType"));
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("actualAmount"),BigDecimal.ZERO);

		Date currTime = new Date();
		Date gmtPayEnd = DateUtil.addHoures(currTime, Constants.SELFSUPPORT_PAY_TIMEOUT_HOUR);
		Integer count = NumberUtil.objToIntDefault(requestDataVo.getParams().get("count"), 1);
		Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), 0);
		if(actualAmount.compareTo(BigDecimal.ZERO)==0){
			throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
		}
		AfGoodsDo goodsDo = afGoodsService.getGoodsById(goodsId);
		if (goodsDo == null) {
			throw new FanbeiException(FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
		}
		AfUserAddressDo addressDo = afUserAddressService.selectUserAddressByrid(addressId);

		if (addressDo == null) {
			throw new FanbeiException(FanbeiExceptionCode.USER_ADDRESS_NOT_EXIST);
		}
		AfOrderDo afOrder = orderDoWithGoodsAndAddressDo(addressDo, goodsDo);
		afOrder.setUserId(userId);
		afOrder.setActualAmount(actualAmount);
		afOrder.setSaleAmount(goodsDo.getSaleAmount().multiply(new BigDecimal(count)));

//		afOrder.setActualAmount(goodsDo.getSaleAmount().multiply(new BigDecimal(count)));
		
		afOrder.setCount(count);
		afOrder.setNper(nper);
		afOrder.setPayType(payType);
		afOrder.setInvoiceHeader(invoiceHeader);
		afOrder.setGmtCreate(currTime);
		afOrder.setGmtPayEnd(gmtPayEnd);
		if (nper.intValue() > 0) {
			// 保存手续费信息
			BorrowRateBo borrowRate = afResourceService.borrowRateWithResource(nper);
			afOrder.setBorrowRate(BorrowRateBoUtil.parseToDataTableStrFromBo(borrowRate));
		}
		afOrderService.createOrder(afOrder);
		afGoodsService.updateSelfSupportGoods(goodsId, count);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("orderId", afOrder.getRid());
		resp.setResponseData(data);
		;
		return resp;
	}

	public AfOrderDo orderDoWithGoodsAndAddressDo(AfUserAddressDo addressDo, AfGoodsDo goodsDo) {
		AfOrderDo afOrder = new AfOrderDo();
		afOrder.setConsignee(addressDo.getConsignee());
		afOrder.setConsigneeMobile(addressDo.getMobile());
		afOrder.setSaleAmount(goodsDo.getSaleAmount());
		afOrder.setPriceAmount(goodsDo.getPriceAmount());
		afOrder.setGoodsIcon(goodsDo.getGoodsIcon());
		afOrder.setGoodsName(goodsDo.getName());
		afOrder.setAddress(addressDo.getAddress());
		afOrder.setGoodsId(goodsDo.getRid());
		afOrder.setOpenId(goodsDo.getOpenId());
		afOrder.setNumId(goodsDo.getNumId());
		afOrder.setShopName(goodsDo.getShopName());
		afOrder.setRebateAmount(goodsDo.getRebateAmount());
		afOrder.setMobile("");

		afOrder.setBankId(0L);

		afOrder.setOrderType(OrderType.SELFSUPPORT.getCode());
		final String orderNo = generatorClusterNo.getOrderNo(OrderType.SELFSUPPORT);
		afOrder.setOrderNo(orderNo);
		return afOrder;
	}

}
