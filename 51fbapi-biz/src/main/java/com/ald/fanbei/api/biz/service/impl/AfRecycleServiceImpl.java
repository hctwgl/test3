package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.thirdpay.ThirdRecycleEnum;
import com.ald.fanbei.api.biz.service.AfRecycleService;
import com.ald.fanbei.api.biz.service.AfRecycleTradeService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RecycleUtil;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import com.ald.fanbei.api.dal.domain.query.AfRecycleRatioQuery;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @类描述： 有得卖  回收业务
 * @author weiqingeng 2018年2月27日上午9:55:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRecycleService")
public class AfRecycleServiceImpl implements AfRecycleService {
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private AfRecycleDao afRecycleDao;
	@Autowired
	private AfRecycleViewDao afRecycleViewDao;
	@Autowired
	private AfUserAccountDao afUserAccountDao;
	@Autowired
	private AfUserService afUserService;
	@Autowired
	private AfUserCouponDao afUserCouponDao;
	@Autowired
	private AfCouponDao afCouponDao;
	@Autowired
	private AfRecycleTradeService afRecycleTradeService;



	/**
	 * 订单添加事物
	 * @param afRecycleQuery
	 * @return
	 */
	@Override
	public Integer addRecycleOrder(final AfRecycleQuery afRecycleQuery) {
		transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus transactionStatus) {
				Integer result = afRecycleDao.addRecycleOrder(afRecycleQuery);
				//回调有得卖确认接口
				Map<String, String> params = new HashMap<String, String>();
				String sign = SignUtil.sign(RecycleUtil.createLinkString(params), RecycleUtil.PRIVATE_KEY);
				String postResult = HttpUtil.post(RecycleUtil.YDM_CALLBACK_URL, params);
				JSONObject jsonObject = JSONObject.parseObject(postResult);
				if(null != jsonObject && StringUtils.equals("1",jsonObject.getString("code"))){//返回成功
					//给用户账号添加回收订单金额
					AfRecycleRatioDo afRecycleRatioDo = afRecycleDao.getRecycleReturnRatio();
					Long userId = afRecycleQuery.getUid();
					BigDecimal settlePrice = afRecycleQuery.getSettlePrice();
					BigDecimal amount = afUserAccountDao.getAuAmountByUserId(userId);//查找用户账号信息
					if(null == amount){//用户账号信息不存在,则需要添加一条账号信息
						//根据用户Id查找用户名
						AfUserDo afUserDo = afUserService.getUserById(userId);
						//给用户的返现金额
						BigDecimal rebateAmount = BigDecimal.ONE.add(afRecycleRatioDo.getRatio()).multiply(settlePrice);						
						AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
						afUserAccountDo.setUserId(userId);
						afUserAccountDo.setUserName(afUserDo.getUserName());
						afUserAccountDo.setRebateAmount(rebateAmount);
						afUserAccountDao.addUserAccount(afUserAccountDo);
						//有得卖账户减钱操作
						recycleTradeSave(afRecycleQuery, afRecycleRatioDo,settlePrice, rebateAmount);
					}else{//直接往账号上添加金额 金额 = 订单金额 *（1 + 返现比例）
						BigDecimal rebateAmount = BigDecimal.ONE.add(afRecycleRatioDo.getRatio()).multiply(settlePrice);
						AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
						afUserAccountDo.setRebateAmount(rebateAmount);
						afUserAccountDao.updateUserAccount(afUserAccountDo);
						//有得卖账户减钱操作
						recycleTradeSave(afRecycleQuery, afRecycleRatioDo,settlePrice, rebateAmount);
					}
				}
				return result;
			}

			private void recycleTradeSave(final AfRecycleQuery afRecycleQuery,
					AfRecycleRatioDo afRecycleRatioDo, BigDecimal settlePrice,
					BigDecimal rebateAmount) {
				AfRecycleTradeDo afRecycleTradeDo = afRecycleTradeService.getLastRecord();
				AfRecycleTradeDo newAfRecycleTradeDo = new AfRecycleTradeDo();
				newAfRecycleTradeDo.setGmtCreate(new Date());
				newAfRecycleTradeDo.setGmtModified(new Date());
				newAfRecycleTradeDo.setRatio(afRecycleRatioDo.getRatio());
				newAfRecycleTradeDo.setRefId(afRecycleQuery.getRid());
				newAfRecycleTradeDo.setRemainAmount(afRecycleTradeDo.getRemainAmount().subtract(settlePrice));
				newAfRecycleTradeDo.setReturnAmount(rebateAmount);
				newAfRecycleTradeDo.setTradeAmount(settlePrice.add(rebateAmount));
				newAfRecycleTradeDo.setType(1);
				afRecycleTradeService.saveRecord(newAfRecycleTradeDo);
			}
		});
		return 1;
	}

	@Override
	public AfRecycleDo getRecycleOrder(AfRecycleQuery afRecycleQuery) {
		return afRecycleDao.getRecycleOrder(afRecycleQuery);
	}

	/**
	 * 翻倍兑换业务
	 * @param uid
	 * @param exchangeAmount
	 * @param remainAmount
	 * @return
	 */
	@Override
	public AfRecycleRatioDo addExchange(final Long uid, final Integer exchangeAmount, final BigDecimal remainAmount) {
		    final AfRecycleRatioDo afRecycleRatioDo = new AfRecycleRatioDo();
		    transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus transactionStatus) {
				List<AfRecycleRatioDo> list = afRecycleDao.getRecycleExchangeRatio();
				BigDecimal ratio = RecycleUtil.getExchangeRatio(list);
				BigDecimal needExchangeAmount = ratio.multiply(BigDecimal.valueOf(exchangeAmount));//翻倍后的金额
				AfUserAccountDo afUserAccountDo = new AfUserAccountDo(uid,BigDecimal.valueOf(exchangeAmount));
				afUserAccountDao.reduceRebateAmount(afUserAccountDo);//用户账号扣除返现金额
				AfCouponDo afCouponDo = afCouponDao.getCouponByName(RecycleUtil.COUPON_NAME);//查找手否存在指定名称的券信息
				Long couponId = 0L;
				if(null != afCouponDo) {
					couponId = afCouponDo.getRid();
					afCouponDao.updateCouponquotaAlreadyById(new AfCouponDo(couponId,1));
				}else {
					//增加券信息
					AfCouponDo couponInfo = new AfCouponDo();
					couponInfo.setModifier("system");
					couponInfo.setCreator("system");
					couponInfo.setName(RecycleUtil.COUPON_NAME);//券名称
					couponInfo.setAmount(needExchangeAmount);
					couponInfo.setQuota(-1L);//优惠券发放总数 -1不限
					couponInfo.setQuotaAlready(1);//已经发放数量
					couponInfo.setLimitAmount(RecycleUtil.LIMIT_AMOUNT);//最小限制金额,50元
					couponInfo.setLimitCount(RecycleUtil.LIMIT_COUNT);//每个人限制领取张数
					couponInfo.setGmtStart(new Date());
					couponInfo.setGmtEnd(DateUtil.getFinalDate());
					couponInfo.setType(CouponType.FULLVOUCHER.getCode());//满减券
					couponInfo.setUseRule("");//使用须知
					couponInfo.setStatus("O");//优惠券状态【O：开启,C:关闭 】
					couponInfo.setIsGlobal(0);// '是否为全场通用券,0表示全场券,1表示活动券'
					couponInfo.setShopUrl("");
					couponInfo.setExpiryType("R");
					afCouponDao.addCoupon(couponInfo);
					couponId = couponInfo.getRid();
				}
				//将券分配给当前兑换用户
				AfUserCouponDo userCoupon = new AfUserCouponDo(uid,couponId,CouponStatus.NOUSE.getCode(),CouponSenceRuleType.DOUBLE_EXCHANGE.getCode(),new Date(),DateUtil.getFinalDate());
				afUserCouponDao.addUserCoupon(userCoupon);// 插入

				afRecycleRatioDo.setRatio(ratio);
				afRecycleRatioDo.setAmount(needExchangeAmount);
				return null;
			}
		});
		return afRecycleRatioDo;
	}


}
