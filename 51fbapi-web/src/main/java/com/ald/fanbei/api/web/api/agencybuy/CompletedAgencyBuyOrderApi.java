package com.ald.fanbei.api.web.api.agencybuy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.bo.assetpush.AssetPushType;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.kafka.KafkaConstants;
import com.ald.fanbei.api.biz.kafka.KafkaSync;
import com.ald.fanbei.api.biz.rebate.RebateContext;
import com.ald.fanbei.api.biz.service.AfAssetSideInfoService;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowPushService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfRetryTemplService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.VersionCheckUitl;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.SpringBeanContextUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowExtendDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.domain.*;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.third.util.AssetSideEdspayUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;


@Component("completedAgencyBuyOrderApi")
public class CompletedAgencyBuyOrderApi implements ApiHandle {

	@Resource
	AfBorrowBillService afBorrowBillService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	RebateContext rebateContext;
	@Resource
	AfBorrowExtendDao afBorrowExtendDao;

	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfAssetSideInfoService afAssetSideInfoService;
	@Resource
	AssetSideEdspayUtil assetSideEdspayUtil;
	@Resource
	AfRetryTemplService afRetryTemplService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	RedisTemplate redisTemplate;
	@Resource
	AfBorrowPushService afBorrowPushService;
	@Autowired
	KafkaSync kafkaSync;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Integer appVersion = context.getAppVersion();
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), 0);
		Long userId = context.getUserId();
		VersionCheckUitl.setVersion(context.getAppVersion());
		//用户订单检查
		AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,userId);
		if(null == orderInfo){
			throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
		}
		//app371之前自营订单不处理
		if(appVersion<371){
			//如果为自营订单,不改变订单状态,直接返回操作成功
			if(StringUtils.equals(orderInfo.getOrderType(), OrderType.SELFSUPPORT.getCode())){
				//自营确认收货走返利
				rebateContext.rebate(orderInfo);
				logger.info("自营订单用户点击确认收货,系统对订单不做修改记录.orderId="+orderId+",userId="+userId);
//				addBorrowBill(orderInfo);
				return resp;
			}else{
				AfOrderDo afOrderDo = new AfOrderDo();
				afOrderDo.setRid(orderId);
				afOrderDo.setStatus("FINISHED");
				afOrderDo.setGmtFinished(new Date());
				if(afOrderService.updateOrder(afOrderDo) > 0){
//					addBorrowBill(orderInfo);
					return resp;
				}
			}
		}else{
			boolean lock =false;
			try {
				lock = bizCacheUtil.getLock(Constants.CACHEKEY_COMPLETEORDER_LOCK+orderId, Constants.CACHEKEY_COMPLETEORDER_LOCK_VALUE);
				if (lock) {
					Boolean flag=true;
					//新增白名单逻辑
					AfResourceDo pushWhiteResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_WHITE.getCode());
					if (pushWhiteResource != null) {
						//白名单开启
						String[] whiteUserIdStrs = pushWhiteResource.getValue3().split(",");
						Long[]  whiteUserIds = (Long[]) ConvertUtils.convert(whiteUserIdStrs, Long.class);
						if(!Arrays.asList(whiteUserIds).contains(userId)){
							//不在白名单不推送
							flag=false;
						}
					}
					if(StringUtils.equals(orderInfo.getOrderType(), OrderType.SELFSUPPORT.getCode())){
						//自营确认收货走返利处理，由于返利在确认收货收货状态之后，所以直接修改为返利成功即可
						rebateContext.rebate(orderInfo);
//						addBorrowBill(orderInfo);
						addBorrowBill_1(orderInfo);
						//实时推送自营分期的债权给钱包
						//防止重复推送
						AfBorrowDo afBorrowDo = afBorrowService.getBorrowByOrderId(orderInfo.getRid());
						if(afBorrowDo==null){
							return resp;
						}
						List<AfRetryTemplDo> afRetryTemplDos = afRetryTemplService.getByBusId(afBorrowDo.getBorrowNo());
						if (afRetryTemplDos == null ||afRetryTemplDos.size() == 0) {
							//没推送过
							AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
							AssetPushType assetPushType = JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue()), AssetPushType.class);
							//浙商维护中逻辑
							Boolean bankIsMaintaining = bankIsMaintaining(assetPushResource);
							if (StringUtil.equals(YesNoStatus.NO.getCode(), assetPushResource.getValue3())&&StringUtil.equals(YesNoStatus.YES.getCode(), assetPushType.getSelfSupport())&&flag&&!bankIsMaintaining){
								//未满额且自营开关开启
								List<EdspayGetCreditRespBo>  pushEdsPayBorrowInfos = riskUtil.pushEdsPayBorrowInfo(afBorrowDo);
								AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoService.getByFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
								//债权实时推送
								boolean result = assetSideEdspayUtil.borrowCashCurPush(pushEdsPayBorrowInfos, afAssetSideInfoDo.getAssetSideFlag(),Constants.ASSET_SIDE_FANBEI_FLAG);
								if (result) {
									logger.info("borrowCurPush suceess,debtType=selfsupport,orderNo="+pushEdsPayBorrowInfos.get(0).getOrderNo());
									//记录push表
									AfBorrowPushDo borrowPush = buildBorrowPush(afBorrowDo.getRid(),pushEdsPayBorrowInfos.get(0).getApr(), pushEdsPayBorrowInfos.get(0).getManageFee());
									afBorrowPushService.saveOrUpdate(borrowPush);
								}
							}
						}
						return resp;
					}else{
						//代买
						if(OrderStatus.DELIVERED.getCode().equals(orderInfo.getStatus())){
							AfOrderDo afOrderDo = new AfOrderDo();
							afOrderDo.setRid(orderId);
							afOrderDo.setStatus(OrderStatus.FINISHED.getCode());
							afOrderDo.setGmtFinished(new Date());
							afOrderDo.setLogisticsInfo("已签收");
							if(afOrderService.updateOrder(afOrderDo) > 0){
//								addBorrowBill(orderInfo);
								addBorrowBill_1(orderInfo);
								//实时推送代买分期的债权给钱包
								AfBorrowDo afBorrowDo = afBorrowService.getBorrowByOrderId(orderInfo.getRid());
								if(afBorrowDo==null){
									return resp;
								}
								List<AfRetryTemplDo> afRetryTemplDos = afRetryTemplService.getByBusId(afBorrowDo.getBorrowNo());
								if (afRetryTemplDos == null ||afRetryTemplDos.size()==0) {
									AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
									AssetPushType assetPushType = JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue()), AssetPushType.class);
									Boolean bankIsMaintaining = bankIsMaintaining(assetPushResource);
									if (StringUtil.equals(YesNoStatus.NO.getCode(), assetPushResource.getValue3())&&StringUtil.equals(YesNoStatus.YES.getCode(), assetPushType.getAgencyBuy())&&flag&&!bankIsMaintaining){
										List<EdspayGetCreditRespBo> pushEdsPayBorrowInfos = riskUtil.pushEdsPayBorrowInfo(afBorrowDo);
										AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoService.getByFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
										//债权实时推送
										boolean result = assetSideEdspayUtil.borrowCashCurPush(pushEdsPayBorrowInfos, afAssetSideInfoDo.getAssetSideFlag(),Constants.ASSET_SIDE_FANBEI_FLAG);
										if (result) {
											logger.info("borrowCurPush suceess,debtType=agencyBuy,orderNo="+pushEdsPayBorrowInfos.get(0).getOrderNo());
											//记录push表
											AfBorrowPushDo borrowPush = buildBorrowPush(afBorrowDo.getRid(),pushEdsPayBorrowInfos.get(0).getApr(), pushEdsPayBorrowInfos.get(0).getManageFee());
											afBorrowPushService.saveOrUpdate(borrowPush);
										}
									}
								}
								return resp;
							}else{
								logger.info("completedAgencyBuyOrder fail,update order fail.orderId="+orderId);
							}
						}else{
							logger.info("completedAgencyBuyOrder fail,order status not support.orderId="+orderId);
						}
					}
				}else{
					logger.error("getlock fail");
					return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.RESUBMIT_ERROR);
				}
			} catch (Exception e) {
				logger.error("completedAgencyBuyOrder fail",e);
			}finally{
				if (lock) {
					bizCacheUtil.delCache(Constants.CACHEKEY_COMPLETEORDER_LOCK+orderId);
				}
			}
	
		}
		return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
	}

	private AfBorrowPushDo buildBorrowPush(Long rid, BigDecimal apr,BigDecimal manageFee) {
		AfBorrowPushDo borrowPush =new AfBorrowPushDo();
		Date now = new Date();
		borrowPush.setGmtCreate(now);
		borrowPush.setGmtModified(now);
		borrowPush.setBorrowId(rid);
		borrowPush.setBorrowRate(apr);
		borrowPush.setProfitRate(manageFee);
		return borrowPush;
	}

	private void addBorrowBill_1(AfOrderDo afOrderDo){
		if(VersionCheckUitl.getVersion()>=VersionCheckUitl.VersionZhangDanSecond) {
			AfBorrowDo afBorrowDo = afBorrowService.getBorrowByOrderId(afOrderDo.getRid());
			if(afBorrowDo !=null && !(afBorrowDo.getStatus().equals(BorrowStatus.CLOSED) || afBorrowDo.getStatus().equals(BorrowStatus.FINISH))) {
				//查询是否己产生
				List<AfBorrowBillDo> borrowList = afBorrowBillService.getAllBorrowBillByBorrowId(afBorrowDo.getRid());
				if (borrowList == null || borrowList.size() == 0) {
					AfBorrowExtendDo _aa = afBorrowExtendDao.getAfBorrowExtendDoByBorrowId(afBorrowDo.getRid());
					if (_aa == null) {
						AfBorrowExtendDo afBorrowExtendDo = new AfBorrowExtendDo();
						afBorrowExtendDo.setId(afBorrowDo.getRid());
						afBorrowExtendDo.setInBill(1);
						afBorrowExtendDao.addBorrowExtend(afBorrowExtendDo);
					} else {
						_aa.setInBill(1);
						afBorrowExtendDao.updateBorrowExtend(_aa);
					}
					List<AfBorrowBillDo> billList = afBorrowService.buildBorrowBillForNewInterest(afBorrowDo, afOrderDo.getPayType());
					for(AfBorrowBillDo _afBorrowExtendDo:billList){
						_afBorrowExtendDo.setStatus("N");
					}
					afBorrowService.addBorrowBill(billList);

					AfUserAccountDo afUserAccountDo = afUserAccountDao.getUserAccountInfoByUserId(afOrderDo.getUserId());
					afBorrowService.updateBorrowStatus(afBorrowDo, afUserAccountDo.getUserName(), afOrderDo.getUserId());
				}
			}
			kafkaSync.syncEvent(afOrderDo.getUserId(), KafkaConstants.SYNC_SCENE_ONE,true);
		}
	}





	/**
	 * v-3.9.1 新增逻辑
	 * @param
	 */
	private void addBorrowBill(AfOrderDo afOrderDo){
//		AfBorrowDo afBorrowDo = afBorrowService.getBorrowByOrderId(afOrderDo.getRid());
//		if(afBorrowDo !=null){
//			//查询是否己产生
//			List<AfBorrowBillDo> borrowList = afBorrowBillService.getAllBorrowBillByBorrowId(afBorrowDo.getRid());
//			if(borrowList == null || borrowList.size()==0 ){
//				List<AfBorrowBillDo> billList = afBorrowService.buildBorrowBillForNewInterest(afBorrowDo, afOrderDo.getPayType());
//				afBorrowService.addBorrowBill(billList);
//			}
//		}
	}
	
	
	
	/**
	   * 锁住目标流水号的还款，防止重复回调
	   */
	  private void lock(String tradeNo) {
	    String key = tradeNo + "_completeOrder";
	        long count = redisTemplate.opsForValue().increment(key, 1);
	        System.out.println("count:"+count);
	        redisTemplate.expire(key, 60, TimeUnit.SECONDS);
	        if (count != 1) {
	            throw new FanbeiException(FanbeiExceptionCode.COMPLETE_ORDER);
	        }
	  }  
	  
	  private void unLock(String tradeNo) {
	    String key = tradeNo + "_completeOrder";
	    redisTemplate.delete(key);
	  }
	  
	private Boolean bankIsMaintaining(AfResourceDo assetPushResource) {
		Boolean bankIsMaintaining=false;
		if (null != assetPushResource && StringUtil.isNotBlank(assetPushResource.getValue4())) {
			String[] split = assetPushResource.getValue4().split(",");
			String maintainStart = split[0];
			String maintainEnd = split[1];
			Date maintainStartDate =DateUtil.parseDate(maintainStart,DateUtil.DATE_TIME_SHORT);
			Date gmtCreateEndDate =DateUtil.parseDate(maintainEnd,DateUtil.DATE_TIME_SHORT);
			 bankIsMaintaining = DateUtil.isBetweenDateRange(new Date(),maintainStartDate,gmtCreateEndDate);
		}
		return bankIsMaintaining;
	}

}
