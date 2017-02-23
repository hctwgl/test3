package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.third.util.KaixinUtil;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.MobileStatus;
import com.ald.fanbei.api.common.enums.OrderSatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfOrderTempDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderTempDo;
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
	private TransactionTemplate transactionTemplate;
	
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
		AfOrderDo order = buildOrder(obj.getString("order_id"), 0l, null, priceAmount, priceAmount, "", BigDecimal.ZERO, 
				orderType, priceAmount, goodsId, goodsObj.getString("auction_id"), 
				goodsObj.getString("auction_title"), Constants.CONFKEY_TAOBAO_ICON_COMMON_LOCATION+goodsObj.getString("auction_pict_url"), count, obj.getString("shop_title"));
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
		AfOrderDo order = new AfOrderDo();
		order.setOrderNo(JSON.parseObject(content).getString("order_id"));
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
	public int createMobileChargeOrder(final Long userId, final AfUserCouponDto couponDto,
			final BigDecimal money, final String mobile,final BigDecimal rebateAmount) {
		final String orderNo = generatorClusterNo.getOrderNo(OrderType.MOBILE);
		if(StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE), Constants.INVELOMENT_TYPE_ONLINE)){
			String msg = kaixinUtil.charge(orderNo, mobile, money.setScale(0).toString());
			JSONObject returnMsg = JSON.parseObject(msg);
			JSONObject result = JSON.parseObject(returnMsg.getString("result"));
			if(!result.getString("ret_code").equals(MobileStatus.SUCCESS.getCode())){
				//TODO  发送短信
				System.out.println(result.getString("ret_msg"));
			}
		}
		
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					BigDecimal actualAmount = money;
					if(null != couponDto){
						//优惠券设置已使用
						afUserCouponDao.updateUserCouponSatusUsedById(couponDto.getRid());
						actualAmount = money.subtract(couponDto.getAmount());
					}
					//订单创建
					orderDao.createOrder(buildOrder(orderNo,userId, couponDto, money,money, mobile, rebateAmount, 
							OrderType.MOBILE.getCode(),actualAmount, 0l, "","", "", 1, ""));
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealMobileChargeOrder error："+e);
					return 0;
				}
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
	private AfOrderDo buildOrder(String orderNo,Long userId, AfUserCouponDto couponDto,
			BigDecimal money,BigDecimal saleAmount, String mobile,BigDecimal rebateAmount,String orderType,BigDecimal actualAmount,
			Long goodsId,String openId,String goodsName,String goodsIcon,int count,String shopName){
		AfOrderDo orderDo = new AfOrderDo();
		orderDo.setUserId(userId);
		orderDo.setOrderNo(orderNo);
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
			orderDo.setUserCouponId(couponDto.getCouponId());
		}
		orderDo.setActualAmount(actualAmount);
		orderDo.setRebateAmount(rebateAmount);
		orderDo.setMobile(mobile);
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
}
