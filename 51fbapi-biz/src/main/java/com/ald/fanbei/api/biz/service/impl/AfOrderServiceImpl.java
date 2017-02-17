package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.third.util.KaixinUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.MobileStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
	
	@Override
	public int createOrderTrade(String content) {
		logger.info("createOrderTrade_content:"+content);
		JSONObject obj = JSON.parseObject(content);
		JSONArray array = JSON.parseArray(obj.getString("auction_infos"));
		Iterator<Object> it = array.iterator();
		while(it.hasNext()){
			JSONObject goods = (JSONObject) it.next();
			Long goodsId =0l;
		}
		return 0;
	}

	@Override
	public int updateOrderTradeSuccess(String content) {
		logger.info("updateOrderTradeSuccess_content:"+content);
		return 0;
	}

	@Override
	public int updateOrderTradePaidDone(String content) {
		logger.info("updateOrderTradePaidDone_content:"+content);
		return 0;
	}

	@Override
	public int updateOrderTradeClosed(String content) {
		logger.info("updateOrderTradeClosed_content:"+content);
		return 0;
	}

	@Override
	public int createMobileChargeOrder(final Long userId, final AfUserCouponDto couponDto,
			final BigDecimal money, final String mobile,final BigDecimal rebateAmount) {
		final String orderNo = generatorClusterNo.getOrderNo(OrderType.MOBILE);
		String msg = kaixinUtil.charge(orderNo, mobile, money.setScale(0).toString());
		JSONObject returnMsg = JSON.parseObject(msg);
		JSONObject result = JSON.parseObject(returnMsg.getString("result"));
		if(!result.getString("ret_code").equals(MobileStatus.SUCCESS.getCode())){
			//TODO  发送短信
			System.out.println(result.getString("ret_msg"));
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
							OrderType.MOBILE.getCode(),actualAmount, 0l, "", "", "", "", 1, ""));
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("dealMobileChargeOrder error："+e);
					return 0;
				}
			}
		});
	}

	private AfOrderDo buildOrder(String orderNo,Long userId, AfUserCouponDto couponDto,
			BigDecimal money,BigDecimal saleAmount, String mobile,BigDecimal rebateAmount,String orderType,BigDecimal actualAmount,
			Long goodsId,String openId,String goodsName,String goodsIcon,String goodsUrl,
			int count,String shopName){
		AfOrderDo orderDo = new AfOrderDo();
		orderDo.setUserId(userId);
		orderDo.setOrderNo(orderNo);
		orderDo.setOrderType(orderType);
		orderDo.setGoodsId(goodsId);
		orderDo.setOpenId(openId);
		orderDo.setGoodsName(goodsName);
		orderDo.setGoodsIcon(goodsIcon);
		orderDo.setGoodsUrl(goodsUrl);
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
}
