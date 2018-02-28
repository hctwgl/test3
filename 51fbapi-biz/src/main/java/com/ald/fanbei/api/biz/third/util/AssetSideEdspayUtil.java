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

import com.ald.fanbei.api.biz.bo.assetside.AssetSideRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.AssetResponseMessage;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayBackCreditReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayCreditDetailInfo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditDayLimit;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetPlatUserInfoReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetPlatUserInfoRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.FanbeiBorrowBankInfoBo;
import com.ald.fanbei.api.biz.service.AfAssetPackageDetailService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiAssetSideRespCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDao;
import com.ald.fanbei.api.dal.dao.AfAssetSideInfoDao;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
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
	public boolean borrowCashCurPush(EdspayGetCreditRespBo borrowCashInfo,String assetSideFlag, String assetSideFanbeiFlag) {
		//发送的资产包信息
		try {
			String borrowerJson = "";
			if (StringUtil.isBlank(assetSideFlag)) {
				logger.error("borrowCashCurPush fail:assetSideFlag is null");
				return false;
			}
			if (borrowCashInfo == null) {
				borrowerJson = JSON.toJSONString("");
			}else{
				borrowerJson = JSON.toJSONString(borrowCashInfo);
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
			String jsonParam = JSON.toJSONString(map);
			//推送数据给钱包
			String respResult = HttpUtil.doHttpPostJsonParam(assideResourceInfo.getValue1()+"/p2p/fanbei/curDebtPush", jsonParam);
			AssetResponseMessage respInfo = JSONObject.parseObject(respResult, AssetResponseMessage.class);
			if (FanbeiAssetSideRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
				//推送成功
				logger.info("borrowCashCurPush success,respInfo:"+respInfo.getMessage());
				if (StringUtil.equals(YesNoStatus.YES.getCode(), respInfo.getIsFull())) {
					//钱包满额,更新配置表
					AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
					assetPushResource.setValue3(YesNoStatus.YES.getCode());
					afResourceService.editResource(assetPushResource);
				}
				return true;
			}else {
				//钱包处理错误
				FanbeiAssetSideRespCode failResp = FanbeiAssetSideRespCode.findByCode(respInfo.getCode());
				logger.error("borrowCashCurPush resp fail,errorCode="+respInfo.getCode()+",errorInfo"+(failResp!=null?failResp.getDesc():""));
				return false;
			}
		} catch (Exception e) {
			//推送失败或超时
			logger.error("borrowCashCurPush exception"+e);
		}
		return false;
	}
}
