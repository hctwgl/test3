package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.bo.QueryEdspayApiHandleReqBo;
import com.ald.fanbei.api.biz.bo.assetpush.AssetPushStrategy;
import com.ald.fanbei.api.biz.bo.assetpush.AssetPushSwitchConf;
import com.ald.fanbei.api.biz.bo.assetside.AssetSideRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.AssetResponseMessage;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayBackCreditReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayCreditDetailInfo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditDayLimit;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetPlatUserInfoReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetPlatUserInfoRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGiveBackPayResultReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.FanbeiBorrowBankInfoBo;
import com.ald.fanbei.api.biz.service.AfAssetPackageDetailService;
import com.ald.fanbei.api.biz.service.AfAssetSideInfoService;
import com.ald.fanbei.api.biz.service.AfBorrowCashPushService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.AfBorrowPushService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfRetryTemplService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.ApplyLegalBorrowCashService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.PushEdspayResult;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.RetryEventType;
import com.ald.fanbei.api.common.enums.RiskReviewStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiAssetSideRespCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDao;
import com.ald.fanbei.api.dal.dao.AfAssetSideInfoDao;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashPushDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfBorrowPushDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfRetryTemplDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：和资产方系统调用工具类
 * @author chengkang 2017年11月29日 16:55:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("assetSideEdspayUtil")
public class AssetSideEdspayUtil extends AbstractThird {

	@Resource
    AfResourceService afResourceService;
	@Resource
	AfAssetSideInfoDao afAssetSideInfoDao;
	@Resource
	AfAssetPackageDao afAssetPackageDao;
	@Resource
	AfAssetPackageDetailService afAssetPackageDetailService;
	@Resource
	AfRetryTemplService afRetryTemplService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	ApplyLegalBorrowCashService applyLegalBorrowCashService;
	@Resource
	AfBorrowLegalOrderService afBorrowLegalOrderService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
	@Resource
	AfBorrowCashPushService afBorrowCashPushService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfBorrowPushService afBorrowPushService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfAssetSideInfoService afAssetSideInfoService;
	@Resource
	AssetSideEdspayUtil assetSideEdspayUtil;
	
	public AssetSideRespBo giveBackCreditInfo(String timestamp, String data, String sign, String appId) {
		// 响应数据,默认成功
		AssetSideRespBo notifyRespBo = new AssetSideRespBo();
		try {
			//获取对应资产方配置信息
			AfResourceDo assideResourceInfo = getAssetSideConfigInfo(appId);
			if(assideResourceInfo == null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_APPID_ERROR);
				return notifyRespBo;
			}
			//资产方及启用状态校验
			AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoDao.getByAssetSideFlag(appId);
			if(afAssetSideInfoDo==null || YesNoStatus.NO.getCode().equals(afAssetSideInfoDo.getStatus()) ){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.ASSET_SIDE_FROZEN);
				return notifyRespBo;
			}
			//请求时间校验
			Long reqTimeStamp = NumberUtil.objToLongDefault(timestamp,0L);
			int result = DateUtil.judgeDiffTimeStamp(reqTimeStamp,DateUtil.getCurrSecondTimeStamp(),60);
			if(result>0){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
				return notifyRespBo;
			}
			//签名验证相关值处理
			String realDataJson = "";
			EdspayBackCreditReqBo edspayBackCreditReqBo  = null;
			try {
				realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
				edspayBackCreditReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayBackCreditReqBo.class);
			} catch (Exception e) {
				logger.error("EdspayController giveBackCreditInfo parseJosn error", e);
			}finally{
				logger.info("EdspayController giveBackCreditInfo,appId="+appId+ ",reqJsonData=" + realDataJson + ",sendTime=" + timestamp);
			}
			if(edspayBackCreditReqBo==null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.PARSE_JSON_ERROR);
				return notifyRespBo;
			}
			
			String currSign = DigestUtil.MD5(realDataJson);
			if (!StringUtil.equals(currSign, sign)) {// 验签成功
				//验证签名失败
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_SIGNATURE_ERROR);
				return notifyRespBo;
			}
			
			//签名成功,业务处理
			List<String> orderNos = edspayBackCreditReqBo.getOrderNos();
			if(orderNos==null || orderNos.size()==0 || orderNos.size()>100){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			Integer debtType = edspayBackCreditReqBo.getDebtType();
			
			//具体撤回操作
			int resultValue = afAssetPackageDetailService.batchGiveBackCreditInfo(afAssetSideInfoDo,orderNos,debtType);
			if(resultValue !=1){
				logger.error("EdspayController giveBackCreditInfo exist error records,appId="+appId+ ",sendTime=" + timestamp);
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.APPLICATION_ERROR);
				return notifyRespBo;
			}
			notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.SUCCESS);
		} catch (Exception e) {
			//系统异常
			logger.error("EdspayController giveBackCreditInfo error,appId="+appId+ ",sendTime=" + timestamp, e);
			notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.APPLICATION_ERROR);
		}
		return notifyRespBo;
	}
	
	/**
	 * 获取债权信息
	 * @param timestamp
	 * @param data
	 * @param sign
	 * @param appIds
	 * @return
	 */
	public AssetSideRespBo getBatchCreditInfo(String timestamp, String data, String sign, String appId) {
		// 响应数据,默认成功
		AssetSideRespBo notifyRespBo = new AssetSideRespBo();
		try {
			//获取对应资产方配置信息
			AfResourceDo assideResourceInfo = getAssetSideConfigInfo(appId);
			if(assideResourceInfo == null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_APPID_ERROR);
				return notifyRespBo;
			}
			//资产方及启用状态校验
			AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoDao.getByAssetSideFlag(appId);
			if(afAssetSideInfoDo==null || YesNoStatus.NO.getCode().equals(afAssetSideInfoDo.getStatus()) ){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.ASSET_SIDE_FROZEN);
				return notifyRespBo;
			}
			
			//请求时间校验
			Long reqTimeStamp = NumberUtil.objToLongDefault(timestamp,0L);
			int result = DateUtil.judgeDiffTimeStamp(reqTimeStamp,DateUtil.getCurrSecondTimeStamp(),60);
			if(result>0){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
				return notifyRespBo;
			}
			//签名验证相关值处理
			String realDataJson = "";
			EdspayGetCreditReqBo edspayGetCreditReqBo = null;
			try {
				realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
				edspayGetCreditReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayGetCreditReqBo.class);
			} catch (Exception e) {
				logger.error("EdspayController getBatchCreditInfo parseJosn error,appId="+appId+ ",sendTime=" + timestamp, e);
			}finally{
				logger.info("EdspayController getBatchCreditInfo,appId="+appId+ ",reqJsonData=" + realDataJson + ",sendTime=" + timestamp);
			}
			if(edspayGetCreditReqBo==null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.PARSE_JSON_ERROR);
				return notifyRespBo;
			}
			
			String currSign = DigestUtil.MD5(realDataJson);
			if (!StringUtil.equals(currSign, sign)) {// 验签成功
				//验证签名失败
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_SIGNATURE_ERROR);
				return notifyRespBo;
			}
			
			//签名成功,业务处理
			if(NumberUtil.isNullOrZeroOrNegative(edspayGetCreditReqBo.getMoney())){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			
			//校验当日限额
			int debtType = NumberUtil.objToIntDefault(edspayGetCreditReqBo.getDebtType(), 0);
			BigDecimal currDayHaveGetTotalBorrowAmount = afAssetPackageDao.getCurrDayHaveGetTotalBorrowAmount(afAssetSideInfoDo.getRid());
			BigDecimal currDayHaveGetTotalBorrowCashAmount = afAssetPackageDao.getCurrDayHaveGetTotalBorrowCashAmount(afAssetSideInfoDo.getRid());
			EdspayGetCreditDayLimit dayLimit = JSON.toJavaObject(JSON.parseObject(assideResourceInfo.getValue3()), EdspayGetCreditDayLimit.class);
			if (debtType == 1 && dayLimit != null && dayLimit.getBorrowDayLimit().compareTo(currDayHaveGetTotalBorrowAmount.add(edspayGetCreditReqBo.getMoney()))<=0) {
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.CREDIT_BORROW_AMOUNT_OVERRUN);
				return notifyRespBo;
			}
			if (debtType == 0 && dayLimit != null && dayLimit.getBorrowCashDayLimit().compareTo(currDayHaveGetTotalBorrowCashAmount.add(edspayGetCreditReqBo.getMoney()))<=0) {
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.CREDIT_BORROWCASH_AMOUNT_OVERRUN);
				return notifyRespBo;
			}
			
			Date nowDate = new Date();
			Date startTime = DateUtil.getSpecDateBySecondDefault(edspayGetCreditReqBo.getLoanStartTime(),DateUtil.getStartOfDate(nowDate));
			Date endTime = DateUtil.getSpecDateBySecondDefault(edspayGetCreditReqBo.getLoanEndTime(),DateUtil.getEndOfDate(nowDate));
			BigDecimal minMoney = null;//借款期限的较小的 
			BigDecimal maxMoney = null;//借款期限的较大的 
			EdspayCreditDetailInfo detailInfo = edspayGetCreditReqBo.getCreditDetails();
			if(detailInfo != null && !NumberUtil.isNullOrZeroOrNegative(detailInfo.getMinMoney()) && !NumberUtil.isNullOrZeroOrNegative(detailInfo.getMaxMoney())){
				minMoney = detailInfo.getMinMoney();
				maxMoney = detailInfo.getMaxMoney();
			}
			if(minMoney!=null && maxMoney!=null && minMoney.add(maxMoney).compareTo(edspayGetCreditReqBo.getMoney())!=0){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			
			//获取开户行信息
			FanbeiBorrowBankInfoBo bankInfo = getAssetSideBankInfo(getAssetSideBankInfo());
			if(bankInfo==null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_APPID_ERROR);
				return notifyRespBo;
			}
			//具体获取债权明细(区分现金贷和消费分期)
			List<EdspayGetCreditRespBo> creditInfoList=new ArrayList<EdspayGetCreditRespBo>();
			if (debtType == 1) {
				//消费分期
				creditInfoList = afAssetPackageDetailService.getBorrowBatchCreditInfo(bankInfo,afAssetSideInfoDo,edspayGetCreditReqBo.getMoney(), startTime, endTime);
			}else{
				//现金贷
				creditInfoList = afAssetPackageDetailService.getBorrowCashBatchCreditInfo(bankInfo,afAssetSideInfoDo,edspayGetCreditReqBo.getMoney(), startTime, endTime, minMoney);
			}
			if(creditInfoList!=null && creditInfoList.size()>0){
				String sourceJsonStr = JSON.toJSONString(creditInfoList);
				logger.info("EdspayController getBatchCreditInfo,appId="+appId+ ",returnJsonData=" + sourceJsonStr + ",sendTime=" + timestamp);
				notifyRespBo.setData(AesUtil.encryptToBase64(sourceJsonStr, assideResourceInfo.getValue2()));;
			}else{
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.ASSET_SIDE_AMOUNT_NOTENOUGH);
				return notifyRespBo;
			}
		} catch (Exception e) {
			//系统异常
			logger.error("EdspayController getBatchCreditInfo error,appId="+appId+ ",sendTime=" + timestamp, e);
			notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.APPLICATION_ERROR);
		}
		return notifyRespBo;
	}
	
	/**
	 * 获取债权对应的用户信息接口
	 * @param sendTime
	 * @param data
	 * @param sign
	 * @param appId
	 * @return
	 */
	public AssetSideRespBo getPlatUserInfo(String timestamp, String data,
			String sign, String appId) {
		// 响应数据,默认成功
		AssetSideRespBo notifyRespBo = new AssetSideRespBo();
		try {
			//获取对应资产方配置信息
			AfResourceDo assideResourceInfo = getAssetSideConfigInfo(appId);
			if(assideResourceInfo == null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_APPID_ERROR);
				return notifyRespBo;
			}
			//资产方及启用状态校验
			AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoDao.getByAssetSideFlag(appId);
			if(afAssetSideInfoDo==null || YesNoStatus.NO.getCode().equals(afAssetSideInfoDo.getStatus()) ){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.ASSET_SIDE_FROZEN);
				return notifyRespBo;
			}
			
			//请求时间校验
			Long reqTimeStamp = NumberUtil.objToLongDefault(timestamp,0L);
			int result = DateUtil.judgeDiffTimeStamp(reqTimeStamp,DateUtil.getCurrSecondTimeStamp(),60);
			if(result>0){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
				return notifyRespBo;
			}
			//签名验证相关值处理
			String realDataJson = "";
			EdspayGetPlatUserInfoReqBo edspayGetPlatUserInfoReqBo = null;
			try {
				realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
				edspayGetPlatUserInfoReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayGetPlatUserInfoReqBo.class);
			} catch (Exception e) {
				logger.error("EdspayController getPlatUserInfo parseJosn error,appId="+appId+ ",sendTime=" + timestamp, e);
			}finally{
				logger.info("EdspayController getPlatUserInfo,appId="+appId+ ",reqJsonData=" + realDataJson + ",sendTime=" + timestamp);
			}
			if(edspayGetPlatUserInfoReqBo==null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.PARSE_JSON_ERROR);
				return notifyRespBo;
			}
			
			String currSign = DigestUtil.MD5(realDataJson);
			if (!StringUtil.equals(currSign, sign)) {// 验签成功
				//验证签名失败
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_SIGNATURE_ERROR);
				return notifyRespBo;
			}
			
			//签名成功,业务处理
			if(edspayGetPlatUserInfoReqBo.getOrderNos()==null || edspayGetPlatUserInfoReqBo.getOrderNos().size()==0){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			//具体获取用户相关信息
			List<EdspayGetPlatUserInfoRespBo> userInfoList = afAssetPackageDetailService.getBatchPlatUserInfo(afAssetSideInfoDo,edspayGetPlatUserInfoReqBo.getOrderNos());
			if(userInfoList!=null && userInfoList.size()>0){
				String sourceJsonStr = JSON.toJSONString(userInfoList);
				logger.info("EdspayController getPlatUserInfo,appId="+appId+ ",returnJsonData=" + sourceJsonStr + ",sendTime=" + timestamp);
				notifyRespBo.setData(AesUtil.encryptToBase64(sourceJsonStr, assideResourceInfo.getValue2()));;
			}else{
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.GEN_RETURN_MSG_ERROR);
				return notifyRespBo;
			}
		} catch (Exception e) {
			//系统异常
			logger.error("EdspayController getPlatUserInfo error,appId="+appId+ ",sendTime=" + timestamp, e);
			notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.APPLICATION_ERROR);
		}
		return notifyRespBo;
	}
	
	/**
	 * 获取资产方配置信息
	 * 如果资产方未启用或者配置未开启，则返回null，否则返回正常配置信息
	 * @param appId
	 * @return
	 */
	private AfResourceDo getAssetSideConfigInfo(String appId){
		//资产方对应配置信息校验
		AfResourceDo assideResourceInfo = afResourceService.getConfigByTypesAndSecType(AfResourceType.ASSET_SIDE_CONFIG.getCode(), appId);
		if(assideResourceInfo==null || !AfCounponStatus.O.getCode().equals(assideResourceInfo.getValue4()) ){
			return null;
		}
		return assideResourceInfo;
	}
	
	/**
	 * 获取资产方开户行信息
	 * @param assetSideFlag
	 * @return
	 */
	public List<FanbeiBorrowBankInfoBo> getAssetSideBankInfo() {
		List<FanbeiBorrowBankInfoBo> bankInfoList = new ArrayList<FanbeiBorrowBankInfoBo>();
		try {
			List<AfResourceDo> bankInfoLists = afResourceService.getConfigsByTypesAndSecType(AfResourceType.ASSET_SIDE_CONFIG.getCode(), AfResourceSecType.ASSET_SIDE_CONFIG_BANK_INFOS.getCode());
			if(bankInfoLists==null){
				return bankInfoList;
			}
			
			for (AfResourceDo afResourceDo : bankInfoLists) {
				bankInfoList.add(JSON.toJavaObject(JSON.parseObject(afResourceDo.getValue()), FanbeiBorrowBankInfoBo.class));
			}
		} catch (Exception e) {
			logger.error("getAssetSideBankInfo error,e=",e);
		}
		return bankInfoList;
	}
	
	/**
	 * 获取随机开户行对象
	 * @return
	 */
	public FanbeiBorrowBankInfoBo getAssetSideBankInfo(List<FanbeiBorrowBankInfoBo> bankInfoList) {
		if(bankInfoList == null || bankInfoList.size() == 0){
			return null;
		}
		Collections.shuffle(bankInfoList);
		return bankInfoList.get(0);
	}

	/**
	 * 债权实时推送接口
	 * @param borrowCashInfo
	 * @param assetSideFlag
	 * @param assetSideFanbeiFlag
	 * @return
	 */
	public boolean borrowCashCurPush(List<EdspayGetCreditRespBo> borrowCashInfos,String assetSideFlag, String assetSideFanbeiFlag) {
		//发送的资产包信息
		try {
			String borrowerJson = "";
			if (StringUtil.isBlank(assetSideFlag)) {
				logger.error("borrowCashCurPush fail:assetSideFlag is null");
				return false;
			}
			EdspayGetCreditRespBo borrowCashInfo=borrowCashInfos.get(0);
			if (borrowCashInfo == null) {
				borrowerJson = JSON.toJSONString("");
			}else{
				borrowerJson = JSON.toJSONString(borrowCashInfos);
			}
			Map<String,String> map = new HashMap<String,String>();
			//时间戳
			long sendTime = DateUtil.getCurrSecondTimeStamp();
			//获取对应资产方配置信息
			AfResourceDo assideResourceInfo = getAssetSideConfigInfo(assetSideFlag);
			//传输的data数据
			String data = AesUtil.encryptToBase64(borrowerJson, assideResourceInfo.getValue2());
			//签名
			String sign = DigestUtil.MD5(borrowerJson);
			map.put("data", data);
			map.put("sendTime", sendTime+"");
			map.put("sign", sign);
			map.put("appId", assetSideFanbeiFlag);
			//发送前数据打印
			logger.info("borrowCashCurPush originBorrowerJson"+borrowerJson+",data="+data+",sendTime="+sendTime+",sign="+sign+",appId="+assetSideFanbeiFlag);
			AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
			AssetPushSwitchConf switchConf =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue1()), AssetPushSwitchConf.class);
			try {
				//推送数据给钱包
				System.out.println(assideResourceInfo.getValue1()+"/p2p/fanbei/debtPush");
				String respResult = HttpUtil.doHttpPostJsonParam(assideResourceInfo.getValue1()+"/p2p/fanbei/debtPush", JSONObject.toJSONString(map));
				logger.info("borrowCashCurPush jsonParam  = {}, respResult = {}", JSONObject.toJSONString(map), respResult);
				AssetResponseMessage respInfo = JSONObject.parseObject(respResult, AssetResponseMessage.class);
				if (FanbeiAssetSideRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
					try {
						//推送成功
						logger.info("borrowCashCurPush success,respInfo:"+respInfo.getMessage());
						//进入查询表
						AfRetryTemplDo afRetryTemplDo =new AfRetryTemplDo();
						afRetryTemplDo.setBusId(borrowCashInfo.getOrderNo());
						afRetryTemplDo.setEventType(RetryEventType.QUERY.getCode());
						Date now =new Date();
						afRetryTemplDo.setGmtCreate(now);
						AssetPushStrategy strategy =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue2()), AssetPushStrategy.class);
						Integer queryInterval = strategy.getTimeOut();
						Date gmtNext = DateUtil.addMins(now, queryInterval);
						afRetryTemplDo.setGmtNext(gmtNext);
						afRetryTemplDo.setTimes(0);
						afRetryTemplDo.setState("N");
						afRetryTemplDo.setGmtModify(now);
						afRetryTemplService.saveRecord(afRetryTemplDo);
						if (StringUtil.equals(YesNoStatus.YES.getCode(), respInfo.getIsFull())) {
							//钱包满额,更新配置表
							assetPushResource.setValue3(YesNoStatus.YES.getCode());
							afResourceService.editResource(assetPushResource);
						}
						
					} catch (Exception e) {
						logger.error("borrowCashCurPush fail:"+e);
					}
					
					return true;
				}else {
					//钱包处理错误
					FanbeiAssetSideRespCode failResp = FanbeiAssetSideRespCode.findByCode(respInfo.getCode());
					logger.error("borrowCashCurPush resp fail,errorCode="+respInfo.getCode()+",errorInfo"+(failResp!=null?failResp.getDesc():""));
					if (StringUtil.equals(YesNoStatus.YES.getCode(), switchConf.getRePush())) {
						//重推开关开启
						recordRePush(borrowCashInfo, borrowerJson,assetPushResource);
					}else{
						//重推开关关闭
						noRepushHandle(borrowCashInfo, switchConf);
					}
					return false;
				}
			} catch (Exception e) {
				if (StringUtil.equals(YesNoStatus.YES.getCode(), switchConf.getRePush())) {
					recordRePush(borrowCashInfo, borrowerJson,assetPushResource);
				}else{
					noRepushHandle(borrowCashInfo, switchConf);
				}
			}
		} catch (Exception e) {
			logger.error("borrowCashCurPush exception"+e);
		}
		return false;
	}

	private void noRepushHandle(EdspayGetCreditRespBo borrowCashInfo,AssetPushSwitchConf switchConf) {
		//区分现金贷和分期分别处理
		if (borrowCashInfo.getDebtType()==0) {
			//现金贷
			AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(borrowCashInfo.getOrderNo());
			AfBorrowLegalOrderDo afBorrowLegalOrderDo =	afBorrowLegalOrderService.getBorrowLegalOrderByBorrowId(borrowCashDo.getRid());
			AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(borrowCashDo.getUserId());
			if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPushFail())) {
				//推送失败不调ups
				//更新借款状态
				AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
				delegateBorrowCashDo.setRid(borrowCashDo.getRid());
				delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
				delegateBorrowCashDo.setRemark("推送失败关闭");
				// 更新订单状态为关闭
				afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
				afBorrowLegalOrderDo.setClosedDetail("推送失败关闭");
				afBorrowLegalOrderDo.setGmtClosed(new Date());
				applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
				//维护拓展表
				AfBorrowCashPushDo afBorrowCashPushDo = new AfBorrowCashPushDo();
				Date now = new Date();
				afBorrowCashPushDo.setGmtCreate(now);
				afBorrowCashPushDo.setGmtModified(now);
				afBorrowCashPushDo.setBorrowCashId(borrowCashDo.getRid());
				afBorrowCashPushDo.setAssetSideFlag(Constants.ASSET_SIDE_FANBEI_FLAG);
				afBorrowCashPushDo.setStatus(PushEdspayResult.PUSHFAIL.getCode());
				afBorrowCashPushService.saveRecord(afBorrowCashPushDo);
			}else{
				//调ups打款
				applyLegalBorrowCashService.delegatePay(borrowCashDo.getUserId()+"", borrowCashDo.getRishOrderNo(),
						"10", afBorrowLegalOrderDo, mainCard, borrowCashDo);
			}
		}else{
			//分期
			//维护拓展表
			AfBorrowDo borrowDo = afBorrowService.getBorrowInfoByBorrowNo(borrowCashInfo.getOrderNo());
			AfBorrowPushDo borrowPushDo = new AfBorrowPushDo();
			Date now = new Date();
			borrowPushDo.setGmtCreate(now);
			borrowPushDo.setGmtModified(now);
			borrowPushDo.setBorrowId(borrowDo.getRid());
			borrowPushDo.setAssetSideFlag(Constants.ASSET_SIDE_FANBEI_FLAG);
			borrowPushDo.setStatus(PushEdspayResult.PUSHFAIL.getCode());
			afBorrowPushService.saveRecord(borrowPushDo);
		}
	}

	private void recordRePush(EdspayGetCreditRespBo borrowCashInfo,
			String borrowerJson, AfResourceDo assetPushResource) {
		AfRetryTemplDo afRetryTemplDo =new AfRetryTemplDo();
		afRetryTemplDo.setBusId(borrowCashInfo.getOrderNo());
		afRetryTemplDo.setEventType(RetryEventType.PUSH.getCode());
		Date now =new Date();
		afRetryTemplDo.setGmtCreate(now);
		AssetPushStrategy strategy =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue2()), AssetPushStrategy.class);
		Integer rePushInterval = strategy.getRePushInterval();
		Date gmtNext = DateUtil.addMins(now, rePushInterval);
		afRetryTemplDo.setGmtNext(gmtNext);
		afRetryTemplDo.setTimes(0);
		afRetryTemplDo.setState("N");
		afRetryTemplDo.setGmtModify(now);
		afRetryTemplDo.setContent(borrowerJson);
		afRetryTemplService.saveRecord(afRetryTemplDo);
	}

	public AssetSideRespBo giveBackPayResult(String sendTime, String data,String sign, String appId){
		// 响应数据,默认成功
		AssetSideRespBo notifyRespBo = new AssetSideRespBo();
		try {
			//获取对应资产方配置信息
			AfResourceDo assideResourceInfo = getAssetSideConfigInfo(appId);
			if(assideResourceInfo == null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_APPID_ERROR);
				return notifyRespBo;
			}
			//请求时间校验
			/*Long reqTimeStamp = NumberUtil.objToLongDefault(sendTime,0L);
			int result = DateUtil.judgeDiffTimeStamp(reqTimeStamp,DateUtil.getCurrSecondTimeStamp(),60);
			if(result>0){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
				return notifyRespBo;
			}*/
			//签名验证相关值处理
			String realDataJson = "";
			EdspayGiveBackPayResultReqBo PayResultReqBo = null;
			try {
				realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
				PayResultReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayGiveBackPayResultReqBo.class);
			} catch (Exception e) {
				logger.error("EdspayController getBatchCreditInfo parseJosn error,appId="+appId+ ",sendTime=" + sendTime, e);
			}
			if(PayResultReqBo==null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.PARSE_JSON_ERROR);
				return notifyRespBo;
			}
			String currSign = DigestUtil.MD5(realDataJson);
			if (!StringUtil.equals(currSign, sign)) {// 验签成功
				//验证签名失败
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_SIGNATURE_ERROR);
				return notifyRespBo;
			}
			//钱包主动通知之后移除主动查询表，不再主动查询
			afRetryTemplService.deleteByBusidAndEventType(PayResultReqBo.getOrderNo(),RetryEventType.QUERY.getCode());
			//业务处理
			//回传区别现金贷和分期不同处理
			if (PayResultReqBo.getDebtType()==0) {
				//现金贷
				AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(PayResultReqBo.getOrderNo());
				AfBorrowLegalOrderDo afBorrowLegalOrderDo =	afBorrowLegalOrderService.getBorrowLegalOrderByBorrowId(borrowCashDo.getRid());
				AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(borrowCashDo.getUserId());
				if (PayResultReqBo.getType()==0&&PayResultReqBo.getCode()==1) {
					//审核失败
					//记录拓展表
					AfBorrowCashPushDo afBorrowCashPushDo = new AfBorrowCashPushDo();
					Date now = new Date();
					afBorrowCashPushDo.setGmtCreate(now);
					afBorrowCashPushDo.setGmtModified(now);
					afBorrowCashPushDo.setBorrowCashId(borrowCashDo.getRid());
					afBorrowCashPushDo.setAssetSideFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
					afBorrowCashPushDo.setStatus(PushEdspayResult.REVIEWFAIL.getCode());
					afBorrowCashPushService.saveRecord(afBorrowCashPushDo);
					AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
					AssetPushSwitchConf switchConf =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue1()), AssetPushSwitchConf.class);
					if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getReviewFail())) {
						//借款关闭
						//更新借款状态
						AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
						delegateBorrowCashDo.setRid(borrowCashDo.getRid());
						delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
						delegateBorrowCashDo.setRemark("浙商审核失败关闭");
						// 更新订单状态为关闭
						afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
						afBorrowLegalOrderDo.setClosedDetail("浙商审核失败关闭");
						afBorrowLegalOrderDo.setGmtClosed(new Date());
						applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
					}else{
						//调ups打款
						applyLegalBorrowCashService.delegatePay(borrowCashDo.getUserId()+"", borrowCashDo.getRishOrderNo(),
								"10", afBorrowLegalOrderDo, mainCard, borrowCashDo);
					}
				}else if(PayResultReqBo.getType()==1&&PayResultReqBo.getCode()==1){
					//打款失败
					//记录拓展表
					AfBorrowCashPushDo afBorrowCashPushDo = new AfBorrowCashPushDo();
					Date now = new Date();
					afBorrowCashPushDo.setGmtCreate(now);
					afBorrowCashPushDo.setGmtModified(now);
					afBorrowCashPushDo.setBorrowCashId(borrowCashDo.getRid());
					afBorrowCashPushDo.setAssetSideFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
					afBorrowCashPushDo.setStatus(PushEdspayResult.PAYFAIL.getCode());
					afBorrowCashPushService.saveRecord(afBorrowCashPushDo);
					AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
					AssetPushSwitchConf switchConf =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue1()), AssetPushSwitchConf.class);
					if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPayFail())) {
						//借款关闭
						//更新借款状态
						AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
						delegateBorrowCashDo.setRid(borrowCashDo.getRid());
						delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
						delegateBorrowCashDo.setRemark("浙商打款失败关闭");
						// 更新订单状态为关闭
						afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
						afBorrowLegalOrderDo.setClosedDetail("浙商打款失败关闭");
						afBorrowLegalOrderDo.setGmtClosed(new Date());
						applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
					}else{
						//调ups打款
						applyLegalBorrowCashService.delegatePay(borrowCashDo.getUserId()+"", borrowCashDo.getRishOrderNo(),
								"10", afBorrowLegalOrderDo, mainCard, borrowCashDo);
					}
				}else if(PayResultReqBo.getType()==1&&PayResultReqBo.getCode()==0){
					//打款成功
					//记录拓展表
					AfBorrowCashPushDo afBorrowCashPushDo = new AfBorrowCashPushDo();
					Date now = new Date();
					afBorrowCashPushDo.setGmtCreate(now);
					afBorrowCashPushDo.setGmtModified(now);
					afBorrowCashPushDo.setBorrowCashId(borrowCashDo.getRid());
					afBorrowCashPushDo.setAssetSideFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
					afBorrowCashPushDo.setStatus(PushEdspayResult.PAYSUCCESS.getCode());
					afBorrowCashPushService.saveRecord(afBorrowCashPushDo);
					AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowCashDo.getRid());
					afBorrowCashDo.setStatus(AfBorrowCashStatus.transed.getCode());
					// FIXME 查询是否有订单，查询订单状态
					final AfBorrowLegalOrderDo legalOrderDo = afBorrowLegalOrderService
							.getLastBorrowLegalOrderByBorrowId(borrowCashDo.getRid());
					
					if (legalOrderDo != null) {
						legalOrderDo.setStatus(BorrowLegalOrderStatus.AWAIT_DELIVER.getCode());
						afBorrowLegalOrderService.updateById(legalOrderDo);
					}
					// 查询借款信息是否存在
					AfBorrowLegalOrderCashDo legalOrderCashDo = afBorrowLegalOrderCashService
							.getBorrowLegalOrderCashByBorrowIdNoStatus(borrowCashDo.getRid());
					if (legalOrderCashDo != null) {
						legalOrderCashDo.setStatus(AfBorrowLegalOrderCashStatus.AWAIT_REPAY.getCode());
						afBorrowLegalOrderCashService.updateById(legalOrderCashDo);
					}
					afBorrowCashService.borrowSuccessForNew(afBorrowCashDo);
				}
			}
			if (PayResultReqBo.getDebtType()==1) {
				//分期
				AfBorrowDo borrowDo = afBorrowService.getBorrowInfoByBorrowNo(PayResultReqBo.getOrderNo());
				AfBorrowPushDo borrowPushDo = new AfBorrowPushDo();
				Date now = new Date();
				borrowPushDo.setGmtCreate(now);
				borrowPushDo.setGmtModified(now);
				borrowPushDo.setBorrowId(borrowDo.getRid());
				borrowPushDo.setAssetSideFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
				if (PayResultReqBo.getType()==0&&PayResultReqBo.getCode()==1){
					//审核失败
					borrowPushDo.setStatus(PushEdspayResult.REVIEWFAIL.getCode());
					//记录拓展表
				}else if(PayResultReqBo.getType()==1&&PayResultReqBo.getCode()==1){
					//打款失败
					borrowPushDo.setStatus(PushEdspayResult.PAYFAIL.getCode());
					//记录拓展表
				}else if(PayResultReqBo.getType()==1&&PayResultReqBo.getCode()==0){
					//打款成功
					borrowPushDo.setStatus(PushEdspayResult.PAYSUCCESS.getCode());
					//记录拓展表
				}
				afBorrowPushService.saveRecord(borrowPushDo);
			}
		}catch(Exception e){
			logger.error("borrowCashCurPush exception"+e);
		}
		return notifyRespBo;
	}

	
	public int queryEdspayApiHandle(String orderNo) {
		try {
			AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(orderNo);
			if (borrowCashDo!=null) {
				//现金贷
				borrowCashDo.setStatus(AfBorrowCashStatus.transed.getCode());
				// FIXME 查询是否有订单，查询订单状态
				final AfBorrowLegalOrderDo legalOrderDo = afBorrowLegalOrderService
						.getLastBorrowLegalOrderByBorrowId(borrowCashDo.getRid());

				if (legalOrderDo != null) {
					legalOrderDo.setStatus(BorrowLegalOrderStatus.AWAIT_DELIVER.getCode());
					afBorrowLegalOrderService.updateById(legalOrderDo);
				}
				// 查询借款信息是否存在
				AfBorrowLegalOrderCashDo legalOrderCashDo = afBorrowLegalOrderCashService
						.getBorrowLegalOrderCashByBorrowIdNoStatus(borrowCashDo.getRid());
				if (legalOrderCashDo != null) {
					legalOrderCashDo.setStatus(AfBorrowLegalOrderCashStatus.AWAIT_REPAY.getCode());
					afBorrowLegalOrderCashService.updateById(legalOrderCashDo);
				}
				afBorrowCashService.borrowSuccessForNew(borrowCashDo);
			}
		} catch (Exception e) {
			logger.error("queryEdspayApiHandle error"+e);
			return 1;
		}
		return 0;
	}

	public int tenementPushEdspay(Long borrowId) {
		try {
			AfBorrowDo borrowDo = afBorrowService.getBorrowById(Long.valueOf(borrowId));
			List<EdspayGetCreditRespBo> pushEdsPayBorrowInfos = riskUtil.pushEdsPayBorrowInfo(borrowDo);
			AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoService.getByFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
			//债权实时推送
			boolean result = assetSideEdspayUtil.borrowCashCurPush(pushEdsPayBorrowInfos, afAssetSideInfoDo.getAssetSideFlag(),Constants.ASSET_SIDE_FANBEI_FLAG);
			if (result) {
				logger.info("borrowCashCurPush suceess,borrowId="+borrowId);
			}
		} catch (Exception e) {
			logger.error("tenementPushEdspay error"+e);
			return 1;
		}
		return 0;
	}

	public int repushMaxApiHandle(String orderNo) {
		try {
			AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(orderNo);
			if (borrowCashDo!=null) {
				//现金贷
				//维护拓展表
				AfBorrowCashPushDo afBorrowCashPushDo = new AfBorrowCashPushDo();
				Date now = new Date();
				afBorrowCashPushDo.setGmtCreate(now);
				afBorrowCashPushDo.setGmtModified(now);
				afBorrowCashPushDo.setBorrowCashId(borrowCashDo.getRid());
				afBorrowCashPushDo.setAssetSideFlag(Constants.ASSET_SIDE_FANBEI_FLAG);
				afBorrowCashPushDo.setStatus(PushEdspayResult.PUSHFAIL.getCode());
				afBorrowCashPushService.saveRecord(afBorrowCashPushDo);
				AfBorrowLegalOrderDo afBorrowLegalOrderDo =	afBorrowLegalOrderService.getBorrowLegalOrderByBorrowId(borrowCashDo.getRid());
				AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(borrowCashDo.getUserId());
				AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
				AssetPushSwitchConf switchConf =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue1()), AssetPushSwitchConf.class);
				if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPushFail())) {
					//借款关闭
					//更新借款状态
					AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
					delegateBorrowCashDo.setRid(borrowCashDo.getRid());
					delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
					delegateBorrowCashDo.setRemark("推送钱包最大失败次数关闭");
					// 更新订单状态为关闭
					afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
					afBorrowLegalOrderDo.setClosedDetail("推送钱包最大失败次数关闭");
					afBorrowLegalOrderDo.setGmtClosed(new Date());
					applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
				}else{
					//调ups打款
					applyLegalBorrowCashService.delegatePay(borrowCashDo.getUserId()+"", borrowCashDo.getRishOrderNo(),
							"10", afBorrowLegalOrderDo, mainCard, borrowCashDo);
				}
			}else{
				//分期
				//维护拓展表
				AfBorrowDo borrowDo = afBorrowService.getBorrowInfoByBorrowNo(orderNo);
				AfBorrowPushDo borrowPushDo = new AfBorrowPushDo();
				Date now = new Date();
				borrowPushDo.setGmtCreate(now);
				borrowPushDo.setGmtModified(now);
				borrowPushDo.setBorrowId(borrowDo.getRid());
				borrowPushDo.setAssetSideFlag(Constants.ASSET_SIDE_FANBEI_FLAG);
				borrowPushDo.setStatus(PushEdspayResult.PUSHFAIL.getCode());
				afBorrowPushService.saveRecord(borrowPushDo);
			}
		} catch (Exception e) {
			logger.error("repushMaxApiHandle error"+e);
			return 1;
		}
		return 0;
	}
}
