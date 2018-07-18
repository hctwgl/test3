package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.aassetside.edspay.AssetResponseMessage;
import com.ald.fanbei.api.biz.bo.aassetside.edspay.AssetSideRespBo;
import com.ald.fanbei.api.biz.bo.aassetside.edspay.EdspayGiveBackPayResultReqBo;
import com.ald.fanbei.api.biz.bo.aassetside.edspay.RepaymentPlan;
import com.ald.fanbei.api.biz.bo.assetpush.AssetPushStrategy;
import com.ald.fanbei.api.biz.bo.assetpush.AssetPushSwitchConf;
import com.ald.fanbei.api.biz.bo.assetpush.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.assetpush.ModifiedBorrowInfoVo;
import com.ald.fanbei.api.biz.bo.assetpush.ModifiedRepaymentPlan;
import com.ald.fanbei.api.biz.service.DsedAssetSideInfoService;
import com.ald.fanbei.api.biz.service.DsedCommitRecordService;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanPushService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedNotifyRecordService;
import com.ald.fanbei.api.biz.service.DsedRecordMaxService;
import com.ald.fanbei.api.biz.service.DsedResourceService;
import com.ald.fanbei.api.biz.service.DsedRetryTemplService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AssetPushBusiType;
import com.ald.fanbei.api.common.enums.DsedLoanStatus;
import com.ald.fanbei.api.common.enums.DsedRepeatAssetSideType;
import com.ald.fanbei.api.common.enums.OpenCloseStatus;
import com.ald.fanbei.api.common.enums.PushEdspayResult;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.RetryEventType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiAssetSideRespCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.DsedLoanDao;
import com.ald.fanbei.api.dal.dao.DsedLoanPeriodsDao;
import com.ald.fanbei.api.dal.dao.DsedUserBankcardDao;
import com.ald.fanbei.api.dal.domain.DsedAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.DsedCommitRecordDo;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPushDo;
import com.ald.fanbei.api.dal.domain.DsedNotifyRecordDo;
import com.ald.fanbei.api.dal.domain.DsedRecordMaxDo;
import com.ald.fanbei.api.dal.domain.DsedResourceDo;
import com.ald.fanbei.api.dal.domain.DsedRetryTemplDo;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanDto;
import com.ald.fanbei.api.dal.domain.dto.DsedOverdueInfoDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：和资金方系统调用工具类
 * @author wujun 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("assetSideEdspayUtil")
public class AssetSideEdspayUtil extends AbstractThird {

	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	UpsUtil upsUtil;
	@Resource
	AssetSideEdspayUtil assetSideEdspayUtil;
	@Resource
	DsedLoanDao dsedLoanDao;
	@Resource
	DsedAssetSideInfoService dsedAssetSideInfoService;
	@Resource
	DsedLoanPeriodsDao dsedLoanPeriodsDao;
	@Resource
	DsedResourceService dsedResourceService;
	@Resource
	DsedRetryTemplService dsedRetryTemplService;
	@Resource
	DsedLoanService dsedLoanService;
	@Resource
	DsedLoanPushService dsedLoanPushService;
	@Resource
	DsedUserBankcardDao dsedUserBankcardDao;
	@Resource
	DsedUserService dsedUserService;
	@Resource
	DsedRecordMaxService dsedRecordMaxService;
	@Resource
	DsedLoanPeriodsService dsedLoanPeriodsService;
	@Resource
	DsedNotifyRecordService dsedNotifyRecordService;
	@Resource
	DsedCommitRecordService dsedCommitRecordService;
	

	/**
	 * 债权实时推送接口
	 * @param borrowCashInfo
	 * @param assetSideFlag
	 * @param assetSideFanbeiFlag
	 * @return
	 */
	public boolean dsedCurPush(List<EdspayGetCreditRespBo> borrowCashInfos,String assetSideFlag, String assetSideFanbeiFlag) {
		try {
			String borrowerJson = "";
			EdspayGetCreditRespBo borrowCashInfo=borrowCashInfos.get(0);
			if (borrowCashInfo == null) {
				borrowerJson = JSON.toJSONString("");
			}else{
				borrowerJson = JSON.toJSONString(borrowCashInfos);
			}
			Map<String,String> map = new HashMap<String,String>();
			long sendTime = DateUtil.getCurrSecondTimeStamp();
			DsedResourceDo assideResourceDo = getAssetSideConfigInfo(assetSideFlag);
			String data = AesUtil.encryptToBase64(borrowerJson, assideResourceDo.getValue2());
			String sign = DigestUtil.MD5(borrowerJson);
			map.put("data", data);
			map.put("sendTime", sendTime+"");
			map.put("sign", sign);
			map.put("appId", assetSideFanbeiFlag);
			DsedResourceDo assetPushResource = dsedResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
			AssetPushSwitchConf switchConf =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue1()), AssetPushSwitchConf.class);
			try {
				String url = assideResourceDo.getValue1();
				logger.info("dsedCurPush url = {},oriParam = {}, req = {}",url,borrowerJson,JSONObject.toJSONString(map));
				String respResult = HttpUtil.doHttpPostJsonParam(url+"/p2p/fanbei/dsedDebtPush", JSONObject.toJSONString(map));
				logger.info("dsedCurPush resp = {}", respResult);
				AssetResponseMessage respInfo = JSONObject.parseObject(respResult, AssetResponseMessage.class);
				if (respInfo != null) {
					if (FanbeiAssetSideRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
						logger.info("dsedCurPush resp success,respInfo= "+respInfo.getMessage());
						//推送成功
						//进入查询表
						DsedRetryTemplDo dsedRetryTemplDo =new DsedRetryTemplDo();
						dsedRetryTemplDo.setBusId(borrowCashInfo.getOrderNo());
						dsedRetryTemplDo.setEventType(RetryEventType.QUERY.getCode());
						Date now =new Date();
						dsedRetryTemplDo.setGmtCreate(now);
						AssetPushStrategy strategy =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue2()), AssetPushStrategy.class);
						Integer queryInterval = strategy.getTimeOut();
						Date gmtNext = DateUtil.addMins(now, queryInterval);
						dsedRetryTemplDo.setGmtNext(gmtNext);
						dsedRetryTemplDo.setTimes(0);
						dsedRetryTemplDo.setState("N");
						dsedRetryTemplDo.setGmtModified(now);
						dsedRetryTemplService.saveRecord(dsedRetryTemplDo);
						if (StringUtil.equals(YesNoStatus.YES.getCode(), respInfo.getIsFull())) {
							//钱包满额,更新配置表
							assetPushResource.setValue3(YesNoStatus.YES.getCode());
							assetPushResource.setGmtModified(now);
							dsedResourceService.updateById(assetPushResource);
						}
						return true;
					}else {
						FanbeiAssetSideRespCode failResp = FanbeiAssetSideRespCode.findByCode(respInfo.getCode());
						logger.error("dsedCurPush resp fail,errorCode="+respInfo.getCode()+",respInfo"+respInfo.getMessage());
						if (StringUtil.equals(YesNoStatus.YES.getCode(), switchConf.getRePush())) {
							//重推开关开启
							recordRePush(borrowCashInfo, borrowerJson,assetPushResource);
							return true;
						}else{
							//重推开关关闭
							noRepushHandle(borrowCashInfo, switchConf);
						}
						return false;
					}
				}else{
				    logger.error("dsedCurPush resp null");
				}
				
			} catch (Exception e) {
				if (StringUtil.equals(YesNoStatus.YES.getCode(), switchConf.getRePush())) {
					recordRePush(borrowCashInfo, borrowerJson,assetPushResource);
					return true;
				}else{
					noRepushHandle(borrowCashInfo, switchConf);
				}
			}
		} catch (Exception e) { 
			logger.error("dsedCurPush error="+e);
		}
		return false;
	}

	private void noRepushHandle(EdspayGetCreditRespBo borrowCashInfo,AssetPushSwitchConf switchConf) {
		Date cur = new Date();
		if (borrowCashInfo.getDebtType()==AssetPushBusiType.DSED.getCode()) {
			//都市e贷
			DsedLoanDo dsedLoanDo = dsedLoanService.getByLoanNo(borrowCashInfo.getOrderNo());
			if (null != dsedLoanDo) {
				DsedLoanPushDo loanPushDo = buildLoanPush(dsedLoanDo.getRid(),Constants.ASSET_SIDE_FANBEI_FLAG,PushEdspayResult.PUSHFAIL.getCode());
				dsedLoanPushService.saveOrUpdate(loanPushDo);
				DsedUserDo afUserDo = dsedUserService.getUserById(dsedLoanDo.getUserId());
				DsedUserBankcardDo bankCard = dsedUserBankcardDao.getUserMainBankcardByUserId(dsedLoanDo.getUserId());
				List<DsedLoanPeriodsDo> periodDos = dsedLoanPeriodsDao.listByLoanId(dsedLoanDo.getRid());
				if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPushFail())) {
					//直接关闭
					dsedLoanService.dealLoanFail(dsedLoanDo, periodDos,"推送失败关闭");
				}else{
					// 调用UPS打款
					UpsDelegatePayRespBo upsResult = upsUtil.dsedDelegatePay(dsedLoanDo.getArrivalAmount(),
							afUserDo.getRealName(), bankCard.getBankCardNumber(), afUserDo.getRid().toString(), bankCard.getMobile(),
	                        bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
	                        "DSED_LOAN", dsedLoanDo.getRid().toString(),afUserDo.getIdNumber());
					dsedLoanDo.setTradeNoOut(upsResult.getOrderNo());
					if (!upsResult.isSuccess()) {
						dsedLoanService.dealLoanFail(dsedLoanDo, periodDos, "UPS打款失败，"+upsResult.getRespCode());
						return;
					}
					dsedLoanDo.setStatus(DsedLoanStatus.TRANSFERING.name());
					dsedLoanDao.updateById(dsedLoanDo);
				}
			}
		}
	}

	private void recordRePush(EdspayGetCreditRespBo borrowCashInfo,String borrowerJson, DsedResourceDo assetPushResource) {
		DsedRetryTemplDo dsedRetryTemplDo =new DsedRetryTemplDo();
		dsedRetryTemplDo.setBusId(borrowCashInfo.getOrderNo());
		dsedRetryTemplDo.setEventType(RetryEventType.PUSH.getCode());
		Date now =new Date();
		dsedRetryTemplDo.setGmtCreate(now);
		AssetPushStrategy strategy =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue2()), AssetPushStrategy.class);
		Integer rePushInterval = strategy.getRePushInterval();
		Date gmtNext = DateUtil.addMins(now, rePushInterval);
		dsedRetryTemplDo.setGmtNext(gmtNext);
		dsedRetryTemplDo.setTimes(0);
		dsedRetryTemplDo.setState("N");
		dsedRetryTemplDo.setGmtModified(now);
		dsedRetryTemplDo.setContent(borrowerJson);
		dsedRetryTemplService.saveRecord(dsedRetryTemplDo);
	}

	public AssetSideRespBo giveBackPayResult(String sendTime, String data,String sign, String appId){
		AssetSideRespBo notifyRespBo = new AssetSideRespBo();
		try {
			//获取对应资产方配置信息
			DsedResourceDo assideResourceInfo = getAssetSideConfigInfo(appId);
			if(assideResourceInfo == null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_APPID_ERROR);
				return notifyRespBo;
			}
			//请求时间校验
			Long reqTimeStamp = NumberUtil.objToLongDefault(sendTime,0L);
			int result = DateUtil.judgeDiffTimeStamp(reqTimeStamp,DateUtil.getCurrSecondTimeStamp(),60);
			/*if(result>0){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
				return notifyRespBo;
			}*/
			//签名验证相关值处理
			String realDataJson = "";
			EdspayGiveBackPayResultReqBo payResultReqBo = null;
			try {
				realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
				logger.info("giveBackPayResult,oriParam = {}",realDataJson);
				payResultReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayGiveBackPayResultReqBo.class);
			} catch (Exception e) {
				logger.error("giveBackPayResult parseJosn error,appId="+appId+ ",sendTime=" + sendTime, e);
			}
			if(payResultReqBo==null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.PARSE_JSON_ERROR);
				return notifyRespBo;
			}
			String currSign = DigestUtil.MD5(realDataJson);
			if (!StringUtil.equals(currSign, sign)) {// 验签成功
				//验证签名失败
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_SIGNATURE_ERROR);
				return notifyRespBo;
			}
			//判断是否已经主动查询处理过
			DsedRetryTemplDo dsedRetryTemplDo = dsedRetryTemplService.getByBusIdAndEventType(payResultReqBo.getOrderNo(), RetryEventType.QUERY.getCode());
			if (null != dsedRetryTemplDo) {
				//钱包主动通知之后移除主动查询表，不再主动查询
				dsedRetryTemplService.deleteByBusidAndEventType(payResultReqBo.getOrderNo(),RetryEventType.QUERY.getCode());
				//移出record_max表
				removeRecordMax(payResultReqBo);
				//回传区别现金贷和分期不同处理
				DsedResourceDo assetPushResource = dsedResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
				AssetPushSwitchConf switchConf =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue1()), AssetPushSwitchConf.class);
				Date now = new Date();
				if (payResultReqBo.getDebtType() == AssetPushBusiType.DSED.getCode()){
					//都市e贷
					DsedLoanDo dsedLoanDo = dsedLoanService.getByLoanNo(payResultReqBo.getOrderNo());
					if (null != dsedLoanDo) {
						DsedUserDo afUserDo = dsedUserService.getUserById(dsedLoanDo.getUserId());
						DsedUserBankcardDo bankCard = dsedUserBankcardDao.getUserMainBankcardByUserId(dsedLoanDo.getUserId());
						List<DsedLoanPeriodsDo> periodDos = dsedLoanPeriodsDao.listByLoanId(dsedLoanDo.getRid());
						if (payResultReqBo.getType()==0&&payResultReqBo.getCode()==1){
							//审核失败
							DsedLoanPushDo loanPushDo = buildLoanPush(dsedLoanDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PushEdspayResult.REVIEWFAIL.getCode());
							dsedLoanPushService.saveOrUpdate(loanPushDo);
							if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getReviewFail())) {
								//直接关闭
								dsedLoanService.dealLoanFail(dsedLoanDo, periodDos,"浙商审核失败关闭");
							}else{
								// 调用UPS打款
								UpsDelegatePayRespBo upsResult = upsUtil.dsedDelegatePay(dsedLoanDo.getArrivalAmount(),
										afUserDo.getRealName(), bankCard.getBankCardNumber(), afUserDo.getRid().toString(), bankCard.getMobile(),
				                        bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
				                        "DSED_LOAN", dsedLoanDo.getRid().toString(),afUserDo.getIdNumber());
								dsedLoanDo.setTradeNoOut(upsResult.getOrderNo());
								if (!upsResult.isSuccess()) {
									dsedLoanService.dealLoanFail(dsedLoanDo, periodDos, "UPS打款失败，"+upsResult.getRespCode());
									return notifyRespBo;
								}
								dsedLoanDo.setStatus(DsedLoanStatus.TRANSFERING.name());
								dsedLoanDao.updateById(dsedLoanDo);
							}
						}else if(payResultReqBo.getType()==1&&payResultReqBo.getCode()==1){
							//打款失败
							DsedLoanPushDo loanPushDo = buildLoanPush(dsedLoanDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PushEdspayResult.PAYFAIL.getCode());
							dsedLoanPushService.saveOrUpdate(loanPushDo);
							if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPayFail())) {
								//借款关闭
								dsedLoanService.dealLoanFail(dsedLoanDo, periodDos,"浙商打款失败关闭");
							}else{
								// 调UPS打款
								UpsDelegatePayRespBo upsResult = upsUtil.dsedDelegatePay(dsedLoanDo.getArrivalAmount(),
										afUserDo.getRealName(), bankCard.getBankCardNumber(), afUserDo.getRid().toString(), bankCard.getMobile(),
				                        bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
				                        "DSED_LOAN", dsedLoanDo.getRid().toString(),afUserDo.getIdNumber());
								dsedLoanDo.setTradeNoOut(upsResult.getOrderNo());
								if (!upsResult.isSuccess()) {
									dsedLoanService.dealLoanFail(dsedLoanDo, periodDos, "UPS打款失败，"+upsResult.getRespCode());
									return notifyRespBo;
								}
								dsedLoanDo.setStatus(DsedLoanStatus.TRANSFERING.getDesz());
								dsedLoanDao.updateById(dsedLoanDo);
							}
						}else if(payResultReqBo.getType()==1&&payResultReqBo.getCode()==0){
							//打款成功
							DsedLoanPushDo loanPushDo = buildLoanPush(dsedLoanDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PushEdspayResult.PAYSUCCESS.getCode());
							loanPushDo.setLoanTime(payResultReqBo.getLoanTime());//记录放款时间
							dsedLoanPushService.saveOrUpdate(loanPushDo);
							dsedLoanService.dealLoanSucc(dsedLoanDo.getRid(),"");
						}	
					}
				
				}
			}
		}catch(Exception e){
			logger.error("giveBackPayResult error="+e);
		}
		return notifyRespBo;
	}
	
	/**
	 * 获取资产方配置信息
	 * 如果资产方未启用或者配置未开启，则返回null，否则返回正常配置信息
	 * @param appId
	 * @return
	 */
	private DsedResourceDo getAssetSideConfigInfo(String appId){
		//资产方对应配置信息校验
		DsedResourceDo assideResourceInfo = dsedResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_SIDE_CONFIG.getCode(), appId);
		if(assideResourceInfo==null || !OpenCloseStatus.O.getCode().equals(assideResourceInfo.getValue4()) ){
			return null;
		}
		return assideResourceInfo;
	}


	private void removeRecordMax(EdspayGiveBackPayResultReqBo payResultReqBo) {
		DsedRecordMaxDo recordMaxDo = dsedRecordMaxService.getByBusIdAndEventype(payResultReqBo.getOrderNo(),RetryEventType.QUERY.getCode());
		if (null != recordMaxDo) {
			dsedRecordMaxService.deleteById(recordMaxDo.getRid());
		}
	}
	
	public int queryEdspayApiHandle(String orderNo) {
		try {
			DsedLoanDo dsedLoanDo = dsedLoanService.getByLoanNo(orderNo);
			if (dsedLoanDo!=null) {
				//都市e贷
				dsedLoanService.dealLoanSucc(dsedLoanDo.getRid(),"");
			}	
		} catch (Exception e) {
			logger.error("queryEdspayApiHandle error"+e);
			return 1;
		}
		return 0;
	}

	public int repushMaxApiHandle(String orderNo) {
		try {
			Date now = new Date();
			DsedResourceDo assetPushResource = dsedResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
			AssetPushSwitchConf switchConf =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue1()), AssetPushSwitchConf.class);
			DsedLoanDo dsedLoanDo = dsedLoanService.getByLoanNo(orderNo);
			if (dsedLoanDo!=null) {
				//都市e贷
				DsedLoanPushDo loanPushDo = buildLoanPush(dsedLoanDo.getRid(),Constants.ASSET_SIDE_FANBEI_FLAG,PushEdspayResult.PUSHFAIL.getCode());
				dsedLoanPushService.saveOrUpdate(loanPushDo);
				DsedUserDo afUserDo = dsedUserService.getUserById(dsedLoanDo.getUserId());
				DsedUserBankcardDo bankCard = dsedUserBankcardDao.getUserMainBankcardByUserId(dsedLoanDo.getUserId());
				List<DsedLoanPeriodsDo> periodDos = dsedLoanPeriodsDao.listByLoanId(dsedLoanDo.getRid());
				if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPushFail())) {
					//直接关闭
					dsedLoanService.dealLoanFail(dsedLoanDo, periodDos,"推送钱包最大失败次数关闭");
				}else{
					// 调用UPS打款
					UpsDelegatePayRespBo upsResult = upsUtil.dsedDelegatePay(dsedLoanDo.getArrivalAmount(),
							afUserDo.getRealName(), bankCard.getBankCardNumber(), afUserDo.getRid().toString(), bankCard.getMobile(),
	                        bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
	                        "DSED_LOAN", dsedLoanDo.getRid().toString(),afUserDo.getIdNumber());
					dsedLoanDo.setTradeNoOut(upsResult.getOrderNo());
					if (!upsResult.isSuccess()) {
						dsedLoanService.dealLoanFail(dsedLoanDo, periodDos, "UPS打款失败，"+upsResult.getRespCode());
					}
				}
			}else{
				logger.error("dsed repushMaxApiHandle loanNo not exsit");
				return 1;
			}
		} catch (Exception e) {
			logger.error("dsed repushMaxApiHandle error"+e);
			return 1;
		}
		return 0;
	}

	private DsedLoanPushDo buildLoanPush(Long loanId, String assetSideFlag,String status) {
		DsedLoanPushDo loanPushDo =new DsedLoanPushDo();
		Date now = new Date();
		loanPushDo.setGmtCreate(now);
		loanPushDo.setGmtModified(now);
		loanPushDo.setLoanId(loanId);
		loanPushDo.setAssetSideFlag(assetSideFlag);
		loanPushDo.setStatus(status);
		return loanPushDo;
	}
	
	public List<EdspayGetCreditRespBo> buildDsedBorrowInfo(DsedLoanDo loanDo) {
		List<EdspayGetCreditRespBo> creditRespBos = new ArrayList<EdspayGetCreditRespBo>();
		EdspayGetCreditRespBo creditRespBo = new EdspayGetCreditRespBo();
		//借款人平台逾期信息
		DsedOverdueInfoDto overdueInfo = dsedLoanDao.getOverdueInfoByUserId(loanDo.getUserId());
		//获取资产方的分润利率
		DsedAssetSideInfoDo dsedAssetSideInfoDo = dsedAssetSideInfoService.getByFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
		DsedLoanDto dsedLoanDto = dsedLoanDao.getBorrowInfoById(loanDo.getRid());
		//还款计划
		List<RepaymentPlan> repaymentPlans=new ArrayList<RepaymentPlan>();
		List<DsedLoanPeriodsDo> dsedLoanPeriodsList = dsedLoanPeriodsDao.listByLoanId(loanDo.getRid());
		Date lastBorrowBillGmtPayTime=null;
		for (int i = 0; i < dsedLoanPeriodsList.size(); i++) {
			RepaymentPlan repaymentPlan = new RepaymentPlan();
			repaymentPlan.setRepaymentNo(dsedLoanPeriodsList.get(i).getRid()+"");
			repaymentPlan.setRepaymentTime(DateUtil.getSpecSecondTimeStamp(dsedLoanPeriodsList.get(i).getGmtPlanRepay()));
			repaymentPlan.setRepaymentDays(DateUtil.getNumberOfDayBetween(dsedLoanDto.getLoanStartTime(), dsedLoanPeriodsList.get(i).getGmtPlanRepay()));
			repaymentPlan.setRepaymentAmount(dsedLoanPeriodsList.get(i).getAmount());
			repaymentPlan.setRepaymentInterest(dsedLoanPeriodsList.get(i).getInterestFee());
			repaymentPlan.setRepaymentPeriod(dsedLoanPeriodsList.get(i).getNper()-1);
			repaymentPlans.add(repaymentPlan);
			if (i == dsedLoanPeriodsList.size() - 1) {
				lastBorrowBillGmtPayTime= dsedLoanPeriodsList.get(i).getGmtPlanRepay();
			}
		}
		creditRespBo.setOrderNo(dsedLoanDto.getOrderNo());
		creditRespBo.setUserId(dsedLoanDto.getUserId());
		creditRespBo.setName(dsedLoanDto.getRealName());
		creditRespBo.setCardId(dsedLoanDto.getCardId());
		creditRespBo.setMobile(dsedLoanDto.getMobile());
		creditRespBo.setBankNo(dsedLoanDto.getBankNo());
		creditRespBo.setAcctName("");
		creditRespBo.setMoney(dsedLoanDto.getArrivalAmount());
		creditRespBo.setApr(BigDecimalUtil.multiply(loanDo.getInterestRate(), new BigDecimal(100)));
		creditRespBo.setTimeLimit((int) DateUtil.getNumberOfDayBetween(dsedLoanDto.getLoanStartTime(), lastBorrowBillGmtPayTime));
		creditRespBo.setLoanStartTime(DateUtil.getSpecSecondTimeStamp(dsedLoanDto.getLoanStartTime()));
		if (StringUtil.isNotBlank(dsedLoanDto.getLoanRemark())) {
			creditRespBo.setPurpose(dsedLoanDto.getLoanRemark());
		}else {
			creditRespBo.setPurpose("个人消费");
		}
		creditRespBo.setRepaymentStatus(0);
		creditRespBo.setRepayName(dsedLoanDto.getRealName());
		creditRespBo.setRepayAcct(dsedLoanDto.getBankNo());
		creditRespBo.setRepayAcctBankNo("");
		creditRespBo.setRepayAcctType(0);
		if (StringUtil.equals(dsedLoanDto.getCardName(),"浙商银行")) {
			creditRespBo.setIsRepayAcctOtherBank(0);
		}else{
			creditRespBo.setIsRepayAcctOtherBank(1);
		}
		creditRespBo.setManageFee(dsedAssetSideInfoDo.getAnnualRate());
		if (StringUtil.isNotBlank(dsedLoanDto.getRepayRemark())) {
			creditRespBo.setRepaymentSource(dsedLoanDto.getRepayRemark());
		}else {
			creditRespBo.setRepaymentSource("工资收入");
		}
		creditRespBo.setDebtType(AssetPushBusiType.DSED.getCode());
		creditRespBo.setIsPeriod(1);
		creditRespBo.setTotalPeriod(dsedLoanDto.getPeriods());
		creditRespBo.setLoanerType(0);
		creditRespBo.setOverdueTimes(overdueInfo.getOverdueNums());
		creditRespBo.setOverdueAmount(overdueInfo.getOverdueAmount());
		creditRespBo.setRepaymentPlans(repaymentPlans);
		creditRespBo.setIsCur(0);
		creditRespBos.add(creditRespBo);
		return creditRespBos;
	}
	
	/**
	 * 发送债权更新信息给资产方
	 * @param modifiedBorrowInfoList
	 * @param assetSideFlag
	 * @param platFlag
	 * @return
	 */
	public boolean transModifiedBorrowInfo(List<ModifiedBorrowInfoVo> modifiedBorrowInfoList,String assetSideFlag,String platFlag) {
		try {
			String borrowJson = "";
			DsedResourceDo afResourceDo = dsedResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_SIDE_CONFIG.getCode(), assetSideFlag);
			if (modifiedBorrowInfoList == null || modifiedBorrowInfoList.isEmpty()) {
				borrowJson = JSON.toJSONString("");
			}else{
				borrowJson = JSON.toJSONString(modifiedBorrowInfoList);
			}
			Map<String,String> map = new HashMap<String,String>();
			//时间戳
			long sendTime = DateUtil.getCurrSecondTimeStamp();
			//传输的data数据
			String data = AesUtil.encryptToBase64(borrowJson, afResourceDo.getValue2());
			//签名
			String sign = DigestUtil.MD5(borrowJson);
			map.put("data", data);
			map.put("sendTime", sendTime+"");
			map.put("sign", sign);
			map.put("appId", platFlag);
			String url = afResourceDo.getValue1();
			String jsonParam = JSON.toJSONString(map);
			//记录通知记录
			DsedNotifyRecordDo notifyRecord = new DsedNotifyRecordDo(); 
			Date cur = new Date();
			notifyRecord.setGmtCreate(cur);
			notifyRecord.setGmtModified(cur);
			notifyRecord.setOrderNo(modifiedBorrowInfoList.get(0).getOrderNo());
			notifyRecord.setUserId(modifiedBorrowInfoList.get(0).getUserId());
			notifyRecord.setAssetSide(Constants.ASSET_SIDE_EDSPAY_FLAG);
			dsedNotifyRecordService.saveRecord(notifyRecord);
			logger.info("transModifiedBorrowInfo url = {},oriParam = {}, req = {}",url,borrowJson,jsonParam);
			String respResult = HttpUtil.doHttpPostJsonParam(url+"/p2p/fanbei/debtOrderInfoPush", jsonParam);
			logger.info("transModifiedBorrowInfo resp  = {}", respResult);
			if (StringUtil.isBlank(respResult)) {
				logger.error("transModifiedBorrowInfo resp null,");
				return false;
			}else {
				AssetResponseMessage respInfo = JSONObject.parseObject(respResult, AssetResponseMessage.class);
				if (FanbeiAssetSideRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
					logger.info("transModifiedBorrowInfo  resp success"+",respInfo"+respInfo.getMessage());
					return true;
				}else {
					//三方处理错误
					FanbeiAssetSideRespCode failResp = FanbeiAssetSideRespCode.findByCode(respInfo.getCode());
					logger.error("transBorrowerInfo resp fail"+",errorCode="+respInfo.getCode()+",errorInfo"+failResp.getDesc());
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("transBorrowerInfo error=",e);
		}
		return false;
	}
	
	public Boolean isPush(Object obj) {
		String borrowNo = null;
		if (obj instanceof DsedLoanDo) {
			DsedLoanDo dsedLoanDo = (DsedLoanDo) obj;
			borrowNo=dsedLoanDo.getLoanNo();
		}
		DsedRetryTemplDo dsedRetryTemplDo = dsedRetryTemplService.getCurPushDebt(borrowNo,RetryEventType.QUERY.getCode());
		if (dsedRetryTemplDo!=null) {
			return true;
		}
		return false;
	}
	
	public List<ModifiedBorrowInfoVo> buildModifiedInfo(Object obj, Integer RepaymentStatus) {
		List<ModifiedBorrowInfoVo> modifiedDebtList= new ArrayList<ModifiedBorrowInfoVo>();
		ModifiedBorrowInfoVo modifiedDebt = new ModifiedBorrowInfoVo();
		List<ModifiedRepaymentPlan> repaymentPlanList=new ArrayList<ModifiedRepaymentPlan>();
		if (obj instanceof DsedLoanDo) {
			DsedLoanDo loanDo = (DsedLoanDo) obj;
			modifiedDebt.setUserId(loanDo.getUserId());
			modifiedDebt.setOrderNo(loanDo.getLoanNo());
			modifiedDebt.setIsPeriod(1);
			List<DsedLoanPeriodsDo> loanPeriodsList = dsedLoanPeriodsService.listByLoanId(loanDo.getRid());
			for (DsedLoanPeriodsDo   dsedLoanPeriodsDo : loanPeriodsList) {
				 ModifiedRepaymentPlan modifiedRepaymentPlan = new ModifiedRepaymentPlan();
				 modifiedRepaymentPlan.setRepaymentNo(dsedLoanPeriodsDo.getRid()+"");
				 modifiedRepaymentPlan.setRepaymentAmount(dsedLoanPeriodsDo.getAmount());
				 modifiedRepaymentPlan.setRepaymentInterest(dsedLoanPeriodsDo.getInterestFee());
				 modifiedRepaymentPlan.setRepaymentStatus(RepaymentStatus);
				 modifiedRepaymentPlan.setRepaymentYesTime((int) DateUtil.getSpecSecondTimeStamp(new Date()));
				 modifiedRepaymentPlan.setIsOverdue(0);
				 modifiedRepaymentPlan.setIsPrepayment(1);
				 modifiedRepaymentPlan.setRepaymentPeriod(dsedLoanPeriodsDo.getNper());
				 repaymentPlanList.add(modifiedRepaymentPlan);
			}
		}
		modifiedDebt.setRepaymentPlans(repaymentPlanList);
		modifiedDebtList.add(modifiedDebt);
		return modifiedDebtList;
	}
	
	public void transFailRecord(Object obj,List<ModifiedBorrowInfoVo> modifiedInfo) {
		Long id = null;
		if (obj instanceof DsedLoanDo) {
			DsedLoanDo loanDo = (DsedLoanDo) obj;
			id = loanDo.getRid();
		}
		thirdLog.error("trans modified dsed loan Info fail,borrowId ="+id);
		Date cur = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String commitTime = sdf.format(cur);
		DsedCommitRecordDo afCommitRecordDo = new DsedCommitRecordDo();
		afCommitRecordDo.setGmtCreate(cur);
		afCommitRecordDo.setGmtModified(cur);
		afCommitRecordDo.setType(DsedRepeatAssetSideType.PREFINISH_NOTICE_ASSETSIDE.getCode());
		afCommitRecordDo.setRelate_id(id+"");
		DsedResourceDo afResourceDo = dsedResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_SIDE_CONFIG.getCode(), Constants.ASSET_SIDE_EDSPAY_FLAG);
		String borrowJson = "";
		if (modifiedInfo == null || modifiedInfo.isEmpty()) {
			borrowJson = JSON.toJSONString("");
		}else{
			borrowJson = JSON.toJSONString(modifiedInfo);
		}
		afCommitRecordDo.setContent(borrowJson);
		afCommitRecordDo.setUrl(afResourceDo.getValue1()+"/p2p/fanbei/debtOrderInfoPush");
		afCommitRecordDo.setCommit_time(commitTime);
		afCommitRecordDo.setCommit_num(1);
		dsedCommitRecordService.addRecord(afCommitRecordDo);
	}
}
