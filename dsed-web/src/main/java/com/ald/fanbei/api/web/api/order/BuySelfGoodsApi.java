/**
 *
 */
package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.biz.service.AfGoodsDouble12Service;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsService;
import com.ald.fanbei.api.biz.service.AfGoodsPriceService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfModelH5ItemService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfSeckillActivityService;
import com.ald.fanbei.api.biz.service.AfShareGoodsService;
import com.ald.fanbei.api.biz.service.AfShareUserGoodsService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAddressService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfGoodsStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
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
	AfUserCouponService afUserCouponService;
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
	AfUserAuthService afUserAuthService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
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
	@Resource
	AfActivityGoodsService afActivityGoodsService;
	@Resource
	AfModelH5ItemService afModelH5ItemService;
	@Resource
	AfUserAccountSenceService afUserAccountSenceService;
	@Resource
	private AfSeckillActivityService afSeckillActivityService;

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
		//BigDecimal clientActualAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("actualAmount"),BigDecimal.ZERO);
		Long couponId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("couponId"), 0);//用户的优惠券id(af_user_coupon的主键)
		boolean fromCashier =NumberUtil.objToIntDefault(request.getAttribute("fromCashier"), 0) == 0 ? false : true;
		Integer count = NumberUtil.objToIntDefault(requestDataVo.getParams().get("count"), 1);
		
		//alter by chengkang 权限包商品校验，不可重复购买 begin
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		
		AfResourceDo vipGoodsResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.WEAK_VERIFY_VIP_CONFIG.getCode(), AfResourceSecType.ORDER_WEAK_VERIFY_VIP_CONFIG.getCode());
		Integer vipGoodsValidDay = 0;
		if (vipGoodsResourceDo != null){
	    	vipGoodsValidDay = NumberUtil.objToIntDefault(vipGoodsResourceDo.getValue4(), 0);
	    }
		//权限包是否在有效期限内
		boolean vipGoodsIsValidForDate = true;
		if(vipGoodsValidDay>0 && authDo.getGmtOrderWeakRisk()!=null
				&& DateUtil.compareDate(new Date(),DateUtil.addDays(authDo.getGmtOrderWeakRisk(), vipGoodsValidDay))){
			vipGoodsIsValidForDate = false;
		}
		
		if(vipGoodsResourceDo!=null){
			Long vipGoodsId = NumberUtil.objToLongDefault(vipGoodsResourceDo.getValue(), 0L);
			if(vipGoodsId>0 && vipGoodsId.equals(goodsId)){
				if(!YesNoStatus.YES.getCode().equals(authDo.getOrderWeakRiskStatus())){
					//如果用户之前没有购买成功过，则校验已下单数量
					Integer countNums = afOrderService.countSpecGoodsBuyNums(goodsId,userId);
					if(countNums!=null && countNums>0){
						throw new FanbeiException(FanbeiExceptionCode.WEAK_VERIFY_VIP_GOODS_REPEAT_BUY);
					}
				}else{
					//如果在有效期限内，则不允许继续下单
					if(vipGoodsIsValidForDate){
						throw new FanbeiException(FanbeiExceptionCode.WEAK_VERIFY_VIP_GOODS_REPEAT_BUY);
					}
				}
				//本单下单个数，只允许购买一个
				if(count>1){
					throw new FanbeiException(FanbeiExceptionCode.WEAK_VERIFY_VIP_GOODS_BUY_NUMS_OVER);
				}
			}
		}
		//alter by chengkang 权限包商品校验，不可重复购买 end
		
        String lc = ObjectUtils.toString(requestDataVo.getParams().get("lc"));//订单来源地址
        logger.info("add self order 1,lc=" + lc);
        if(StringUtils.isBlank(lc)){
            lc = ObjectUtils.toString(request.getAttribute("lc"));
        }
        logger.info("add self order 2,lc=" + lc);

		Integer appversion = context.getAppVersion();
		Date currTime = new Date();
		int order_pay_time_limit= Constants.ORDER_PAY_TIME_LIMIT;
		//秒杀活动id
		Long activityOrderId = 0l;
		try{
			AfResourceDo resourceDo= afResourceService.getSingleResourceBytype("order_pay_time_limit");
			if(resourceDo!=null){
				order_pay_time_limit=Integer.valueOf(resourceDo.getValue()) ;
				if(order_pay_time_limit==0){
					order_pay_time_limit= Constants.ORDER_PAY_TIME_LIMIT;
				}
			}
		}catch (Exception e){
			logger.error("resource config error:",e);
		}
		Date gmtPayEnd = DateUtil.addHoures(currTime, order_pay_time_limit);
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
		AfGoodsPriceDo afGoodsPriceDo= 	afGoodsPriceService.getById(goodsPriceId);
		BigDecimal couponAmount=BigDecimal.ZERO;
		if(couponId!=0){
			AfUserCouponDto afUserCouponDto=afUserCouponService.getUserCouponById(couponId);
			String type = afUserCouponDto.getType();
			if(StringUtil.isNotBlank(type)&&StringUtil.equals("DISCOUNT",type)){
				BigDecimal amount = afUserCouponDto.getAmount();
				BigDecimal discount = afUserCouponDto.getDiscount();
				discount = BigDecimal.ONE.subtract(discount);
				couponAmount=afGoodsPriceDo.getActualAmount().multiply(new BigDecimal(count)).multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
				if(couponAmount.compareTo(amount)>0){
					couponAmount = amount;
				}
			}else{
				couponAmount=afUserCouponDto.getAmount();
			}
		}
		actualAmount=afGoodsPriceDo.getActualAmount().multiply(new BigDecimal(count)).subtract(couponAmount);
		if(actualAmount.compareTo(BigDecimal.ZERO)<=0){
			actualAmount = new BigDecimal(0.01);
		}
		afOrder.setActualAmount(actualAmount);
		afOrder.setCouponAmount(couponAmount);
		afOrder.setSaleAmount(goodsDo.getSaleAmount().multiply(new BigDecimal(count)));// TODO:售价取规格的。
		//新增下单时，记录ip和同盾设备指纹锁 cxk
		afOrder.setIp(CommonUtil.getIpAddr(request));//用户ip地址
		afOrder.setBlackBox(ObjectUtils.toString(requestDataVo.getParams().get("blackBox")));//加入同盾设备指纹
		afOrder.setBqsBlackBox(ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox")));//加入白骑士设备指纹
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
				BorrowRateBo borrowRate = afResourceService.borrowRateWithResource(nper,context.getUserName(),afOrder.getGoodsId());
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
				List<AfGoodsDoubleEggsDo> listGoods = afGoodsDoubleEggsService.getByGoodsId(goodsId);
				if (CollectionUtil.isNotEmpty(listGoods)) {
					
					Long doubleEggsId = afGoodsDoubleEggsService.getCurrentDoubleGoodsId(goodsId);
					if(doubleEggsId != null){
						doubleEggsGoodsCheck(userId, goodsId,count,doubleEggsId);
					}else {
					    throw new FanbeiException(FanbeiExceptionCode.DOUBLE_EGGS_LIMIT_TIME);
					}
				
					
				}
				// ------------------------------------end mqp doubleEggs------------------------------------

	/*			// 双十二秒杀新增逻辑+++++++++++++>
				if(afGoodsDouble12Service.getByGoodsId(goodsId).size()!=0){
					//是双十二秒杀商品
					double12GoodsCheck(userId, goodsId,count);
				}
				// +++++++++++++++++++++++++<
*/
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
			
			//是否是首单爆款类型
			List<AfModelH5ItemDo> afModelH5ItemDoList = afModelH5ItemService.getModelH5ItemForFirstSingleByGoodsId(goodsId);
			if(afModelH5ItemDoList.size()>0){
			  //判断数量是否是一个
				if (count != 1) {
					//报错提示只能买一件商品
					FanbeiExceptionCode code = FanbeiExceptionCode.ONLY_ONE_GOODS_ACCEPTED;
					ApiHandleResponse resp1 = new ApiHandleResponse(requestDataVo.getId(), code);
					return resp1;

				}
				if(oldUserOrderAmount >0){
				  //报错提示只能买一件商品
					FanbeiExceptionCode code = FanbeiExceptionCode.HAVE_BOUGHT_GOODS;
					ApiHandleResponse resp1 = new ApiHandleResponse(requestDataVo.getId(), code);
					return resp1;

				}
//			       for(AfModelH5ItemDo afModelH5ItemDo:afModelH5ItemDoList ){
//			       }
			}
		}
			//-------------------------------
			//限时抢购增加逻辑
			AfActivityGoodsDo afActivityGoodsDo = afActivityGoodsService.getActivityGoodsByGoodsIdAndType(goodsId);
			if(null != afActivityGoodsDo){
				AfOrderDo afOrderDo = new AfOrderDo();
				afOrderDo.setUserId(userId);
				afOrderDo.setGoodsId(goodsId);
				Integer sum = afOrderService.selectSumCountByGoodsIdAndType(afOrderDo);
				Long limitCount = afActivityGoodsDo.getLimitCount();

				if(null != sum){
					if(limitCount.intValue() - sum < count && limitCount.intValue() != 0){
						throw new FanbeiException(FanbeiExceptionCode.EXCEED_THE_LIMIT_OF_PURCHASE);
					}
				}else{
					if(limitCount.intValue()  < count && limitCount.intValue() != 0){
						throw new FanbeiException(FanbeiExceptionCode.EXCEED_THE_LIMIT_OF_PURCHASE);
					}
				}
			}

			//秒杀活动增加逻辑
			Long activityId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("activityId"), ""),
					0l);
			if(context.getAppVersion()<409){
				//AfGoodsPriceDo  realActualAmount = afGoodsPriceService.getById(goodsPriceId);
				//BigDecimal realAmount = realActualAmount.getActualAmount().multiply(new BigDecimal(count)).subtract(couponAmount);
				//取最新的活动
				AfSeckillActivityDo afSeckillActivityDo = afSeckillActivityService.getStartActivityByPriceId(goodsPriceId);
				if(afSeckillActivityDo!=null){
					activityId = afSeckillActivityDo.getRid();
				}
			}
			logger.error("afSeckillActivity for userId:" + userId + ",activityId:" + activityId);
			if(activityId>0){
				//AfSeckillActivityGoodsDto afSeckillActivityGoodsDto = afSeckillActivityService.getActivityInfoByPriceIdAndActId(goodsPriceId,activityId);
				AfSeckillActivityDo afSeckillActivityDo = afSeckillActivityService.getActivityById(activityId);
				if(afSeckillActivityDo==null){
					//活动未开始或已结束
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SECKILL_ERROR_END);
				}
				AfSeckillActivityGoodsDto afSeckillActivityGoodsDto = afSeckillActivityService.getActivityInfoByPriceIdAndActId(goodsPriceId,activityId);
				//秒杀
				if(afSeckillActivityDo.getType()==2){
					//Long activityId = afSeckillActivityGoodsDto.getActivityId();
					Integer goodsLimitCount = afSeckillActivityGoodsDto.getGoodsLimitCount();
					if(goodsLimitCount!=null&&goodsLimitCount<count){
						//超过购买数量
						return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SECKILL_ERROR_STOCK);
					}
					try{
						//重新计算秒杀实付金额跟返利
						if(afSeckillActivityGoodsDto!=null&&afSeckillActivityGoodsDto.getSpecialPrice().compareTo(BigDecimal.ZERO)>0){
							logger.error("afSeckillActivity getSpecialPrice for userId:" + userId);
							afOrder.setActualAmount(afSeckillActivityGoodsDto.getSpecialPrice().multiply(new BigDecimal(count)).subtract(couponAmount));
							BigDecimal secKillRebAmount = afOrder.getActualAmount().multiply(goodsDo.getRebateRate());
							if(afOrder.getRebateAmount().compareTo(secKillRebAmount)>0){
								afOrder.setRebateAmount(secKillRebAmount);
							}
						}else{
							//秒杀价有问题
							return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SECKILL_ERROR_STOCK);
						}
						Integer remainCount = afSeckillActivityGoodsDto.getLimitCount();
						if(remainCount<0||remainCount-count<0){
							//超过购买数量
							return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SECKILL_ERROR_STOCK);
						}else{
							//判断是否已经购买过该活动商品
							if(goodsLimitCount!=null){
								AfSeckillActivityOrderDo seckillActivityOrderInfo = afSeckillActivityService.getActivityOrderByGoodsIdAndActId(goodsId,activityId,userId);
								if(seckillActivityOrderInfo!=null){
									return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SECKILL_ERROR_STOCK);
								}
							}
							//更新数据库
							AfSeckillActivityGoodsDo afSeckillActivityGoodsDo = new AfSeckillActivityGoodsDo();
							afSeckillActivityGoodsDo.setPriceId(goodsPriceId);
							afSeckillActivityGoodsDo.setLimitCount(count);
							afSeckillActivityGoodsDo.setActivityId(activityId);
							if(afSeckillActivityService.updateActivityGoodsById(afSeckillActivityGoodsDo)>0){
								//创建秒杀单
								AfSeckillActivityOrderDo afSeckillActivityOrderDo = new AfSeckillActivityOrderDo();
								afSeckillActivityOrderDo.setActivityId(activityId);
								afSeckillActivityOrderDo.setSpecialPrice(afSeckillActivityGoodsDto.getSpecialPrice());
								afSeckillActivityOrderDo.setGmtStart(afSeckillActivityGoodsDto.getGmtStart());
								afSeckillActivityOrderDo.setGmtEnd(afSeckillActivityGoodsDto.getGmtEnd());
								afSeckillActivityOrderDo.setOrderId(afOrder.getRid());
								afSeckillActivityOrderDo.setGoodsId(goodsId);
								afSeckillActivityOrderDo.setGmtCreate(new Date());
								afSeckillActivityOrderDo.setGmtModified(new Date());
								afSeckillActivityService.saveActivityOrde(afSeckillActivityOrderDo);
								activityOrderId = afSeckillActivityOrderDo.getRid();
								int closeTime = afSeckillActivityGoodsDto.getCloseTime();
								if(closeTime>0){
									gmtPayEnd = DateUtil.addMins(currTime, closeTime);
									afOrder.setGmtPayEnd(gmtPayEnd);
								}
							}else{
								//超过购买数量
								return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SECKILL_ERROR_STOCK);
							}
						}
					}catch (Exception ex){
						logger.error("afSeckillActivity error for:" + ex);
						//人太多了，被挤爆了
						return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SECKILL_ERROR);
					}
				}else{
					//如果是特惠，规格未配置或者特惠价格为0，正常购买
					/*if(afSeckillActivityGoodsDto==null){

					}else{
						if(afSeckillActivityGoodsDto.getSpecialPrice().compareTo(BigDecimal.ZERO)==0){
							//正常销售
						}
					}*/
					if(afSeckillActivityGoodsDto!=null&&afSeckillActivityGoodsDto.getSpecialPrice().compareTo(BigDecimal.ZERO)>0){
						logger.error("afSeckillActivity getSpecialPrice for userId:" + userId);
						afOrder.setActualAmount(afSeckillActivityGoodsDto.getSpecialPrice().multiply(new BigDecimal(count)).subtract(couponAmount));
						BigDecimal secKillRebAmount = afOrder.getActualAmount().multiply(goodsDo.getRebateRate()).setScale(2,BigDecimal.ROUND_HALF_UP);
						if(afOrder.getRebateAmount().compareTo(secKillRebAmount)>0){
							afOrder.setRebateAmount(secKillRebAmount);
						}
					}
				}
			}
			//-------------------------------

			afGoodsPriceService.updateNewStockAndSaleByPriceId(goodsPriceId,count, true);
			afOrder.setGoodsPriceName(priceDo.getPropertyValueNames());
			afOrder.setSaleAmount(priceDo.getActualAmount().multiply(new BigDecimal(count)));
			afOrder.setPriceAmount(priceDo.getPriceAmount());

		}


		if(couponId!=0){

		}
		afOrder.setLc(lc);
		afOrder.setUserCouponId(couponId);
		//下单时所有场景额度使用情况
		List<AfOrderSceneAmountDo> listSceneAmount = new ArrayList<AfOrderSceneAmountDo>();
		//线上使用情况
		AfOrderSceneAmountDo onlineSceneAmount = new AfOrderSceneAmountDo();
		//培训使用情况
		AfOrderSceneAmountDo trainSceneAmount = new AfOrderSceneAmountDo();
		//获取所有场景额度
		List<AfUserAccountSenceDo> list = afUserAccountSenceService.getByUserId(userId);
		//当前场景额度
		AfUserAccountSenceDo afUserAccountSenceDo = null;

		for (AfUserAccountSenceDo item:list){
			if(item.getScene().equals(UserAccountSceneType.ONLINE.getCode())){
				afUserAccountSenceDo = item;
				onlineSceneAmount.setAuAmount(item.getAuAmount());
				onlineSceneAmount.setScene(UserAccountSceneType.ONLINE.getCode());
				onlineSceneAmount.setUsedAmount(item.getUsedAmount());
				onlineSceneAmount.setUserId(userId);
			}
			if(item.getScene().equals(UserAccountSceneType.TRAIN.getCode())){
				trainSceneAmount.setAuAmount(item.getAuAmount());
				trainSceneAmount.setScene(UserAccountSceneType.TRAIN.getCode());
				trainSceneAmount.setUsedAmount(item.getUsedAmount());
				trainSceneAmount.setUserId(userId);
			}
		}
		if(afUserAccountSenceDo == null){
			afUserAccountSenceDo = new AfUserAccountSenceDo();
			afUserAccountSenceDo.setAuAmount(new BigDecimal(0));
			afUserAccountSenceDo.setFreezeAmount(new BigDecimal(0));
			afUserAccountSenceDo.setUsedAmount(new BigDecimal(0));
		}
		BigDecimal useableAmount = afUserAccountSenceDo.getAuAmount().subtract(afUserAccountSenceDo.getUsedAmount()).subtract(afUserAccountSenceDo.getFreezeAmount());
		afOrder.setAuAmount(afUserAccountSenceDo.getAuAmount());
		afOrder.setUsedAmount(afUserAccountSenceDo.getUsedAmount());
		afOrderService.createOrder(afOrder);
		//如果是秒杀单，创建秒杀订单
		if(activityOrderId!=0){
			AfSeckillActivityOrderDo afSeckillActivityOrderDo = new AfSeckillActivityOrderDo();
			afSeckillActivityOrderDo.setRid(activityOrderId);
			afSeckillActivityOrderDo.setOrderId(afOrder.getRid());
			afSeckillActivityService.updateActivityOrderById(afSeckillActivityOrderDo);
		}
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

		//获取现金贷额度
		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
		//现金贷使用情况
		AfOrderSceneAmountDo cashSceneAmount = new AfOrderSceneAmountDo();
		cashSceneAmount.setOrderId(afOrder.getRid());
		cashSceneAmount.setAuAmount(userAccountInfo.getAuAmount());
		cashSceneAmount.setScene(UserAccountSceneType.CASH.getCode());
		cashSceneAmount.setUsedAmount(userAccountInfo.getUsedAmount());
		cashSceneAmount.setUserId(userId);
		if(onlineSceneAmount.getUserId() == null) {
			onlineSceneAmount.setAuAmount(new BigDecimal(0));
			onlineSceneAmount.setScene(UserAccountSceneType.ONLINE.getCode());
			onlineSceneAmount.setUsedAmount(new BigDecimal(0));
			onlineSceneAmount.setUserId(userId);
		}
		if(trainSceneAmount.getUserId() == null) {
			trainSceneAmount.setAuAmount(new BigDecimal(0));
			trainSceneAmount.setScene(UserAccountSceneType.TRAIN.getCode());
			trainSceneAmount.setUsedAmount(new BigDecimal(0));
			trainSceneAmount.setUserId(userId);
		}
		onlineSceneAmount.setOrderId(afOrder.getRid());
		trainSceneAmount.setOrderId(afOrder.getRid());
		listSceneAmount.add(cashSceneAmount);
		listSceneAmount.add(onlineSceneAmount);
		listSceneAmount.add(trainSceneAmount);
		//添加下单时所有场景额度使用情况
		afOrderService.addSceneAmount(listSceneAmount);

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
//		//首次信用购物,送优惠券
//		  try{
//		        afUserCouponService.sentUserCoupon(afOrder);
//		        }catch(Exception e){
//		          logger.error("first shopping sentUserCoupon error:"+e+afOrder.toString());
//		      }
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
	/*private void double12GoodsCheck(Long userId, Long goodsId, Integer count){
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
		
	}*/
	
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
	private void doubleEggsGoodsCheck(Long userId, Long goodsId, Integer count,Long doubleEggsId){
		String key = Constants.CACHKEY_BUY_GOODS_LOCK + ":" + userId + ":" + goodsId;
		try {
			boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
			if (isNotLock) {

				//--------------------------mqp add redis for goodsDoubleEggs to get rid of different activity goods num limitation--------------
				String key1 = Constants.CACHKEY_DOUBLE_USER +userId+doubleEggsId;
				Integer value = (Integer)bizCacheUtil.getObject(key1);
				if (count != 1 || value != null) {
					throw new FanbeiException(FanbeiExceptionCode.ONLY_ONE_DOUBLE12GOODS_ACCEPTED);
				}
				//--------------------------mqp add redis for goodsDoubleEggs to get rid of different activity goods num limitation--------------

				AfGoodsDoubleEggsDo doubleEggsDo = afGoodsDoubleEggsService.getByDoubleGoodsId(doubleEggsId);
				if(doubleEggsDo != null){
					if (doubleEggsDo.getStartTime().after(new Date())) {
						//before start
						throw new FanbeiException(FanbeiExceptionCode.DOUBLE_EGGS_WITHOUT_START);
					}
					
					if (doubleEggsDo.getEndTime().before(new Date())) {
						//expire
						throw new FanbeiException(FanbeiExceptionCode.DOUBLE_EGGS_EXPIRE);
					}
					
					Integer alreadyCount = 0;
					alreadyCount = afGoodsDoubleEggsService.getAlreadyCount(goodsId);

					//根据goodsId查询商品信息
					AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
					int goodsDouble12Count = (int) (Integer.parseInt(afGoodsDo.getStockCount())-alreadyCount);//秒杀商品余量
					if(goodsDouble12Count <= 0){
						//报错提示秒杀商品已售空
						throw new FanbeiException(FanbeiExceptionCode.NO_DOUBLE12GOODS_ACCEPTED);
					}
					
	            	//---->update 更新 已被秒杀的商品数量（count+1）
	            	afGoodsDoubleEggsService.updateCountById(goodsId);

	            	//--------------------------mqp add redis for goodsDoubleEggs to get rid of different activity goods num limitation--------------
	            	bizCacheUtil.saveObject(key1, 1, Constants.SECOND_OF_ONE_MONTH);

				}
			}
		} catch(FanbeiException e){
			logger.error("doubleEggsGoodsCheck activity order error = {}", e.getStackTrace());
			throw e;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("doubleEggsGoodsCheck activity order error = {}", e.getStackTrace());
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
		
		//新增下单时记录 省、 市、 区 、详细地址 、IP 、设备指纹 2017年12月12日11:17:51 cxk
		String province = addressDo.getProvince() !=null?addressDo.getProvince():"";
		String city = addressDo.getCity() !=null?addressDo.getCity():"";
		String district = addressDo.getCounty() !=null?addressDo.getCounty():"";
		String address = addressDo.getAddress() !=null?addressDo.getAddress():"";
		afOrder.setProvince(province);//省
		afOrder.setCity(city);//市
		afOrder.setDistrict(district);//区
		afOrder.setAddress(address);//详细地址
		/*if (addressDo.getCity() != null) {
			address = address.concat(addressDo.getCity());

		}
		if (addressDo.getCounty() != null) {
			address = address.concat(addressDo.getCounty());

		}
		if (addressDo.getAddress() != null) {
			address = address.concat(addressDo.getAddress());
		}
		afOrder.setAddress(address);*/
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
