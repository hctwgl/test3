package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.KaixinUtil;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.MobileStatus;
import com.ald.fanbei.api.common.enums.OrderSatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderTempDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderTempDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
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
		order.setStatus(OrderSatus.FINISHED.getCode());
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
		order.setStatus(OrderSatus.PAID.getCode());
		order.setGmtPay(new Date());
		return orderDao.updateOrderByOrderNo(order);
	}

	@Override
	public int updateOrderTradeClosed(String content) {
		logger.info("updateOrderTradeClosed_content:"+content);
		AfOrderDo order = new AfOrderDo();
		order.setOrderNo(JSON.parseObject(content).getString("order_id"));
		order.setStatus(OrderSatus.CLOSED.getCode());
		
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
					newOrder.setStatus(OrderSatus.REBATED.getCode());
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
							newOrder.setStatus(OrderSatus.CLOSED.getCode());
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
}
