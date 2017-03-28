package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.dbunit.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.KaixinUtil;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AccountLogType;
import com.ald.fanbei.api.common.enums.MobileStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowBillDao;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderTempDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderTempDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfOrderQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.response.TaeItemDetailGetResponse;
/**
 *@类描述：
 *@author 何鑫 2017年16月20日 下午4:20:22
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afOrderService")
public class AfOrderServiceImpl extends BaseService implements AfOrderService{

	@Resource
	private AfOrderDao orderDao;
	
	@Resource
	private KaixinUtil kaixinUtil;
	
	@Resource
	private AfUserCouponDao afUserCouponDao;
	
	@Resource
	private  AfUserAccountDao afUserAccountDao;
	
	@Resource
	private GeneratorClusterNo generatorClusterNo;
	
	@Resource
	private AfGoodsDao afGoodsDao;
	
	@Resource
	private TaobaoApiUtil taobaoApiUtil;
	@Resource
	private BizCacheUtil bizCacheUtil;
	
	@Resource
	AfResourceDao afResourceDao;
	@Resource
	private AfOrderTempDao afUserOrderDao;
	@Resource
	private JpushService pushService;
	@Resource
	private AfUserDao afUserDao;
	@Resource
	private AfUserBankcardDao afUserBankcardDao;
	@Resource
	private TransactionTemplate transactionTemplate;
	@Resource
	private AfOrderTempDao afOrderTempDao;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfBorrowBillService afBorrowBillService;
	@Resource
	AfBorrowBillDao afBorrowBillDao;
	
	@Override
	public int createOrderTrade(String content) {
		logger.info("createOrderTrade_content:"+content);
		JSONObject obj = JSON.parseObject(content);
		JSONArray array = JSON.parseArray(obj.getString("auction_infos"));
		JSONObject goodsObj = array.getJSONObject(0);
		Long goodsId =0l;
		String orderType="";
		BigDecimal priceAmount = BigDecimal.ZERO;
		int count = NumberUtil.objToIntDefault(goodsObj.getString("auction_amount"), 1);
		priceAmount = NumberUtil.objToBigDecimalDefault(goodsObj.getString("real_pay"), BigDecimal.ZERO).multiply(new BigDecimal(count));
		AfGoodsDo goods = afGoodsDao.getGoodsByOpenId(goodsObj.getString("auction_id"));
		if(null == goods){
			try {
				TaeItemDetailGetResponse res = taobaoApiUtil.executeTaeItemDetailSearch(goodsObj.getString("auction_id"));
				JSONObject resObj = JSON.parseObject(res.getBody());
				JSONObject sellerInfo = resObj.getJSONObject("tae_item_detail_get_response").getJSONObject("data").getJSONObject("seller_info");
				orderType = sellerInfo.getString("seller_type").toUpperCase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			goodsId = goods.getRid();
			orderType = goods.getSource();
		}
		AfOrderDo order = buildOrder(null,obj.getString("order_id"), "",0l, null, priceAmount, priceAmount, "", BigDecimal.ZERO, 
				orderType, priceAmount, goodsId, goodsObj.getString("auction_id"), 
				goodsObj.getString("auction_title"), Constants.CONFKEY_TAOBAO_ICON_COMMON_LOCATION+goodsObj.getString("auction_pict_url"), count, obj.getString("shop_title"),0l);
		return orderDao.createOrder(order);
	}

	@Override
	public int updateOrderTradeSuccess(String content) {
		logger.info("updateOrderTradeSuccess_content:"+content);
		AfOrderDo order = new AfOrderDo();
		order.setOrderNo(JSON.parseObject(content).getString("order_id"));
		order.setStatus(OrderStatus.FINISHED.getCode());
		order.setGmtFinished(new Date());
		return orderDao.updateOrderByOrderNo(order);
	}

	@Override
	public int updateOrderTradePaidDone(String content) {
		logger.info("updateOrderTradePaidDone_content:"+content);
		String orderNo = JSON.parseObject(content).getString("order_id");
		AfOrderTempDo orderTemp = afOrderTempDao.getByOrderNo(orderNo);
		AfOrderDo order = new AfOrderDo();
		order.setOrderNo(orderNo);
		if(orderTemp != null){
			order.setUserId(orderTemp.getUserId());
			orderTemp.setStatus(YesNoStatus.YES.getCode());
			afOrderTempDao.updateUserOrderTemp(orderTemp);
		}
		order.setStatus(OrderStatus.PAID.getCode());
		order.setGmtPay(new Date());
		return orderDao.updateOrderByOrderNo(order);
	}

	@Override
	public int updateOrderTradeClosed(String content) {
		logger.info("updateOrderTradeClosed_content:"+content);
		AfOrderDo order = new AfOrderDo();
		order.setOrderNo(JSON.parseObject(content).getString("order_id"));
		order.setStatus(OrderStatus.CLOSED.getCode());
		
		return orderDao.updateOrderByOrderNo(order);
	}

	@Override
	public void dealMobileChargeOrder(final String payOrderNo,final String tradeNo) {
		transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					//支付成功后,直接返利
					AfOrderDo newOrder = new AfOrderDo();
					newOrder.setPayTradeNo(payOrderNo);
					newOrder.setStatus(OrderStatus.REBATED.getCode());
					newOrder.setGmtFinished(new Date());
					newOrder.setGmtRebated(new Date());
					newOrder.setTradeNo(tradeNo);
					orderDao.updateOrderByOutTradeNo(newOrder);
					//查询订单
					AfOrderDo order = orderDao.getOrderInfoByPayOrderNo(payOrderNo);
					//优惠券设置已使用
					afUserCouponDao.updateUserCouponSatusUsedById(order.getUserCouponId());
					//返利金额
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(order.getUserId());
					account.setRebateAmount(order.getRebateAmount());
					afUserAccountDao.updateUserAccount(account);
					//获取用户信息
					AfUserDo userDo = afUserDao.getUserById(order.getUserId());
					if(StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE), Constants.INVELOMENT_TYPE_ONLINE)){
						String msg = kaixinUtil.charge(order.getOrderNo(), order.getMobile(), order.getSaleAmount().setScale(0).toString());
						JSONObject returnMsg = JSON.parseObject(msg);
						JSONObject result = JSON.parseObject(returnMsg.getString("result"));
						if(!result.getString("ret_code").equals(MobileStatus.SUCCESS.getCode())){
							//System.out.println(result.getString("ret_msg"));
							//退款 生成退款记录  走微信退款流程，或者银行卡代付
							//设置优惠券为未使用状态
							afUserCouponDao.updateUserCouponSatusNouseById(order.getUserCouponId());
							//返利金额
							account.setRebateAmount(order.getRebateAmount().multiply(new BigDecimal(-1)));
							afUserAccountDao.updateUserAccount(account);
							if(order.getBankId()<0){//微信退款
								try {
									String refundResult = UpsUtil.wxRefund(order.getOrderNo(), order.getPayTradeNo(), order.getActualAmount(), order.getActualAmount());
									if(!"SUCCESS".equals(refundResult)){
										throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
									}
								} catch (Exception e) {
									pushService.refundMobileError(userDo.getUserName(), order.getGmtCreate());
									logger.info("wxRefund error:",e);
								}
							}else{//银行卡代付
								//TODO 转账处理
								AfBankUserBankDto card = afUserBankcardDao.getUserBankcardByBankId(order.getBankId());
								UpsDelegatePayRespBo upsResult = UpsUtil.delegatePay(order.getActualAmount(), userDo.getRealName(), card.getCardNumber(), order.getUserId()+"", 
										card.getMobile(), card.getBankName(), card.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02",OrderType.MOBILE.getCode(),"");
								if(!upsResult.isSuccess()){
									pushService.refundMobileError(userDo.getUserName(), order.getGmtCreate());
								}
							}
							newOrder.setStatus(OrderStatus.CLOSED.getCode());
							orderDao.updateOrderByOutTradeNo(newOrder);
							pushService.chargeMobileError(userDo.getUserName(), order.getMobile(), order.getGmtCreate());
						}else{
							pushService.chargeMobileSucc(userDo.getUserName(), order.getMobile(), order.getGmtCreate());
						}
					}else{
						pushService.chargeMobileSucc(userDo.getUserName(), order.getMobile(), order.getGmtCreate());
					}
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealMobileChargeOrder error:",e);
				}
				return null;
			}
		});
	}

	/**
	 * 
	 * @param orderNo  --订单编号
	 * @param userId   --用户id
	 * @param couponDto --优惠券对象
	 * @param money     --订单原价
	 * @param saleAmount --订单折后价
	 * @param mobile  --手机号
	 * @param rebateAmount --返利金额
	 * @param orderType  --订单类型
	 * @param actualAmount --实际支付价格
	 * @param goodsId --商品id
	 * @param openId  --商品混淆id
	 * @param goodsName --商品名称
	 * @param goodsIcon --商品图片
	 * @param count --数量
	 * @param shopName --店铺名称
	 * @return
	 */
	private AfOrderDo buildOrder(Date now,String orderNo,String payTradeNo,Long userId, AfUserCouponDto couponDto,
			BigDecimal money,BigDecimal saleAmount, String mobile,BigDecimal rebateAmount,String orderType,BigDecimal actualAmount,
			Long goodsId,String openId,String goodsName,String goodsIcon,int count,String shopName,Long bankId){
		AfOrderDo orderDo = new AfOrderDo();
		orderDo.setGmtCreate(now);
		orderDo.setUserId(userId);
		orderDo.setOrderNo(orderNo);
		orderDo.setPayTradeNo(payTradeNo);
		orderDo.setOrderType(orderType);
		orderDo.setGoodsId(goodsId);
		orderDo.setOpenId(openId);
		orderDo.setGoodsName(goodsName);
		orderDo.setGoodsIcon(goodsIcon);
		orderDo.setCount(count);
		orderDo.setShopName(shopName);
		orderDo.setPriceAmount(money);
		orderDo.setSaleAmount(saleAmount);
		if(null == couponDto){
			orderDo.setUserCouponId(0l);
			
		}else{
			orderDo.setUserCouponId(couponDto.getRid());
		}
		orderDo.setActualAmount(actualAmount);
		orderDo.setRebateAmount(rebateAmount);
		orderDo.setMobile(mobile);
		orderDo.setBankId(bankId);
		return orderDo;
	}

	@Override
	public String getCurrentLastOrderNo(Date currentDate, String orderType) {
		Date startDate = DateUtil.getStartOfDate(currentDate);
		Date endDate = DateUtil.getEndOfDate(currentDate);
		return orderDao.getCurrentLastOrderNo(startDate,endDate, orderType);
	}

	@Override
	public AfOrderDo getOrderInfoById(Long id,Long userId) {
		return orderDao.getOrderInfoById(id,userId);
	}

	@Override
	public List<AfOrderDo> getOrderListByStatus(Integer pageNo, String status,Long userId) {
		AfOrderQuery query = new AfOrderQuery();
		query.setPageNo(pageNo);
		query.setOrderStatus(status);
		query.setUserId(userId);
		return orderDao.getOrderListByStatus(query);
	}

	@Override
	public int syncOrderNoWithUser(Long userId, String orderNo) {
		AfOrderTempDo order = new AfOrderTempDo();
		order.setOrderNo(orderNo);
		order.setUserId(userId);
		return afUserOrderDao.addUserOrder(order);
	}

	@Override
	public Map<String,Object> createMobileChargeOrder(AfUserBankcardDo card,String userName,final Long userId,
			final AfUserCouponDto couponDto,final BigDecimal money,final String mobile,
			final BigDecimal rebateAmount,final Long bankId,String clientIp,AfUserAccountDo afUserAccountDo) {
		final Date now = new Date();
		final String orderNo = generatorClusterNo.getOrderNo(OrderType.MOBILE);
		final BigDecimal actualAmount = couponDto==null?money:money.subtract(couponDto.getAmount());
		Map<String,Object> map;
		//订单创建
		orderDao.createOrder(buildOrder(now,orderNo,orderNo,userId, couponDto, money,money, mobile, rebateAmount, 
				OrderType.MOBILE.getCode(),actualAmount, 0l, "",Constants.DEFAULT_MOBILE_CHARGE_NAME, "", 1, "",bankId));
		if(bankId<0){//微信支付
			map = UpsUtil.buildWxpayTradeOrder(orderNo, userId, Constants.DEFAULT_MOBILE_CHARGE_NAME, actualAmount,PayOrderSource.ORDER.getCode());
		}else{//银行卡支付 代收
			map = new HashMap<String,Object>();
			UpsCollectRespBo respBo = UpsUtil.collect(orderNo,actualAmount, userId+"", afUserAccountDo.getRealName(), card.getMobile(), 
					card.getBankCode(), card.getCardNumber(), afUserAccountDo.getIdNumber(), Constants.DEFAULT_MOBILE_CHARGE_NAME, "手机充值", "02",OrderType.MOBILE.getCode());
			map.put("resp", respBo);
		}
		return map;
	}

	@Override
	public AfOrderDo getThirdOrderInfoByOrderTypeAndOrderNo(String orderType,
			String thirdOrderNo) {
		return orderDao.getThirdOrderInfoByOrderTypeAndOrderNo(orderType, thirdOrderNo);
	}

	@Override
	public int createOrder(AfOrderDo afOrder) {
		logger.info("createOrder begin orderInfo = {}" , afOrder);
		return orderDao.createOrder(afOrder);
	}

	@Override
	public int updateOrder(AfOrderDo afOrder) {
		logger.info("updateOrder begin orderInfo = {}" , afOrder);
		return orderDao.updateOrder(afOrder);
	}

	@Override
	public int dealBoluomeOrder(final AfOrderDo afOrder) {
		logger.info("dealBoluomeOrder begin , afOrder = {}"+afOrder);
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					OrderStatus orderStatus = OrderStatus.findRoleTypeByCode(afOrder.getStatus());
					switch (orderStatus) {
					case FINISHED:
						logger.info("status is finished");
						Long userId = afOrder.getUserId();
						afOrder.setStatus(OrderStatus.REBATED.getCode());
						afOrder.setGmtRebated(new Date());
						afOrder.setGmtFinished(new Date());
						AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(userId);
						accountInfo.setRebateAmount(BigDecimalUtil.add(accountInfo.getRebateAmount(), afOrder.getRebateAmount()));
						AfUserAccountLogDo accountLog = buildUserAccount(accountInfo.getRebateAmount(), userId, afOrder.getRid(), AccountLogType.REBATE);
						afUserAccountDao.updateOriginalUserAccount(accountInfo);
						afUserAccountLogDao.addUserAccountLog(accountLog);
						orderDao.updateOrder(afOrder);
						break;
					default:
						logger.info(" status is {} ",afOrder.getStatus());
						orderDao.updateOrder(afOrder);
						break;
					}
					logger.info("dealBoluomeOrder complete!");
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealBoluomeOrder error:",e);
					return 0;
				}
			}
		});
	}
	
	private AfUserAccountLogDo buildUserAccount(BigDecimal amount,Long userId,Long orderId, AccountLogType logType){
		//增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(orderId+"");
		accountLog.setType(logType.getCode());
		return accountLog;
	}

	@Override
	public AfOrderDo getOrderById(Long id) {
		return orderDao.getOrderById(id);
	}

	@Override
	public Map<String, Object> payBrandOrder(final AfOrderDo orderInfo, final Integer nper) {
		return transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doInTransaction(TransactionStatus status) {
				try {
					Map<String,Object> resultMap = new HashMap<String,Object>();
					orderInfo.setGmtPay(new Date());
					orderInfo.setPayStatus(PayStatus.PAYED.getCode());
					orderInfo.setStatus(OrderStatus.PAID.getCode());
					orderInfo.setActualAmount(orderInfo.getSaleAmount());
					Long payId = orderInfo.getBankId();
					if(payId < 0 ){
						orderInfo.setPayType(PayType.WECHAT.getCode());
						//微信支付
						return UpsUtil.buildWxpayTradeOrder(orderInfo.getOrderNo(), orderInfo.getUserId(), orderInfo.getGoodsName(), orderInfo.getSaleAmount(),PayOrderSource.BRAND_ORDER.getCode());
					} else if (payId == 0) {
						//代付
						orderInfo.setPayType(PayType.AGENT_PAY.getCode());
						AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(orderInfo.getUserId());
						BigDecimal useableAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount()).subtract(userAccountInfo.getFreezeAmount());
						if (useableAmount.compareTo(orderInfo.getSaleAmount()) < 0) {
							throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_MONEY_ERROR);
						}
						afBorrowService.dealBrandConsumeApply(userAccountInfo, orderInfo.getSaleAmount(), orderInfo.getGoodsName(), nper, orderInfo.getRid(), orderInfo.getOrderNo());
						
					} else {
						orderInfo.setPayType(PayType.BANK.getCode());
						
						AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(orderInfo.getUserId());
						
						AfUserBankcardDo cardInfo = afUserBankcardService.getUserBankcardById(payId);
						
						resultMap = new HashMap<String,Object>();
						
						if(null == cardInfo){
							throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
						}
						//银行卡支付 代收
						UpsCollectRespBo respBo = UpsUtil.collect(orderInfo.getOrderNo(),orderInfo.getSaleAmount(), orderInfo.getUserId()+"", userAccountInfo.getRealName(), cardInfo.getMobile(), 
								cardInfo.getBankCode(), cardInfo.getCardNumber(), userAccountInfo.getIdNumber(), Constants.DEFAULT_BRAND_SHOP, "品牌订单支付", "02",OrderType.BOLUOME.getCode());
						if(!respBo.isSuccess()) {
							throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
						}
						Map<String,Object> newMap = new HashMap<String,Object>();
						newMap.put("outTradeNo", respBo.getOrderNo());
						newMap.put("tradeNo", respBo.getTradeNo());
						newMap.put("cardNo", Base64.encodeString(respBo.getCardNo()));
						resultMap.put("resp", respBo);
					}
					orderDao.updateOrder(orderInfo);
			 		return resultMap;
				} catch (FanbeiException exception) {
					status.setRollbackOnly();
					throw new FanbeiException("bank card pay error", exception.getErrorCode());
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("payBrandOrder faied e = {}", e );
					return null;
				}
			}
		});
	}

	@Override
	public int dealBrandOrder(final String payOrderNo, final String tradeNo, final String payType) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					logger.info("dealBrandOrder begin , payOrderNo = {} and tradeNo = {} and type = {}", new Object[]{payOrderNo, tradeNo, payType});
					AfOrderDo orderInfo = orderDao.getOrderInfoByPayOrderNo(payOrderNo);
					orderInfo.setPayTradeNo(payOrderNo);
					orderInfo.setStatus(OrderStatus.PAID.getCode());
					orderInfo.setPayType(payType);
					orderInfo.setGmtPay(new Date());
					orderInfo.setTradeNo(tradeNo);
					orderDao.updateOrder(orderInfo);
					logger.info("dealBrandOrder comlete , orderInfo = {} ", orderInfo);
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealBrandOrder error:",e);
					return 0;
				}
			}
		});
	}

	@Override
	public int dealBrandOrderRefund(final AfOrderDo orderInfo) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					logger.info("dealBrandOrderRefund begin , orderInfo = {} ", orderInfo);
					
					PayType payType = PayType.findRoleTypeByCode(orderInfo.getPayType());
					switch (payType) {
					case WECHAT:
						String refundResult = UpsUtil.wxRefund(orderInfo.getOrderNo(), orderInfo.getPayTradeNo(), orderInfo.getActualAmount(), orderInfo.getActualAmount());
						logger.info("wx refund  , refundResult = {} ", refundResult);
						if(!"SUCCESS".equals(refundResult)){
							throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
						}
						break;
					case AGENT_PAY:
						logger.info("agent pay refund  , refundResult = {} ");
//						AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(orderInfo.getUserId());
//						
//						BigDecimal usedAmount = BigDecimalUtil.add(accountInfo.getUsedAmount(), orderInfo.getActualAmount());
//						accountInfo.setUsedAmount(usedAmount);
//						afUserAccountDao.updateOriginalUserAccount(accountInfo);
//						AfBorrowDo borrowInfo = afBorrowService.getBorrowByOrderId(orderInfo.getRid());
//						afBorrowBillDao.getBorrowBillByBorrowId(borrowId)
//						afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.AP_REFUND, orderInfo.getActualAmount(), orderInfo.getUserId(), orderInfo.getRid()));
						
						break;
					case BANK:
						Long userId = orderInfo.getUserId();
						Long bankId = orderInfo.getBankId();
						AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
						AfUserBankcardDo card = afUserBankcardDao.getUserBankInfo(bankId);
						UpsDelegatePayRespBo upsResult = UpsUtil.delegatePay(orderInfo.getActualAmount(), userAccount.getRealName(), card.getCardNumber(), orderInfo.getUserId()+"", 
								card.getMobile(), card.getBankName(), card.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02",OrderType.BOLUOME.getCode(),"");
						logger.info("bank refund upsResult = {}", upsResult);
						if(!upsResult.isSuccess()){
							throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
						}
						break;
					default:
						break;
					}
					
					orderInfo.setStatus(OrderStatus.DEAL_REFUNDING.getCode());
					orderDao.updateOrder(orderInfo);
					
					logger.info("dealBrandOrderRefund comlete , orderInfo = {} ", orderInfo);
					return 1;
				} catch (FanbeiException e) {
					status.setRollbackOnly();
					logger.error("dealBrandOrderRefund error = {}", e);
					throw new FanbeiException("reund error", e.getErrorCode());
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealBrandOrderRefund error:",e);
					return 0;
				}
			}
		});
	}
	
	private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType logType,BigDecimal amount,Long userId,Long orderId){
		//增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(orderId+"");
		accountLog.setType(logType.getCode());
		return accountLog;
	}
	
	
	public static void main(String[] args) {
		System.out.println(UUID.randomUUID());
	}
}
