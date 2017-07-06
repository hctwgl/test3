/**
 * 
 */
package com.ald.fanbei.api.web.api.agencybuy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAddressService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAddressDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
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
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String numId = ObjectUtils.toString(requestDataVo.getParams().get("numId"));

		String openId = ObjectUtils.toString(requestDataVo.getParams().get("openId"));
		String goodsName = ObjectUtils.toString(requestDataVo.getParams().get("goodsName"));
		String goodsIcon = ObjectUtils.toString(requestDataVo.getParams().get("goodsIcon"));
		BigDecimal priceAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("priceAmount"),BigDecimal.ZERO); // 原价
		BigDecimal saleAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("saleAmount"), BigDecimal.ZERO);
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("actualAmount"),BigDecimal.ZERO);
        Integer nper  = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), 0);
		Long addressId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("addressId"), 0);
		String capture = ObjectUtils.toString(requestDataVo.getParams().get("capture"));
		String remark = ObjectUtils.toString(requestDataVo.getParams().get("remark"));
		if (  StringUtils.isBlank(numId) ) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		Long couponId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("couponId"), 0);
       
		if(actualAmount.compareTo( BigDecimal.ZERO)<0){
        	actualAmount = BigDecimal.ZERO;
        }
		
		AfAgentOrderDo afAgentOrderDo = new AfAgentOrderDo();
		AfOrderDo afOrder = new AfOrderDo();
	
		afOrder.setUserId(userId);
		afOrder.setActualAmount(actualAmount);
		afOrder.setSaleAmount(saleAmount);
		afOrder.setPriceAmount(priceAmount);
		afOrder.setGoodsIcon(goodsIcon);
		afOrder.setGoodsName(goodsName);
		afOrder.setNumId(numId);
		afOrder.setOpenId(openId);
		afOrder.setUserCouponId(couponId);
		afOrder.setInterestFreeJson(getInterestFreeRule(numId));
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
		afAgentOrderDo.setCouponId(couponId);
		if(couponId>0){
			AfUserCouponDto couponDo =	afUserCouponService.getUserCouponById(afAgentOrderDo.getCouponId());
			if(couponDo.getGmtEnd().before(new Date())||StringUtils.equals(couponDo.getStatus(), CouponStatus.EXPIRE.getCode()) ){
				logger.error("coupon end less now");
				throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
			}
			afUserCouponService.updateUserCouponSatusUsedById(afAgentOrderDo.getCouponId());
		}
		if(nper>0){
			AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
			BigDecimal useableAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount())
					.subtract(userAccountInfo.getFreezeAmount());
			if (useableAmount.compareTo(actualAmount) < 0) {
				logger.error("borrow consume money error");

				throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_MONEY_ERROR);
			}
		}
		if(afAgentOrderService.insertAgentOrderAndNper(afAgentOrderDo, afOrder,nper)>0){
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("orderId", afOrder.getRid());
			resp.setResponseData(data);;
			return resp;

		}
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
	}
	
	/**
	 * 获取免息规则
	 * @param numId
	 * @return
	 */
	private String getInterestFreeRule(String numId) {
		AfGoodsDo goodsInfo = afGoodsService.getGoodsByNumId(numId);
		if (goodsInfo == null) {
			return StringUtils.EMPTY;
		}
		AfSchemeGoodsDo schemeGoodsInfo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsInfo.getRid());
		if (schemeGoodsInfo == null) {
			return StringUtils.EMPTY;
		}
		AfInterestFreeRulesDo ruleInfo = afInterestFreeRulesService.getById(schemeGoodsInfo.getInterestFreeId());
		if (ruleInfo == null) {
			return StringUtils.EMPTY;
		}
		return ruleInfo.getRuleJson();
	}

}
