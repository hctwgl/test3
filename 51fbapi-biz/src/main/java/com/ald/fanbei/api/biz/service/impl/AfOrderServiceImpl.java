package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.KaixinUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AccountLogType;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.MobileStatus;
import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.RefundSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowBillDao;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderRefundDao;
import com.ald.fanbei.api.dal.dao.AfOrderTempDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;
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
import com.taobao.api.domain.XItem;
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
	@Resource
	BoluomeUtil boluomeUtil;
	@Resource
	AfOrderRefundDao afOrderRefundDao;
	@Resource
	UpsUtil upsUtil;
	@Resource
	AfAgentOrderService afAgentOrderService;

	@Resource
	RiskUtil riskUtil;
	
	@Override
	public int createOrderTrade(final String content) {
		logger.info("createOrderTrade_content:"+content);
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					JSONObject obj = JSON.parseObject(content);
					JSONArray array = JSON.parseArray(obj.getString("auction_infos"));
					List<AfOrderDo> orderList = new ArrayList<AfOrderDo>();
					if (CollectionUtils.isNotEmpty(array)) {
						//如果有多个 生成多个订单
						for (int i = 0; i < array.size(); i++) {
							JSONObject goodsObj = array.getJSONObject(i);
							Long goodsId =0l;
							String orderType="";
							String numId = StringUtils.EMPTY;
							BigDecimal priceAmount = BigDecimal.ZERO;
							int count = NumberUtil.objToIntDefault(goodsObj.getString("auction_amount"), 1);
							priceAmount = NumberUtil.objToBigDecimalDefault(goodsObj.getString("real_pay"), BigDecimal.ZERO);
							AfGoodsDo goods = afGoodsDao.getGoodsByOpenId(goodsObj.getString("auction_id"));
							if(null == goods){
								Map<String, Object> params = new HashMap<String, Object>();
								params.put(TaobaoApiUtil.OPEN_IID, goodsObj.getString("auction_id"));
								List<XItem> nTbkItemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();
								XItem item = nTbkItemList.get(0);
								if (item != null) {
									logger.info("createOrderTrade_content item is not null");
									orderType = item.getMall() ? OrderType.TMALL.getCode() : OrderType.TAOBAO.getCode();
									numId = item.getOpenId() + StringUtils.EMPTY;
								} else {
									//默认值
									TaeItemDetailGetResponse res = taobaoApiUtil.executeTaeItemDetailSearch(goodsObj.getString("auction_id"));
									logger.info("createOrderTrade_content item is null res = {}", res);
									JSONObject resObj = JSON.parseObject(res.getBody());
									JSONObject sellerInfo = resObj.getJSONObject("tae_item_detail_get_response").getJSONObject("data").getJSONObject("seller_info");
									orderType = sellerInfo.getString("seller_type").toUpperCase();
									numId = StringUtils.EMPTY;
								}
							}else{
								goodsId = goods.getRid();
								orderType = goods.getSource();
								numId = goods.getNumId();
							}
							AfOrderDo order = buildFullInfo(0l, obj.getString("order_id"), goodsObj.getString("detail_order_id"), StringUtils.EMPTY, OrderStatus.NEW.getCode(), 0l, orderType, 
									StringUtils.EMPTY, goodsId, goodsObj.getString("auction_id"), numId, goodsObj.getString("auction_title"), Constants.CONFKEY_TAOBAO_ICON_COMMON_LOCATION+goodsObj.getString("auction_pict_url"), count, 
									priceAmount, priceAmount, priceAmount, obj.getString("shop_title"), PayStatus.NOTPAY.getCode(), StringUtils.EMPTY, StringUtils.EMPTY, null, StringUtils.EMPTY, null, StringUtils.EMPTY, null, BigDecimal.ZERO, BigDecimal.ZERO, 0l, null); 
							orderList.add(order);
						}
						logger.info("createOrderTrade_content orderList = {}" ,orderList);
						return orderDao.createOrderList(orderList);
					}
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealMobileChargeOrder error:",e);
					return 0;
				}
			}
		});
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
			if (orderTemp.getOrderId() > 0) {
				logger.info("orderTemp1=="+JSON.toJSONString(orderTemp));

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
					AfOrderDo orderInfo = orderDao.getOrderInfoByOrderNo(payOrderNo);
					if (orderInfo == null || orderInfo.getStatus().equals(OrderStatus.REBATED.getCode())) {
						return null;
					}
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
								String refundNo = generatorClusterNo.getRefundNo(new Date());
								String refundResult = UpsUtil.wxRefund(order.getOrderNo(), order.getPayTradeNo(), order.getActualAmount(), order.getActualAmount());
								if(!"SUCCESS".equals(refundResult)){
									afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(),order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FAIL,PayType.WECHAT,StringUtils.EMPTY,null,"充值失败微信退款",RefundSource.PLANT_FORM.getCode(),payOrderNo));
									throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
								}else{
                                	afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(),order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FINISH,PayType.WECHAT,StringUtils.EMPTY,null,"充值失败微信退款",RefundSource.PLANT_FORM.getCode(),payOrderNo));
								}
							} catch (Exception e) {
								pushService.refundMobileError(userDo.getUserName(), order.getGmtCreate());
								logger.info("wxRefund error:",e);
							}
						}else{//银行卡代付
							//TODO 转账处理
							AfBankUserBankDto card = afUserBankcardDao.getUserBankcardByBankId(order.getBankId());
							String refundNo = generatorClusterNo.getRefundNo(new Date());
							UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(order.getActualAmount(), userDo.getRealName(), card.getCardNumber(), order.getUserId()+"", 
									card.getMobile(), card.getBankName(), card.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02",OrderType.MOBILE.getCode(),"");
							if(!upsResult.isSuccess()){
								afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo,order.getActualAmount(), order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FAIL,PayType.BANK,card.getCardNumber(),card.getBankName(),"充值失败银行卡退款",RefundSource.PLANT_FORM.getCode(),upsResult.getOrderNo()));
								pushService.refundMobileError(userDo.getUserName(), order.getGmtCreate());
							}else{
                            	afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo,order.getActualAmount(), order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FINISH,PayType.BANK,card.getCardNumber(), card.getBankName(),"充值失败银行卡退款",RefundSource.PLANT_FORM.getCode(),upsResult.getOrderNo()));
							}
						}
						newOrder.setStatus(OrderStatus.CLOSED.getCode());
						orderDao.updateOrderByOutTradeNo(newOrder);
						pushService.chargeMobileError(userDo.getUserName(), order.getMobile(), order.getGmtCreate());
					}
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealMobileChargeOrder error:",e);
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
                        } else if (!"SUCCESS".equals(orderStatus) && OrderStatus.REBATED.getCode().equals(order.getStatus())) {
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
                                    String refundResult = UpsUtil.wxRefund(order.getOrderNo(), order.getPayTradeNo(), order.getActualAmount(), order.getActualAmount());
                                    if (!"SUCCESS".equals(refundResult)) {
                                    	afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(),order.getActualAmount(), order.getUserId(), order.getRid(), orderNo, OrderRefundStatus.FAIL,PayType.WECHAT,StringUtils.EMPTY,null,"充值失败微信退款",RefundSource.PLANT_FORM.getCode(), order.getPayTradeNo()));
            							throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
                                    }else{
                                    	afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(),order.getActualAmount(), order.getUserId(), order.getRid(), orderNo, OrderRefundStatus.FINISH,PayType.WECHAT,StringUtils.EMPTY,null,"充值失败微信退款",RefundSource.PLANT_FORM.getCode(), order.getPayTradeNo()));
                                    }
                                } catch (Exception e) {
                                    pushService.refundMobileError(userDo.getUserName(), order.getGmtCreate());
                                    logger.info("wxRefund error:", e);
                                }
                            } else {// 银行卡代付
                                    // TODO 转账处理
                                AfBankUserBankDto card = afUserBankcardDao.getUserBankcardByBankId(order.getBankId());
                                UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(order.getActualAmount(), userDo.getRealName(), card.getCardNumber(), order.getUserId() + "",
                                        card.getMobile(), card.getBankName(), card.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02", OrderType.MOBILE.getCode(), "");
                                if (!upsResult.isSuccess()) {
                                	afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(),order.getActualAmount(), order.getUserId(), order.getRid(), orderNo, OrderRefundStatus.FAIL,PayType.BANK,card.getCardNumber(),card.getBankName(),"充值失败银行卡退款",RefundSource.PLANT_FORM.getCode(), upsResult.getOrderNo()));
        							pushService.refundMobileError(userDo.getUserName(), order.getGmtCreate());
                                }else{
                                	afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(),order.getActualAmount(), order.getUserId(), order.getRid(), orderNo, OrderRefundStatus.FINISH,PayType.BANK,card.getCardNumber(),card.getBankName(),"充值失败银行卡退款",RefundSource.PLANT_FORM.getCode(), upsResult.getOrderNo()));
                                }
                            }
                            // 支付成功后,直接返利
                            AfOrderDo newOrder = new AfOrderDo();
                            newOrder.setGmtFinished(new Date());
                            newOrder.setGmtRebated(new Date());
                            newOrder.setStatus(OrderStatus.CLOSED.getCode());
                            orderDao.updateOrderByOutTradeNo(newOrder);
                            pushService.chargeMobileError(userDo.getUserName(), order.getMobile(), order.getGmtCreate());
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
	 * @param numId  --商品数字id
	 * @param goodsName --商品名称
	 * @param goodsIcon --商品图片
	 * @param count --数量
	 * @param shopName --店铺名称
	 * @return
	 */
	private AfOrderDo buildOrder(Date now,String orderNo,String payTradeNo,Long userId, AfUserCouponDto couponDto,
			BigDecimal money,BigDecimal saleAmount, String mobile,BigDecimal rebateAmount,String orderType,BigDecimal actualAmount,
			Long goodsId,String openId, String goodsName,String goodsIcon,int count,String shopName,Long bankId){
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
	
	private AfOrderDo buildFullInfo(Long userId,String orderNo,String thirdOrderNo,String thirdDetailUrl,String status,Long userCouponId, String orderType,
			String secType, Long goodsId, String openId, String numId, String goodsName, String goodsIcon, Integer count, BigDecimal priceAmount, BigDecimal saleAmount,
			BigDecimal actualAmount, String shopName, String payStatus, String payType, String payTradeNo, Date gmtPay, String tradeNo, Date gmtRebated, String mobile,
			Date gmtFinished, BigDecimal rebateAmount, BigDecimal commissionAmount, Long bankId, Date gmtPayEnd) {
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
			UpsCollectRespBo respBo = upsUtil.collect(orderNo,actualAmount, userId+"", afUserAccountDo.getRealName(), card.getMobile(), 
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
	public Map<String, Object> payBrandOrder(final Long payId, final Long orderId, final Long userId,
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
						orderDao.updateOrder(orderInfo);
						BigDecimal useableAmount = userAccountInfo.getAuAmount()
								.subtract(userAccountInfo.getUsedAmount()).subtract(userAccountInfo.getFreezeAmount());

						if (useableAmount.compareTo(saleAmount) < 0) {
							throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_MONEY_ERROR);
						}
						// 在申请代买的时候就会扣去额度，如果审批不同过的话再加上响应的额度
						afBorrowService.dealAgentPayConsumeApply(userAccountInfo, orderInfo.getActualAmount(),
								orderInfo.getGoodsName(), orderInfo.getNper(), orderInfo.getRid(),
								orderInfo.getOrderNo(), null);

						// 最后调用风控控制
						logger.info("verify userId" + userId);

						String cardNo = card.getCardNumber();
						String riskOrderNo = riskUtil.getOrderNo("vefy",
								cardNo.substring(cardNo.length() - 4, cardNo.length()));
						orderInfo.setRiskOrderNo(riskOrderNo);
						orderDao.updateOrder(orderInfo);

						try {
							riskUtil.verify(ObjectUtils.toString(userId, ""), "40",
									card.getCardNumber(), appName, ipAddress, StringUtil.EMPTY, StringUtil.EMPTY,
									"/third/risk/payOrder",riskOrderNo);
						} catch (Exception e) {
							//throw new FanbeiException();
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
					if (orderInfo == null 
   						 || (!orderInfo.getStatus().equals(OrderStatus.NEW.getCode()) && !orderInfo.getStatus().equals(OrderStatus.DEALING.getCode()))) {
						return 0;
					}
					logger.info("dealBrandOrder begin , payOrderNo = {} and tradeNo = {} and type = {}", new Object[]{payOrderNo, tradeNo, payType});
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
					logger.error("dealBrandOrder error:",e);
					return 0;
				}
			}
		});
		if (result == 1) {
			boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, orderInfo.getUserId(), orderInfo.getActualAmount());
		}
		return result;
	}

	@Override
	public int dealBrandOrderRefund(final Long orderId,final Long userId, final Long bankId, final String orderNo, final String thirdOrderNo,
			final BigDecimal refundAmount, final BigDecimal totalAmount, final String payType, final String payTradeNo, final String refundNo, final String refundSource) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					logger.info("dealBrandOrderRefund begin , orderId = {} userId = {} orderNo = {} refundAmount = {} totalAmount = {} payType = {} payTradeNo = {} refundNo = {} refundSource = {}", 
							new Object[]{orderId,userId,orderNo,refundAmount,totalAmount,payType,payTradeNo,refundNo,refundSource});
					AfOrderDo orderInfo = null;
					//金额小于0
					if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
						throw new FanbeiException("reund amount error", FanbeiExceptionCode.REFUND_AMOUNT_ERROR);
					}
					PayType type = PayType.findRoleTypeByCode(payType);
					//已经退款金额
					BigDecimal refundedAmount = afOrderRefundDao.calculateTotalRefundAmount(orderId);
					BigDecimal totalRefundAmount = refundAmount.add(refundedAmount == null ? BigDecimal.ZERO : refundedAmount);
					//总退款金额大于订单金额
					if (totalRefundAmount.compareTo(totalAmount) > 0) {
						throw new FanbeiException("reund total amount error", FanbeiExceptionCode.REFUND_TOTAL_AMOUNT_ERROR);
					}
					switch (type) {
					case WECHAT:
						//微信退款
						String refundResult = UpsUtil.wxRefund(orderNo, payTradeNo, refundAmount, totalAmount);
						logger.info("wx refund  , refundResult = {} ", refundResult);
						if(!"SUCCESS".equals(refundResult)){
							afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, refundAmount, userId, orderId, orderNo, OrderRefundStatus.FAIL,PayType.WECHAT,StringUtils.EMPTY, null,"菠萝觅微信退款",refundSource,payTradeNo));
							throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
						} else {
							afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, refundAmount, userId, orderId, orderNo, OrderRefundStatus.FINISH,PayType.WECHAT,StringUtils.EMPTY, null,"菠萝觅微信退款",refundSource,payTradeNo));
							boluomeUtil.pushRefundStatus(orderId, orderNo, thirdOrderNo, PushStatus.REFUND_SUC, userId, refundAmount,refundNo);
						}
						orderInfo = new AfOrderDo();
						orderInfo.setRid(orderId);
						orderInfo.setStatus(OrderStatus.CLOSED.getCode());
						orderDao.updateOrder(orderInfo);
						break;
					case AGENT_PAY:
						//代付退款
						logger.info("agent pay refund begin");
						orderInfo = orderDao.getOrderById(orderId);
						
						AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(orderInfo.getUserId());
						
						AfBorrowDo borrowInfo = afBorrowService.getBorrowByOrderIdAndStatus(orderInfo.getRid(), BorrowStatus.TRANSED.getCode());
						
						//重新需要生成账单的金额
						BigDecimal borrowAmount = afBorrowService.calculateBorrowAmount(borrowInfo.getRid(), refundAmount, refundSource.equals(RefundSource.USER.getCode()));
						logger.info("dealBrandOrderRefund borrowAmount = {}", borrowAmount);
						
						//更新账户金额
						BigDecimal usedAmount = BigDecimalUtil.subtract(accountInfo.getUsedAmount(), calculateUsedAmount(borrowInfo));
						accountInfo.setUsedAmount(usedAmount);
						afUserAccountDao.updateOriginalUserAccount(accountInfo);
						//增加Account记录
						afUserAccountLogDao.addUserAccountLog(BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.AP_REFUND, borrowInfo.getAmount(), userId, borrowInfo.getRid()));
						
						afBorrowService.updateBorrowStatus(borrowInfo.getRid(), BorrowStatus.FINISH.getCode());
						
						afBorrowBillDao.updateNotRepayedBillStatus(borrowInfo.getRid(), BorrowBillStatus.CLOSE.getCode());
						
						orderInfo = new AfOrderDo();
						orderInfo.setRid(orderId);
						orderInfo.setStatus(OrderStatus.CLOSED.getCode());
						orderDao.updateOrder(orderInfo);
						
						AfUserBankcardDo cardInfo = afUserBankcardDao.getUserMainBankcardByUserId(userId);
						if (borrowAmount.compareTo(BigDecimal.ZERO) < 0) {
							//退款最后放置，因为如果其他过程抛异常就不需要退款操作
							AfOrderRefundDo refundInfo = BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, borrowAmount.abs(), userId, orderId, orderNo, OrderRefundStatus.REFUNDING,PayType.BANK,cardInfo.getCardNumber(),cardInfo.getBankName(),"菠萝觅代付多余还款退款"+borrowAmount.abs(), refundSource,StringUtils.EMPTY);
							afOrderRefundDao.addOrderRefund(refundInfo);
							UpsDelegatePayRespBo tempUpsResult = upsUtil.delegatePay(borrowAmount.abs(), accountInfo.getRealName(), cardInfo.getCardNumber(), userId+"", 
									cardInfo.getMobile(), cardInfo.getBankName(), cardInfo.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02",UserAccountLogType.BANK_REFUND.getCode(),refundInfo.getRid() + StringUtils.EMPTY);
							logger.info("agent bank refund upsResult = {}", tempUpsResult);
							if(!tempUpsResult.isSuccess()){
								refundInfo.setStatus(OrderRefundStatus.FAIL.getCode());
								refundInfo.setPayTradeNo(tempUpsResult.getOrderNo());
								afOrderRefundDao.updateOrderRefund(refundInfo);
								throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
							} else {
								refundInfo.setPayTradeNo(tempUpsResult.getOrderNo());
								afOrderRefundDao.updateOrderRefund(refundInfo);
							}
						} else if (borrowAmount.compareTo(BigDecimal.ZERO) > 0){
							afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, BigDecimal.ZERO, userId, orderId, orderNo, OrderRefundStatus.FINISH,PayType.AGENT_PAY,StringUtils.EMPTY, null,"菠萝觅代付退款生成新账单" + borrowAmount.abs(),refundSource,StringUtils.EMPTY));
							afBorrowService.dealAgentPayConsumeApply(accountInfo, borrowAmount, borrowInfo.getName(), borrowInfo.getNper() - borrowInfo.getNperRepayment(), orderId, orderNo, borrowInfo.getNper());
						} else {
							afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, BigDecimal.ZERO, userId, orderId, orderNo, OrderRefundStatus.FINISH,PayType.AGENT_PAY,StringUtils.EMPTY, null,"菠萝觅代付退款",refundSource,StringUtils.EMPTY));
						}
						boluomeUtil.pushRefundStatus(orderId, orderNo, thirdOrderNo, PushStatus.REFUND_SUC, userId, refundAmount, refundNo);
						break;
					case BANK:
						//银行卡退款
						AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
						AfUserBankcardDo card = afUserBankcardDao.getUserBankInfo(bankId);
						
						AfOrderRefundDo refundInfo = BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount,refundAmount, userId, orderId, orderNo, OrderRefundStatus.REFUNDING,PayType.BANK,card.getCardNumber(),card.getBankName(),"菠萝觅银行卡退款",refundSource,StringUtils.EMPTY);
						afOrderRefundDao.addOrderRefund(refundInfo);
						orderInfo = new AfOrderDo();
						orderInfo.setRid(orderId);
						orderInfo.setStatus(OrderStatus.DEAL_REFUNDING.getCode());
						orderDao.updateOrder(orderInfo);
						UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(refundAmount, userAccount.getRealName(), card.getCardNumber(), userId+"", 
								card.getMobile(), card.getBankName(), card.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02",UserAccountLogType.BANK_REFUND.getCode(),refundInfo.getRid() + StringUtils.EMPTY);
						logger.info("bank refund upsResult = {}", upsResult);
						if(!upsResult.isSuccess()){
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
					return 1;
				} catch (FanbeiException e) {
					logger.error("dealBrandOrderRefund error = {}", e);
					boluomeUtil.pushRefundStatus(orderId, orderNo, thirdOrderNo, PushStatus.REFUND_FAIL, userId, refundAmount, refundNo);
					throw new FanbeiException("reund error", e.getErrorCode());
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealBrandOrderRefund error:",e);
					boluomeUtil.pushRefundStatus(orderId, orderNo, thirdOrderNo, PushStatus.REFUND_FAIL, userId, refundAmount, refundNo);
					return 0;
				}
			}
		});
	}
	
	/**
	 * 获取应该退多少额度
	 * @param borrowInfo
	 * @return
	 */
	private BigDecimal calculateUsedAmount(AfBorrowDo borrowInfo) {
		List<AfBorrowBillDo> repaymentedBillList = afBorrowBillDao.getBillListByBorrowIdAndStatus(borrowInfo.getRid(), BorrowBillStatus.YES.getCode());
		if (CollectionUtils.isEmpty(repaymentedBillList)) {
			return borrowInfo.getAmount();
		} else {
			if (borrowInfo.getNper().equals(borrowInfo.getNperRepayment())) {
				//如果已经还清
				return borrowInfo.getAmount();
			} else {
				//算还了几期金额
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


}
