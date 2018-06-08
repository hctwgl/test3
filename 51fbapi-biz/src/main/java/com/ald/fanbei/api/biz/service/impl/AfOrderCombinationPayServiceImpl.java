package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.KuaijieOrderCombinationPayBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfBorrowExtendDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("afOrderCombinationPayService")
public class AfOrderCombinationPayServiceImpl extends UpsPayKuaijieServiceAbstract implements AfOrderCombinationPayService {

	@Autowired
	AfUserAccountService afUserAccountService;

	@Autowired
	BoluomeUtil boluomeUtil;

	@Autowired
	AfOrderDao orderDao;

	@Autowired
	AfAgentOrderService afAgentOrderService;

	@Autowired
	AfUserCouponService afUserCouponService;

	@Autowired
	JpushService jpushService;

	@Autowired
	AfUserService afUserService;

	@Autowired
	SmsUtil smsUtil;

	@Autowired
	AfOrderService afOrderService;
	@Autowired
	AfUserVirtualAccountService afUserVirtualAccountService;

	@Autowired
	AfBorrowDao afBorrowDao;

	@Autowired
	AfBorrowExtendDao afBorrowExtendDao;

	@Autowired
	AfBorrowService afBorrowService;

	@Autowired
	AfOrderDao afOrderDao;

	@Autowired
	RiskUtil riskUtil;

	@Resource
	AfBklService afBklService;

	/**
	 * 风控通过后组合支付
	 *
	 * @param userId
	 * @param orderNo
	 * @param tradeNo
	 * @param resultMap
	 * @param isSelf
	 * @param bankAmount
	 * @param borrow
	 * @param verybo
	 * @param cardInfo
	 * @return
	 */
	public Map<String, Object> combinationPay(final Long userId, final String orderNo, AfOrderDo orderInfo, String tradeNo, Map<String, Object> resultMap, Boolean isSelf, Map<String, Object> virtualMap, BigDecimal bankAmount, AfBorrowDo borrow, RiskVerifyRespBo verybo, AfUserBankcardDo cardInfo, String bankChannel) {
		String result = verybo.getResult();

		logger.info("combinationPay:borrow=" + borrow + ",orderNo=" + orderNo + ",result=" + result);
		// 如果风控审核结果是不成功则关闭订单，修改订单状态是支付中
		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(orderInfo.getUserId());

		if (!result.equals("10")) {
			resultMap.put("success", false);
			resultMap.put("verifybo", JSONObject.toJSONString(verybo));
			resultMap.put("errorCode", FanbeiExceptionCode.RISK_VERIFY_ERROR);

			orderInfo.setPayStatus(PayStatus.NOTPAY.getCode());
			orderInfo.setStatus(OrderStatus.CLOSED.getCode());
			orderInfo.setClosedDetail("系统关闭");
			// maqiaopan 2017-9-8 10:54:15风控拒绝原因字段添加
			String rejectCode = verybo.getRejectCode();
			orderInfo.setClosedReason("风控审批不通过");
			if (StringUtils.isNotBlank(rejectCode)) {
				orderInfo.setClosedReason("风控审批不通过" + rejectCode);
			}
			orderInfo.setGmtClosed(new Date());
			logger.info("updateOrder orderInfo = {}", orderInfo);
			if (OrderType.BOLUOME.getCode().equals(orderInfo.getOrderType())) {
				try {
					// 菠萝觅风控拒绝的订单自动取消
					boluomeUtil.cancelOrder(orderInfo.getThirdOrderNo(), orderInfo.getSecType(), orderInfo.getClosedReason());
					orderDao.updateOrder(orderInfo);
				} catch (UnsupportedEncodingException e) {
					logger.info("cancel Order error");
				}
			} else {
				if (StringUtils.equals(orderInfo.getOrderType(), OrderType.AGENTBUY.getCode())) {
					AfAgentOrderDo afAgentOrderDo = afAgentOrderService.getAgentOrderByOrderId(orderInfo.getRid());
					afAgentOrderDo.setClosedReason("风控审批失败");
					afAgentOrderDo.setGmtClosed(new Date());
					afAgentOrderService.updateAgentOrder(afAgentOrderDo);

					// 添加关闭订单释放优惠券
					if (afAgentOrderDo.getCouponId() > 0) {
						AfUserCouponDo couponDo = afUserCouponService.getUserCouponById(afAgentOrderDo.getCouponId());

						if (couponDo != null && couponDo.getGmtEnd().after(new Date())) {
							couponDo.setStatus(CouponStatus.NOUSE.getCode());
							afUserCouponService.updateUserCouponSatusNouseById(afAgentOrderDo.getCouponId());
						} else if (couponDo != null && couponDo.getGmtEnd().before(new Date())) {
							couponDo.setStatus(CouponStatus.EXPIRE.getCode());
							afUserCouponService.updateUserCouponSatusExpireById(afAgentOrderDo.getCouponId());
						}
					}

				}
				orderDao.updateOrder(orderInfo);
			}
			jpushService.dealBorrowApplyFail(userAccountInfo.getUserName(), new Date());
			return resultMap;
		}

		String orderType = OrderType.SELFSUPPORT.getCode();
		if (StringUtil.equals(orderInfo.getOrderType(), OrderType.AGENTBUY.getCode())) {
			orderType = OrderType.AGENTCPBUY.getCode();
		} else if (StringUtil.equals(orderInfo.getOrderType(), OrderType.BOLUOME.getCode())) {
			orderType = OrderType.BOLUOMECP.getCode();
		} else if (StringUtil.equals(orderInfo.getOrderType(), OrderType.SELFSUPPORT.getCode())) {
			orderType = OrderType.SELFSUPPORTCP.getCode();
		}

		// 银行卡支付
		KuaijieOrderCombinationPayBo bizObject = new KuaijieOrderCombinationPayBo(orderInfo, borrow, userAccountInfo, virtualMap);
		String remark = isSelf ? "自营商品订单支付" : "品牌订单支付";
		if (BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {// 快捷支付
			resultMap = sendKuaiJieSms(cardInfo.getRid(), tradeNo, bankAmount, userId, userAccountInfo.getRealName(), userAccountInfo.getIdNumber(), JSON.toJSONString(bizObject), "afOrderCombinationPayService", Constants.DEFAULT_BRAND_SHOP, remark, orderType);
		} else {// 代扣
			resultMap = doUpsPay(bankChannel, cardInfo.getRid(), tradeNo, bankAmount, userId, userAccountInfo.getRealName(), userAccountInfo.getIdNumber(), "", JSON.toJSONString(bizObject), Constants.DEFAULT_BRAND_SHOP, remark, orderType);
		}

		return resultMap;
	}

	@Override
	protected void quickPaySendSmmSuccess(String payTradeNo, String payBizObject, UpsCollectRespBo respBo) {

	}

	@Override
	protected void daikouConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {

	}

	@Override
	protected void kuaijieConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {

	}

	@Override
	protected Map<String, Object> upsPaySuccess(String payTradeNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
		// 租赁逻辑
		KuaijieOrderCombinationPayBo kuaijieOrderCombinationPayBo = JSON.parseObject(payBizObject, KuaijieOrderCombinationPayBo.class);
		if (kuaijieOrderCombinationPayBo.getOrderInfo() != null) {

			AfOrderDo orderInfo = kuaijieOrderCombinationPayBo.getOrderInfo();

			//更新订单为支付中状态
			AfOrderDo updateOrderInfo = new AfOrderDo();
			if(!BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {
				updateOrderInfo.setRid(orderInfo.getRid());
				updateOrderInfo.setPayStatus(PayStatus.DEALING.getCode());
				updateOrderInfo.setStatus(OrderStatus.DEALING.getCode());
				orderDao.updateOrder(updateOrderInfo);
			}
			logger.info("combinationPay upsPaySuccess bankChannel:"+bankChannel+" ,updateOrderInfo:" + updateOrderInfo);

			AfBorrowDo borrow = kuaijieOrderCombinationPayBo.getBorrow();
			AfUserAccountDo userAccountInfo = kuaijieOrderCombinationPayBo.getUserAccountInfo();
			Map<String, Object> virtualMap = kuaijieOrderCombinationPayBo.getVirtualMap();

			String virtualCode = afOrderService.getVirtualCode(virtualMap);
			// 是虚拟商品
			if (StringUtils.isNotBlank(virtualCode)) {
				AfUserVirtualAccountDo virtualAccountInfo = BuildInfoUtil.buildUserVirtualAccountDo(orderInfo.getUserId(), orderInfo.getBorrowAmount(), afOrderService.getVirtualAmount(virtualMap), orderInfo.getRid(), orderInfo.getOrderNo(), virtualCode);
				// 增加虚拟商品记录
				afUserVirtualAccountService.saveRecord(virtualAccountInfo);
			}

			// 新增借款信息
			afBorrowDao.addBorrow(borrow); // 冻结状态
			// 在风控审批通过后额度不变生成账单
			AfBorrowExtendDo afBorrowExtendDo = new AfBorrowExtendDo();
			afBorrowExtendDo.setId(borrow.getRid());
			afBorrowExtendDo.setInBill(0);
			afBorrowExtendDao.addBorrowExtend(afBorrowExtendDo);

			if (orderInfo.getOrderType().equals(OrderType.TRADE.getCode()) || orderInfo.getOrderType().equals(OrderType.BOLUOME.getCode())) {
				afBorrowService.updateBorrowStatus(borrow, userAccountInfo.getUserName(), userAccountInfo.getUserId());
				afBorrowService.dealAgentPayBorrowAndBill(borrow, userAccountInfo.getUserId(), userAccountInfo.getUserName(), orderInfo.getActualAmount(), PayType.AGENT_PAY.getCode(), orderInfo.getOrderType());
			} else if (orderInfo.getOrderType().equals(OrderType.AGENTBUY.getCode())) {
				afBorrowService.updateBorrowStatus(borrow, userAccountInfo.getUserName(), userAccountInfo.getUserId());
			} else if (orderInfo.getOrderType().equals(OrderType.SELFSUPPORT.getCode())) {
				afBorrowService.updateBorrowStatus(borrow, userAccountInfo.getUserName(), userAccountInfo.getUserId());
			}

			// 更新拆分场景使用额度
			riskUtil.updateUsedAmount(orderInfo, borrow);
			logger.info("upsPaySuccess bklUtils submitBklInfo orderInfo ="+JSONObject.toJSONString(orderInfo));
			if (orderInfo.getOrderType().equals(OrderType.SELFSUPPORT.getCode())) {
				//新增白名单逻辑
				try {
					String bklResult = afBklService.isBklResult(orderInfo);
					if (bklResult.equals("v2")){//需电核
						logger.info("upsPaySuccess bklUtils submitBklInfo result isBklResult v2 orderInfo ="+JSON.toJSONString(orderInfo));
						afBklService.submitBklInfo(orderInfo,"组合支付",orderInfo.getBorrowAmount());
						if (orderInfo.getIagentStatus()==null)
							orderInfo.setIagentStatus("C");
					}else if (bklResult.equals("v1")){//不需电核
						logger.info("upsPaySuccess bklUtils submitBklInfo result isBklResult v1 orderInfo ="+JSON.toJSONString(orderInfo));
						if (orderInfo.getIagentStatus()==null)
							orderInfo.setIagentStatus("A");
						afOrderService.updateIagentStatusByOrderId(orderInfo.getRid(),orderInfo.getIagentStatus());
					}
				}catch (Exception e){
					logger.error("upsPaySuccess bklUtils submitBklInfo error",e);
				}
			}
		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("status", PayStatus.DEALING.getCode());
		resultMap.put("success", true);

		return resultMap;
	}

	@Override
	protected void roolbackBizData(String payTradeNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo) {

	}
}
