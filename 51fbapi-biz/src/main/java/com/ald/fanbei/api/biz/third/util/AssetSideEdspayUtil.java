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

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.bo.QueryEdspayApiHandleReqBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.assetpush.AssetPushStrategy;
import com.ald.fanbei.api.biz.bo.assetpush.AssetPushSwitchConf;
import com.ald.fanbei.api.biz.bo.assetpush.ModifiedBorrowInfoVo;
import com.ald.fanbei.api.biz.bo.assetpush.ModifiedRepaymentPlan;
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
import com.ald.fanbei.api.biz.bo.assetside.edspay.RepaymentPlan;
import com.ald.fanbei.api.biz.service.AfAssetPackageDetailService;
import com.ald.fanbei.api.biz.service.AfAssetSideInfoService;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowCashPushService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.AfBorrowPushService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfCommitRecordService;
import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfLoanPushService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfRecordMaxService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfRetryTemplService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.ApplyLegalBorrowCashService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfAssetPackageBusiType;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfLoanStatus;
import com.ald.fanbei.api.common.enums.AfRepeatAssetSideType;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.PushEdspayResult;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.RetryEventType;
import com.ald.fanbei.api.common.enums.RiskReviewStatus;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiAssetSideRespCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDao;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDetailDao;
import com.ald.fanbei.api.dal.dao.AfAssetSideInfoDao;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.dao.AfLoanPeriodsDao;
import com.ald.fanbei.api.dal.dao.AfNotifyRecordDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfAssetPackageDetailDo;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashPushDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfBorrowPushDo;
import com.ald.fanbei.api.dal.domain.AfCommitRecordDo;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.AfLoanPushDo;
import com.ald.fanbei.api.dal.domain.AfNotifyRecordDo;
import com.ald.fanbei.api.dal.domain.AfRecordMaxDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfRetryTemplDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowDto;
import com.ald.fanbei.api.dal.domain.dto.AfLoanDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBorrowCashOverdueInfoDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAccountSenceService afUserAccountSenceService;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	AfBorrowCashDao afBorrowCashDao;
	@Resource
	AfLoanDao afLoanDao;
	@Resource
	AfLoanPeriodsDao afLoanPeriodsDao;
	@Resource
	JpushService jpushService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfLoanService afLoanService;
	@Resource
	AfLoanPushService afLoanPushService;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	SmsUtil smsUtil;
	@Resource
	UpsUtil upsUtil;
	@Resource
	AfRecordMaxService afRecordMaxService;
	@Resource
	AfAssetPackageDetailDao afAssetPackageDetailDao;
	@Resource
	AfLoanPeriodsService afLoanPeriodsService;
	@Resource
	AfCommitRecordService afCommitRecordService;
	@Resource
	AfBorrowBillService afBorrowBillService;
	@Resource
	AfNotifyRecordDao afNotifyRecordDao;
	
	public AssetSideRespBo giveBackCreditInfo(String timestamp, String data, String sign, String appId) {
		// 响应数据,默认成功
		AssetSideRespBo notifyRespBo = new AssetSideRespBo();
		try {
			//获取对应资产方配置信息
			AfResourceDo assideResourceInfo = getAssetSideConfigInfo(appId);
			if (assideResourceInfo == null) {
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_APPID_ERROR);
				return notifyRespBo;
			}
			//资产方及启用状态校验
			AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoDao.getByAssetSideFlag(appId);
			if (afAssetSideInfoDo == null || YesNoStatus.NO.getCode().equals(afAssetSideInfoDo.getStatus())) {
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.ASSET_SIDE_FROZEN);
				return notifyRespBo;
			}
			//请求时间校验
			Long reqTimeStamp = NumberUtil.objToLongDefault(timestamp, 0L);
			int result = DateUtil.judgeDiffTimeStamp(reqTimeStamp, DateUtil.getCurrSecondTimeStamp(), 60);
			if (result > 0) {
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
				return notifyRespBo;
			}
			//签名验证相关值处理
			String realDataJson = "";
			EdspayBackCreditReqBo edspayBackCreditReqBo = null;
			try {
				realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
				edspayBackCreditReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayBackCreditReqBo.class);
			} catch (Exception e) {
				logger.error("EdspayController giveBackCreditInfo parseJosn error", e);
			} finally {
				logger.info("EdspayController giveBackCreditInfo,appId=" + appId + ",reqJsonData=" + realDataJson + ",sendTime=" + timestamp);
			}
			if (edspayBackCreditReqBo == null) {
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
			logger.info("giveBackCreditInfo:" + orderNos);
			if (orderNos == null || orderNos.size() == 0 || orderNos.size() > 100) {
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			Integer debtType = edspayBackCreditReqBo.getDebtType();

			//具体撤回操作
			if (edspayBackCreditReqBo.getType() == 0) {
				//审核结果
				int resultValue = afAssetPackageDetailService.batchGiveBackCreditInfo(afAssetSideInfoDo, orderNos, debtType);
				if (resultValue != 1) {
					logger.error("EdspayController giveBackCreditInfo exist error records,appId=" + appId + ",sendTime=" + timestamp);
					notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.APPLICATION_ERROR);
					return notifyRespBo;
				}
			} else if (edspayBackCreditReqBo.getType() == 1 && edspayBackCreditReqBo.getCode()== 0) {
				//放款结果
				int resultValue =afAssetPackageDetailService.addPackageDetailLoanTime(orderNos, edspayBackCreditReqBo.getLoanTime(),debtType);
				if (resultValue != 1) {
					logger.error("EdspayController giveBackCreditInfo exist error records,appId=" + appId + ",sendTime=" + timestamp);
					notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.APPLICATION_ERROR);
					return notifyRespBo;
				}
			}

			notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.SUCCESS);
		} catch (Exception e) {
			//系统异常
			logger.error("EdspayController giveBackCreditInfo error,appId=" + appId + ",sendTime=" + timestamp, e);
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
		/*	int result = DateUtil.judgeDiffTimeStamp(reqTimeStamp,DateUtil.getCurrSecondTimeStamp(),60);
			if(result>0){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
				return notifyRespBo;
			}*/
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
			if(detailInfo != null && !NumberUtil.isNull(detailInfo.getMinMoney()) && !NumberUtil.isNull(detailInfo.getMaxMoney())){
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
			}else if (debtType == 0) {
				//现金贷
				creditInfoList = afAssetPackageDetailService.getBorrowCashBatchCreditInfo(bankInfo,afAssetSideInfoDo,edspayGetCreditReqBo.getMoney(), startTime, endTime, minMoney);
			}else {
				//白领贷
				creditInfoList = afAssetPackageDetailService.getLoanBatchCreditInfo(bankInfo,afAssetSideInfoDo,edspayGetCreditReqBo.getMoney(), startTime, endTime);
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
			AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
			AssetPushSwitchConf switchConf =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue1()), AssetPushSwitchConf.class);
			try {
				String url = assideResourceInfo.getValue1();
				logger.info("borrowCashCurPush url = {},originBorrowerJson = {}, request = {}",url,borrowerJson,JSONObject.toJSONString(map));
				//推送数据给钱包
				String respResult = HttpUtil.doHttpPostJsonParam(url+"/p2p/fanbei/debtPush", JSONObject.toJSONString(map));
				logger.info("borrowCashCurPush response = {}", respResult);
				AssetResponseMessage respInfo = JSONObject.parseObject(respResult, AssetResponseMessage.class);
				if (FanbeiAssetSideRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
					try {
						//推送成功
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
						afRetryTemplDo.setGmtModified(now);
						afRetryTemplService.saveRecord(afRetryTemplDo);
						if (StringUtil.equals(YesNoStatus.YES.getCode(), respInfo.getIsFull())) {
							//钱包满额,更新配置表
							assetPushResource.setValue3(YesNoStatus.YES.getCode());
							assetPushResource.setGmtModified(now);
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
						return true;
					}else{
						//重推开关关闭
						noRepushHandle(borrowCashInfo, switchConf);
					}
					return false;
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
			logger.error("borrowCashCurPush exception"+e);
		}
		return false;
	}

	private void noRepushHandle(EdspayGetCreditRespBo borrowCashInfo,AssetPushSwitchConf switchConf) {
		Date cur = new Date();
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
				delegateBorrowCashDo.setGmtModified(cur);
				// 更新订单状态为关闭
				afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
				afBorrowLegalOrderDo.setClosedDetail("推送失败关闭");
				afBorrowLegalOrderDo.setGmtClosed(cur);
				afBorrowLegalOrderDo.setGmtModified(cur);
				applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
				AfBorrowCashPushDo borrowCashPush = buildBorrowCashPush(borrowCashDo.getRid(),Constants.ASSET_SIDE_FANBEI_FLAG,PushEdspayResult.PUSHFAIL.getCode());
				afBorrowCashPushService.saveOrUpdate(borrowCashPush);
			}else{
				//调ups打款
				applyLegalBorrowCashService.delegatePay(borrowCashDo.getUserId()+"", borrowCashDo.getRishOrderNo(),
						"10", afBorrowLegalOrderDo, mainCard, borrowCashDo);
			}
		}else if(borrowCashInfo.getDebtType()==1){
			//分期
			AfBorrowDo borrowDo = afBorrowService.getBorrowInfoByBorrowNo(borrowCashInfo.getOrderNo());
			if (null != borrowDo) {
				AfBorrowPushDo borrowPush = buildBorrowPush(borrowDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PushEdspayResult.PUSHFAIL.getCode());
				afBorrowPushService.saveOrUpdate(borrowPush);
			}
		}else{
			//白领贷
			AfLoanDo loanDo = afLoanService.getByLoanNo(borrowCashInfo.getOrderNo());
			if (null != loanDo) {
				AfLoanPushDo loanPushDo = buildLoanPush(loanDo.getRid(),Constants.ASSET_SIDE_FANBEI_FLAG,PushEdspayResult.PUSHFAIL.getCode());
				afLoanPushService.saveOrUpdate(loanPushDo);
				AfUserDo afUserDo = afUserService.getUserById(loanDo.getUserId());
				AfUserBankcardDo bankCard = afUserBankcardService.getUserMainBankcardByUserId(loanDo.getUserId());
				List<AfLoanPeriodsDo> periodDos = afLoanPeriodsDao.listByLoanId(loanDo.getRid());
				if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPushFail())) {
					//直接关闭
					dealLoanFail(loanDo, periodDos,"推送失败关闭");
					jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), new Date());
					smsUtil.sendBorrowPayMoneyFail(afUserDo.getUserName());
				}else{
					// 调用UPS打款
					UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(loanDo.getAmount(),
							afUserDo.getRealName(), bankCard.getCardNumber(), afUserDo.getRid().toString(), bankCard.getMobile(),
							bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
							UserAccountLogType.LOAN.getCode(), loanDo.getRid().toString());
					loanDo.setTradeNoOut(upsResult.getOrderNo());
					if (!upsResult.isSuccess()) {
						dealLoanFail(loanDo, periodDos, "UPS打款失败，"+upsResult.getRespCode());
						jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), new Date());
						smsUtil.sendBorrowPayMoneyFail(afUserDo.getUserName());
					}
					loanDo.setStatus(AfLoanStatus.TRANSFERING.name());
					afLoanDao.updateById(loanDo);
				}
			}

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
		afRetryTemplDo.setGmtModified(now);
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
			Long reqTimeStamp = NumberUtil.objToLongDefault(sendTime,0L);
			/*int result = DateUtil.judgeDiffTimeStamp(reqTimeStamp,DateUtil.getCurrSecondTimeStamp(),60);
			if(result>0){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
				return notifyRespBo;
			}*/
			//签名验证相关值处理
			String realDataJson = "";
			EdspayGiveBackPayResultReqBo PayResultReqBo = null;
			try {
				realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
				logger.info("giveBackPayResult,request = {}",realDataJson);
				PayResultReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayGiveBackPayResultReqBo.class);
			} catch (Exception e) {
				logger.error("EdspayController giveBackPayResult parseJosn error,appId="+appId+ ",sendTime=" + sendTime, e);
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
			logger.info("giveBackPayResult orderNo"+PayResultReqBo.getOrderNo());
			//判断是否已经主动查询处理过
			AfRetryTemplDo retryTemplDo = afRetryTemplService.getByBusIdAndEventType(PayResultReqBo.getOrderNo(), RetryEventType.QUERY.getCode());
			if (null != retryTemplDo) {
				//钱包主动通知之后移除主动查询表，不再主动查询
				afRetryTemplService.deleteByBusidAndEventType(PayResultReqBo.getOrderNo(),RetryEventType.QUERY.getCode());
				//移出record_max表
				removeRecordMax(PayResultReqBo);
				//回传区别现金贷和分期不同处理
				AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
				AssetPushSwitchConf switchConf =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue1()), AssetPushSwitchConf.class);
				Date now = new Date();
				if (PayResultReqBo.getDebtType()==0) {
					//现金贷
					AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(PayResultReqBo.getOrderNo());
					AfBorrowLegalOrderDo afBorrowLegalOrderDo =	afBorrowLegalOrderService.getBorrowLegalOrderByBorrowId(borrowCashDo.getRid());
					AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(borrowCashDo.getUserId());
					if (PayResultReqBo.getType()==0&&PayResultReqBo.getCode()==1) {
						//审核失败
						AfBorrowCashPushDo borrowCashPush = buildBorrowCashPush(borrowCashDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PushEdspayResult.REVIEWFAIL.getCode());
						afBorrowCashPushService.saveOrUpdate(borrowCashPush);
						if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getReviewFail())) {
							//借款关闭
							//更新借款状态
							AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
							delegateBorrowCashDo.setRid(borrowCashDo.getRid());
							delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
							delegateBorrowCashDo.setRemark("浙商审核失败关闭");
							delegateBorrowCashDo.setGmtModified(now);
							// 更新订单状态为关闭
							afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
							afBorrowLegalOrderDo.setClosedDetail("浙商审核失败关闭");
							afBorrowLegalOrderDo.setGmtClosed(new Date());
							afBorrowLegalOrderDo.setGmtModified(now);
							applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
						}else{
							//调ups打款
							applyLegalBorrowCashService.delegatePay(borrowCashDo.getUserId()+"", borrowCashDo.getRishOrderNo(),
									"10", afBorrowLegalOrderDo, mainCard, borrowCashDo);
						}
					}else if(PayResultReqBo.getType()==1&&PayResultReqBo.getCode()==1){
						//打款失败
						AfBorrowCashPushDo borrowCashPush = buildBorrowCashPush(borrowCashDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PushEdspayResult.PAYFAIL.getCode());
						afBorrowCashPushService.saveOrUpdate(borrowCashPush);
						if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPayFail())) {
							//借款关闭
							//更新借款状态
							AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
							delegateBorrowCashDo.setRid(borrowCashDo.getRid());
							delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
							delegateBorrowCashDo.setRemark("浙商打款失败关闭");
							delegateBorrowCashDo.setGmtModified(now);
							// 更新订单状态为关闭
							afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
							afBorrowLegalOrderDo.setClosedDetail("浙商打款失败关闭");
							afBorrowLegalOrderDo.setGmtClosed(new Date());
							afBorrowLegalOrderDo.setGmtModified(now);
							applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
						}else{
							//调ups打款
							applyLegalBorrowCashService.delegatePay(borrowCashDo.getUserId()+"", borrowCashDo.getRishOrderNo(),
									"10", afBorrowLegalOrderDo, mainCard, borrowCashDo);
						}
					}else if(PayResultReqBo.getType()==1&&PayResultReqBo.getCode()==0){
						//打款成功
						AfBorrowCashPushDo borrowCashPush = buildBorrowCashPush(borrowCashDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PushEdspayResult.PAYSUCCESS.getCode());
						borrowCashPush.setLoanTime(PayResultReqBo.getLoanTime());//记录放款时间
						afBorrowCashPushService.saveOrUpdate(borrowCashPush);
						AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowCashDo.getRid());
						// 打款成功，更新借款状态、可用额度等信息
						try {
							BigDecimal auAmount = afUserAccountService.getAuAmountByUserId(borrowCashDo.getUserId());
							afBorrowCashService.updateAuAmountByRid(borrowCashDo.getRid(), auAmount);
						} catch (Exception e) {
							logger.error("updateAuAmountByRid is fail;msg=" + e);
						}
						// 减少额度，包括搭售商品借款
						afUserAccountSenceService.syncLoanUsedAmount(borrowCashDo.getUserId(), SceneType.CASH, borrowCashDo.getAmount());
						// 增加日志
						AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.BorrowCash,
								borrowCashDo.getAmount(), borrowCashDo.getUserId(), borrowCashDo.getRid());
						afUserAccountLogDao.addUserAccountLog(accountLog);
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
					if (null != borrowDo) {
						AfBorrowPushDo borrowPush = buildBorrowPush(borrowDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PayResultReqBo);
						if(PayResultReqBo.getCode()==0)
						{//记录放款时间
							borrowPush.setLoanTime(PayResultReqBo.getLoanTime());
						}
						afBorrowPushService.saveOrUpdate(borrowPush);
					}
				}
				if (PayResultReqBo.getDebtType() == 2){
					//白领贷
					AfLoanDo loanDo = afLoanService.getByLoanNo(PayResultReqBo.getOrderNo());
					if (null != loanDo) {
						AfUserDo afUserDo = afUserService.getUserById(loanDo.getUserId());
						AfUserBankcardDo bankCard = afUserBankcardService.getUserMainBankcardByUserId(loanDo.getUserId());
						List<AfLoanPeriodsDo> periodDos = afLoanPeriodsDao.listByLoanId(loanDo.getRid());
						if (PayResultReqBo.getType()==0&&PayResultReqBo.getCode()==1){
							//审核失败
							AfLoanPushDo loanPushDo = buildLoanPush(loanDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PushEdspayResult.REVIEWFAIL.getCode());
							afLoanPushService.saveOrUpdate(loanPushDo);
							if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getReviewFail())) {
								//直接关闭
								dealLoanFail(loanDo, periodDos,"浙商审核失败关闭");
								jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), new Date());
								smsUtil.sendBorrowPayMoneyFail(afUserDo.getUserName());
							}else{
								// 调用UPS打款
								UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(loanDo.getAmount(),
										afUserDo.getRealName(), bankCard.getCardNumber(), afUserDo.getRid().toString(), bankCard.getMobile(),
										bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
										UserAccountLogType.LOAN.getCode(), loanDo.getRid().toString());
								loanDo.setTradeNoOut(upsResult.getOrderNo());
								if (!upsResult.isSuccess()) {
									dealLoanFail(loanDo, periodDos, "UPS打款失败，"+upsResult.getRespCode());
									jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), new Date());
									smsUtil.sendBorrowPayMoneyFail(afUserDo.getUserName());
								}
								loanDo.setStatus(AfLoanStatus.TRANSFERING.name());
								afLoanDao.updateById(loanDo);
							}
						}else if(PayResultReqBo.getType()==1&&PayResultReqBo.getCode()==1){
							//打款失败
							AfLoanPushDo loanPushDo = buildLoanPush(loanDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PushEdspayResult.PAYFAIL.getCode());
							afLoanPushService.saveOrUpdate(loanPushDo);
							if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPayFail())) {
								//借款关闭
								dealLoanFail(loanDo, periodDos,"浙商打款失败关闭");
								jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), new Date());
								smsUtil.sendBorrowPayMoneyFail(afUserDo.getUserName());
							}else{
								//调ups打款
								UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(loanDo.getAmount(),
										afUserDo.getRealName(), bankCard.getCardNumber(), afUserDo.getRid().toString(), bankCard.getMobile(),
										bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
										UserAccountLogType.LOAN.getCode(), loanDo.getRid().toString());
								loanDo.setTradeNoOut(upsResult.getOrderNo());
								if (!upsResult.isSuccess()) {
									dealLoanFail(loanDo, periodDos, "UPS打款失败，"+upsResult.getRespCode());
									jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), new Date());
									smsUtil.sendBorrowPayMoneyFail(afUserDo.getUserName());
								}
								loanDo.setStatus(AfLoanStatus.TRANSFERING.name());
								afLoanDao.updateById(loanDo);
							}
						}else if(PayResultReqBo.getType()==1&&PayResultReqBo.getCode()==0){
							//打款成功
							AfLoanPushDo loanPushDo = buildLoanPush(loanDo.getRid(),Constants.ASSET_SIDE_EDSPAY_FLAG,PushEdspayResult.PAYSUCCESS.getCode());
							loanPushDo.setLoanTime(PayResultReqBo.getLoanTime());//记录放款时间
							afLoanPushService.saveOrUpdate(loanPushDo);
							afLoanService.dealLoanSucc(loanDo.getRid(),"");
						}	
					}
				
				}
			}
		}catch(Exception e){
			logger.error("borrowCashCurPush exception"+e);
		}
		return notifyRespBo;
	}

	private AfBorrowPushDo buildBorrowPush(Long rid,String assetSideEdspayFlag,EdspayGiveBackPayResultReqBo payResultReqBo) {
		AfBorrowPushDo borrowPushDo = new AfBorrowPushDo();
		Date now = new Date();
		borrowPushDo.setGmtCreate(now);
		borrowPushDo.setGmtModified(now);
		borrowPushDo.setBorrowId(rid);
		borrowPushDo.setAssetSideFlag(assetSideEdspayFlag);
		if (payResultReqBo.getType()==0&&payResultReqBo.getCode()==1){
			//审核失败
			borrowPushDo.setStatus(PushEdspayResult.REVIEWFAIL.getCode());
			//记录拓展表
		}else if(payResultReqBo.getType()==1&&payResultReqBo.getCode()==1){
			//打款失败
			borrowPushDo.setStatus(PushEdspayResult.PAYFAIL.getCode());
			//记录拓展表
		}else if(payResultReqBo.getType()==1&&payResultReqBo.getCode()==0){
			//打款成功
			borrowPushDo.setStatus(PushEdspayResult.PAYSUCCESS.getCode());
			//记录拓展表
		}
		return borrowPushDo;
	}

	private AfBorrowCashPushDo buildBorrowCashPush(Long rid,String assetSideEdspayFlag, String status) {
		AfBorrowCashPushDo afBorrowCashPushDo = new AfBorrowCashPushDo();
		Date now = new Date();
		afBorrowCashPushDo.setGmtCreate(now);
		afBorrowCashPushDo.setGmtModified(now);
		afBorrowCashPushDo.setBorrowCashId(rid);
		afBorrowCashPushDo.setAssetSideFlag(assetSideEdspayFlag);
		afBorrowCashPushDo.setStatus(status);
		return afBorrowCashPushDo;
	}

	private void removeRecordMax(EdspayGiveBackPayResultReqBo PayResultReqBo) {
		AfRecordMaxDo recordMaxDo = afRecordMaxService.getByBusIdAndEventype(PayResultReqBo.getOrderNo(),RetryEventType.QUERY.getCode());
		if (null != recordMaxDo) {
			afRecordMaxService.deleteById(recordMaxDo.getRid());
		}
	}

	
	public int queryEdspayApiHandle(String orderNo) {
		try {
			AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(orderNo);
			if (borrowCashDo!=null) {
				//现金贷
				try {
					BigDecimal auAmount = afUserAccountService.getAuAmountByUserId(borrowCashDo.getUserId());
					afBorrowCashService.updateAuAmountByRid(borrowCashDo.getRid(), auAmount);
				} catch (Exception e) {
					logger.error("updateAuAmountByRid is fail;msg=" + e);
				}
				// 减少额度，包括搭售商品借款 
				afUserAccountSenceService.syncLoanUsedAmount(borrowCashDo.getUserId(), SceneType.CASH, borrowCashDo.getAmount());
				// 增加日志
				AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.BorrowCash,
						borrowCashDo.getAmount(), borrowCashDo.getUserId(), borrowCashDo.getRid());
				afUserAccountLogDao.addUserAccountLog(accountLog);
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
			}else{
				AfLoanDo loanDo = afLoanService.getByLoanNo(orderNo);
				if (null != loanDo) {
					//白领贷
					afLoanService.dealLoanSucc(loanDo.getRid(),"");
				}
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
				//记录push表
				AfBorrowPushDo borrowPush = buildBorrowPush(borrowDo.getRid(),pushEdsPayBorrowInfos.get(0).getApr(), pushEdsPayBorrowInfos.get(0).getManageFee());
				afBorrowPushService.saveOrUpdate(borrowPush);
			}
		} catch (Exception e) {
			logger.error("tenementPushEdspay error"+e);
			return 1;
		}
		return 0;
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

	public int repushMaxApiHandle(String orderNo) {
		try {
			Date now = new Date();
			AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
			AssetPushSwitchConf switchConf =JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue1()), AssetPushSwitchConf.class);
			AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(orderNo);
			if (borrowCashDo!=null) {
				//现金贷
				AfBorrowCashPushDo borrowCashPush = buildBorrowCashPush(borrowCashDo.getRid(),Constants.ASSET_SIDE_FANBEI_FLAG,PushEdspayResult.PUSHFAIL.getCode());
				afBorrowCashPushService.saveOrUpdate(borrowCashPush);
				AfBorrowLegalOrderDo afBorrowLegalOrderDo =	afBorrowLegalOrderService.getBorrowLegalOrderByBorrowId(borrowCashDo.getRid());
				AfUserDo afUserDo = afUserService.getUserById(borrowCashDo.getUserId());
				AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(borrowCashDo.getUserId());
				if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPushFail())) {
					//借款关闭
					//更新借款状态
					AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
					delegateBorrowCashDo.setRid(borrowCashDo.getRid());
					delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
					delegateBorrowCashDo.setRemark("推送钱包最大失败次数关闭");
					delegateBorrowCashDo.setGmtModified(now);
					// 更新订单状态为关闭
					afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
					afBorrowLegalOrderDo.setClosedDetail("推送钱包最大失败次数关闭");
					afBorrowLegalOrderDo.setGmtClosed(new Date());
					afBorrowLegalOrderDo.setGmtModified(now);
					applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
					jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), now);
				}else{
					//调ups打款
					applyLegalBorrowCashService.delegatePay(borrowCashDo.getUserId()+"", borrowCashDo.getRishOrderNo(),
							"10", afBorrowLegalOrderDo, mainCard, borrowCashDo);
				}
			}else{
				//维护拓展表
				AfBorrowDo borrowDo = afBorrowService.getBorrowInfoByBorrowNo(orderNo);
				if (null!=borrowDo) {
					//分期 
					AfBorrowPushDo borrowPush = buildBorrowPush(borrowDo.getRid(),Constants.ASSET_SIDE_FANBEI_FLAG,PushEdspayResult.PUSHFAIL.getCode());
					afBorrowPushService.saveOrUpdate(borrowPush);
				}else{
					//白领贷
					AfLoanDo loanDo = afLoanService.getByLoanNo(orderNo);
					if (null != loanDo) {
						AfLoanPushDo loanPushDo = buildLoanPush(loanDo.getRid(),Constants.ASSET_SIDE_FANBEI_FLAG,PushEdspayResult.PUSHFAIL.getCode());
						afLoanPushService.saveOrUpdate(loanPushDo);
						AfUserDo afUserDo = afUserService.getUserById(loanDo.getUserId());
						AfUserBankcardDo bankCard = afUserBankcardService.getUserMainBankcardByUserId(loanDo.getUserId());
						List<AfLoanPeriodsDo> periodDos = afLoanPeriodsDao.listByLoanId(loanDo.getRid());
						if (StringUtil.equals(YesNoStatus.NO.getCode(), switchConf.getPushFail())) {
							//直接关闭
							dealLoanFail(loanDo, periodDos,"推送钱包最大失败次数关闭");
							jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), new Date());
							smsUtil.sendBorrowPayMoneyFail(afUserDo.getUserName());
						}else{
							// 调用UPS打款
							UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(loanDo.getAmount(),
									afUserDo.getRealName(), bankCard.getCardNumber(), afUserDo.getRid().toString(), bankCard.getMobile(),
									bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
									UserAccountLogType.LOAN.getCode(), loanDo.getRid().toString());
							loanDo.setTradeNoOut(upsResult.getOrderNo());
							if (!upsResult.isSuccess()) {
								dealLoanFail(loanDo, periodDos, upsResult.getRespCode());
								dealLoanFail(loanDo, periodDos, "UPS打款失败，"+upsResult.getRespCode());
								jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), new Date());
								smsUtil.sendBorrowPayMoneyFail(afUserDo.getUserName());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("repushMaxApiHandle error"+e);
			return 1;
		}
		return 0;
	}

	private AfBorrowPushDo buildBorrowPush(Long rid,String assetSideFanbeiFlag, String status) {
		AfBorrowPushDo borrowPushDo = new AfBorrowPushDo();
		Date now = new Date();
		borrowPushDo.setGmtCreate(now);
		borrowPushDo.setGmtModified(now);
		borrowPushDo.setBorrowId(rid);
		borrowPushDo.setStatus(status);
		return borrowPushDo;
	}

	private AfLoanPushDo buildLoanPush(Long loanId, String assetSideFlag,String status) {
		AfLoanPushDo loanPushDo =new AfLoanPushDo();
		Date now = new Date();
		loanPushDo.setGmtCreate(now);
		loanPushDo.setGmtModified(now);
		loanPushDo.setLoanId(loanId);
		loanPushDo.setAssetSideFlag(assetSideFlag);
		loanPushDo.setStatus(status);
		return loanPushDo;
	}
	

	private void dealLoanFail(final AfLoanDo loanDo, List<AfLoanPeriodsDo> periodDos, String msg) {
		Date now = new Date();
		loanDo.setStatus(AfLoanStatus.CLOSED.name());
		loanDo.setRemark(msg);
		loanDo.setGmtClose(now);
		loanDo.setGmtModified(now);
		transactionTemplate.execute(new TransactionCallback<Long>() { public Long doInTransaction(TransactionStatus status) {
            try {
            	afLoanDao.updateById(loanDo);
        		afUserAccountSenceService.syncLoanUsedAmount(loanDo.getUserId(), SceneType.valueOf(loanDo.getPrdType()), loanDo.getAmount().negate());
                return 1L;
            } catch (Exception e) {
                logger.error("dealLoanSucc update db error", e);
                throw e;
            }
		}});
	}

	public List<EdspayGetCreditRespBo> buildWhiteCollarBorrowInfo(AfLoanDo loanDo) {
		List<EdspayGetCreditRespBo> creditRespBos = new ArrayList<EdspayGetCreditRespBo>();
		EdspayGetCreditRespBo creditRespBo = new EdspayGetCreditRespBo();
		//获取开户行信息
		FanbeiBorrowBankInfoBo bankInfo = assetSideEdspayUtil.getAssetSideBankInfo(assetSideEdspayUtil.getAssetSideBankInfo());
		//借款人平台逾期信息
		AfUserBorrowCashOverdueInfoDto overdueInfoByUserId = afBorrowCashDao.getOverdueInfoByUserId(loanDo.getUserId());
		//获取资产方的分润利率
		AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoService.getByFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
		AfLoanDto afLoanDto = afLoanDao.getBorrowInfoById(loanDo);
		//白领贷的还款计划
		List<RepaymentPlan> repaymentPlans=new ArrayList<RepaymentPlan>();
		List<AfLoanPeriodsDo> afLoanPeriods = afLoanPeriodsDao.listByLoanId(loanDo.getRid());
		Date lastBorrowBillGmtPayTime=null;
		for (int i = 0; i < afLoanPeriods.size(); i++) {
			RepaymentPlan repaymentPlan = new RepaymentPlan();
			repaymentPlan.setRepaymentNo(afLoanPeriods.get(i).getRid()+"");
			repaymentPlan.setRepaymentTime(DateUtil.getSpecSecondTimeStamp(afLoanPeriods.get(i).getGmtPlanRepay()));
			repaymentPlan.setRepaymentDays(DateUtil.getNumberOfDayBetween(afLoanDto.getLoanStartTime(), afLoanPeriods.get(i).getGmtPlanRepay()));
			repaymentPlan.setRepaymentAmount(afLoanPeriods.get(i).getAmount());
			repaymentPlan.setRepaymentInterest(afLoanPeriods.get(i).getInterestFee());
			repaymentPlan.setRepaymentPeriod(afLoanPeriods.get(i).getNper()-1);
			repaymentPlans.add(repaymentPlan);
			if (i == afLoanPeriods.size() - 1) {
				lastBorrowBillGmtPayTime= afLoanPeriods.get(i).getGmtPlanRepay();
			}
		}
		creditRespBo.setOrderNo(afLoanDto.getOrderNo());
		creditRespBo.setUserId(loanDo.getUserId());
		creditRespBo.setName(afLoanDto.getName());
		creditRespBo.setCardId(afLoanDto.getCardId());
		creditRespBo.setMobile(afLoanDto.getMobile());
		creditRespBo.setBankNo(afLoanDto.getBankNo());
		creditRespBo.setAcctName("");
		creditRespBo.setMoney(afLoanDto.getMoney());
		creditRespBo.setApr(BigDecimalUtil.multiply(loanDo.getInterestRate(), new BigDecimal(100)));
		creditRespBo.setTimeLimit((int) DateUtil.getNumberOfDayBetween(afLoanDto.getLoanStartTime(), lastBorrowBillGmtPayTime));
		creditRespBo.setLoanStartTime(DateUtil.getSpecSecondTimeStamp(afLoanDto.getLoanStartTime()));
		if (StringUtil.isNotBlank(afLoanDto.getLoanRemark())) {
			creditRespBo.setPurpose(afLoanDto.getLoanRemark());
		}else {
			creditRespBo.setPurpose("个人消费");
		}
		creditRespBo.setPurpose("个人消费");
		creditRespBo.setRepaymentStatus(0);
		creditRespBo.setRepayName(afLoanDto.getName());
		creditRespBo.setRepayAcct(afLoanDto.getBankNo());
		creditRespBo.setRepayAcctBankNo("");
		creditRespBo.setRepayAcctType(0);
		if (StringUtil.equals(afLoanDto.getCardName(),"浙商银行")) {
			creditRespBo.setIsRepayAcctOtherBank(0);
		}else{
			creditRespBo.setIsRepayAcctOtherBank(1);
		}
		creditRespBo.setManageFee(afAssetSideInfoDo.getAnnualRate());
		if (StringUtil.isNotBlank(afLoanDto.getRepayRemark())) {
			creditRespBo.setRepaymentSource(afLoanDto.getRepayRemark());
		}else {
			creditRespBo.setRepaymentSource("工资收入");
		}
		creditRespBo.setDebtType(AfAssetPackageBusiType.LOAN.getCode());
		creditRespBo.setIsPeriod(1);
		creditRespBo.setTotalPeriod(afLoanDto.getPeriods());
		creditRespBo.setLoanerType(0);
		creditRespBo.setOverdueTimes(overdueInfoByUserId.getOverdueNums());
		creditRespBo.setOverdueAmount(overdueInfoByUserId.getOverdueAmount());
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
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.ASSET_SIDE_CONFIG.getCode(), assetSideFlag);
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
			AfNotifyRecordDo notifyRecord = new AfNotifyRecordDo(); 
			Date cur = new Date();
			notifyRecord.setGmtCreate(cur);
			notifyRecord.setGmtModified(cur);
			notifyRecord.setOrderNo(modifiedBorrowInfoList.get(0).getOrderNo());
			notifyRecord.setUserId(modifiedBorrowInfoList.get(0).getUserId());
			notifyRecord.setAssetSide(Constants.ASSET_SIDE_EDSPAY_FLAG);
			afNotifyRecordDao.saveRecord(notifyRecord);
			logger.info("transBorrowerInfo to wallet  url = {},originBorrowerJson = {}, request = {}",url,borrowJson,jsonParam);
			String respResult = HttpUtil.doHttpPostJsonParam(url+"/p2p/fanbei/debtOrderInfoPush", jsonParam);
			logger.info("transBorrowerInfo to wallet response = {}", respResult);
			if (StringUtil.isBlank(respResult)) {
				logger.error("transBorrowerInfo to wallet req success,respResult is null,");
				return false;
			}else {
				AssetResponseMessage respInfo = JSONObject.parseObject(respResult, AssetResponseMessage.class);
				if (respInfo != null) {
					if (FanbeiAssetSideRespCode.SUCCESS.getCode().equals(respInfo.getCode())) {
						logger.info("transBorrowerInfo to wallet req success"+",respInfo"+respInfo.getMessage());
						return true;
					}else {
						//三方处理错误
						FanbeiAssetSideRespCode failResp = FanbeiAssetSideRespCode.findByCode(respInfo.getCode());
						logger.error("transBorrowerInfo to wallet req success,resp fail"+",errorCode="+respInfo.getCode()+",errorInfo"+(failResp!=null?failResp.getDesc():""));
						return false;
					}
				} else {
					logger.error("transBorrowerInfo to wallet req success,respInfo is null");
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("transBorrowerInfo to wallet exist exception",e);
		}
		return false;
	}
	
	public Boolean isPush(Object obj) {
		String borrowNo = null;
		if (obj instanceof AfLoanDo) {
			AfLoanDo loanDo = (AfLoanDo) obj;
			borrowNo = loanDo.getLoanNo();
		}else if (obj instanceof AfBorrowCashDo) {
			AfBorrowCashDo borrowCashDo=(AfBorrowCashDo) obj;
			borrowNo = borrowCashDo.getBorrowNo();
		}else if (obj instanceof AfBorrowDo) {
			AfBorrowDo borrowDo=(AfBorrowDo) obj;
			borrowNo = borrowDo.getBorrowNo();
		}
		List<AfAssetPackageDetailDo> AssetPackageDetailList = afAssetPackageDetailDao.getPackageDetailByBorrowNo(borrowNo);
		AfRetryTemplDo afRetryTemplDo = afRetryTemplService.getCurPushDebt(borrowNo,RetryEventType.QUERY.getCode());
		if ((AssetPackageDetailList != null&&AssetPackageDetailList.size() > 0) || afRetryTemplDo!=null) {
			return true;
		}
		return false;
	}
	
	public List<ModifiedBorrowInfoVo> buildModifiedInfo(Object obj, Integer RepaymentStatus) {
		List<ModifiedBorrowInfoVo> modifiedDebtList= new ArrayList<ModifiedBorrowInfoVo>();
		ModifiedBorrowInfoVo modifiedDebt = new ModifiedBorrowInfoVo();
		List<ModifiedRepaymentPlan> repaymentPlanList=new ArrayList<ModifiedRepaymentPlan>();
		if (obj instanceof AfLoanDo) {
			AfLoanDo loanDo = (AfLoanDo) obj;
			modifiedDebt.setUserId(loanDo.getUserId());
			modifiedDebt.setOrderNo(loanDo.getLoanNo());
			modifiedDebt.setIsPeriod(1);
			List<AfLoanPeriodsDo> loanPeriodsList = afLoanPeriodsService.listByLoanId(loanDo.getRid());
			for (AfLoanPeriodsDo afLoanPeriodsDo : loanPeriodsList) {
				 ModifiedRepaymentPlan modifiedRepaymentPlan = new ModifiedRepaymentPlan();
				 modifiedRepaymentPlan.setRepaymentNo(afLoanPeriodsDo.getRid()+"");
				 modifiedRepaymentPlan.setRepaymentAmount(afLoanPeriodsDo.getAmount());
				 modifiedRepaymentPlan.setRepaymentInterest(afLoanPeriodsDo.getInterestFee());
				 modifiedRepaymentPlan.setRepaymentStatus(RepaymentStatus);
				 modifiedRepaymentPlan.setRepaymentYesTime((int) DateUtil.getSpecSecondTimeStamp(new Date()));
				 modifiedRepaymentPlan.setIsOverdue(0);
				 modifiedRepaymentPlan.setIsPrepayment(1);
				 modifiedRepaymentPlan.setRepaymentPeriod(afLoanPeriodsDo.getNper());
				 repaymentPlanList.add(modifiedRepaymentPlan);
			}
		}else if (obj instanceof AfBorrowCashDo) {
			 AfBorrowCashDo borrowCashDo=(AfBorrowCashDo) obj;
			 modifiedDebt.setUserId(borrowCashDo.getUserId());
			 modifiedDebt.setOrderNo(borrowCashDo.getBorrowNo());
			 modifiedDebt.setIsPeriod(0);
			 ModifiedRepaymentPlan modifiedRepaymentPlan = new ModifiedRepaymentPlan();
			 modifiedRepaymentPlan.setRepaymentNo(borrowCashDo.getRid()+"");
			 modifiedRepaymentPlan.setRepaymentAmount(borrowCashDo.getArrivalAmount());
			 BigDecimal borrowRate =BigDecimal.ZERO;
			 List<AfAssetPackageDetailDo> assetPackageDetailList = afAssetPackageDetailDao.getPackageDetailByBorrowNo(borrowCashDo.getBorrowNo());
			 if (assetPackageDetailList != null&&assetPackageDetailList.size() > 0) {
				 borrowRate =assetPackageDetailList.get(0).getBorrowRate();
			 }else{
				 AfRetryTemplDo afRetryTemplDo = afRetryTemplService.getCurPushDebt(borrowCashDo.getBorrowNo(),RetryEventType.QUERY.getCode());
				 if (afRetryTemplDo != null) {
					AfBorrowCashPushDo afBorrowCashPushDo = afBorrowCashPushService.getByBorrowCashId(borrowCashDo.getRid());
					borrowRate = afBorrowCashPushDo.getBorrowRate();
				}
			 }
			Integer timeLimit = NumberUtil.objToIntDefault(borrowCashDo.getType(), null);
			 modifiedRepaymentPlan.setRepaymentInterest(BigDecimalUtil.multiply(borrowCashDo.getArrivalAmount(), new BigDecimal(borrowRate.doubleValue()*timeLimit/ 36000d)));
			 modifiedRepaymentPlan.setRepaymentStatus(RepaymentStatus);
			 modifiedRepaymentPlan.setRepaymentYesTime((int) DateUtil.getSpecSecondTimeStamp(new Date()));
			 modifiedRepaymentPlan.setIsOverdue(0);
			 modifiedRepaymentPlan.setIsPrepayment(1);
			 modifiedRepaymentPlan.setRepaymentPeriod(0);
			 repaymentPlanList.add(modifiedRepaymentPlan);
		}else if (obj instanceof AfBorrowDo) {
			AfBorrowDo borrowDo=(AfBorrowDo) obj;
			modifiedDebt.setUserId(borrowDo.getUserId());
			modifiedDebt.setOrderNo(borrowDo.getBorrowNo());
			modifiedDebt.setIsPeriod(1);
			List<AfBorrowBillDo> afBorrowBillDos = afBorrowBillService.getAllBorrowBillByBorrowId(borrowDo.getRid());
			for (AfBorrowBillDo afBorrowBillDo : afBorrowBillDos) {
				 ModifiedRepaymentPlan modifiedRepaymentPlan = new ModifiedRepaymentPlan();
				 modifiedRepaymentPlan.setRepaymentNo(afBorrowBillDo.getRid()+"");
				 modifiedRepaymentPlan.setRepaymentAmount(afBorrowBillDo.getPrincipleAmount());
				 modifiedRepaymentPlan.setRepaymentInterest(afBorrowBillDo.getInterestAmount());
				 modifiedRepaymentPlan.setRepaymentStatus(RepaymentStatus);
				 modifiedRepaymentPlan.setRepaymentYesTime((int) DateUtil.getSpecSecondTimeStamp(new Date()));
				 modifiedRepaymentPlan.setIsOverdue(0);
				 modifiedRepaymentPlan.setIsPrepayment(1);
				 modifiedRepaymentPlan.setRepaymentPeriod(afBorrowBillDo.getBillNper()-1);
				 repaymentPlanList.add(modifiedRepaymentPlan);
			}
		}
		modifiedDebt.setRepaymentPlans(repaymentPlanList);
		modifiedDebtList.add(modifiedDebt);
		return modifiedDebtList;
	}
	
	public void transFailRecord(Object obj,List<ModifiedBorrowInfoVo> modifiedInfo) {
		Long id = null;
		if (obj instanceof AfLoanDo) {
			AfLoanDo loanDo = (AfLoanDo) obj;
			id = loanDo.getRid();
		}else if (obj instanceof AfBorrowCashDo) {
			AfBorrowCashDo borrowCashDo=(AfBorrowCashDo) obj;
			id=borrowCashDo.getRid();
		}else if (obj instanceof AfBorrowDo) {
			AfBorrowDo borrowDo=(AfBorrowDo) obj;
			id=borrowDo.getRid();
		}
		thirdLog.error("trans modified loan Info fail,borrowId ="+id);
		Date cur = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String commitTime = sdf.format(cur);
		AfCommitRecordDo afCommitRecordDo = new AfCommitRecordDo();
		afCommitRecordDo.setGmtCreate(cur);
		afCommitRecordDo.setGmtModified(cur);
		afCommitRecordDo.setType(AfRepeatAssetSideType.REFUND_ASSETSIDE.getCode());
		afCommitRecordDo.setRelate_id(id+"");
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.ASSET_SIDE_CONFIG.getCode(), Constants.ASSET_SIDE_EDSPAY_FLAG);
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
		afCommitRecordService.addRecord(afCommitRecordDo);
	}
}
