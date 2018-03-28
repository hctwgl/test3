package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.bo.InterestFreeJsonBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaRespBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityService;
import com.ald.fanbei.api.biz.service.AfBoluomeRebateService;
import com.ald.fanbei.api.biz.service.AfBoluomeUserCouponService;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfCheckoutCounterService;
import com.ald.fanbei.api.biz.service.AfContractPdfCreateService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsReservationService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfTradeCodeInfoService;
import com.ald.fanbei.api.biz.service.AfTradeOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAmountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserCouponTigerMachineService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.KaixinUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AccountLogType;
import com.ald.fanbei.api.common.enums.AfGoodsReservationStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.BorrowCalculateMethod;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.MobileStatus;
import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.OrderSecType;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.OrderTypeSecSence;
import com.ald.fanbei.api.common.enums.OrderTypeThirdSence;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.RefundSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.InterestFreeUitl;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.OrderNoUtils;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowBillDao;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfBorrowExtendDao;
import com.ald.fanbei.api.dal.dao.AfGoodsCategoryDao;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.dao.AfInterimAuDao;
import com.ald.fanbei.api.dal.dao.AfInterimDetailDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderRefundDao;
import com.ald.fanbei.api.dal.dao.AfOrderTempDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfShopDao;
import com.ald.fanbei.api.dal.dao.AfTradeBusinessInfoDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountSenceDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfCheckoutCounterDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGoodsCategoryDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsReservationDo;
import com.ald.fanbei.api.dal.domain.AfInterimAuDo;
import com.ald.fanbei.api.dal.domain.AfInterimDetailDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;
import com.ald.fanbei.api.dal.domain.AfOrderSceneAmountDo;
import com.ald.fanbei.api.dal.domain.AfOrderTempDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfTradeOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserVirtualAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfOrderDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfOrderQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.domain.XItem;
import com.taobao.api.response.TaeItemDetailGetResponse;

/**
 * @author 何鑫 2017年16月20日 下午4:20:22 @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afOrderService")
public class AfOrderServiceImpl extends BaseService implements AfOrderService {

	@Resource
	private AfOrderDao orderDao;
	@Resource
	private AfBorrowDao afBorrowDao;
	@Resource
	private KaixinUtil kaixinUtil;

	@Resource
	private AfUserCouponDao afUserCouponDao;

	@Resource
	private AfUserAccountDao afUserAccountDao;

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
	@Resource
	BoluomeUtil boluomeUtil;
	@Resource
	AfOrderRefundDao afOrderRefundDao;
	@Resource
	UpsUtil upsUtil;
	@Resource
	AfAgentOrderService afAgentOrderService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserVirtualAccountService afUserVirtualAccountService;
	@Resource
	AfTradeBusinessInfoDao afTradeBusinessInfoDao;
	@Resource
	AfTradeOrderService afTradeOrderService;

	@Resource
	AfRecommendUserService afRecommendUserService;
	@Resource
	AfBoluomeActivityService afBoluomeActivityService;
	@Resource
	private AfGoodsService afGoodsService;
	@Resource
	private AfUserService afUserService;
	@Resource
	private SmsUtil smsUtil;
	@Resource
	private AfGoodsReservationService afGoodsReservationService;
	@Resource
	JpushService jpushService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfUserCouponService afUserCouponService;

	@Resource
	AfContractPdfCreateService afContractPdfCreateService;

	@Resource
	AfBoluomeRebateService afBoluomeRebateService;
	@Resource
	AfBoluomeUserCouponService afBoluomeUserCouponService;
	@Resource
	AfUserAccountSenceService afUserAccountSenceService;
	@Resource
	private AfTradeCodeInfoService afTradeCodeInfoService;
	@Resource
	AfInterimDetailDao afInterimDetailDao;
	@Resource
	AfCheckoutCounterService afCheckoutCounterService;

	@Autowired
	private AfShopDao afShopDao;
	@Resource
	AfUserCouponTigerMachineService afUserCouponTigerMachineService;

	@Override
	public AfOrderDo getOrderInfoByPayOrderNo(String payTradeNo) {
		return orderDao.getOrderInfoByPayOrderNo(payTradeNo);
	}

	@Resource
	AfInterimAuDao afInterimAuDao;

	@Resource
	AfBorrowExtendDao afBorrowExtendDao;
	@Resource
	AfUserAccountSenceDao afUserAccountSenceDao;

	@Override
	public int getNoFinishOrderCount(Long userId) {
		return orderDao.getNoFinishOrderCount(userId);
	}

	@Resource
	AfUserAmountService afUserAmountService;
	@Autowired
	AfGoodsCategoryDao afGoodsCategoryDao;

	@Override
	public int createOrderTrade(final String content) {
		logger.info("createOrderTrade_content:" + content);
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					JSONObject obj = JSON.parseObject(content);
					JSONArray array = JSON.parseArray(obj.getString("auction_infos"));
					List<AfOrderDo> orderList = new ArrayList<AfOrderDo>();
					if (CollectionUtils.isNotEmpty(array)) {
						// 如果有多个 生成多个订单
						for (int i = 0; i < array.size(); i++) {
							JSONObject goodsObj = array.getJSONObject(i);
							Long goodsId = 0l;
							String orderType = "";
							String numId = StringUtils.EMPTY;
							BigDecimal priceAmount = BigDecimal.ZERO;
							int count = NumberUtil.objToIntDefault(goodsObj.getString("auction_amount"), 1);
							priceAmount = NumberUtil.objToBigDecimalDefault(goodsObj.getString("real_pay"),
									BigDecimal.ZERO);
							AfGoodsDo goods = afGoodsDao.getGoodsByOpenId(goodsObj.getString("auction_id"));
							if (null == goods) {
								Map<String, Object> params = new HashMap<String, Object>();
								params.put(TaobaoApiUtil.OPEN_IID, goodsObj.getString("auction_id"));

								List<XItem> nTbkItemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();
								if (nTbkItemList != null && nTbkItemList.get(0) != null) {
									XItem item = nTbkItemList.get(0);

									logger.info("createOrderTrade_content item is not null");
									orderType = item.getMall() ? OrderType.TMALL.getCode() : OrderType.TAOBAO.getCode();
									numId = item.getOpenId() + StringUtils.EMPTY;

								} else {
									// 默认值
									TaeItemDetailGetResponse res = taobaoApiUtil
											.executeTaeItemDetailSearch(goodsObj.getString("auction_id"));
									logger.info("createOrderTrade_content item is null res = {}", res);
									JSONObject resObj = JSON.parseObject(res.getBody());
									if (resObj.getJSONObject("tae_item_detail_get_response") != null) {
										JSONObject sellerInfo = resObj.getJSONObject("tae_item_detail_get_response")
												.getJSONObject("data").getJSONObject("seller_info");
										orderType = sellerInfo.getString("seller_type").toUpperCase();
									}

									numId = StringUtils.EMPTY;
								}

							} else {
								goodsId = goods.getRid();
								orderType = goods.getSource();
								numId = goods.getNumId();
							}
							AfOrderDo order = buildFullInfo(0l, obj.getString("order_id"),
									goodsObj.getString("detail_order_id"), StringUtils.EMPTY, OrderStatus.NEW.getCode(),
									0l, orderType, StringUtils.EMPTY, goodsId, goodsObj.getString("auction_id"), numId,
									goodsObj.getString("auction_title"),
									Constants.CONFKEY_TAOBAO_ICON_COMMON_LOCATION
											+ goodsObj.getString("auction_pict_url"),
									count, priceAmount, priceAmount, priceAmount, obj.getString("shop_title"),
									PayStatus.NOTPAY.getCode(), StringUtils.EMPTY, StringUtils.EMPTY, null,
									StringUtils.EMPTY, null, StringUtils.EMPTY, null, BigDecimal.ZERO, BigDecimal.ZERO,
									0l, null, "", 0L);
							orderList.add(order);
						}
						logger.info("createOrderTrade_content orderList = {}", orderList);
						return orderDao.createOrderList(orderList);
					}
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealMobileChargeOrder error:", e);
					return 0;
				}
			}
		});
	}

	@Override
	public int updateOrderTradeSuccess(String content) {
		logger.info("updateOrderTradeSuccess_content:" + content);
		AfOrderDo order = new AfOrderDo();
		order.setOrderNo(JSON.parseObject(content).getString("order_id"));
		order.setStatus(OrderStatus.FINISHED.getCode());
		order.setGmtFinished(new Date());
		return orderDao.updateOrderByOrderNo(order);
	}

	@Override
	public int updateOrderTradePaidDone(String content) {
		logger.info("updateOrderTradePaidDone_content:" + content);
		String orderNo = JSON.parseObject(content).getString("order_id");
		AfOrderTempDo orderTemp = afOrderTempDao.getByOrderNo(orderNo);
		AfOrderDo order = new AfOrderDo();
		order.setOrderNo(orderNo);
		if (orderTemp != null) {
			if (orderTemp.getOrderId() > 0) {
				logger.info("orderTemp1==" + JSON.toJSONString(orderTemp));

				AfAgentOrderDo afAgentOrderDo = new AfAgentOrderDo();
				AfOrderDo temorder = orderDao.getOrderInfoByOrderNo(orderNo);
				afAgentOrderDo.setOrderId(orderTemp.getOrderId());
				afAgentOrderDo.setStatus("BUY");
				afAgentOrderDo.setMatchOrderId(temorder.getRid());
				afAgentOrderService.updateAgentOrder(afAgentOrderDo);
				AfOrderDo applyOrder = new AfOrderDo();
				applyOrder.setRid(orderTemp.getOrderId());
				applyOrder.setStatus(OrderStatus.AGENCYCOMPLETED.getCode());
				orderDao.updateOrder(applyOrder);
			}
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
		logger.info("updateOrderTradeClosed_content:" + content);
		AfOrderDo order = new AfOrderDo();
		order.setOrderNo(JSON.parseObject(content).getString("order_id"));
		order.setStatus(OrderStatus.CLOSED.getCode());

		return orderDao.updateOrderByOrderNo(order);
	}

	@Override
	public void dealMobileChargeOrder(final String payOrderNo, final String tradeNo) {
		transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					AfOrderDo orderInfo = orderDao.getOrderInfoByOrderNo(payOrderNo);
					if (orderInfo == null || orderInfo.getStatus().equals(OrderStatus.REBATED.getCode())) {
						return null;
					}
					// 支付成功后,直接返利
					AfOrderDo newOrder = new AfOrderDo();
					newOrder.setPayTradeNo(payOrderNo);
					newOrder.setStatus(OrderStatus.REBATED.getCode());
					newOrder.setGmtFinished(new Date());
					newOrder.setGmtRebated(new Date());
					newOrder.setTradeNo(tradeNo);
					orderDao.updateOrderByOutTradeNo(newOrder);
					// 查询订单
					AfOrderDo order = orderDao.getOrderInfoByPayOrderNo(payOrderNo);
					// 优惠券设置已使用
					afUserCouponDao.updateUserCouponSatusUsedById(order.getUserCouponId());
					// 返利金额
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(order.getUserId());
					account.setRebateAmount(order.getRebateAmount());
					afUserAccountDao.updateUserAccount(account);
					// 获取用户信息
					AfUserDo userDo = afUserDao.getUserById(order.getUserId());
					String msg = kaixinUtil.charge(order.getOrderNo(), order.getMobile(),
							order.getSaleAmount().setScale(0).toString());
					JSONObject returnMsg = JSON.parseObject(msg);
					JSONObject result = JSON.parseObject(returnMsg.getString("result"));
					if (!result.getString("ret_code").equals(MobileStatus.SUCCESS.getCode())) {
						// System.out.println(result.getString("ret_msg"));
						// 退款 生成退款记录 走微信退款流程，或者银行卡代付
						// 设置优惠券为未使用状态
						afUserCouponDao.updateUserCouponSatusNouseById(order.getUserCouponId());
						// 返利金额
						account.setRebateAmount(order.getRebateAmount().multiply(new BigDecimal(-1)));
						afUserAccountDao.updateUserAccount(account);
						if (order.getBankId() < 0) {// 微信退款
							try {
								String refundNo = generatorClusterNo.getRefundNo(new Date());
								String refundResult = UpsUtil.wxRefund(order.getOrderNo(), order.getPayTradeNo(),
										order.getActualAmount(), order.getActualAmount());
								if (!"SUCCESS".equals(refundResult)) {
									afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo,
											order.getActualAmount(), order.getActualAmount(), order.getUserId(),
											order.getRid(), order.getOrderNo(), OrderRefundStatus.FAIL, PayType.WECHAT,
											StringUtils.EMPTY, null, "充值失败微信退款", RefundSource.PLANT_FORM.getCode(),
											payOrderNo));
									throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
								} else {
									afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo,
											order.getActualAmount(), order.getActualAmount(), order.getUserId(),
											order.getRid(), order.getOrderNo(), OrderRefundStatus.FINISH,
											PayType.WECHAT, StringUtils.EMPTY, null, "充值失败微信退款",
											RefundSource.PLANT_FORM.getCode(), payOrderNo));
								}
							} catch (Exception e) {
								pushService.refundMobileError(userDo.getUserName(), order.getGmtCreate());
								logger.info("wxRefund error:", e);
							}
						} else {// 银行卡代付
							// TODO 转账处理
							AfBankUserBankDto card = afUserBankcardDao.getUserBankcardByBankId(order.getBankId());
							String refundNo = generatorClusterNo.getRefundNo(new Date());
							UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(order.getActualAmount(),
									userDo.getRealName(), card.getCardNumber(), order.getUserId() + "",
									card.getMobile(), card.getBankName(), card.getBankCode(),
									Constants.DEFAULT_REFUND_PURPOSE, "02", OrderType.MOBILE.getCode(), "");
							if (!upsResult.isSuccess()) {
								afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo,
										order.getActualAmount(), order.getActualAmount(), order.getUserId(),
										order.getRid(), order.getOrderNo(), OrderRefundStatus.FAIL, PayType.BANK,
										card.getCardNumber(), card.getBankName(), "充值失败银行卡退款",
										RefundSource.PLANT_FORM.getCode(), upsResult.getOrderNo()));
								pushService.refundMobileError(userDo.getUserName(), order.getGmtCreate());
							} else {
								afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo,
										order.getActualAmount(), order.getActualAmount(), order.getUserId(),
										order.getRid(), order.getOrderNo(), OrderRefundStatus.FINISH, PayType.BANK,
										card.getCardNumber(), card.getBankName(), "充值失败银行卡退款",
										RefundSource.PLANT_FORM.getCode(), upsResult.getOrderNo()));
							}
						}
						newOrder.setStatus(OrderStatus.CLOSED.getCode());
						orderDao.updateOrderByOutTradeNo(newOrder);
						pushService.chargeMobileError(userDo.getUserName(), order.getMobile(), order.getGmtCreate());
					}
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealMobileChargeOrder error:", e);
				}
				return null;
			}
		});
	}

	@Override
	public void notifyMobileChargeOrder(final String orderNo, final String orderStatus) {
		transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					// 查询订单
					AfOrderDo order = orderDao.getOrderInfoByOrderNo(orderNo);
					if (order != null) {
						// 获取用户信息
						AfUserDo userDo = afUserDao.getUserById(order.getUserId());
						if ("SUCCESS".equals(orderStatus) && OrderStatus.CLOSED.getCode().equals(order.getStatus())) {
							order.setStatus(OrderStatus.REBATED.getCode());
							orderDao.updateOrderByOrderNo(order);

							// 返利金额
							AfUserAccountDo account = new AfUserAccountDo();
							account.setUserId(order.getUserId());
							account.setRebateAmount(order.getRebateAmount());
							afUserAccountDao.updateUserAccount(account);
							pushService.chargeMobileSucc(userDo.getUserName(), order.getMobile(), order.getGmtCreate());
						} else if (!"SUCCESS".equals(orderStatus)
								&& OrderStatus.REBATED.getCode().equals(order.getStatus())) {
							// 退款 生成退款记录 走微信退款流程，或者银行卡代付
							// 设置优惠券为未使用状态
							afUserCouponDao.updateUserCouponSatusNouseById(order.getUserCouponId());
							// // 返利金额
							AfUserAccountDo account = new AfUserAccountDo();
							account.setUserId(order.getUserId());
							account.setRebateAmount(order.getRebateAmount().multiply(new BigDecimal(-1)));
							afUserAccountDao.updateUserAccount(account);
							String refundNo = generatorClusterNo.getRefundNo(new Date());
							if (order.getBankId() < 0) {// 微信退款
								try {
									String refundResult = UpsUtil.wxRefund(order.getOrderNo(), order.getPayTradeNo(),
											order.getActualAmount(), order.getActualAmount());
									if (!"SUCCESS".equals(refundResult)) {
										afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo,
												order.getActualAmount(), order.getActualAmount(), order.getUserId(),
												order.getRid(), orderNo, OrderRefundStatus.FAIL, PayType.WECHAT,
												StringUtils.EMPTY, null, "充值失败微信退款", RefundSource.PLANT_FORM.getCode(),
												order.getPayTradeNo()));
										throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
									} else {
										afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo,
												order.getActualAmount(), order.getActualAmount(), order.getUserId(),
												order.getRid(), orderNo, OrderRefundStatus.FINISH, PayType.WECHAT,
												StringUtils.EMPTY, null, "充值失败微信退款", RefundSource.PLANT_FORM.getCode(),
												order.getPayTradeNo()));
									}
								} catch (Exception e) {
									pushService.refundMobileError(userDo.getUserName(), order.getGmtCreate());
									logger.info("wxRefund error:", e);
								}
							} else {// 银行卡代付
								// TODO 转账处理
								AfBankUserBankDto card = afUserBankcardDao.getUserBankcardByBankId(order.getBankId());
								UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(order.getActualAmount(),
										userDo.getRealName(), card.getCardNumber(), order.getUserId() + "",
										card.getMobile(), card.getBankName(), card.getBankCode(),
										Constants.DEFAULT_REFUND_PURPOSE, "02", OrderType.MOBILE.getCode(), "");
								if (!upsResult.isSuccess()) {
									afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo,
											order.getActualAmount(), order.getActualAmount(), order.getUserId(),
											order.getRid(), orderNo, OrderRefundStatus.FAIL, PayType.BANK,
											card.getCardNumber(), card.getBankName(), "充值失败银行卡退款",
											RefundSource.PLANT_FORM.getCode(), upsResult.getOrderNo()));
									pushService.refundMobileError(userDo.getUserName(), order.getGmtCreate());
								} else {
									afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo,
											order.getActualAmount(), order.getActualAmount(), order.getUserId(),
											order.getRid(), orderNo, OrderRefundStatus.FINISH, PayType.BANK,
											card.getCardNumber(), card.getBankName(), "充值失败银行卡退款",
											RefundSource.PLANT_FORM.getCode(), upsResult.getOrderNo()));
								}
							}
							// 支付成功后,直接返利
							AfOrderDo newOrder = new AfOrderDo();
							newOrder.setGmtFinished(new Date());
							newOrder.setGmtRebated(new Date());
							newOrder.setStatus(OrderStatus.CLOSED.getCode());
							orderDao.updateOrderByOutTradeNo(newOrder);
							pushService.chargeMobileError(userDo.getUserName(), order.getMobile(),
									order.getGmtCreate());
						}
					} else {
						logger.error("notifyPhoneRecharge error,orderNo：【" + orderNo + "】");
					}

				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("notifyMobileChargeOrder error:", e);
				}
				return null;
			}
		});
	}

	/**
	 * @param orderNo
	 *            --订单编号
	 * @param userId
	 *            --用户id
	 * @param couponDto
	 *            --优惠券对象
	 * @param money
	 *            --订单原价
	 * @param saleAmount
	 *            --订单折后价
	 * @param mobile
	 *            --手机号
	 * @param rebateAmount
	 *            --返利金额
	 * @param orderType
	 *            --订单类型
	 * @param actualAmount
	 *            --实际支付价格
	 * @param goodsId
	 *            --商品id
	 * @param openId
	 *            --商品混淆id
	 * @param goodsName
	 *            --商品名称
	 * @param goodsIcon
	 *            --商品图片
	 * @param count
	 *            --数量
	 * @param shopName
	 *            --店铺名称
	 * @return
	 */
	private AfOrderDo buildOrder(Date now, String orderNo, String payTradeNo, Long userId, AfUserCouponDto couponDto,
			BigDecimal money, BigDecimal saleAmount, String mobile, BigDecimal rebateAmount, String orderType,
			BigDecimal actualAmount, Long goodsId, String openId, String goodsName, String goodsIcon, int count,
			String shopName, Long bankId, String clientIp, String blackBox, String bqsBlackBox) {
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
		// 新增下单时记录 IP 、设备指纹 2017年12月12日13:28:59 cxk
		orderDo.setIp(clientIp);// 用户ip地址
		orderDo.setBlackBox(blackBox);// 加入同盾设备指纹
		orderDo.setBqsBlackBox(bqsBlackBox);// 加入白骑士设备指纹
		if (null == couponDto) {
			orderDo.setUserCouponId(0l);

		} else {
			orderDo.setUserCouponId(couponDto.getRid());
		}
		orderDo.setActualAmount(actualAmount);
		orderDo.setRebateAmount(rebateAmount);
		orderDo.setMobile(mobile);
		orderDo.setBankId(bankId);
		// AfUserAccountDo userAccountInfo =
		// AfUserAccountDo userAccountInfo =
		// afUserAccountService.getUserAccountByUserId(userId);
		AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService
				.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), NumberUtil.objToLongDefault(userId, 0l));
		if (afUserAccountSenceDo != null) {
			orderDo.setAuAmount(afUserAccountSenceDo.getAuAmount());
			orderDo.setUsedAmount(afUserAccountSenceDo.getUsedAmount());
		}

		return orderDo;
	}

	private AfOrderDo buildFullInfo(Long userId, String orderNo, String thirdOrderNo, String thirdDetailUrl,
			String status, Long userCouponId, String orderType, String secType, Long goodsId, String openId,
			String numId, String goodsName, String goodsIcon, Integer count, BigDecimal priceAmount,
			BigDecimal saleAmount, BigDecimal actualAmount, String shopName, String payStatus, String payType,
			String payTradeNo, Date gmtPay, String tradeNo, Date gmtRebated, String mobile, Date gmtFinished,
			BigDecimal rebateAmount, BigDecimal commissionAmount, Long bankId, Date gmtPayEnd, String goodsPriceName,
			Long goodsPriceId) {
		AfOrderDo orderDo = new AfOrderDo();
		orderDo.setUserId(userId);
		orderDo.setOrderNo(orderNo);
		orderDo.setThirdOrderNo(thirdOrderNo);
		orderDo.setThirdDetailUrl(thirdDetailUrl);
		orderDo.setStatus(status);
		orderDo.setUserCouponId(userCouponId);
		orderDo.setOrderType(orderType);
		orderDo.setSecType(secType);
		orderDo.setGoodsId(goodsId);
		orderDo.setOpenId(openId);
		orderDo.setNumId(numId);
		orderDo.setGoodsName(goodsName);
		orderDo.setGoodsIcon(goodsIcon);
		orderDo.setCount(count);
		orderDo.setPriceAmount(priceAmount);
		orderDo.setSaleAmount(saleAmount);
		orderDo.setActualAmount(actualAmount);
		orderDo.setShopName(shopName);
		orderDo.setPayStatus(payStatus);
		orderDo.setPayType(payType);
		orderDo.setPayTradeNo(payTradeNo);
		orderDo.setGmtPay(gmtPay);
		orderDo.setTradeNo(tradeNo);
		orderDo.setGmtRebated(gmtRebated);
		orderDo.setMobile(mobile);
		orderDo.setGmtFinished(gmtFinished);
		orderDo.setRebateAmount(rebateAmount);
		orderDo.setCommissionAmount(commissionAmount);
		orderDo.setBankId(bankId);
		orderDo.setGmtPayEnd(gmtPayEnd);
		orderDo.setGoodsPriceId(goodsPriceId);
		orderDo.setGoodsPriceName(goodsPriceName);

		// AfUserAccountDo accountDo =
		// afUserAccountDao.getUserAccountInfoByUserId(userId);
		AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService
				.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), NumberUtil.objToLongDefault(userId, 0l));
		if (afUserAccountSenceDo != null) {
			orderDo.setAuAmount(afUserAccountSenceDo.getAuAmount());
			orderDo.setUsedAmount(afUserAccountSenceDo.getUsedAmount());
		}
		return orderDo;
	}

	@Override
	public String getCurrentLastOrderNo(Date currentDate, String orderType) {
		Date startDate = DateUtil.getStartOfDate(currentDate);
		Date endDate = DateUtil.getEndOfDate(currentDate);
		return orderDao.getCurrentLastOrderNo(startDate, endDate, orderType);
	}

	@Override
	public AfOrderDo getOrderInfoById(Long id, Long userId) {
		return orderDao.getOrderInfoById(id, userId);
	}

	@Override
	public List<AfOrderDo> getOrderListByStatus(Integer pageNo, String status, Long userId) {
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
	public Map<String, Object> createMobileChargeOrder(AfUserBankcardDo card, String userName, Long userId,
			AfUserCouponDto couponDto, BigDecimal money, String mobile, BigDecimal rebateAmount, Long bankId,
			String clientIp, AfUserAccountDo afUserAccountDo, String blackBox, String bqsBlackBox) {
		final Date now = new Date();
		final String orderNo = generatorClusterNo.getOrderNo(OrderType.MOBILE);
		final BigDecimal actualAmount = couponDto == null ? money : money.subtract(couponDto.getAmount());
		Map<String, Object> map;
		// 订单创建
		orderDao.createOrder(buildOrder(now, orderNo, orderNo, userId, couponDto, money, money, mobile, rebateAmount,
				OrderType.MOBILE.getCode(), actualAmount, 0l, "", Constants.DEFAULT_MOBILE_CHARGE_NAME, "", 1, "",
				bankId, clientIp, blackBox, bqsBlackBox));
		if (bankId < 0) {// 微信支付
			map = UpsUtil.buildWxpayTradeOrder(orderNo, userId, Constants.DEFAULT_MOBILE_CHARGE_NAME, actualAmount,
					PayOrderSource.ORDER.getCode());
		} else {// 银行卡支付 代收
			map = new HashMap<String, Object>();
			UpsCollectRespBo respBo = upsUtil.collect(orderNo, actualAmount, userId + "", afUserAccountDo.getRealName(),
					card.getMobile(), card.getBankCode(), card.getCardNumber(), afUserAccountDo.getIdNumber(),
					Constants.DEFAULT_MOBILE_CHARGE_NAME, "手机充值", "02", OrderType.MOBILE.getCode());
			if (!respBo.isSuccess()) {
				if (StringUtil.isNotBlank(respBo.getRespCode())) {
					// 模版数据map处理
					Map<String, String> replaceMapData = new HashMap<String, String>();
					String errorMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());
					replaceMapData.put("errorMsg", errorMsg);
					try {
						AfUserDo userDo = afUserService.getUserById(userId);
						smsUtil.sendConfigMessageToMobile(userDo.getMobile(), replaceMapData, 0,
								AfResourceType.SMS_TEMPLATE.getCode(),
								AfResourceSecType.SMS_BANK_PAY_ORDER_FAIL.getCode());
					} catch (Exception e) {
						logger.error("pay order rela bank pay error,userId=" + userId, e);
					}
					throw new FanbeiException(errorMsg);
				}
				throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
			}
			map.put("resp", respBo);
		}
		return map;
	}

	@Override
	public AfOrderDo getThirdOrderInfoByOrderTypeAndOrderNo(String orderType, String thirdOrderNo) {
		return orderDao.getThirdOrderInfoByOrderTypeAndOrderNo(orderType, thirdOrderNo);
	}

	@Override
	public int createOrder(AfOrderDo afOrder) {
		logger.info("createOrder begin orderInfo = {}", afOrder);
		return orderDao.createOrder(afOrder);
	}

	@Override
	public int updateOrder(AfOrderDo afOrder) {
		logger.info("updateOrder begin orderInfo = {}", afOrder);
		return orderDao.updateOrder(afOrder);
	}

	// @Override
	// public int dealBoluomeOrder(final AfOrderDo afOrder) {
	// try {
	// OrderStatus orderStatus =
	// OrderStatus.findRoleTypeByCode(afOrder.getStatus());
	// switch (orderStatus) {
	// case FINISHED:
	// callbackCompleteOrder(afOrder);
	// break;
	// default:
	// logger.info(" status is {} ", afOrder.getStatus());
	// orderDao.updateOrder(afOrder);
	// break;
	// }
	// logger.info("dealBoluomeOrder complete!");
	// return 1;
	// } catch (Exception e) {
	// logger.info("dealBoluomeOrder error:", e);
	// return 0;
	// }
	// }

	@Override
	public int callbackCompleteOrder(final AfOrderDo afOrder) {
		logger.info("callbackCompleteOrder begin , afOrder = {}" + afOrder);
		final String key = Constants.GG_SURPRISE_LOCK + ":" + afOrder.getUserId() + ":" + afOrder.getRid();
		long c = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 60, TimeUnit.SECONDS);
		if (c > 1) {
			return 0;
		}
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					Long userId = afOrder.getUserId();
					afOrder.setStatus(OrderStatus.REBATED.getCode());
					afOrder.setGmtRebated(new Date());
					afOrder.setGmtFinished(new Date());
					AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(userId);
					accountInfo.setRebateAmount(
							BigDecimalUtil.add(accountInfo.getRebateAmount(), afOrder.getRebateAmount()));
					AfUserAccountLogDo accountLog = buildUserAccount(accountInfo.getRebateAmount(), userId,
							afOrder.getRid(), AccountLogType.REBATE);
					afUserAccountDao.updateOriginalUserAccount(accountInfo);
					afUserAccountLogDao.addUserAccountLog(accountLog);
					orderDao.updateOrder(afOrder);
					try {
						doboluomeActivityRebate(afOrder, userId, key);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace(); 不影响主业务
						logger.info("doboluomeActivityRebate error:", e);
					}
					redisTemplate.delete(key);
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					redisTemplate.delete(key);
					logger.info("callbackCompleteOrder error:", e);
					return 0;
				}
			}
		});
	}

	private void doboluomeActivityRebate(AfOrderDo afOrder, Long userId, String key) {

		// ----------------------------------------------mqp add a
		// switch--------------------------------------------------
		AfResourceDo afResourceDo = new AfResourceDo();
		afResourceDo = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "ACTIVITY_SWITCH");
		if (afResourceDo != null) {
			String swtich = "";
			String ctype = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
			// 线上为开启状态
			if (Constants.INVELOMENT_TYPE_ONLINE.equals(ctype) || Constants.INVELOMENT_TYPE_TEST.equals(ctype)) {
				swtich = afResourceDo.getValue();
			} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(ctype)) {
				swtich = afResourceDo.getValue1();
			}
			// String swtich = afResourceDo.getValue();

			if (StringUtil.isNotBlank(swtich) && swtich.equals("O")) {
				// qiao+2017-11-14 15:30:27:the second time to light the
				// activity
				logger.info(
						"doboluomeActivityRebate afBoluomeRebateService.doboluomeActivityRebate params orderId = {} , userId = {}",
						afOrder.getRid(), userId);
				// send red packet
				try {
					afBoluomeRebateService.addRedPacket(afOrder.getRid(), userId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.info("doboluomeActivityRebate addRedPacket error:", e);
				}

				// qiao+2017-11-14 15:30:27:the second time to light the
				// activity
				logger.info(
						"doboluomeActivityRebate afBoluomeRebateService.sendCoupon  doboluomeActivityRebateparams orderId = {} , userId = {}",
						afOrder.getRid(), userId);
				// send coupon
				boolean flag1 = afBoluomeUserCouponService.sendCoupon(userId);
			}
		}

		// ----------------------------------------------mqp end add a
		// switch--------------------------------------------------
		logger.info("doboluomeActivityRebate complete!");
		redisTemplate.delete(key);
	}

	@Resource
	RedisTemplate redisTemplate;

	@Override
	public int dealBoluomeOrder(final AfOrderDo afOrder) {
		final String key = Constants.GG_SURPRISE_LOCK + ":" + afOrder.getUserId() + ":" + afOrder.getRid();
		long c = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 60, TimeUnit.SECONDS);
		if (c > 1) {
			return 0;
		}

		logger.info("dealBoluomeOrder begin , afOrder = {}" + afOrder);
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

						// update the table af_user_account with row lock mood
						AfUserAccountDo accountInfo = new AfUserAccountDo();
						accountInfo.setUserId(userId);
						accountInfo.setRebateAmount(afOrder.getRebateAmount());
						int updateResult = afUserAccountDao.updateRebateAmount(accountInfo);

						AfUserAccountLogDo accountLog = buildUserAccount(accountInfo.getRebateAmount(), userId,
								afOrder.getRid(), AccountLogType.REBATE);
						afUserAccountLogDao.addUserAccountLog(accountLog);
						orderDao.updateOrder(afOrder);

						// ----------------------------------------------mqp add
						// a
						// switch--------------------------------------------------
						AfResourceDo afResourceDo = new AfResourceDo();
						afResourceDo = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "ACTIVITY_SWITCH");
						if (afResourceDo != null) {
							String swtich = "";
							String ctype = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
							// 线上为开启状态
							if (Constants.INVELOMENT_TYPE_ONLINE.equals(ctype)
									|| Constants.INVELOMENT_TYPE_TEST.equals(ctype)) {
								swtich = afResourceDo.getValue();
							} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(ctype)) {
								swtich = afResourceDo.getValue1();
							}
							// String swtich = afResourceDo.getValue();

							if (StringUtil.isNotBlank(swtich) && swtich.equals("O")) {
								// qiao+2017-11-14 15:30:27:the second time to
								// light the activity
								logger.info("afBoluomeRebateService.addRedPacket params orderId = {} , userId = {}",
										afOrder.getRid(), userId);
								// send red packet
								afBoluomeRebateService.addRedPacket(afOrder.getRid(), userId);

								// qiao+2017-11-14 15:30:27:the second time to
								// light the activity
								logger.info("afBoluomeRebateService.sendCoupon params orderId = {} , userId = {}",
										afOrder.getRid(), userId);
								// send coupon
								boolean flag1 = afBoluomeUserCouponService.sendCoupon(userId);
							}
						}

						// ----------------------------------------------mqp end
						// add a
						// switch--------------------------------------------------
						break;
					default:
						logger.info(" status is {} ", afOrder.getStatus());
						orderDao.updateOrder(afOrder);
						break;
					}
					logger.info("dealBoluomeOrder complete!");
					redisTemplate.delete(key);
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealBoluomeOrder error:", e);
					redisTemplate.delete(key);
					return 0;
				}
			}
		});
	}

	private AfUserAccountLogDo buildUserAccount(BigDecimal amount, Long userId, Long orderId, AccountLogType logType) {
		// 增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(orderId + "");
		accountLog.setType(logType.getCode());
		return accountLog;
	}

	@Override
	public void deleteOrder(Long userId, Long orderId) {

		// 参数基本检查
		if (orderId == null) {
			throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
		}

		// 用户订单检查
		AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId, userId);
		if (null == orderInfo) {
			throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
		}

		// 校验订单状态是否满足删除条件
		List<String> canDelStatus = new ArrayList<String>();
		canDelStatus.add(OrderStatus.NEW.getCode());
		canDelStatus.add(OrderStatus.FINISHED.getCode());
		canDelStatus.add(OrderStatus.REBATED.getCode());
		canDelStatus.add(OrderStatus.CLOSED.getCode());
		if (canDelStatus.contains(orderInfo.getStatus())) {
			// 订单状态改为删除
			int nums = orderDao.deleteOrder(orderId);
			if (nums <= 0) {
				throw new FanbeiException(FanbeiExceptionCode.FAILED);
			}
			// 如果为待支付定单，在删除时减少商品销量
			if (OrderStatus.NEW.getCode().equals(orderInfo.getStatus())) {
				afGoodsService.updateSelfSupportGoods(orderInfo.getGoodsId(), -orderInfo.getCount());
			}
		} else {
			throw new FanbeiException(FanbeiExceptionCode.ORDER_NOFINISH_CANNOT_DELETE);
		}
	}

	@Override
	public AfOrderDo getOrderById(Long id) {
		return orderDao.getOrderById(id);
	}

	// @Override
	// public int deleteOrder(Long id) {
	// return orderDao.deleteOrder(id);
	// }

	@Override
	public Map<String, Object> payBrandOrder(final String userName, final Long payId, final String payType,
			final Long orderId, final Long userId, final String orderNo, final String thirdOrderNo,
			final String goodsName, final BigDecimal saleAmount, final Integer nper, final String appName,
			final String ipAddress) {
		final AfOrderDo orderInfo = orderDao.getOrderInfoById(orderId, userId);
		final HashMap<String, HashMap> riskDataMap = new HashMap();

		final HashMap summaryData = afBorrowDao.getUserSummary(userId);
		riskDataMap.put("summaryData", summaryData);

		if (orderId > 0) {
			HashMap summaryOrderData = afBorrowDao.getUserSummaryOrderById(orderId);
			if (OrderType.BOLUOME.getCode().equals(orderInfo.getOrderType())) {
				HashMap bolume = afBorrowDao.getBolumeSumDataById(orderId);
				if (bolume != null) {
					summaryOrderData.putAll(bolume);
				}
			}
			riskDataMap.put("summaryOrderData", summaryOrderData);
		} else {
			riskDataMap.put("summaryOrderData", new HashMap<>());
		}

		return transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doInTransaction(TransactionStatus status) {
				Date currentDate = new Date();
				String tradeNo = generatorClusterNo.getOrderPayNo(currentDate);
				Map<String, Object> resultMap = new HashMap<String, Object>();

				try {
					// 查卡号，用于调用风控接口
					AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);

					Boolean isSelf = StringUtils.equals(orderInfo.getOrderType(), OrderType.SELFSUPPORT.getCode());
					orderInfo.setRid(orderId);
					orderInfo.setPayTradeNo(tradeNo);
					orderInfo.setGmtPay(currentDate);
					orderInfo.setActualAmount(saleAmount);
					orderInfo.setBankId(payId);
					if (payType.equals(PayType.WECHAT.getCode())) {
						orderInfo.setPayType(PayType.WECHAT.getCode());
						logger.info("payBrandOrder orderInfo = {}", orderInfo);
						orderDao.updateOrder(orderInfo);
						// 微信支付
						resultMap = UpsUtil.buildWxpayTradeOrder(tradeNo, userId, goodsName, saleAmount, isSelf
								? PayOrderSource.SELFSUPPORT_ORDER.getCode() : PayOrderSource.BRAND_ORDER.getCode());
						resultMap.put("success", true);
						// 活动返利
						return resultMap;
					} else if (payType.equals(PayType.AGENT_PAY.getCode())) {
						// 先做判断
						AfUserAccountSenceDo userAccountInfo = afUserAccountSenceDao
								.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), userId);// afUserAccountService.getUserAccountByUserId(userId);
						Map<String, Object> virtualMap = afOrderService.getVirtualCodeAndAmount(orderInfo);
						// 判断使用额度
						BigDecimal leftAmount = checkUsedAmount(virtualMap, orderInfo, userAccountInfo);
						if (leftAmount.compareTo(orderInfo.getActualAmount()) < 0) {
							throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_MONEY_ERROR);
						}

						orderInfo.setNper(nper);
						BorrowRateBo bo = null;
						if (nper == 1) {
							// 信用支付
							bo = afResourceService.borrowRateWithResourceCredit(nper);
						} else {
							if (OrderType.TRADE.getCode().equals(orderInfo.getOrderType())) {
								bo = afResourceService.borrowRateWithResourceForTrade(nper);
							} else {
								bo = afResourceService.borrowRateWithResource(nper, userName);
							}
						}
						String boStr = BorrowRateBoUtil.parseToDataTableStrFromBo(bo);
						orderInfo.setBorrowRate(boStr);
						logger.info("updateOrder orderInfo = {}", orderInfo);

						String cardNo = card.getCardNumber();
						String riskOrderNo = riskUtil.getOrderNo("vefy",
								cardNo.substring(cardNo.length() - 4, cardNo.length()));
						orderInfo.setRiskOrderNo(riskOrderNo);
						orderDao.updateOrder(orderInfo);

						String name = orderInfo.getGoodsName();
						if (orderInfo.getOrderType().equals(OrderType.TRADE.getCode())) {
							name = orderInfo.getShopName();
						}
						AfBorrowDo borrow = buildAgentPayBorrow(name, BorrowType.TOCONSUME, userId,
								orderInfo.getActualAmount(), nper, BorrowStatus.APPLY.getCode(), orderId, orderNo,
								orderInfo.getBorrowRate(), orderInfo.getInterestFreeJson(), orderInfo.getOrderType());
						borrow.setVersion(1);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String borrowTime = sdf.format(borrow.getGmtCreate());
						// 最后调用风控控制
						String _vcode = getVirtualCode(virtualMap);
						String str = orderInfo.getGoodsName();
						if (OrderType.BOLUOME.getCode().equals(orderInfo.getOrderType())) {
							str = OrderType.BOLUOME.getCode();
						}
						if (OrderType.TRADE.getCode().equals(orderInfo.getOrderType())) {
							AfTradeOrderDo afTradeOrderDo = new AfTradeOrderDo();
							afTradeOrderDo.setOrderId(orderInfo.getRid());
							AfTradeOrderDo result = afTradeOrderService.getByCommonCondition(afTradeOrderDo);
							str = String.valueOf(result.getBusinessId());
							_vcode = "99";
						}
						logger.info("verify userId" + userId);
						RiskVerifyRespBo verybo = riskUtil.weakRiskForXd(ObjectUtils.toString(userId, ""),
								borrow.getBorrowNo(), borrow.getNper().toString(), "40", card.getCardNumber(), appName,
								ipAddress, orderInfo.getBlackBox(), riskOrderNo, userName, orderInfo.getActualAmount(),
								BigDecimal.ZERO, borrowTime, str, _vcode, orderInfo.getOrderType(),
								orderInfo.getSecType(), orderInfo.getRid(), card.getBankName(), borrow, payType,
								riskDataMap, orderInfo.getBqsBlackBox(), orderInfo);
						logger.info("verybo=" + verybo);
						if (verybo.isSuccess()) {
							logger.info("pay result is true");
							// #region add by honghzengpei
							// afRecommendUserService.updateRecommendByBorrow(userId,borrow.getGmtCreate());
							// #endregion
							return riskUtil.payOrder(resultMap, borrow, verybo.getOrderNo(), verybo, virtualMap,
									orderInfo);
						}

						// verybo.getResult()=10,则成功，活动返利
					} else if (payType.equals(PayType.COMBINATION_PAY.getCode())) {// 组合支付
						// 先做判断
						AfUserAccountSenceDo userAccountInfo = afUserAccountSenceDao
								.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), userId);// afUserAccountService.getUserAccountByUserId(userId);
						Map<String, Object> virtualMap = afOrderService.getVirtualCodeAndAmount(orderInfo);
						// 判断使用额度
						BigDecimal leftAmount = checkUsedAmount(virtualMap, orderInfo, userAccountInfo);

						// 银行卡需要支付的金额
						BigDecimal bankAmount = BigDecimalUtil.subtract(saleAmount, leftAmount);
						AfUserBankcardDo cardInfo = afUserBankcardService.getUserBankcardById(payId);
						//还款金额是否大于银行单笔限额
					        //afUserBankcardService.checkUpsBankLimit(cardInfo.getBankCode(), bankAmount);
						
						orderInfo.setNper(nper);
						BorrowRateBo bo = afResourceService.borrowRateWithResource(nper, userName);
						String boStr = BorrowRateBoUtil.parseToDataTableStrFromBo(bo);
						orderInfo.setBorrowRate(boStr);

						String cardNo = card.getCardNumber();
						String riskOrderNo = riskUtil.getOrderNo("vefy",
								cardNo.substring(cardNo.length() - 4, cardNo.length()));
						orderInfo.setRiskOrderNo(riskOrderNo);
						orderInfo.setBorrowAmount(leftAmount);
						orderInfo.setBankAmount(bankAmount);
						orderDao.updateOrder(orderInfo);

						// 用额度进行分期
						AfBorrowDo borrow = buildAgentPayBorrow(orderInfo.getGoodsName(), BorrowType.TOCONSUME, userId,
								leftAmount, nper, BorrowStatus.APPLY.getCode(), orderId, orderNo,
								orderInfo.getBorrowRate(), orderInfo.getInterestFreeJson(), orderInfo.getOrderType());
						borrow.setVersion(1);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String borrowTime = sdf.format(borrow.getGmtCreate());
						String codeForSecond = null;
						String codeForThird = null;
						codeForSecond = OrderTypeSecSence.getCodeByNickName(orderInfo.getOrderType());
						codeForThird = OrderTypeThirdSence.getCodeByNickName(orderInfo.getSecType());
						// 通过弱风控后才进行后续操作
						RiskVerifyRespBo verybo = riskUtil.weakRiskForXd(ObjectUtils.toString(userId, ""),
								borrow.getBorrowNo(), borrow.getNper().toString(), "40", card.getCardNumber(), appName,
								ipAddress, orderInfo.getBlackBox(), riskOrderNo, userName, leftAmount, BigDecimal.ZERO,
								borrowTime,
								OrderType.BOLUOME.getCode().equals(orderInfo.getOrderType())
										? OrderType.BOLUOME.getCode() : orderInfo.getGoodsName(),
								getVirtualCode(virtualMap), orderInfo.getOrderType(), orderInfo.getSecType(),
								orderInfo.getRid(), card.getBankName(), borrow, payType, riskDataMap,
								orderInfo.getBqsBlackBox(), orderInfo);
						if (verybo.isSuccess()) {
							logger.info("combination_pay result is true");
							orderInfo.setPayType(PayType.COMBINATION_PAY.getCode());
							orderInfo.setPayStatus(PayStatus.DEALING.getCode());
							orderInfo.setStatus(OrderStatus.DEALING.getCode());
							orderDao.updateOrder(orderInfo);

							resultMap = new HashMap<String, Object>();

							if (null == cardInfo) {
								throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
							}
							logger.info("combination_pay orderInfo = {}", orderInfo);

							Map<String, Object> result = riskUtil.combinationPay(userId, orderNo, orderInfo, tradeNo,
									resultMap, isSelf, virtualMap, bankAmount, borrow, verybo, cardInfo);
							result.put("status", PayStatus.DEALING.getCode());
							return result;
						}
					} else {
						orderInfo.setPayType(PayType.BANK.getCode());
						Map<String, Object> newMap = new HashMap<String, Object>();
						if (saleAmount.compareTo(BigDecimal.ZERO) <= 0) {
							orderInfo.setPayStatus(PayStatus.PAYED.getCode());
							orderInfo.setStatus(OrderStatus.PAID.getCode());
							logger.info("payBrandOrder orderInfo = {}", orderInfo);
							orderDao.updateOrder(orderInfo);
							resultMap.put("resp", newMap);
							resultMap.put("status", PayStatus.PAYED.getCode());
							resultMap.put("success", true);
						} else {
							orderInfo.setPayStatus(PayStatus.DEALING.getCode());
							orderInfo.setStatus(OrderStatus.DEALING.getCode());
							AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);

							AfUserBankcardDo cardInfo = afUserBankcardService.getUserBankcardById(payId);
							if (null == cardInfo) {
								throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
							}
							//还款金额是否大于银行单笔限额
						        //afUserBankcardService.checkUpsBankLimit(cardInfo.getBankCode(), saleAmount);							
							resultMap = new HashMap<String, Object>();
						        
							logger.info("payBrandOrder orderInfo = {}", orderInfo);
							orderDao.updateOrder(orderInfo);
							// 银行卡支付 代收
							UpsCollectRespBo respBo = upsUtil.collect(tradeNo, saleAmount, userId + "",
									userAccountInfo.getRealName(), cardInfo.getMobile(), cardInfo.getBankCode(),
									cardInfo.getCardNumber(), userAccountInfo.getIdNumber(),
									Constants.DEFAULT_BRAND_SHOP, isSelf ? "自营商品订单支付" : "品牌订单支付", "02",
									isSelf ? OrderType.SELFSUPPORT.getCode() : OrderType.BOLUOME.getCode());
							if (!respBo.isSuccess()) {
								if (StringUtil.isNotBlank(respBo.getRespCode())) {
									// 模版数据map处理
									Map<String, String> replaceMapData = new HashMap<String, String>();
									String errorMsg = afTradeCodeInfoService
											.getRecordDescByTradeCode(respBo.getRespCode());
									replaceMapData.put("errorMsg", errorMsg);
									try {
										AfUserDo userDo = afUserService.getUserById(userId);
										smsUtil.sendConfigMessageToMobile(userDo.getMobile(), replaceMapData, 0,
												AfResourceType.SMS_TEMPLATE.getCode(),
												AfResourceSecType.SMS_BANK_PAY_ORDER_FAIL.getCode());
									} catch (Exception e) {
										logger.error("pay order rela bank pay error,userId=" + userId, e);
									}
									throw new FanbeiException(errorMsg);
								}
								throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
							}
							newMap.put("outTradeNo", respBo.getOrderNo());
							newMap.put("tradeNo", respBo.getTradeNo());
							newMap.put("cardNo", Base64.encodeString(respBo.getCardNo()));
							resultMap.put("resp", newMap);
							resultMap.put("status", PayStatus.DEALING.getCode());
							resultMap.put("success", true);
						}
						// 活动返利
					}
					return resultMap;
				} catch (FanbeiException exception) {
					logger.error("payBrandOrder faied e = {}", exception);
					// 自营,代买或商圈记录支付失败信息，然后返回客户端提示
					if (OrderType.getNeedRecordPayFailCodes().contains(orderInfo.getOrderType())) {
						String payFailMsg = "";
						if (FanbeiExceptionCode.BORROW_CONSUME_MONEY_ERROR.getCode()
								.equals(exception.getErrorCode().getCode())) {
							payFailMsg = Constants.PAY_ORDER_USE_AMOUNT_LESS;
						} else if (FanbeiExceptionCode.RISK_VERIFY_ERROR.getCode()
								.equals(exception.getErrorCode().getCode())
								|| FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR.getCode()
										.equals(exception.getErrorCode().getCode())
								|| FanbeiExceptionCode.UPS_COLLECT_ERROR.getCode()
										.equals(exception.getErrorCode().getCode())
								|| FanbeiExceptionCode.BANK_CARD_PAY_ERR.getCode()
										.equals(exception.getErrorCode().getCode())) {
							payFailMsg = exception.getErrorCode().getDesc();
						} else if (FanbeiExceptionCode.UPS_COLLECT_ERROR.getCode()
								.equals(exception.getErrorCode().getCode())) {
							payFailMsg = FanbeiExceptionCode.BANK_CARD_PAY_ERR.getDesc();
						}
						AfOrderDo currUpdateOrder = new AfOrderDo();
						currUpdateOrder.setRid(orderInfo.getRid());
						currUpdateOrder.setPayStatus(PayStatus.NOTPAY.getCode());
						currUpdateOrder.setStatus(OrderStatus.PAYFAIL.getCode());
						currUpdateOrder.setStatusRemark(payFailMsg);
						orderDao.updateOrder(currUpdateOrder);
						logger.info("ap pay order fail,reason is useamount is too less orderId=" + orderInfo.getRid());
					}
					throw exception;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("payBrandOrder faied e = {}", e);
					throw e;
				}
			}

		});
	}

	public JSONObject borrowRateWithOrder(Long orderId, Integer nper) {
		AfOrderDo order = afOrderService.getOrderById(orderId);

		JSONObject borrowRate = null;
		if (StringUtils.equals(order.getOrderType(), OrderType.AGENTBUY.getCode()) && order.getNper() > 0) {
			AfAgentOrderDo agentOrderDo = afAgentOrderService.getAgentOrderByOrderId(orderId);
			borrowRate = JSON.parseObject(agentOrderDo.getBorrowRate());
		}
		if (borrowRate == null) {
			borrowRate = afResourceService.borrowRateWithResourceOld(nper);
		}
		return borrowRate;
	}

	/**
	 * @param name
	 *            分期名称
	 * @param type
	 *            分期类型
	 * @param userId
	 *            用户id
	 * @param amount
	 *            分期金额
	 * @param nper
	 *            分期期数
	 * @param status
	 *            状态
	 * @param orderId
	 *            订单id
	 * @param orderNo
	 *            订单编号
	 * @param borrowRate
	 *            借款利率等参数
	 * @param interestFreeJson
	 *            分期规则
	 * @return
	 */
	public AfBorrowDo buildAgentPayBorrow(String name, BorrowType type, Long userId, BigDecimal amount, int nper,
			String status, Long orderId, String orderNo, String borrowRate, String interestFreeJson, String orderType) {

		Integer freeNper = 0;
		List<InterestFreeJsonBo> interestFreeList = StringUtils.isEmpty(interestFreeJson) ? null
				: JSONObject.parseArray(interestFreeJson, InterestFreeJsonBo.class);
		if (CollectionUtils.isNotEmpty(interestFreeList)) {
			for (InterestFreeJsonBo bo : interestFreeList) {
				if (bo.getNper().equals(nper)) {
					freeNper = bo.getFreeNper();
					break;
				}
			}
		}

		// 获取借款分期配置信息
		AfResourceDo resource = null;
		if (orderType.equals(OrderType.TRADE.getCode())) {
			resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
					Constants.RES_BORROW_TRADE);
		} else {
			// 获取借款分期配置信息
			// 11.27加入用户专有利率
			AfUserDo afUserDo = afUserService.getUserById(userId);
			resource = afResourceService.getVipUserRate(afUserDo.getUserName());
			if (resource == null) {
				resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
						Constants.RES_BORROW_CONSUME);
			}
		}
		JSONArray array = JSON.parseArray(resource.getValue());
		// 删除2分期
		if (array == null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}
		// removeSecondNper(array);
		JSONArray interestFreeArray = null;
		if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
			interestFreeArray = JSON.parseArray(interestFreeJson);
		}
		List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray,
				BigDecimal.ONE.intValue(), amount, resource.getValue1(), resource.getValue2());
		BigDecimal perAmount = null;
		for (Map<String, Object> nperMap : nperList) {

			Object nperObj = nperMap.get("nper");
			int nperTemp = 0;
			if (nperObj instanceof BigDecimal) {
				nperTemp = ((BigDecimal) nperObj).intValue();
			} else {
				nperTemp = Integer.parseInt((String) nperObj);
			}
			if (nper == nperTemp) {
				String isFree = (String) nperMap.get("isFree");
				if ("1".equals(isFree)) {
					perAmount = (BigDecimal) nperMap.get("freeAmount");
				} else if ("0".equals(isFree) || "2".equals(isFree)) {
					perAmount = (BigDecimal) nperMap.get("amount");
				}
			}
		}
		Date currDate = new Date();
		AfBorrowDo borrow = new AfBorrowDo();
		borrow.setGmtCreate(currDate);
		borrow.setAmount(amount);
		borrow.setType(type.getCode());
		borrow.setBorrowNo(generatorClusterNo.getBorrowNo(currDate));
		borrow.setStatus(status);// 默认转账成功
		borrow.setName(name);
		borrow.setUserId(userId);
		borrow.setNper(nper);
		borrow.setNperAmount(perAmount);
		borrow.setCardNumber(StringUtils.EMPTY);
		borrow.setCardName("代付");
		borrow.setRemark(name);
		borrow.setOrderId(orderId);
		borrow.setOrderNo(orderNo);
		borrow.setBorrowRate(borrowRate);
		borrow.setCalculateMethod(BorrowCalculateMethod.DENG_BEN_DENG_XI.getCode());
		borrow.setFreeNper(freeNper);
		return borrow;
	}

	private void removeSecondNper(JSONArray array) {
		if (array == null) {
			return;
		}
		Iterator<Object> it = array.iterator();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			if (json.getString(Constants.DEFAULT_NPER).equals("2")) {// mark
				it.remove();
				break;
			}
		}
	}

	@Override
	public Map<String, Object> payBrandOrderOld(final Long payId, final Long orderId, final Long userId,
			final String orderNo, final String thirdOrderNo, final String goodsName, final BigDecimal saleAmount,
			final Integer nper, final String appName, final String ipAddress) {
		return transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doInTransaction(TransactionStatus status) {
				try {
					Date currentDate = new Date();
					String tradeNo = generatorClusterNo.getOrderPayNo(currentDate);
					Map<String, Object> resultMap = new HashMap<String, Object>();
					AfOrderDo orderInfo = orderDao.getOrderInfoById(orderId, userId);// new
					// AfOrderDo();
					// 查卡号，用于调用风控接口
					AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);

					orderInfo.setRid(orderId);
					orderInfo.setPayTradeNo(tradeNo);
					orderInfo.setGmtPay(currentDate);
					orderInfo.setActualAmount(saleAmount);
					orderInfo.setBankId(payId);
					if (payId < 0) {
						orderInfo.setPayType(PayType.WECHAT.getCode());
						logger.info("payBrandOrder orderInfo = {}", orderInfo);
						orderDao.updateOrder(orderInfo);
						// 微信支付
						return UpsUtil.buildWxpayTradeOrder(tradeNo, userId, goodsName, saleAmount,
								PayOrderSource.BRAND_ORDER.getCode());
					} else if (payId == 0) {
						// 代付
						orderInfo.setPayStatus(PayStatus.DEALING.getCode());
						orderInfo.setStatus(OrderStatus.DEALING.getCode());
						orderInfo.setPayType(PayType.AGENT_PAY.getCode());

						AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);

						logger.info("updateOrder orderInfo = {}", orderInfo);
						orderInfo.setNper(nper);
						orderDao.updateOrder(orderInfo);
						BigDecimal useableAmount = userAccountInfo.getAuAmount()
								.subtract(userAccountInfo.getUsedAmount()).subtract(userAccountInfo.getFreezeAmount());

						if (useableAmount.compareTo(saleAmount) < 0) {
							throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_MONEY_ERROR);
						}
						// 修改用户账户信息
						AfUserAccountDo account = new AfUserAccountDo();
						account.setUsedAmount(orderInfo.getActualAmount());
						account.setUserId(userAccountInfo.getUserId());
						afUserAccountDao.updateUserAccount(account);

						// 最后调用风控控制
						logger.info("verify userId" + userId);

						String cardNo = card.getCardNumber();
						String riskOrderNo = riskUtil.getOrderNo("vefy",
								cardNo.substring(cardNo.length() - 4, cardNo.length()));
						orderInfo.setRiskOrderNo(riskOrderNo);
						orderDao.updateOrder(orderInfo);

						try {
							riskUtil.verify(ObjectUtils.toString(userId, ""), "40", card.getCardNumber(), appName,
									ipAddress, StringUtil.EMPTY, StringUtil.EMPTY, "/third/risk/payOrder", riskOrderNo);
						} catch (Exception e) {
							// throw new FanbeiException();
							e.printStackTrace();
						}

					} else {
						orderInfo.setPayType(PayType.BANK.getCode());
						orderInfo.setPayStatus(PayStatus.DEALING.getCode());
						orderInfo.setStatus(OrderStatus.DEALING.getCode());

						AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);

						AfUserBankcardDo cardInfo = afUserBankcardService.getUserBankcardById(payId);

						resultMap = new HashMap<String, Object>();

						if (null == cardInfo) {
							throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
						}
						logger.info("payBrandOrder orderInfo = {}", orderInfo);
						orderDao.updateOrder(orderInfo);
						// 银行卡支付 代收
						UpsCollectRespBo respBo = upsUtil.collect(tradeNo, saleAmount, userId + "",
								userAccountInfo.getRealName(), cardInfo.getMobile(), cardInfo.getBankCode(),
								cardInfo.getCardNumber(), userAccountInfo.getIdNumber(), Constants.DEFAULT_BRAND_SHOP,
								"品牌订单支付", "02", OrderType.BOLUOME.getCode());
						if (!respBo.isSuccess()) {
							if (StringUtil.isNotBlank(respBo.getRespCode())) {
								// 模版数据map处理
								Map<String, String> replaceMapData = new HashMap<String, String>();
								String errorMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());
								replaceMapData.put("errorMsg", errorMsg);
								try {
									AfUserDo userDo = afUserService.getUserById(userId);
									smsUtil.sendConfigMessageToMobile(userDo.getMobile(), replaceMapData, 0,
											AfResourceType.SMS_TEMPLATE.getCode(),
											AfResourceSecType.SMS_BANK_PAY_ORDER_FAIL.getCode());
								} catch (Exception e) {
									logger.error("pay order rela bank pay error,userId=" + userId, e);
								}
								throw new FanbeiException(errorMsg);
							}
							throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
						}
						Map<String, Object> newMap = new HashMap<String, Object>();
						newMap.put("outTradeNo", respBo.getOrderNo());
						newMap.put("tradeNo", respBo.getTradeNo());
						newMap.put("cardNo", Base64.encodeString(respBo.getCardNo()));
						resultMap.put("resp", respBo);
					}
					return resultMap;
				} catch (FanbeiException exception) {
					status.setRollbackOnly();
					logger.error("payBrandOrder faied e = {}", exception);
					throw new FanbeiException("bank card pay error", exception.getErrorCode());
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("payBrandOrder faied e = {}", e);
					throw e;
				}
			}
		});
	}

	@Override
	public int dealBrandOrderSucc(final String payOrderNo, final String tradeNo, final String payType) {
		final AfOrderDo orderInfo = orderDao.getOrderInfoByPayOrderNo(payOrderNo);
		Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					// 菠萝觅，临时增加CLOSED订单：因为额度支付可能会先关闭订单
					if (orderInfo == null || (!orderInfo.getStatus().equals(OrderStatus.PAYFAIL.getCode())
							&& !orderInfo.getStatus().equals(OrderStatus.CLOSED.getCode())
							&& !orderInfo.getStatus().equals(OrderStatus.NEW.getCode())
							&& !orderInfo.getStatus().equals(OrderStatus.DEALING.getCode()))) {
						return 0;
					}
					if (StringUtil.equals(payType, PayType.COMBINATION_PAY.getCode())) {

						logger.info("dealBrandOrder cp begin , payOrderNo = {} and tradeNo = {} and type = {}",
								new Object[] { payOrderNo, tradeNo, payType });

						AfBorrowDo afBorrowDo = afBorrowDao.getBorrowByOrderId(orderInfo.getRid());

						logger.info("dealBrandOrder cp = " + afBorrowDo.getRid());

						afBorrowDao.updateBorrowStatus(afBorrowDo.getRid(), BorrowStatus.TRANSED.getCode());
						afBorrowBillDao.updateBorrowBillStatusByBorrowId(afBorrowDo.getRid(),
								BorrowBillStatus.NO.getCode());

						// #region add by hongzhengpei
						// afRecommendUserService.updateRecommendByBorrow(afBorrowDo.getUserId(),new
						// Date());
						// #endregion
					}
					logger.info("dealBrandOrder begin , payOrderNo = {} and tradeNo = {} and type = {}",
							new Object[] { payOrderNo, tradeNo, payType });
					orderInfo.setPayTradeNo(payOrderNo);
					orderInfo.setPayStatus(PayStatus.PAYED.getCode());
					orderInfo.setStatus(OrderStatus.PAID.getCode());
					orderInfo.setPayType(payType);
					orderInfo.setGmtPay(new Date());
					orderInfo.setTradeNo(tradeNo);
					orderDao.updateOrder(orderInfo);
					logger.info("dealBrandOrder comlete , orderInfo = {} ", orderInfo);

					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealBrandOrder error:", e);
					return 0;
				}
			}
		});
		if (result == 1) {

			// ----------------------------begin map:add one time for tiger
			// machine in the certain date---------------------------------
			AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("SPRING_FESTIVAL_ACTIVITY",
					"START_END_TIME");
			if (resourceDo != null) {
				Date current = new Date();
				SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strCurrent = sFormat.format(current);
				if (strCurrent.compareTo(resourceDo.getValue()) > 0
						&& strCurrent.compareTo(resourceDo.getValue1()) < 0) {
					afUserCouponTigerMachineService.addOneTime(orderInfo.getUserId(), "SHOPPING");
				}

			}
			// ----------------------------end map:add one time for tiger
			// machine---------------------------------

			boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderType(), orderInfo.getOrderNo(),
					orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, orderInfo.getUserId(), orderInfo.getActualAmount(),
					orderInfo.getSecType());
			// iPhonX预约
			AfGoodsDo goods = afGoodsService.getGoodsById(orderInfo.getGoodsId());
			logger.info("iPhone8 reservationActivity" + (goods != null ? goods.getRid() : 0L));
			if (goods != null) {
				if (goods.getTags().equals("subscribe")) {
					AfUserDo afUserDo = afUserService.getUserById(orderInfo.getUserId());
					Map<String, Object> returnData = new HashMap<String, Object>();
					Long rsvNums = 1L;
					// 预约成功后发送短信开关 Y发送 N不发送
					String sendMsgStatus = "";
					String sendMsgInfo = "";
					String jpushMsgInfo = "";
					AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(
							AfResourceType.ReservationActivity.getCode(),
							AfResourceType.Iphone8ReservationActivity.getCode());
					// 解析对应配置并校验
					Map<String, Object> jsonObjRes = (Map<String, Object>) JSONObject.parse(resource.getValue3());
					sendMsgStatus = StringUtil.null2Str(jsonObjRes.get("sendMsgStatus"));
					sendMsgInfo = StringUtil.null2Str(jsonObjRes.get("sendMsgInfo"));
					jpushMsgInfo = StringUtil.null2Str(jsonObjRes.get("jpushMsgInfo"));
					String aId = StringUtil.null2Str(jsonObjRes.get("activityId"));
					String gId = StringUtil.null2Str(jsonObjRes.get("goodsId"));
					String cId = StringUtil.null2Str(jsonObjRes.get("couponId"));
					long couponId = Long.parseLong(cId);
					long activityId = Long.parseLong(aId);
					long goodsId = Long.parseLong(gId);
					String rsvNo = OrderNoUtils.getInstance().getSerialNumber();
					AfGoodsReservationDo afGoodsReservationDo = new AfGoodsReservationDo(afUserDo.getRid(), activityId,
							goodsId, rsvNums, rsvNo, new Date(), new Date(), AfGoodsReservationStatus.SUCCESS.getCode(),
							"");

					if (!(afGoodsReservationService.addGoodsReservation(afGoodsReservationDo) > 0)) {
						logger.info("iPhone8 reservationActivity is fail");
						return result;
					}
					// 统计预约成功数量
					String str = (String) bizCacheUtil.getObject(Constants.RESERVATION_IPHONEX_RESERVATION_COUNT);
					if (str != null) {
						int couponCount = Integer.parseInt(str);
						try {
							couponCount++;
							bizCacheUtil.saveObject(Constants.RESERVATION_IPHONEX_RESERVATION_COUNT, couponCount + "",
									60 * 60 * 24 * 13);
						} catch (Exception e) {
							logger.error("统计预约成功数量失败，数量为：" + couponCount + ",", e);
						}
					} else {
						bizCacheUtil.saveObject(Constants.RESERVATION_IPHONEX_RESERVATION_COUNT, 1 + "",
								60 * 60 * 24 * 13);
					}
					// 预约成功，发送优惠券
					try {
						AfCouponDo couponDo = afCouponService.getCouponById(couponId);
						AfUserCouponDo userCoupon = new AfUserCouponDo();
						userCoupon.setCouponId(couponDo.getRid());
						userCoupon.setGmtCreate(new Date());
						userCoupon.setGmtStart(couponDo.getGmtStart());
						userCoupon.setGmtEnd(couponDo.getGmtEnd());
						userCoupon.setUserId(afUserDo.getRid());
						userCoupon.setStatus("NOUSE");
						userCoupon.setSourceType(CouponSenceRuleType.RESERVATION.getCode());
						afUserCouponService.addUserCoupon(userCoupon);
					} catch (Exception e) {
						logger.error("活动产品预约成功发送优惠券异常userId：" + afUserDo.getRid() + ",", e);
					}

					// 预约成功，短信通知
					if (StringUtil.isBlank(sendMsgStatus) || sendMsgStatus.equals(YesNoStatus.YES.getCode())) {
						try {
							boolean r = smsUtil.sendGoodsReservationSuccessMsgInfo(afUserDo.getMobile(), sendMsgInfo);
							// 推送通知
							jpushService.reservationActivity(afUserDo.getMobile(), jpushMsgInfo);
							if (r == false) {
								logger.error("活动产品预约成功消息通知发送失败userId：" + afUserDo.getRid());
							}
						} catch (Exception e) {
							logger.error("活动产品预约成功消息通知异常userId：" + afUserDo.getRid() + ",", e);
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public int dealAgentCpOrderSucc(final String payOrderNo, final String tradeNo, final String payType) {
		final AfOrderDo orderInfo = orderDao.getOrderInfoByPayOrderNo(payOrderNo);
		Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					if (orderInfo == null || (!orderInfo.getStatus().equals(OrderStatus.NEW.getCode())
							&& !orderInfo.getStatus().equals(OrderStatus.DEALING.getCode()))) {
						return 0;
					}
					AfBorrowDo afBorrowDo = afBorrowDao.getBorrowByOrderId(orderInfo.getRid());
					afBorrowDao.updateBorrowStatus(afBorrowDo.getRid(), BorrowStatus.TRANSED.getCode());
					afBorrowBillDao.updateBorrowBillStatusByBorrowId(afBorrowDo.getRid(),
							BorrowBillStatus.NO.getCode());

					logger.info("dealAgentCpOrderSucc begin , payOrderNo = {} and tradeNo = {} and type = {}",
							new Object[] { payOrderNo, tradeNo, payType });
					orderInfo.setPayTradeNo(payOrderNo);
					orderInfo.setPayStatus(PayStatus.PAYED.getCode());
					orderInfo.setStatus(OrderStatus.PAID.getCode());
					orderInfo.setPayType(payType);
					orderInfo.setGmtPay(new Date());
					orderInfo.setTradeNo(tradeNo);
					orderDao.updateOrder(orderInfo);
					logger.info("dealAgentCpOrderSucc comlete , orderInfo = {} ", orderInfo);

					// #region add by hongzhengpei
					// afRecommendUserService.updateRecommendByBorrow(afBorrowDo.getUserId(),new
					// Date());
					// #endregion

					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealAgentCpOrderSucc error:", e);
					return 0;
				}
			}
		});
		return result;
	}

	public int dealBrandOrderFail(final String payOrderNo, final String tradeNo, final String payType) {
		final AfOrderDo orderInfo = orderDao.getOrderInfoByPayOrderNo(payOrderNo);
		Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					if (orderInfo == null || (!orderInfo.getStatus().equals(OrderStatus.NEW.getCode())
							&& !orderInfo.getStatus().equals(OrderStatus.DEALING.getCode())
							&& !orderInfo.getStatus().equals(OrderStatus.PAYFAIL.getCode()))) {
						return 0;
					}
					logger.info("dealBrandOrder fail begin , payOrderNo = {} and tradeNo = {} and type = {}",
							new Object[] { payOrderNo, tradeNo, payType });
					String failMsg = "";
					if (PayType.BANK.getCode().equals(payType)) {
						failMsg = Constants.PAY_ORDER_UPS_FAIL_BANK;
					} else if (PayType.WECHAT.getCode().equals(payType)) {
						failMsg = Constants.PAY_ORDER_UPS_FAIL_WX;
					} else {
						failMsg = Constants.PAY_ORDER_UPS_FAIL;
					}
					orderInfo.setPayTradeNo(payOrderNo);
					orderInfo.setPayStatus(PayStatus.NOTPAY.getCode());
					orderInfo.setStatus(OrderStatus.PAYFAIL.getCode());
					orderInfo.setStatusRemark(failMsg);
					orderInfo.setPayType(payType);
					orderInfo.setGmtPay(new Date());
					orderInfo.setTradeNo(tradeNo);
					orderDao.updateOrder(orderInfo);
					logger.info("dealBrandOrder fail comlete , orderInfo = {} ", orderInfo);
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealBrandOrder fail error:", e);
					return 0;
				}
			}
		});
		if (result == 1 && OrderType.BOLUOME.getCode().equals(orderInfo.getOrderType())) {
			boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderType(), orderInfo.getOrderNo(),
					orderInfo.getThirdOrderNo(), PushStatus.PAY_FAIL, orderInfo.getUserId(),
					orderInfo.getActualAmount(), orderInfo.getSecType());
		}
		return result;
	}

	@Override
	public int dealBrandOrderRefund(final Long orderId, final Long userId, final Long bankId, final String orderNo,
			final String thirdOrderNo, final BigDecimal refundAmount, final BigDecimal totalAmount,
			final String payType, final String payTradeNo, final String refundNo, final String refundSource) {
		Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					logger.info(
							"dealBrandOrderRefund begin , orderId = {} userId = {} orderNo = {} refundAmount = {} totalAmount = {} payType = {} payTradeNo = {} refundNo = {} refundSource = {}",
							new Object[] { orderId, userId, orderNo, refundAmount, totalAmount, payType, payTradeNo,
									refundNo, refundSource });
					AfOrderDo orderInfo = null;
					// 金额小于0
					if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
						throw new FanbeiException("reund amount error", FanbeiExceptionCode.REFUND_AMOUNT_ERROR);
					}
					PayType type = PayType.findRoleTypeByCode(payType);
					// 已经退款金额
					BigDecimal refundedAmount = afOrderRefundDao.calculateTotalRefundAmount(orderId);
					BigDecimal totalRefundAmount = refundAmount
							.add(refundedAmount == null ? BigDecimal.ZERO : refundedAmount);
					// 总退款金额大于订单金额
					if (totalRefundAmount.compareTo(totalAmount) > 0) {
						throw new FanbeiException("reund total amount error",
								FanbeiExceptionCode.REFUND_TOTAL_AMOUNT_ERROR);
					}
					switch (type) {
					case WECHAT:
						// 微信退款
						String refundResult = UpsUtil.wxRefund(orderNo, payTradeNo, refundAmount, totalAmount);
						logger.info("wx refund  , refundResult = {} ", refundResult);
						if (!"SUCCESS".equals(refundResult)) {
							afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount,
									refundAmount, userId, orderId, orderNo, OrderRefundStatus.FAIL, PayType.WECHAT,
									StringUtils.EMPTY, null, "菠萝觅微信退款", refundSource, payTradeNo));
							throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
						} else {
							afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount,
									refundAmount, userId, orderId, orderNo, OrderRefundStatus.FINISH, PayType.WECHAT,
									StringUtils.EMPTY, null, "菠萝觅微信退款", refundSource, payTradeNo));
							boluomeUtil.pushRefundStatus(orderId, orderNo, thirdOrderNo, PushStatus.REFUND_SUC, userId,
									refundAmount, refundNo);
						}
						orderInfo = new AfOrderDo();
						orderInfo.setRid(orderId);
						orderInfo.setStatus(OrderStatus.CLOSED.getCode());
						orderDao.updateOrder(orderInfo);
						break;
					case AGENT_PAY:
						// 代付退款
						logger.info("agent pay refund begin");
						orderInfo = orderDao.getOrderById(orderId);

						AfUserAccountDo accountInfo = afUserAccountDao
								.getUserAccountInfoByUserId(orderInfo.getUserId());

						AfBorrowDo borrowInfo = afBorrowService.getBorrowByOrderIdAndStatus(orderInfo.getRid(),
								BorrowStatus.TRANSED.getCode());
						if (borrowInfo == null) {
							borrowInfo = afBorrowService.getBorrowByOrderIdAndStatus(orderInfo.getRid(),
									BorrowStatus.FINISH.getCode());
						}

						// 重新需要生成账单的金额
						BigDecimal borrowAmount = afBorrowService.calculateBorrowAmount(borrowInfo.getRid(),
								refundAmount, refundSource.equals(RefundSource.USER.getCode()));
						logger.info("dealBrandOrderRefund borrowAmount = {}", borrowAmount);

						// 更新账户金额
						BigDecimal usedAmount = calculateUsedAmount(borrowInfo);
						// 减少线上使用额度(优先还临时额度)
						updateUsedAmount(accountInfo.getUserId(), usedAmount, orderId);
						// 增加Account记录
						afUserAccountLogDao.addUserAccountLog(BuildInfoUtil.buildUserAccountLogDo(
								UserAccountLogType.AP_REFUND, borrowInfo.getAmount(), userId, borrowInfo.getRid()));
						// 修改借款状态
						afBorrowService.updateBorrowStatus(borrowInfo.getRid(), BorrowStatus.FINISH.getCode());
						// 修改账单状态
						afBorrowBillDao.updateNotRepayedBillStatus(borrowInfo.getRid(),
								BorrowBillStatus.CLOSE.getCode());
						// 修改订单状态
						orderInfo.setStatus(OrderStatus.CLOSED.getCode());
						orderDao.updateOrder(orderInfo);

						AfUserBankcardDo cardInfo = afUserBankcardDao.getUserMainBankcardByUserId(userId);
						if (borrowAmount.compareTo(BigDecimal.ZERO) < 0) {
							// 退款最后放置，因为如果其他过程抛异常就不需要退款操作
							AfOrderRefundDo refundInfo = BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount,
									borrowAmount.abs(), userId, orderId, orderNo, OrderRefundStatus.REFUNDING,
									PayType.BANK, cardInfo.getCardNumber(), cardInfo.getBankName(),
									"菠萝觅代付多余还款退款" + borrowAmount.abs(), refundSource, StringUtils.EMPTY);
							afOrderRefundDao.addOrderRefund(refundInfo);
							UpsDelegatePayRespBo tempUpsResult = upsUtil.delegatePay(borrowAmount.abs(),
									accountInfo.getRealName(), cardInfo.getCardNumber(), userId + "",
									cardInfo.getMobile(), cardInfo.getBankName(), cardInfo.getBankCode(),
									Constants.DEFAULT_REFUND_PURPOSE, "02", UserAccountLogType.BANK_REFUND.getCode(),
									refundInfo.getRid() + StringUtils.EMPTY);
							logger.info("agent bank refund upsResult = {}", tempUpsResult);
							if (!tempUpsResult.isSuccess()) {
								refundInfo.setStatus(OrderRefundStatus.FAIL.getCode());
								refundInfo.setPayTradeNo(tempUpsResult.getOrderNo());
								afOrderRefundDao.updateOrderRefund(refundInfo);
								throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
							} else {
								refundInfo.setPayTradeNo(tempUpsResult.getOrderNo());
								afOrderRefundDao.updateOrderRefund(refundInfo);
							}
						} else if (borrowAmount.compareTo(BigDecimal.ZERO) > 0) {
							afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount,
									BigDecimal.ZERO, userId, orderId, orderNo, OrderRefundStatus.FINISH,
									PayType.AGENT_PAY, StringUtils.EMPTY, null, "菠萝觅代付退款生成新账单" + borrowAmount.abs(),
									refundSource, StringUtils.EMPTY));
							// 修改用户账户信息
							afUserAccountSenceDao.updateUsedAmount(UserAccountSceneType.ONLINE.getCode(),
									accountInfo.getUserId(), borrowAmount);

							afBorrowService.dealAgentPayBorrowAndBill(accountInfo.getUserId(),
									accountInfo.getUserName(), borrowAmount, borrowInfo.getName(),
									borrowInfo.getNper() - borrowInfo.getNperRepayment(), orderId, orderNo,
									orderInfo.getBorrowRate(), orderInfo.getInterestFreeJson(), true);
						} else {
							afOrderRefundDao.addOrderRefund(
									BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, BigDecimal.ZERO, userId,
											orderId, orderNo, OrderRefundStatus.FINISH, PayType.AGENT_PAY,
											StringUtils.EMPTY, null, "菠萝觅代付退款", refundSource, StringUtils.EMPTY));
						}
						// 如果成功推送退款成功状态给菠萝觅
						boluomeUtil.pushRefundStatus(orderId, orderNo, thirdOrderNo, PushStatus.REFUND_SUC, userId,
								refundAmount, refundNo);
						break;
					case COMBINATION_PAY:
						// 组合支付退款
						logger.info("CP pay refund begin");
						orderInfo = orderDao.getOrderById(orderId);

						AfUserAccountDo afUserAccountDo = afUserAccountDao
								.getUserAccountInfoByUserId(orderInfo.getUserId());

						AfBorrowDo afBorrowDo = afBorrowService.getBorrowByOrderIdAndStatus(orderInfo.getRid(),
								BorrowStatus.TRANSED.getCode());
						if (afBorrowDo == null) {
							afBorrowDo = afBorrowService.getBorrowByOrderIdAndStatus(orderInfo.getRid(),
									BorrowStatus.FINISH.getCode());
						}
						BigDecimal newBorrowAmount = BigDecimal.ZERO;
						BigDecimal backAmount = BigDecimal.ZERO;
						// 重新需要生成账单的金额
						if (refundAmount.compareTo(orderInfo.getBorrowAmount()) > 0) {
							newBorrowAmount = afBorrowService.calculateBorrowAmount(afBorrowDo.getRid(),
									orderInfo.getBorrowAmount(), refundSource.equals(RefundSource.USER.getCode()));
							backAmount = refundAmount.subtract(orderInfo.getBorrowAmount()).subtract(newBorrowAmount);
						} else {
							newBorrowAmount = afBorrowService.calculateBorrowAmount(afBorrowDo.getRid(), refundAmount,
									refundSource.equals(RefundSource.USER.getCode()));
							backAmount = newBorrowAmount.negate();
						}
						logger.info("dealBrandOrderCPRefund newBorrowAmount = {} backAmount = {}",
								new Object[] { newBorrowAmount, backAmount });

						// 更新账户金额
						BigDecimal thisTimeUsedAmount = calculateUsedAmount(afBorrowDo);
						// 减少线上使用额度(优先还临时额度)
						updateUsedAmount(afUserAccountDo.getUserId(), thisTimeUsedAmount, orderId);
						// 增加Account记录
						afUserAccountLogDao.addUserAccountLog(BuildInfoUtil.buildUserAccountLogDo(
								UserAccountLogType.CP_REFUND, afBorrowDo.getAmount(), userId, afBorrowDo.getRid()));
						// 修改借款状态
						afBorrowService.updateBorrowStatus(afBorrowDo.getRid(), BorrowStatus.FINISH.getCode());
						// 修改账单状态
						afBorrowBillDao.updateNotRepayedBillStatus(afBorrowDo.getRid(),
								BorrowBillStatus.CLOSE.getCode());
						// 修改订单状态
						orderInfo.setStatus(OrderStatus.CLOSED.getCode());
						orderDao.updateOrder(orderInfo);

						AfUserBankcardDo afUserBankcardDo = afUserBankcardDao.getUserMainBankcardByUserId(userId);
						if (backAmount.compareTo(BigDecimal.ZERO) > 0) {
							AfOrderRefundDo refundInfo = BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount,
									backAmount.abs(), userId, orderId, orderNo, OrderRefundStatus.FINISH,
									PayType.COMBINATION_PAY, afUserBankcardDo.getCardNumber(),
									afUserBankcardDo.getBankName(), "组合支付多余还款退款" + backAmount.abs(), refundSource,
									StringUtils.EMPTY);
							afOrderRefundDao.addOrderRefund(refundInfo);

							AfUserAccountDo account = new AfUserAccountDo();
							account.setRebateAmount(backAmount.abs());
							account.setUserId(afUserAccountDo.getUserId());
							afUserAccountDao.updateUserAccount(account);
							afUserAccountLogDao.addUserAccountLog(BuildInfoUtil.buildUserAccountLogDo(
									UserAccountLogType.CP_REFUND, backAmount.abs(), userId, orderInfo.getRid()));
						} else if (backAmount.compareTo(BigDecimal.ZERO) < 0) {
							afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount,
									BigDecimal.ZERO, userId, orderId, orderNo, OrderRefundStatus.FINISH,
									PayType.COMBINATION_PAY, StringUtils.EMPTY, null, "组合支付退款生成新账单" + backAmount.abs(),
									refundSource, StringUtils.EMPTY));
							// 修改用户账户信息
							afUserAccountSenceDao.updateUsedAmount(UserAccountSceneType.ONLINE.getCode(),
									afUserAccountDo.getUserId(), backAmount.abs());

							afBorrowService.dealAgentPayBorrowAndBill(afUserAccountDo.getUserId(),
									afUserAccountDo.getUserName(), backAmount.abs(), afBorrowDo.getName(),
									afBorrowDo.getNper() - afBorrowDo.getNperRepayment(), orderId, orderNo,
									orderInfo.getBorrowRate(), orderInfo.getInterestFreeJson(), true);
						} else {
							afOrderRefundDao.addOrderRefund(
									BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, BigDecimal.ZERO, userId,
											orderId, orderNo, OrderRefundStatus.FINISH, PayType.COMBINATION_PAY,
											StringUtils.EMPTY, null, "组合支付退款", refundSource, StringUtils.EMPTY));
						}
						// 如果成功推送退款成功状态给菠萝觅
						boluomeUtil.pushRefundStatus(orderId, orderNo, thirdOrderNo, PushStatus.REFUND_SUC, userId,
								refundAmount, refundNo);
						break;
					case BANK:
						// 银行卡退款
						AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
						AfUserBankcardDo card = afUserBankcardDao.getUserBankInfoRefund(bankId);

						AfOrderRefundDo refundInfo = BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount,
								refundAmount, userId, orderId, orderNo, OrderRefundStatus.REFUNDING, PayType.BANK,
								card.getCardNumber(), card.getBankName(), "菠萝觅银行卡退款", refundSource, StringUtils.EMPTY);
						afOrderRefundDao.addOrderRefund(refundInfo);
						orderInfo = new AfOrderDo();
						orderInfo.setRid(orderId);
						orderInfo.setStatus(OrderStatus.DEAL_REFUNDING.getCode());
						orderDao.updateOrder(orderInfo);
						UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(refundAmount, userAccount.getRealName(),
								card.getCardNumber(), userId + "", card.getMobile(), card.getBankName(),
								card.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02",
								UserAccountLogType.BANK_REFUND.getCode(), refundInfo.getRid() + StringUtils.EMPTY);
						logger.info("bank refund upsResult = {}", upsResult);
						if (!upsResult.isSuccess()) {
							refundInfo.setStatus(OrderRefundStatus.FAIL.getCode());
							refundInfo.setPayTradeNo(upsResult.getOrderNo());
							afOrderRefundDao.updateOrderRefund(refundInfo);
							throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
						} else {
							refundInfo.setPayTradeNo(upsResult.getOrderNo());
							afOrderRefundDao.updateOrderRefund(refundInfo);
						}
						break;
					default:
						break;
					}
					logger.info("dealBrandOrderRefund comlete");
					try {
						afUserAmountService.refundOrder(orderId);
					} catch (Exception e) {
						logger.error("add refund detail error", e);
					}
					return 1;
				} catch (FanbeiException e) {
					status.setRollbackOnly();
					logger.error("dealBrandOrderRefund error = {}", e);
					return 0;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealBrandOrderRefund error = {}", e);
					return 0;
				}
			}
		});
		// 退款失败推送状态给菠萝觅
		if (result == 0) {
			boluomeUtil.pushRefundStatus(orderId, orderNo, thirdOrderNo, PushStatus.REFUND_FAIL, userId, refundAmount,
					refundNo);
		}
		return result;
	}

	private void updateUsedAmount(Long userId, BigDecimal onlineAmount, Long orderId) {
		// 获取临时额度
		AfInterimAuDo afInterimAuDo = afInterimAuDao.getByUserId(userId);
		if (afInterimAuDo == null) {
			afInterimAuDo = new AfInterimAuDo();
			afInterimAuDo.setInterimAmount(new BigDecimal(0));
			afInterimAuDo.setInterimUsed(new BigDecimal(0));
		}
		// 判断临时额度是否使用
		if (afInterimAuDo.getInterimUsed().compareTo(BigDecimal.ZERO) == 1) {
			// 还款金额是否大于使用的临时额度
			BigDecimal backInterim = BigDecimal.ZERO;
			if (afInterimAuDo.getInterimUsed().compareTo(onlineAmount) >= 0) {
				// 还临时额度
				backInterim = onlineAmount;
				afInterimAuDao.updateInterimUsed(userId, backInterim.multiply(new BigDecimal(-1)));
			} else {
				// 先还临时额度再还使用额度
				backInterim = afInterimAuDo.getInterimUsed();
				afInterimAuDao.updateInterimUsed(userId, backInterim.multiply(new BigDecimal(-1)));
				// 减少线上使用额度
				afUserAccountSenceDao.updateUsedAmount(UserAccountSceneType.ONLINE.getCode(), userId,
						onlineAmount.subtract(backInterim).multiply(new BigDecimal(-1)));
			}
			// 增加临时额度使用记录
			AfInterimDetailDo afInterimDetailDo = new AfInterimDetailDo();
			afInterimDetailDo.setAmount(backInterim);
			afInterimDetailDo.setInterimUsed(afInterimAuDo.getInterimUsed().subtract(backInterim));
			afInterimDetailDo.setType(3);
			afInterimDetailDo.setOrderId(orderId);
			afInterimDetailDo.setUserId(userId);
			afInterimDetailDao.addAfInterimDetail(afInterimDetailDo);
		} else {
			// 减少线上使用额度
			afUserAccountSenceDao.updateUsedAmount(UserAccountSceneType.ONLINE.getCode(), userId,
					onlineAmount.multiply(new BigDecimal(-1)));
		}
	}

	/**
	 * 获取应该退多少额度
	 *
	 * @param borrowInfo
	 * @return
	 */
	private BigDecimal calculateUsedAmount(AfBorrowDo borrowInfo) {
		List<AfBorrowBillDo> repaymentedBillList = afBorrowBillDao.getBillListByBorrowIdAndStatus(borrowInfo.getRid(),
				BorrowBillStatus.YES.getCode());
		if (CollectionUtils.isEmpty(repaymentedBillList)) {
			return borrowInfo.getAmount();
		} else {
			if (borrowInfo.getNper().equals(borrowInfo.getNperRepayment())) {
				// 如果已经还清
				return borrowInfo.getAmount();
			} else {
				// 算还了几期金额
				BigDecimal totalAmount = BigDecimal.ZERO;
				for (AfBorrowBillDo billInfo : repaymentedBillList) {
					totalAmount = BigDecimalUtil.add(totalAmount, billInfo.getPrincipleAmount());
				}
				return borrowInfo.getAmount().subtract(totalAmount);
			}
		}
	}

	@Override
	public String getCurrentLastPayNo(Date current) {
		Date startDate = DateUtil.getStartOfDate(current);
		Date endDate = DateUtil.getEndOfDate(current);
		return orderDao.getCurrentLastPayNo(startDate, endDate);
	}

	@Override
	public int syncOrderNoWithAgencyUser(Long userId, String orderNo, Long orderId) {
		AfOrderTempDo order = new AfOrderTempDo();
		order.setOrderNo(orderNo);
		order.setUserId(userId);
		order.setOrderId(orderId);

		AfAgentOrderDo afAgentOrderDo = new AfAgentOrderDo();
		afAgentOrderDo.setOrderId(orderId);
		afAgentOrderDo.setStatus("BUY");
		afAgentOrderDo.setGmtAgentBuy(new Date());
		afAgentOrderService.updateAgentOrder(afAgentOrderDo);

		return afUserOrderDao.addUserOrder(order);
	}

	@Override
	public String isCanApplyAfterSale(Long orderId) {
		String isCanApply = YesNoStatus.NO.getCode();
		try {
			AfOrderDo order = orderDao.getOrderById(orderId);
			List<String> agentbuyStatus = new ArrayList<String>();
			agentbuyStatus.add(OrderStatus.AGENCYCOMPLETED.getCode());
			agentbuyStatus.add(OrderStatus.DELIVERED.getCode());
			agentbuyStatus.add(OrderStatus.FINISHED.getCode());
			agentbuyStatus.add(OrderStatus.REBATED.getCode());
			List<String> otherbuyStatus = new ArrayList<String>();
			otherbuyStatus.add(OrderStatus.PAID.getCode());
			otherbuyStatus.add(OrderStatus.DELIVERED.getCode());
			otherbuyStatus.add(OrderStatus.FINISHED.getCode());
			otherbuyStatus.add(OrderStatus.REBATED.getCode());
			// 代买和自营及状态匹配才满足申请售后条件
			if (!((OrderType.AGENTBUY.getCode().equals(order.getOrderType())
					&& agentbuyStatus.contains(order.getStatus()))
					|| (OrderType.SELFSUPPORT.getCode().equals(order.getOrderType())
							&& otherbuyStatus.contains(order.getStatus())))) {
				return isCanApply;
			}

			// 符合订单类型及状态，继续时间校验，自订单状态为待收货开始30个自然日内
			Date waitDelieveDate = null;
			if (order.getGmtDeliver() != null) {
				// 发货时间不为空，以发货时间为准，因为此字段app3.7.0版本后添加
				waitDelieveDate = order.getGmtDeliver();
			} else {
				// 代表app3.7.1版本前数据，做兼容
				if (OrderType.SELFSUPPORT.getCode().equals(order.getOrderType())) {
					waitDelieveDate = order.getGmtPay();
				} else {
					AfAgentOrderDo agentOrderDo = afAgentOrderService.getAgentOrderByOrderId(orderId);
					AfOrderTempDo orderTemp = afOrderTempDao.getByOrderId(orderId);
					waitDelieveDate = agentOrderDo.getGmtAgentBuy();
					// 涉及老的数据当初有问题，所有先取af_order_temp 如果没有，再取订单支付时间
					if (waitDelieveDate == null && orderTemp != null) {
						waitDelieveDate = orderTemp.getGmtCreate();
					}
					if (waitDelieveDate == null) {
						waitDelieveDate = order.getGmtCreate();
					}
				}
			}

			Date deadlineTime = DateUtil.addDays(waitDelieveDate, Constants.AFTER_SALE_DAYS);
			if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(deadlineTime),
					DateUtil.getToday()) < 0) {
				isCanApply = YesNoStatus.YES.getCode();
			}
		} catch (Exception e) {
			logger.error("isCanApplyAfterSale request error,orderId:" + orderId, e);
		}

		return isCanApply;
	}

	@Override
	public AfOrderDo getThirdOrderInfoBythirdOrderNo(String thirdOrderNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AfOrderDo getOrderInfoByIdWithoutDeleted(Long rid, Long userId) {
		return orderDao.getOrderInfoByIdWithoutDeleted(rid, userId);
	}

	@Override
	public Map<String, Object> getVirtualCodeAndAmount(AfOrderDo orderInfo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// String virtualCode = StringUtils.EMPTY;
		if (orderInfo == null) {
			return resultMap;
		}
		RiskVirtualProductQuotaRespBo response;
		// 菠萝觅传code，其他的传goodsName
		if (OrderType.BOLUOME.getCode().equals(orderInfo.getOrderType())) {

			AfShopDo shopDo = afShopDao.getShopInfoBySecType(orderInfo.getSecType());
			response = riskUtil.virtualProductQuota(orderInfo.getUserId() + StringUtils.EMPTY, orderInfo.getOrderType(),
					orderInfo.getSecType(), shopDo.getName());
			if (response != null) {
				resultMap.put(Constants.VIRTUAL_CODE, orderInfo.getSecType());
				resultMap.put(Constants.VIRTUAL_CHECK_NAME, shopDo.getName());
			}
		} else if (OrderType.TRADE.getCode().equals(orderInfo.getOrderType())) {
			return resultMap;
		} else {
			AfGoodsDo afGoodsDo = afGoodsDao.getGoodsById(orderInfo.getGoodsId());
			AfGoodsCategoryDo afGoodsCategoryDo = afGoodsCategoryDao
					.getGoodsCategoryById(afGoodsDo.getPrimaryCategoryId());
			response = riskUtil.virtualProductQuota(orderInfo.getUserId() + StringUtils.EMPTY, orderInfo.getOrderType(),
					afGoodsCategoryDo.getName(), afGoodsCategoryDo.getId() + StringUtils.EMPTY);
			if (response != null) {
				resultMap.put(Constants.VIRTUAL_CODE, afGoodsCategoryDo.getId());
				resultMap.put(Constants.VIRTUAL_CHECK_NAME, afGoodsCategoryDo.getName());
			}
		}
		if (response != null
				&& (response.getData().getAmount() != null || response.getData().getTotalAmount() != null)) {
			resultMap.put(Constants.VIRTUAL_AMOUNT, response.getData().getAmount());
			resultMap.put(Constants.VIRTUAL_RECENT_DAY, response.getData().getRecentDay());
			resultMap.put(Constants.VIRTUAL_TOTAL_AMOUNT, response.getData().getTotalAmount());
			resultMap.put(Constants.VIRTUAL_CHECK, "TRUE");
		}
		return resultMap;
	}

	@Override
	public boolean isVirtualGoods(Map<String, Object> resultMap) {
		if (resultMap == null) {
			return false;
		}
		if (resultMap.get(Constants.VIRTUAL_CODE) == null) {
			return false;
		}
		if (resultMap.get(Constants.VIRTUAL_AMOUNT) == null) {
			return false;
		}
		return true;
	}

	@Override
	public BigDecimal getVirtualAmount(Map<String, Object> resultMap) {
		if (resultMap == null) {
			return null;
		}
		if (resultMap.get(Constants.VIRTUAL_CODE) == null) {
			return null;
		}
		if (resultMap.get(Constants.VIRTUAL_AMOUNT) == null) {
			return null;
		}
		return new BigDecimal(resultMap.get(Constants.VIRTUAL_AMOUNT).toString());
	}

	@Override
	public String getVirtualCode(Map<String, Object> resultMap) {
		if (resultMap == null) {
			return null;
		}
		if (resultMap.get(Constants.VIRTUAL_CODE) == null) {
			return null;
		}
		return resultMap.get(Constants.VIRTUAL_CODE).toString();
	}

	// /**
	// * 判断额度是否足够
	// *
	// * @param orderInfo
	// * @param userAccountInfo
	// */
	// private void checkUsedAmount(Map<String, Object> resultMap, AfOrderDo
	// orderInfo, AfUserAccountDo userAccountInfo) {
	// if (afOrderService.isVirtualGoods(resultMap)) {
	// BigDecimal virtualTotalAmount =
	// afOrderService.getVirtualAmount(resultMap);
	// String virtualCode = afOrderService.getVirtualCode(resultMap);
	// BigDecimal leftAmount =
	// afUserVirtualAccountService.getCurrentMonthLeftAmount(orderInfo.getUserId(),
	// virtualCode, virtualTotalAmount);
	// BigDecimal useableAmount =
	// userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount())
	// .subtract(userAccountInfo.getFreezeAmount());
	// // 虚拟剩余额度大于信用可用额度 则为可用额度
	// leftAmount = leftAmount.compareTo(useableAmount) > 0 ? useableAmount :
	// leftAmount;
	// if (leftAmount.compareTo(orderInfo.getActualAmount()) < 0) {
	// throw new
	// FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_MONEY_ERROR);
	// }
	// } else {
	// BigDecimal useableAmount =
	// userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount())
	// .subtract(userAccountInfo.getFreezeAmount());
	// if (useableAmount.compareTo(orderInfo.getActualAmount()) < 0) {
	// throw new
	// FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_MONEY_ERROR);
	// }
	// }
	// }

	/**
	 * 判断额度是否足够
	 *
	 * @param orderInfo
	 * @param userAccountInfo
	 */
	@Override
	public BigDecimal checkUsedAmount(Map<String, Object> resultMap, AfOrderDo orderInfo,
			AfUserAccountSenceDo userAccountInfo) {
		// 获取临时额度
		AfInterimAuDo afInterimAuDo = getInterimAuDo(orderInfo);
		if (afInterimAuDo == null) {
			afInterimAuDo = new AfInterimAuDo();
			afInterimAuDo.setGmtFailuretime(DateUtil.getStartDate());
		}

		if ("TRUE".equals(resultMap.get(Constants.VIRTUAL_CHECK))) {
			BigDecimal leftAmount = BigDecimal.ZERO;
			if (resultMap.get(Constants.VIRTUAL_TOTAL_AMOUNT) != null) {
				String virtualCode = resultMap.get(Constants.VIRTUAL_CODE).toString();
				BigDecimal virtualTotalAmount = new BigDecimal(
						resultMap.get(Constants.VIRTUAL_TOTAL_AMOUNT).toString());
				Integer virtualRecentDay = new Integer(resultMap.get(Constants.VIRTUAL_RECENT_DAY).toString());
				// 验证累计额度
				leftAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(orderInfo.getUserId(), virtualCode,
						virtualTotalAmount, virtualRecentDay);

				// 判断单笔限额
				if (leftAmount.compareTo(BigDecimal.ZERO) > 0 && resultMap.get(Constants.VIRTUAL_AMOUNT) != null) {
					BigDecimal virtualAmount = new BigDecimal(resultMap.get(Constants.VIRTUAL_AMOUNT).toString());
					leftAmount = leftAmount.compareTo(virtualAmount) > 0 ? virtualAmount : leftAmount;
				}
			} else if (resultMap.get(Constants.VIRTUAL_AMOUNT) != null) {
				BigDecimal virtualAmount = new BigDecimal(resultMap.get(Constants.VIRTUAL_AMOUNT).toString());
				// 当前可用额度为虚拟限额额度（后面逻辑再与用户账户可用额度判断）
				leftAmount = virtualAmount;
			}

			BigDecimal useableAmount;
			// 判断临时额度是否到期

			if (afInterimAuDo.getGmtFailuretime().compareTo(DateUtil.getToday()) >= 0
					&& !orderInfo.getOrderType().equals("TRADE")) {
				// 获取当前用户可用临时额度
				BigDecimal interim = afInterimAuDo.getInterimAmount().subtract(afInterimAuDo.getInterimUsed());
				// 用户额度加上临时额度
				useableAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount())
						.subtract(userAccountInfo.getFreezeAmount()).add(interim);
			} else {
				useableAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount())
						.subtract(userAccountInfo.getFreezeAmount());
			}
			// 虚拟剩余额度大于信用可用额度 则为可用额度
			leftAmount = leftAmount.compareTo(useableAmount) > 0 ? useableAmount : leftAmount;

			return leftAmount;
		} else {
			BigDecimal useableAmount = getUseableAmount(orderInfo, userAccountInfo, afInterimAuDo);

			return useableAmount;
		}
	}

	@Override
	public AfInterimAuDo getInterimAuDo(AfOrderDo orderInfo) {
		// mqp add switch for different scene without TRADE
		String isSwitch = "";
		if (!orderInfo.getOrderType().equals("TRADE")) {

			String orderType = orderInfo.getOrderType();
			String secOrderType = orderInfo.getSecType();

			if (orderInfo.equals("SELFSUPPORT")) {
				secOrderType = "SELFSUPPORT";
			}

			AfCheckoutCounterDo checkoutCounterDo = afCheckoutCounterService.getByType(orderType, secOrderType);
			if (checkoutCounterDo == null) {
				logger.info("checkUsedAmount checkoutcounterdo is null");
				throw new FanbeiException(FanbeiExceptionCode.TEMPORARY_AMOUNT_SWITH_EMPTY);
			}

			isSwitch = checkoutCounterDo.getTemporaryAmountStatus();

			if (StringUtil.isEmpty(isSwitch)) {
				logger.info("checkUsedAmount isSwitch is null");
				throw new FanbeiException(FanbeiExceptionCode.TEMPORARY_AMOUNT_SWITH_EMPTY);
			}
			if (isSwitch.equals("Y")) {
				return afInterimAuDao.getByUserId(orderInfo.getUserId());
			}

		}

		// end mqp add switch for different scene
		return null;
	}

	/**
	 * 获取可使用额度+临时额度
	 *
	 * @param orderInfo
	 * @param userDo
	 * @param afInterimAuDo
	 * @return
	 */
	private BigDecimal getUseableAmount(AfOrderDo orderInfo, AfUserAccountSenceDo userDo, AfInterimAuDo afInterimAuDo) {
		BigDecimal useableAmount = BigDecimal.ZERO;
		// 判断商圈订单
		if (orderInfo.getOrderType().equals(OrderType.TRADE.getCode())) {
			// 教育培训订单
			if (orderInfo.getSecType().equals(UserAccountSceneType.TRAIN.getCode())) {
				AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService
						.getByUserIdAndType(UserAccountSceneType.TRAIN.getCode(), orderInfo.getUserId());
				if (afUserAccountSenceDo != null) {
					useableAmount = afUserAccountSenceDo.getAuAmount().subtract(afUserAccountSenceDo.getUsedAmount())
							.subtract(afUserAccountSenceDo.getFreezeAmount());
				}
			}
		} else { // 线上分期订单
			AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService
					.getByUserIdAndType(UserAccountSceneType.ONLINE.getCode(), orderInfo.getUserId());
			if (afUserAccountSenceDo != null) {
				// 额度判断
				if (afInterimAuDo.getGmtFailuretime().compareTo(DateUtil.getToday()) >= 0) {
					// 获取当前用户可用临时额度
					BigDecimal interim = afInterimAuDo.getInterimAmount().subtract(afInterimAuDo.getInterimUsed());
					useableAmount = afUserAccountSenceDo.getAuAmount().subtract(afUserAccountSenceDo.getUsedAmount())
							.subtract(afUserAccountSenceDo.getFreezeAmount()).add(interim);
				} else {
					useableAmount = afUserAccountSenceDo.getAuAmount().subtract(afUserAccountSenceDo.getUsedAmount())
							.subtract(afUserAccountSenceDo.getFreezeAmount());
				}
			}
		}
		return useableAmount;
	}

	/**
	 * 处理组合支付失败的情况恢复额度
	 * 
	 * @param orderInfo
	 */
	private void updateUsedAmount(AfOrderDo orderInfo) {
		// 判断商圈订单
		if (orderInfo.getOrderType().equals(OrderType.TRADE.getCode())) {
			// 教育培训订单
			if (orderInfo.getSecType().equals(UserAccountSceneType.TRAIN.getCode())) {
				// 减少培训使用额度
				afUserAccountSenceDao.updateUsedAmount(UserAccountSceneType.TRAIN.getCode(), orderInfo.getUserId(),
						orderInfo.getBorrowAmount().negate());
			}
		} else {
			// 获取临时额度
			AfInterimAuDo afInterimAuDo = afInterimAuDao.getByUserId(orderInfo.getUserId());
			if (afInterimAuDo == null) {
				afInterimAuDo = new AfInterimAuDo();
				afInterimAuDo.setInterimAmount(new BigDecimal(0));
				afInterimAuDo.setInterimUsed(new BigDecimal(0));
			}
			// 判断临时额度是否使用
			if (afInterimAuDo.getInterimUsed().compareTo(BigDecimal.ZERO) == 1) {
				// 还款金额是否大于使用的临时额度
				BigDecimal backInterim = BigDecimal.ZERO;
				if (afInterimAuDo.getInterimUsed().compareTo(orderInfo.getBorrowAmount()) >= 0) {
					// 还临时额度
					backInterim = orderInfo.getBorrowAmount();
					afInterimAuDao.updateInterimUsed(orderInfo.getUserId(), backInterim.multiply(new BigDecimal(-1)));
				} else {
					// 先还临时额度再还使用额度
					backInterim = afInterimAuDo.getInterimUsed();
					afInterimAuDao.updateInterimUsed(orderInfo.getUserId(), backInterim.multiply(new BigDecimal(-1)));
					// 减少线上使用额度
					afUserAccountSenceDao.updateUsedAmount(UserAccountSceneType.ONLINE.getCode(), orderInfo.getUserId(),
							orderInfo.getBorrowAmount().subtract(backInterim).multiply(new BigDecimal(-1)));
				}
				// 增加临时额度使用记录
				AfInterimDetailDo afInterimDetailDo = new AfInterimDetailDo();
				afInterimDetailDo.setAmount(backInterim);
				afInterimDetailDo.setInterimUsed(afInterimAuDo.getInterimUsed().subtract(backInterim));
				afInterimDetailDo.setType(3);
				afInterimDetailDo.setOrderId(orderInfo.getRid());
				afInterimDetailDo.setUserId(orderInfo.getUserId());
				afInterimDetailDao.addAfInterimDetail(afInterimDetailDo);
			} else {
				// 减少线上使用额度
				afUserAccountSenceDao.updateUsedAmount(UserAccountSceneType.ONLINE.getCode(), orderInfo.getUserId(),
						orderInfo.getBorrowAmount().negate());
			}
		}
	}

	/**
	 * 处理组合支付失败的情况
	 */
	@Override
	public int dealPayCpOrderFail(final String payOrderNo, final String tradeNo, final String payType) {
		final AfOrderDo orderInfo = orderDao.getOrderInfoByPayOrderNo(payOrderNo);
		Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {

					// 不处理新建，处理
					if (orderInfo == null) {
						return 0;
					}
					// 只处理订单处理中的状态
					if (!orderInfo.getStatus().equals(OrderStatus.DEALING.getCode())) {
						return 0;
					}
					logger.info("dealPayCpOrderFail fail begin , payOrderNo = {} and tradeNo = {}",
							new Object[] { payOrderNo, tradeNo });

					// 恢复额度

					updateUsedAmount(orderInfo);
					// 增加资金变化的记录
					afUserAccountLogDao
							.addUserAccountLog(BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.CP_PAY_FAIL,
									orderInfo.getBorrowAmount(), orderInfo.getUserId(), orderInfo.getRid()));

					// 恢复虚拟额度
					AfUserVirtualAccountDo queryVirtualAccountDo = new AfUserVirtualAccountDo();
					queryVirtualAccountDo.setUserId(orderInfo.getUserId());
					queryVirtualAccountDo.setOrderId(orderInfo.getRid());
					AfUserVirtualAccountDo userVirtualAccountDo = afUserVirtualAccountService
							.getByCommonCondition(queryVirtualAccountDo);
					if (userVirtualAccountDo != null) {
						userVirtualAccountDo.setStatus(YesNoStatus.NO.getCode());
						afUserVirtualAccountService.updateById(userVirtualAccountDo);
					}

					// 如果使用了优惠卷，恢复优惠卷
					if (orderInfo.getUserCouponId() != null && orderInfo.getUserCouponId() != 0) {
						afUserCouponDao.updateUserCouponSatusNouseById(orderInfo.getUserCouponId());
					}

					// 处理订单
					orderInfo.setPayTradeNo(payOrderNo);
					orderInfo.setPayStatus(PayStatus.NOTPAY.getCode());
					orderInfo.setStatus(OrderStatus.PAYFAIL.getCode());
					orderInfo.setStatusRemark(Constants.PAY_ORDER_UPS_FAIL_BANK);
					orderInfo.setPayType(payType);
					orderInfo.setGmtPay(new Date());
					orderInfo.setTradeNo(tradeNo);
					orderDao.updateOrder(orderInfo);
					logger.info("dealPayCpOrderFail fail complete , orderInfo = {} ", orderInfo);
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealPayCpOrderFail fail error:", e);
					return 0;
				}
			}
		});
		if (result == 1 && OrderType.BOLUOME.getCode().equals(orderInfo.getOrderType())) {
			boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderType(), orderInfo.getOrderNo(),
					orderInfo.getThirdOrderNo(), PushStatus.PAY_FAIL, orderInfo.getUserId(),
					orderInfo.getActualAmount(), orderInfo.getSecType());
		}
		return result;
	}

	/**
	 * 处理菠萝觅组合支付失败的情况
	 */
	@Override
	public int dealBrandPayCpOrderFail(final String payOrderNo, final String tradeNo, final String payType) {
		final AfOrderDo orderInfo = orderDao.getOrderInfoByPayOrderNo(payOrderNo);
		Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {

					AfOrderDo orderInfo = orderDao.getOrderInfoByPayOrderNo(payOrderNo);
					// 不处理新建，处理
					if (orderInfo == null) {
						return 0;
					}
					// 只处理订单处理中的状态
					if (!orderInfo.getStatus().equals(OrderStatus.DEALING.getCode())) {
						return 0;
					}
					logger.info("dealPayCpOrderFail fail begin , payOrderNo = {} and tradeNo = {}",
							new Object[] { payOrderNo, tradeNo });

					// 恢复额度
					updateUsedAmount(orderInfo);
					// 增加资金变化的记录
					afUserAccountLogDao
							.addUserAccountLog(BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.CP_PAY_FAIL,
									orderInfo.getBorrowAmount(), orderInfo.getUserId(), orderInfo.getRid()));

					AfBorrowDo borrowInfo = afBorrowService.getBorrowByOrderIdAndStatus(orderInfo.getRid(),
							BorrowStatus.TRANSED.getCode());
					if (borrowInfo != null) {
						// 修改借款状态
						afBorrowService.updateBorrowStatus(borrowInfo.getRid(), BorrowStatus.FINISH.getCode());
						// 修改账单状态
						afBorrowBillDao.updateNotRepayedBillStatus(borrowInfo.getRid(),
								BorrowBillStatus.CLOSE.getCode());
					}
					// 恢复虚拟额度
					AfUserVirtualAccountDo queryVirtualAccountDo = new AfUserVirtualAccountDo();
					queryVirtualAccountDo.setUserId(orderInfo.getUserId());
					queryVirtualAccountDo.setOrderId(orderInfo.getRid());
					AfUserVirtualAccountDo userVirtualAccountDo = afUserVirtualAccountService
							.getByCommonCondition(queryVirtualAccountDo);
					if (userVirtualAccountDo != null) {
						userVirtualAccountDo.setStatus(YesNoStatus.NO.getCode());
						afUserVirtualAccountService.updateById(userVirtualAccountDo);
					}

					// 如果使用了优惠卷，恢复优惠卷
					if (orderInfo.getUserCouponId() != null && orderInfo.getUserCouponId() != 0) {
						afUserCouponDao.updateUserCouponSatusNouseById(orderInfo.getUserCouponId());
					}

					// 处理订单
					orderInfo.setPayTradeNo(payOrderNo);
					orderInfo.setPayStatus(PayStatus.NOTPAY.getCode());
					orderInfo.setStatus(OrderStatus.PAYFAIL.getCode());
					orderInfo.setStatusRemark(Constants.PAY_ORDER_UPS_FAIL_BANK);
					orderInfo.setPayType(payType);
					orderInfo.setGmtPay(new Date());
					orderInfo.setTradeNo(tradeNo);
					orderDao.updateOrder(orderInfo);
					logger.info("dealPayCpOrderFail fail complete , orderInfo = {} ", orderInfo);
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealPayCpOrderFail fail error:", e);
					return 0;
				}
			}
		});
		if (result == 1 && OrderType.BOLUOME.getCode().equals(orderInfo.getOrderType())) {
			boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderType(), orderInfo.getOrderNo(),
					orderInfo.getThirdOrderNo(), PushStatus.PAY_FAIL, orderInfo.getUserId(),
					orderInfo.getActualAmount(), orderInfo.getSecType());
		}

		return result;
	}

	@Override
	public Integer getDealAmount(Long userId, String orderType) {

		return orderDao.getDealAmount(userId, orderType);
	}

	@Override
	public List<AfOrderDo> getStatusByGoodsAndUserId(long goodsId, long userId) {
		return orderDao.getStatusByGoodsAndUserId(goodsId, userId);
	}

	@Override
	public void syncOrderInfo(String orderId, String plantform, AfOrderDo orderInfo) {
		// 订单补偿
		String lockKey = Constants.CACHEKEY_BUILD_BOLUOME_ORDER_LOCK + orderId;
		Object lockValue = bizCacheUtil.getObject(lockKey);
		if (lockValue == null) {
			bizCacheUtil.saveObject(lockKey, "", 600);
			// 验证订单是否已经存在
			AfOrderDo existOrderDo = afOrderService.getThirdOrderInfoByOrderTypeAndOrderNo(plantform, orderId);
			if (existOrderDo == null)
				afOrderService.createOrder(orderInfo);
		}

	}

	@Override
	public int getOrderCountByStatusAndUserId(AfOrderDo queryCount) {
		// TODO Auto-generated method stub
		return orderDao.getOrderCountByStatusAndUserId(queryCount);
	}

	@Override
	public int getOldUserOrderAmount(long userId) {
		// TODO Auto-generated method stub
		return orderDao.getOldUserOrderAmount(userId);
	}

	@Override
	public List<AfOrderDo> getOverOrderByGoodsIdAndUserId(Long goodsId, Long userId) {
		// TODO Auto-generated method stub
		return orderDao.getOverOrderByGoodsIdAndUserId(goodsId, userId);
	}

	@Override
	public String getBoluomeOrderDetailUrl(AfOrderDo orderInfo) {
		logger.info("getBoluomeOrderDetailUrl :" + orderInfo.toString());
		if (!orderInfo.getSecType().equals(OrderSecType.SUP_GAME.getCode())) {
			Map<String, String> buildParams = new HashMap<String, String>();

			buildParams.put(BoluomeCore.CUSTOMER_USER_ID, orderInfo.getUserId() + StringUtils.EMPTY);
			buildParams.put(BoluomeCore.CUSTOMER_USER_PHONE, orderInfo.getMobile());
			buildParams.put(BoluomeCore.TIME_STAMP, System.currentTimeMillis() / 1000 + StringUtils.EMPTY);

			String sign = BoluomeCore.buildSignStr(buildParams);
			buildParams.put(BoluomeCore.SIGN, sign);

			String paramsStr = BoluomeCore.createLinkString(buildParams);

			return orderInfo.getThirdDetailUrl() + "?" + paramsStr;
		} else {
			return orderInfo.getThirdDetailUrl();
		}
	}

	/**
	 * 根据订单号，查询订单信息
	 *
	 * @param orderNo
	 * @return
	 * @author gaojb
	 * @Time 2017年11月24日 下午5:10:47
	 */
	@Override
	public AfOrderDo getOrderByOrderNo(String orderNo) {

		return orderDao.getOrderByOrderNo(orderNo);
	}

	@Override
	public List<AfOrderDo> getOverOrderByUserId(Long userId) {
		// TODO Auto-generated method stub
		return orderDao.getOverOrderByUserId(userId);
	}

	@Override
	public List<AfOrderDto> selectSumCountByGoodsId(List<AfEncoreGoodsDto> list) {
		return orderDao.selectSumCountByGoodsId(list);
	}

	@Override
	public Integer selectSumCountByGoodsIdAndType(AfOrderDo afOrderDo) {
		return orderDao.selectSumCountByGoodsIdAndType(afOrderDo);
	}

	@Override
	public List<AfOrderDo> getDouble12OrderByGoodsIdAndUserId(Long goodsId, Long userId) {
		// TODO Auto-generated method stub
		return orderDao.getDouble12OrderByGoodsIdAndUserId(goodsId, userId);
	}

	@Override
	public HashMap getCountPaidOrderByUserAndOrderType(Long userId, String orderType) {
		// TODO Auto-generated method stub
		return orderDao.getCountPaidOrderByUserAndOrderType(userId, orderType);
	}

	@Override
	public List<AfOrderDo> getSelfsupportOrderByUserIdOrActivityTime(Long userId, String activityTime) {
		// TODO Auto-generated method stub
		return orderDao.getSelfsupportOrderByUserIdOrActivityTime(userId, activityTime);
	}

	@Override
	public int getAuthShoppingByUserId(Long userId, String activityTime) {
		// TODO Auto-generated method stub
		return orderDao.getAuthShoppingByUserId(userId, activityTime);
	}

	@Override
	public int getCountByUserId(Long rid) {
		// TODO Auto-generated method stub
		return orderDao.getCountByUserId(rid);
	}

	@Override
	public int updateAuAndUsed(Long orderId, BigDecimal auAmount, BigDecimal usedAmount) {
		return orderDao.updateAuAndUsed(orderId, auAmount, usedAmount);
	}

	@Override
	public int addSceneAmount(List<AfOrderSceneAmountDo> list) {
		return orderDao.addSceneAmount(list);
	}

	@Override
	public String getTradeBusinessNameByOrderId(Long orderid) {
		// TODO Auto-generated method stub
		return orderDao.getTradeBusinessNameByOrderId(orderid);
	}
}
