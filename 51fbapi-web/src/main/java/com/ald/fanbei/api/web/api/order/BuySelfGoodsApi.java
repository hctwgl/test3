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
import com.ald.fanbei.api.biz.service.AfGoodsPriceService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAddressService;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfGoodsStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
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
	@Resource 
	AfGoodsPriceService afGoodsPriceService;
    @Resource
    AfSchemeGoodsService afSchemeGoodsService;
    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;
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
		AfOrderDo afOrder = orderDoWithGoodsAndAddressDo(addressDo, goodsDo);
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
			
			afGoodsPriceService.updateStockAndSaleByPriceId(goodsPriceId, true);
			afOrder.setGoodsPriceName(priceDo.getPropertyValueNames());
			afOrder.setSaleAmount(priceDo.getActualAmount().multiply(new BigDecimal(count)));
			afOrder.setPriceAmount(priceDo.getPriceAmount());
			
		}
		afOrderService.createOrder(afOrder);
		afGoodsService.updateSelfSupportGoods(goodsId, count);
		
		String isEnoughAmount = "Y";
		String isNoneQuota = "N";
		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
		BigDecimal useableAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount()).subtract(userAccountInfo.getFreezeAmount());
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

		resp.setResponseData(data);
		return resp;
	}

	public AfOrderDo orderDoWithGoodsAndAddressDo(AfUserAddressDo addressDo, AfGoodsDo goodsDo) {
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
		afOrder.setRebateAmount(goodsDo.getRebateAmount());
		afOrder.setMobile("");

		afOrder.setBankId(0L);

		afOrder.setOrderType(OrderType.SELFSUPPORT.getCode());
		final String orderNo = generatorClusterNo.getOrderNo(OrderType.SELFSUPPORT);
		afOrder.setOrderNo(orderNo);
		return afOrder;
	}

}
