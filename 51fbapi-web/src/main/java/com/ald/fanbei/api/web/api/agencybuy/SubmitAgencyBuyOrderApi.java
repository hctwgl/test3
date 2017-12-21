package com.ald.fanbei.api.web.api.agencybuy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.common.checkoutcounter.CocConstants;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaRespBo;
import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAddressService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
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
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * @author suweili 2017年4月18日下午2:53:22
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitAgencyBuyOrderApi")
public class SubmitAgencyBuyOrderApi implements ApiHandle {

	@Resource
	RiskUtil riskUtil;
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
	@Resource
	AfUserVirtualAccountService afUserVirtualAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String numId = ObjectUtils.toString(requestDataVo.getParams().get("numId"));

		String openId = ObjectUtils.toString(requestDataVo.getParams().get("openId"));
		String goodsName = ObjectUtils.toString(requestDataVo.getParams().get("goodsName"));
		String goodsIcon = ObjectUtils.toString(requestDataVo.getParams().get("goodsIcon"));
		BigDecimal priceAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("priceAmount"), BigDecimal.ZERO); // 原价
		BigDecimal saleAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("saleAmount"), BigDecimal.ZERO);
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("actualAmount"), BigDecimal.ZERO);
		Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), 0);
		Integer appversion = context.getAppVersion();
		boolean fromCashier = NumberUtil.objToIntDefault(request.getAttribute("fromCashier"), 0) == 0 ? false : true;

		//收银台功能之后，此处不进行分期处理
		if (fromCashier) {
			nper=0;
		}

		Long addressId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("addressId"), 0);
		String capture = ObjectUtils.toString(requestDataVo.getParams().get("capture"));
		String remark = ObjectUtils.toString(requestDataVo.getParams().get("remark"));
		if (StringUtils.isBlank(numId)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		Long couponId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("couponId"), 0);

		if (actualAmount.compareTo(BigDecimal.ZERO) < 0) {
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
		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
		BigDecimal useableAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount()).subtract(userAccountInfo.getFreezeAmount());
		afOrder.setAuAmount(userAccountInfo.getAuAmount());
		afOrder.setUsedAmount(userAccountInfo.getUsedAmount());
		AfUserAddressDo addressDo = afUserAddressService.selectUserAddressByrid(addressId);
		if (addressDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		afOrder.setConsignee(addressDo.getConsignee());
		//新增下单时记录 省、 市、 区 、详细地址 、IP 、设备指纹 2017年12月12日11:17:51 cxk
		String province = addressDo.getProvince() !=null?addressDo.getProvince():"";
		String city = addressDo.getCity() !=null?addressDo.getCity():"";
		String district = addressDo.getCounty() !=null?addressDo.getCounty():"";
		String address = addressDo.getAddress() !=null?addressDo.getAddress():"";
		afOrder.setProvince(province);//省
		afOrder.setCity(city);//市
		afOrder.setDistrict(district);//区
		afOrder.setAddress(address);//详细地址
		/*
		if(addressDo.getCity()!=null){
			//address=address.concat(addressDo.getCity());			
		}
		if(addressDo.getCounty()!=null){
			//address=address.concat(addressDo.getCounty());			
		}
		if(addressDo.getAddress()!=null){
			//address=address.concat(addressDo.getAddress());			
		}**/
		afOrder.setIp(request.getRemoteAddr());//用户ip地址		
		afOrder.setBlackBox(ObjectUtils.toString(requestDataVo.getParams().get("blackBox")));//加入同盾设备指纹
		
		afOrder.setConsigneeMobile(addressDo.getMobile());
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
		if (couponId > 0) {
			AfUserCouponDto couponDo = afUserCouponService.getUserCouponById(afAgentOrderDo.getCouponId());
			if (couponDo.getGmtEnd().before(new Date()) || StringUtils.equals(couponDo.getStatus(), CouponStatus.EXPIRE.getCode())) {
				logger.error("coupon end less now");
				throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
			}
			afUserCouponService.updateUserCouponSatusUsedById(afAgentOrderDo.getCouponId());
		}
		String isEnoughAmount = "Y";
		String isNoneQuota = "N";
		BigDecimal leftAmount = BigDecimal.ZERO;
		if (!fromCashier) {
			leftAmount = useableAmount;
			if (useableAmount.compareTo(actualAmount) < 0) {
				isEnoughAmount = "N";
			}
			RiskVirtualProductQuotaRespBo quotaBo = riskUtil.virtualProductQuota(userId.toString(), "", goodsName);
			String quotaData = quotaBo.getData();
			if (StringUtils.isNotBlank(quotaData) && !StringUtil.equals(quotaData, "{}")) {
				JSONObject json = JSONObject.parseObject(quotaData);
				String virtualCode = json.getString("virtualCode");
				BigDecimal goodsUseableAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(userId, virtualCode, json.getBigDecimal("amount"));
				if (goodsUseableAmount.compareTo(actualAmount) < 0) {
					isEnoughAmount = "N";
				}
				if (goodsUseableAmount.compareTo(leftAmount) < 0)
					leftAmount = goodsUseableAmount;
			}
			if (leftAmount.compareTo(BigDecimal.ZERO) == 0) {
				isNoneQuota = "Y";
			}

		}




		if (afAgentOrderService.insertAgentOrderAndNper(afAgentOrderDo, afOrder, nper) > 0) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("orderId", afOrder.getRid());
			data.put("isEnoughAmount", isEnoughAmount);
			data.put("isNoneQuota", isNoneQuota);
			resp.setResponseData(data);
			return resp;
		}
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
	}

	/**
	 * 获取免息规则
	 * 
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
