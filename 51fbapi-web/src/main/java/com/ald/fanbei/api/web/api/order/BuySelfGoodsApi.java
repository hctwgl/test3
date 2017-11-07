/**
 *
 */
package com.ald.fanbei.api.web.api.order;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfGoodsStatus;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	@Resource
	AfGoodsPriceService afGoodsPriceService;
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	
	@Autowired
	AfDeUserGoodsService afDeUserGoodsService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId"), ""),
				0l);
		Long goodsPriceId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsPriceId"), ""),
				0l);
		Long addressId = NumberUtil
				.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("addressId"), ""), 0l);
		String invoiceHeader = ObjectUtils.toString(requestDataVo.getParams().get("invoiceHeader"));
		String payType = ObjectUtils.toString(requestDataVo.getParams().get("payType"));
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("actualAmount"),BigDecimal.ZERO);
		Long couponId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("couponId"), 0);//用户的优惠券id(af_user_coupon的主键)
		
		Integer appversion = context.getAppVersion();
		Date currTime = new Date();
		Date gmtPayEnd = DateUtil.addHoures(currTime, Constants.ORDER_PAY_TIME_LIMIT);
		Integer count = NumberUtil.objToIntDefault(requestDataVo.getParams().get("count"), 1);
		Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), 0);
		if(actualAmount.compareTo(BigDecimal.ZERO)==0){
			throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
		}
		AfGoodsPriceDo priceDo = afGoodsPriceService.getById(goodsPriceId);
		AfGoodsDo goodsDo = afGoodsService.getGoodsById(goodsId);
		if(appversion >= 371){
			if ( goodsDo == null || priceDo == null) {
				throw new FanbeiException(FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
			}
		}else
		{
			if ( goodsDo == null ) {
				throw new FanbeiException(FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
			}
		}
		if(!AfGoodsStatus.PUBLISH.getCode().equals(goodsDo.getStatus())){
			throw new FanbeiException(FanbeiExceptionCode.GOODS_HAVE_CANCEL);
		}
		AfUserAddressDo addressDo = afUserAddressService.selectUserAddressByrid(addressId);

		if (addressDo == null) {
			throw new FanbeiException(FanbeiExceptionCode.USER_ADDRESS_NOT_EXIST);
		}
		AfOrderDo afOrder = orderDoWithGoodsAndAddressDo(addressDo, goodsDo,count);
		afOrder.setUserId(userId);
		afOrder.setGoodsPriceId(goodsPriceId);

		afOrder.setActualAmount(actualAmount);
		afOrder.setSaleAmount(goodsDo.getSaleAmount().multiply(new BigDecimal(count)));//TODO:售价取规格的。

//		afOrder.setActualAmount(goodsDo.getSaleAmount().multiply(new BigDecimal(count)));

		afOrder.setCount(count);
		afOrder.setNper(nper);
		afOrder.setPayType(payType);
		afOrder.setInvoiceHeader(invoiceHeader);
		afOrder.setGmtCreate(currTime);
		afOrder.setGmtPayEnd(gmtPayEnd);


		//通过商品查询免息规则配置
		AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
		if(null != afSchemeGoodsDo){
			Long interestFreeId = afSchemeGoodsDo.getInterestFreeId();
			AfInterestFreeRulesDo afInterestFreeRulesDo = afInterestFreeRulesService.getById(interestFreeId);
			if (afInterestFreeRulesDo != null) {
				afOrder.setInterestFreeJson(afInterestFreeRulesDo.getRuleJson());
			}
		}

		if (nper.intValue() > 0) {
			// 保存手续费信息
			BorrowRateBo borrowRate = afResourceService.borrowRateWithResource(nper);
			afOrder.setBorrowRate(BorrowRateBoUtil.parseToDataTableStrFromBo(borrowRate));
		}
		if(priceDo != null){
		    	//+++双十一砍价活动增加逻辑++++
		    	AfDeUserGoodsDo afDeUserGoodsDo = afDeUserGoodsService.getUserGoodsPrice(userId, goodsPriceId);
			if(afDeUserGoodsDo !=null)
			{
			    //更新商品价格为砍价后价格
			    priceDo.setActualAmount(priceDo.getActualAmount().subtract(afDeUserGoodsDo.getCutprice()));
			    //使用ThirdOrderNo记录砍价商品价格信息的id（为了避免扩展order表），自营商品ThirdOrderNo均为'',所以选择该字段扩展。
			    afOrder.setThirdOrderNo(afDeUserGoodsDo.getRid().toString());
			}		    
			//-----------------------------
			
			afGoodsPriceService.updateStockAndSaleByPriceId(goodsPriceId, true);
			afOrder.setGoodsPriceName(priceDo.getPropertyValueNames());
			afOrder.setSaleAmount(priceDo.getActualAmount().multiply(new BigDecimal(count)));
			afOrder.setPriceAmount(priceDo.getPriceAmount());

		}
		afOrder.setUserCouponId(couponId);
		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
		BigDecimal useableAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount()).subtract(userAccountInfo.getFreezeAmount());
		afOrder.setAuAmount(userAccountInfo.getAuAmount());
		afOrder.setUseableAmount(useableAmount);
		afOrderService.createOrder(afOrder);
		afGoodsService.updateSelfSupportGoods(goodsId, count);
		String isEnoughAmount = "Y";
		String isNoneQuota = "N";
		if (useableAmount.compareTo(actualAmount) < 0) {
			isEnoughAmount = "N";
		}
		if (useableAmount.compareTo(BigDecimal.ZERO) == 0) {
			isNoneQuota = "Y";
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("orderId", afOrder.getRid());
		data.put("isEnoughAmount", isEnoughAmount);
		data.put("isNoneQuota", isNoneQuota);

		//爬取商品开关
		AfResourceDo isWorm = afResourceService.getConfigByTypesAndSecType(Constants.THIRD_GOODS_TYPE,Constants.THIRD_GOODS_IS_WORM_SECTYPE);
		if(null != isWorm){
			data.put("isWorm",isWorm.getValue());
		}else{
			data.put("isWorm",0);
		}

		if (couponId > 0) {
			AfUserCouponDto couponDo = afUserCouponService.getUserCouponById(couponId);
			if (couponDo.getGmtEnd().before(new Date()) || StringUtils.equals(couponDo.getStatus(), CouponStatus.EXPIRE.getCode())) {
				logger.error("coupon end less now");
				throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
			}
			afUserCouponService.updateUserCouponSatusUsedById(couponId);
		}

		
		resp.setResponseData(data);
		return resp;
	}

	public AfOrderDo orderDoWithGoodsAndAddressDo(AfUserAddressDo addressDo, AfGoodsDo goodsDo,int count) {
		AfOrderDo afOrder = new AfOrderDo();
		afOrder.setConsignee(addressDo.getConsignee());
		afOrder.setConsigneeMobile(addressDo.getMobile());
		afOrder.setSaleAmount(goodsDo.getSaleAmount());//TODO:售价改成从规格中取得。

		afOrder.setPriceAmount(goodsDo.getPriceAmount());
		afOrder.setGoodsIcon(goodsDo.getGoodsIcon());
		afOrder.setGoodsName(goodsDo.getName());
		String address = addressDo.getProvince() !=null?addressDo.getProvince():"";
		if(addressDo.getCity()!=null){
			address=address.concat(addressDo.getCity());

		}
		if(addressDo.getCounty()!=null){
			address=address.concat(addressDo.getCounty());

		}
		if(addressDo.getAddress()!=null){
			address=address.concat(addressDo.getAddress());
		}
		afOrder.setAddress(address);
		afOrder.setGoodsId(goodsDo.getRid());
		afOrder.setOpenId(goodsDo.getOpenId());
		afOrder.setNumId(goodsDo.getNumId());
		afOrder.setShopName(goodsDo.getShopName());
		afOrder.setRebateAmount(goodsDo.getRebateAmount().multiply(new BigDecimal(count)));
		afOrder.setMobile("");

		afOrder.setBankId(0L);
		String source = afOrder.getSource();
		if (StringUtil.equals(source, "SELFBUILD")) {
			afOrder.setOrderType("SELFBUILD");
		}else {
			afOrder.setOrderType(OrderType.SELFSUPPORT.getCode());
		}
		final String orderNo = generatorClusterNo.getOrderNo(OrderType.SELFSUPPORT);
		afOrder.setOrderNo(orderNo);
		return afOrder;
	}

}
