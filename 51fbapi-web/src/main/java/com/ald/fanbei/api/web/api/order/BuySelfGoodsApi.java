/**
 *
 */
package com.ald.fanbei.api.web.api.order;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
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
import com.ald.fanbei.api.web.vo.AfGoodsPriceVo;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
	@Resource
	AfGoodsDouble12Service afGoodsDouble12Service;

	@Autowired
	AfDeUserGoodsService afDeUserGoodsService;

	@Resource
	AfShareGoodsService afShareGoodsService;

	@Resource
	AfShareUserGoodsService afShareUserGoodsService;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	BizCacheUtil bizCacheUtil;

	@Resource
	AfGoodsDoubleEggsService afGoodsDoubleEggsService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		final Long userId = context.getUserId();
		Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId"), ""),
				0l);
		Long goodsPriceId = NumberUtil
				.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsPriceId"), ""), 0l);
		Long addressId = NumberUtil
				.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("addressId"), ""), 0l);
		String invoiceHeader = ObjectUtils.toString(requestDataVo.getParams().get("invoiceHeader"));
		String payType = ObjectUtils.toString(requestDataVo.getParams().get("payType"));
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("actualAmount"),BigDecimal.ZERO);
		Long couponId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("couponId"), 0);//用户的优惠券id(af_user_coupon的主键)
		boolean fromCashier =NumberUtil.objToIntDefault(request.getAttribute("fromCashier"), 0) == 0 ? false : true;
		Integer appversion = context.getAppVersion();
		Date currTime = new Date();
		Date gmtPayEnd = DateUtil.addHoures(currTime, Constants.ORDER_PAY_TIME_LIMIT);
		Integer count = NumberUtil.objToIntDefault(requestDataVo.getParams().get("count"), 1);
		Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), 0);
//		if (actualAmount.compareTo(BigDecimal.ZERO) == 0) {
//			throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
//		}
		final AfGoodsPriceDo priceDo = afGoodsPriceService.getById(goodsPriceId);
		AfGoodsDo goodsDo = afGoodsService.getGoodsById(goodsId);
		if (appversion >= 371) {
			if (goodsDo == null || priceDo == null) {
				throw new FanbeiException(FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
			}
		} else {
			if (goodsDo == null) {
				throw new FanbeiException(FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
			}
		}
		if (!AfGoodsStatus.PUBLISH.getCode().equals(goodsDo.getStatus())) {
			throw new FanbeiException(FanbeiExceptionCode.GOODS_HAVE_CANCEL);
		}
		AfUserAddressDo addressDo = afUserAddressService.selectUserAddressByrid(addressId);

		if (addressDo == null) {
			throw new FanbeiException(FanbeiExceptionCode.USER_ADDRESS_NOT_EXIST);
		}
		final AfOrderDo afOrder = orderDoWithGoodsAndAddressDo(addressDo, goodsDo, count);
		afOrder.setUserId(userId);
		afOrder.setGoodsPriceId(goodsPriceId);

		afOrder.setActualAmount(actualAmount);
		afOrder.setSaleAmount(goodsDo.getSaleAmount().multiply(new BigDecimal(count)));// TODO:售价取规格的。

		// afOrder.setActualAmount(goodsDo.getSaleAmount().multiply(new
		// BigDecimal(count)));

		afOrder.setCount(count);
		//收银台支持版本控制
		if (!fromCashier) {
			afOrder.setNper(nper);
			afOrder.setPayType(payType);
		}
		afOrder.setInvoiceHeader(invoiceHeader);
		afOrder.setGmtCreate(currTime);
		afOrder.setGmtPayEnd(gmtPayEnd);

		// 通过商品查询免息规则配置
		AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
		if (null != afSchemeGoodsDo) {
			Long interestFreeId = afSchemeGoodsDo.getInterestFreeId();
			AfInterestFreeRulesDo afInterestFreeRulesDo = afInterestFreeRulesService.getById(interestFreeId);
			if (afInterestFreeRulesDo != null) {
				afOrder.setInterestFreeJson(afInterestFreeRulesDo.getRuleJson());
			}
		}

		//收银台支持版本控制
		if (!fromCashier) {
			if (nper.intValue() > 0) {
				// 保存手续费信息
				BorrowRateBo borrowRate = afResourceService.borrowRateWithResource(nper,context.getUserName());
				afOrder.setBorrowRate(BorrowRateBoUtil.parseToDataTableStrFromBo(borrowRate));
			}
		}
		if (priceDo != null) {
			// +++双十一砍价活动增加逻辑++++
			AfDeUserGoodsDo afDeUserGoodsDo = afDeUserGoodsService.getUserGoodsPrice(userId, goodsPriceId);
			if (afDeUserGoodsDo != null) {
				// 更新商品价格为砍价后价格
				priceDo.setActualAmount(priceDo.getActualAmount().subtract(afDeUserGoodsDo.getCutprice()));
				// 使用ThirdOrderNo记录砍价商品价格信息的id（为了避免扩展order表），自营商品ThirdOrderNo均为'',所以选择该字段扩展。
				afOrder.setThirdOrderNo(afDeUserGoodsDo.getRid().toString());
			}
			// -----------------------------

			//mqp_新人专享活动增加逻辑
			if (userId != null) {
				
				// ------------------------------------begin mqp doubleEggs------------------------------------
				if(afGoodsDoubleEggsService.getByGoodsId(goodsId) != null){
					doubleEggsGoodsCheck(userId, goodsId,count);
				}
				// ------------------------------------end mqp doubleEggs------------------------------------

				// 双十二秒杀新增逻辑+++++++++++++>
				if(afGoodsDouble12Service.getByGoodsId(goodsId).size()!=0){
					//是双十二秒杀商品
					double12GoodsCheck(userId, goodsId,count);
				}
				// +++++++++++++++++++++++++<
				
				//查询用户订单数
				int oldUserOrderAmount = afOrderService.getOldUserOrderAmount(userId);
				if(oldUserOrderAmount==0){
					//是新用户
					AfShareGoodsDo condition = new AfShareGoodsDo();
					condition.setGoodsId(goodsId);
					AfShareGoodsDo resultDo = afShareGoodsService.getByCommonCondition(condition);
					if (null != resultDo) {
						//这个商品是砍价商品
						//判断数量是否是一个
						if (count != 1) {
							//报错提示只能买一件商品
							FanbeiExceptionCode code = FanbeiExceptionCode.ONLY_ONE_GOODS_ACCEPTED;
							ApiHandleResponse resp1 = new ApiHandleResponse(requestDataVo.getId(), code);
							return resp1;

						}
						
						//后端优化:商品详情页面展示的商品价格，各个规格的价格取后台商品的售价即可；（商品的售价会维护成商品折扣后的新人价）
						//BigDecimal decreasePrice = resultDo.getDecreasePrice();
						//priceDo.setActualAmount(priceDo.getActualAmount().subtract(decreasePrice));
						transactionTemplate
								.execute(new TransactionCallback<Long>() {

									@Override
									public Long doInTransaction(
											TransactionStatus status) {

										try {
											// 增加一条记录,用户购买记录
											AfShareUserGoodsDo t = new AfShareUserGoodsDo();
											t.setGmtCreate(new Date());
											t.setGmtModified(new Date());
											t.setGoodsPriceId(priceDo.getRid());
											t.setIsBuy(0);
											t.setUserId(userId);
											afShareUserGoodsService
													.saveRecord(t);

											// 使用ThirdOrderNo记录砍价商品价格信息的id（为了避免扩展order表），自营商品ThirdOrderNo均为'',所以选择该字段扩展。
											afOrder.setThirdOrderNo(t.getRid()
													.toString());

											return t.getRid();
										} catch (Exception e) {
											logger.info("AfShareUserGoodsDo error:" + e);
											status.setRollbackOnly();
											return 0L;
										}

									}
								});
					}

				}
			}
			//-------------------------------
			
			afGoodsPriceService.updateStockAndSaleByPriceId(goodsPriceId, true);
			afOrder.setGoodsPriceName(priceDo.getPropertyValueNames());
			afOrder.setSaleAmount(priceDo.getActualAmount().multiply(new BigDecimal(count)));
			afOrder.setPriceAmount(priceDo.getPriceAmount());

		}
		afOrder.setUserCouponId(couponId);
		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
		BigDecimal useableAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount()).subtract(userAccountInfo.getFreezeAmount());
		afOrder.setAuAmount(userAccountInfo.getAuAmount());
		afOrder.setUsedAmount(userAccountInfo.getUsedAmount());
		afOrderService.createOrder(afOrder);
		afGoodsService.updateSelfSupportGoods(goodsId, count);
		String isEnoughAmount = "Y";
		String isNoneQuota = "N";
		if (!fromCashier) {
			if (useableAmount.compareTo(actualAmount) < 0) {
				isEnoughAmount = "N";
			}
			if (useableAmount.compareTo(BigDecimal.ZERO) == 0) {
				isNoneQuota = "Y";
			}
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("orderId", afOrder.getRid());
		data.put("isEnoughAmount", isEnoughAmount);
		data.put("isNoneQuota", isNoneQuota);

		// 爬取商品开关
		AfResourceDo isWorm = afResourceService.getConfigByTypesAndSecType(Constants.THIRD_GOODS_TYPE,
				Constants.THIRD_GOODS_IS_WORM_SECTYPE);
		if (null != isWorm) {
			data.put("isWorm", isWorm.getValue());
		} else {
			data.put("isWorm", 0);
		}

		if (couponId > 0) {
			AfUserCouponDto couponDo = afUserCouponService.getUserCouponById(couponId);
			if (couponDo.getGmtEnd().before(new Date())
					|| StringUtils.equals(couponDo.getStatus(), CouponStatus.EXPIRE.getCode())) {
				logger.error("coupon end less now");
				throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
			}
			afUserCouponService.updateUserCouponSatusUsedById(couponId);
		}

		resp.setResponseData(data);
		return resp;
	}

	/**
	 * 
	 * @Title: double12GoodsCheck
	 * @Description:  双十二秒杀新增逻辑 —— 秒杀商品校验
	 * @return  void  
	 * @author yanghailong
	 * @data  2017年11月21日
	 */
	private void double12GoodsCheck(Long userId, Long goodsId, Integer count){
		String key = Constants.CACHKEY_BUY_GOODS_LOCK + ":" + userId + ":" + goodsId;
		try {
			boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
			if (isNotLock) {
				List<AfGoodsDouble12Do> afGoodsDouble12DoList = afGoodsDouble12Service.getByGoodsId(goodsId);
				if(afGoodsDouble12DoList.size()!=0){
					//这个商品是双十二秒杀商品
					if (count != 1||afOrderService.getDouble12OrderByGoodsIdAndUserId(goodsId, userId).size()>0) {
						//报错提示只能买一件商品
						throw new FanbeiException(FanbeiExceptionCode.ONLY_ONE_DOUBLE12GOODS_ACCEPTED);
					}
					
					//根据goodsId查询商品信息
					AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
					int goodsDouble12Count = Integer.parseInt(afGoodsDo.getStockCount())-afGoodsDouble12DoList.get(0).getAlreadyCount();//秒杀商品余量
					if(goodsDouble12Count<=0){
						//报错提示秒杀商品已售空
						throw new FanbeiException(FanbeiExceptionCode.NO_DOUBLE12GOODS_ACCEPTED);
					}
					
					//iphoneX特殊处理
					if(goodsId==134882||goodsId==135405){
						//更新 已被秒杀的商品数量（count+1）
						afGoodsDouble12Service.updateCountById(goodsId);
						//报错提示秒杀商品已售空
						throw new FanbeiException(FanbeiExceptionCode.NO_DOUBLE12GOODS_ACCEPTED);
					}
					
	            	//---->update 更新 已被秒杀的商品数量（count+1）
	            	afGoodsDouble12Service.updateCountById(goodsId);
		            
				}
			}
		} catch(FanbeiException e){
			logger.error("double12 activity order error = {}", e.getStackTrace());
			throw e;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("double12 activity order error = {}", e.getStackTrace());
			throw new FanbeiException(FanbeiExceptionCode.DOUBLE12ORDER_ERROR);
		} finally{
			bizCacheUtil.delCache(key);
		}
		
	}
	
	/**
	 * 
	* @Title: doubleEggsGoodsCheck
	* @author qiao
	* @date 2017年12月8日 下午3:43:33
	* @Description: 双蛋活动校验，是否是一件，是否是一件
	* @param userId
	* @param goodsId
	* @param count    
	* @return void   
	* @throws
	 */
	private void doubleEggsGoodsCheck(Long userId, Long goodsId, Integer count){
		String key = Constants.CACHKEY_BUY_GOODS_LOCK + ":" + userId + ":" + goodsId;
		try {
			boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
			if (isNotLock) {
				AfGoodsDoubleEggsDo doubleEggsDo = afGoodsDoubleEggsService.getByGoodsId(goodsId);
				if(doubleEggsDo != null){
					if (doubleEggsDo.getEndTime().before(new Date())) {
						//expire
						throw new FanbeiException(FanbeiExceptionCode.DOUBLE_EGGS_EXPIRE);
					}
					
					if (count != 1||afOrderService.getDouble12OrderByGoodsIdAndUserId(goodsId, userId).size()>0) {
						//报错提示只能买一件商品
						throw new FanbeiException(FanbeiExceptionCode.ONLY_ONE_DOUBLE12GOODS_ACCEPTED);
					}
					
					//根据goodsId查询商品信息
					AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
					int goodsDouble12Count = (int) (Integer.parseInt(afGoodsDo.getStockCount())-doubleEggsDo.getAlreadyCount());//秒杀商品余量
					if(goodsDouble12Count<=0){
						//报错提示秒杀商品已售空
						throw new FanbeiException(FanbeiExceptionCode.NO_DOUBLE12GOODS_ACCEPTED);
					}
					
	            	//---->update 更新 已被秒杀的商品数量（count+1）
	            	afGoodsDoubleEggsService.updateCountById(goodsId);
		            
				}
			}
		} catch(FanbeiException e){
			logger.error("double1Egg activity order error = {}", e.getStackTrace());
			throw e;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("double1Egg activity order error = {}", e.getStackTrace());
			throw new FanbeiException(FanbeiExceptionCode.DOUBLE12ORDER_ERROR);
		} finally{
			bizCacheUtil.delCache(key);
		}
		
	}
	
	
	public AfOrderDo orderDoWithGoodsAndAddressDo(AfUserAddressDo addressDo, AfGoodsDo goodsDo, int count) {
		AfOrderDo afOrder = new AfOrderDo();
		afOrder.setConsignee(addressDo.getConsignee());
		afOrder.setConsigneeMobile(addressDo.getMobile());
		afOrder.setSaleAmount(goodsDo.getSaleAmount());// TODO:售价改成从规格中取得。

		afOrder.setPriceAmount(goodsDo.getPriceAmount());
		afOrder.setGoodsIcon(goodsDo.getGoodsIcon());
		afOrder.setGoodsName(goodsDo.getName());
		String address = addressDo.getProvince() != null ? addressDo.getProvince() : "";
		if (addressDo.getCity() != null) {
			address = address.concat(addressDo.getCity());

		}
		if (addressDo.getCounty() != null) {
			address = address.concat(addressDo.getCounty());

		}
		if (addressDo.getAddress() != null) {
			address = address.concat(addressDo.getAddress());
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
		} else {
			afOrder.setOrderType(OrderType.SELFSUPPORT.getCode());
		}
		final String orderNo = generatorClusterNo.getOrderNo(OrderType.SELFSUPPORT);
		afOrder.setOrderNo(orderNo);
		return afOrder;
	}

}
