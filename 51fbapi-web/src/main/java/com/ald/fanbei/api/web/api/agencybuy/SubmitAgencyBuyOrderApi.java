/**
 * 
 */
package com.ald.fanbei.api.web.api.agencybuy;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfUserAddressService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAddressDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年4月18日下午2:53:22
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitAgencyBuyOrderApi")
public class SubmitAgencyBuyOrderApi implements ApiHandle {

	@Resource
	AfAgentOrderService afAgentOrderService;
	@Resource
	AfUserAddressService afUserAddressService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		String openId = ObjectUtils.toString(requestDataVo.getParams().get("openId"));
		String goodsName = ObjectUtils.toString(requestDataVo.getParams().get("goodsName"));
		String goodsIcon = ObjectUtils.toString(requestDataVo.getParams().get("goodsIcon"));
		String shopName = ObjectUtils.toString(requestDataVo.getParams().get("shopName"));
		BigDecimal priceAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("priceAmount")),BigDecimal.ZERO);
		BigDecimal saleAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("saleAmount")),BigDecimal.ZERO);

		Long addressId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("addressId"), 0);
		String capture = ObjectUtils.toString(requestDataVo.getParams().get("capture"));
		String remark = ObjectUtils.toString(requestDataVo.getParams().get("remark"));
		if (StringUtils.isBlank(openId) || StringUtils.isBlank(goodsName) || StringUtils.isBlank(goodsIcon)
				|| StringUtils.isBlank(shopName) ) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);

		}
		AfAgentOrderDo afAgentOrderDo = new AfAgentOrderDo();
		AfOrderDo afOrder = new AfOrderDo();
		afOrder.setOpenId(openId);
		afOrder.setGoodsName(goodsName);
		afOrder.setUserId(userId);
		afOrder.setGoodsIcon(goodsIcon);
		afOrder.setPriceAmount(priceAmount);
		afOrder.setSaleAmount(saleAmount);
		afOrder.setActualAmount(saleAmount);
		afOrder.setShopName(shopName);
		
		AfUserAddressDo addressDo = afUserAddressService.selectUserAddressByrid(addressId);
		if(addressDo==null){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);

		}
		afAgentOrderDo.setAddress(addressDo.getAddress());
		afAgentOrderDo.setProvince(addressDo.getProvince());
		afAgentOrderDo.setCity(addressDo.getCity());
		afAgentOrderDo.setCounty(addressDo.getCounty());
		afAgentOrderDo.setConsignee(addressDo.getConsignee());
		afAgentOrderDo.setMobile(addressDo.getMobile());
		afAgentOrderDo.setUserId(userId);
		afAgentOrderDo.setCapture(capture);
		afAgentOrderDo.setRemark(remark);
		if(afAgentOrderService.insertAgentOrder(afAgentOrderDo, afOrder)>0){
			return resp;

		}
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);

	}

}
