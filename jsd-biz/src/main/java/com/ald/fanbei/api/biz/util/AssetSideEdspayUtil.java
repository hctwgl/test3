package com.ald.fanbei.api.biz.util;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.*;
import com.ald.fanbei.api.biz.bo.assetpush.*;
import com.ald.fanbei.api.biz.bo.ups.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.BizAssetSideRespCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfUserBorrowCashOverdueInfoDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @类描述：和资产方系统调用工具类
 * @author chengkang 2017年11月29日 16:55:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("assetSideEdspayUtil")
public class AssetSideEdspayUtil extends AbstractThird {

	@Resource
    JsdResourceService jsdResourceService;
	@Resource
	JsdAssetSideInfoDao jsdAssetSideInfoDao;
	@Resource
	JsdAssetPackageDao jsdAssetPackageDao;
	@Resource
	JsdAssetPackageDetailService jsdAssetPackageDetailService;

	public AssetSideRespBo giveBackCreditInfo(String timestamp, String data, String sign, String appId) {
		// 响应数据,默认成功
		AssetSideRespBo notifyRespBo = new AssetSideRespBo();
		try {
			//获取对应资产方配置信息
			JsdResourceDo assideResourceInfo = getAssetSideConfigInfo(appId);
			if (assideResourceInfo == null) {
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_APPID_ERROR);
				return notifyRespBo;
			}
			//资产方及启用状态校验
			JsdAssetSideInfoDo afAssetSideInfoDo = jsdAssetSideInfoDao.getByAssetSideFlag(appId);
			if (afAssetSideInfoDo == null || YesNoStatus.NO.getCode().equals(afAssetSideInfoDo.getStatus())) {
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.ASSET_SIDE_FROZEN);
				return notifyRespBo;
			}
			//请求时间校验
			Long reqTimeStamp = NumberUtil.objToLongDefault(timestamp, 0L);
			int result = DateUtil.judgeDiffTimeStamp(reqTimeStamp, DateUtil.getCurrSecondTimeStamp(), 60);
			if (result > 0) {
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
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
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.PARSE_JSON_ERROR);
				return notifyRespBo;
			}
			String currSign = DigestUtil.MD5(realDataJson);
			if (!StringUtil.equals(currSign, sign)) {// 验签成功
				//验证签名失败
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_SIGNATURE_ERROR);
				return notifyRespBo;
			}

			//签名成功,业务处理
			List<String> orderNos = edspayBackCreditReqBo.getOrderNos();
			logger.info("giveBackCreditInfo:" + orderNos);
			if (orderNos == null || orderNos.size() == 0 || orderNos.size() > 100) {
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			Integer debtType = edspayBackCreditReqBo.getDebtType();

			//具体撤回操作
			if (edspayBackCreditReqBo.getType() == 0) {
				//审核结果
				int resultValue = jsdAssetPackageDetailService.batchGiveBackCreditInfo(afAssetSideInfoDo, orderNos, debtType);
				if (resultValue != 1) {
					logger.error("EdspayController giveBackCreditInfo exist error records,appId=" + appId + ",sendTime=" + timestamp);
					notifyRespBo.resetRespInfo(BizAssetSideRespCode.APPLICATION_ERROR);
					return notifyRespBo;
				}
			} else if (edspayBackCreditReqBo.getType() == 1 && edspayBackCreditReqBo.getCode()== 0) {
				//放款结果
				int resultValue =jsdAssetPackageDetailService.addPackageDetailLoanTime(orderNos, edspayBackCreditReqBo.getLoanTime(),debtType);
				if (resultValue != 1) {
					logger.error("EdspayController giveBackCreditInfo exist error records,appId=" + appId + ",sendTime=" + timestamp);
					notifyRespBo.resetRespInfo(BizAssetSideRespCode.APPLICATION_ERROR);
					return notifyRespBo;
				}
			}

			notifyRespBo.resetRespInfo(BizAssetSideRespCode.SUCCESS);
		} catch (Exception e) {
			//系统异常
			logger.error("EdspayController giveBackCreditInfo error,appId=" + appId + ",sendTime=" + timestamp, e);
			notifyRespBo.resetRespInfo(BizAssetSideRespCode.APPLICATION_ERROR);
		}
		return notifyRespBo;
	}

	/**
	 * 获取债权信息
	 * @param timestamp
	 * @param data
	 * @param sign
	 * @param
	 * @return
	 */
	public AssetSideRespBo getBatchCreditInfo(String timestamp, String data, String sign, String appId) {
		// 响应数据,默认成功
		AssetSideRespBo notifyRespBo = new AssetSideRespBo();
		try {
			//获取对应资产方配置信息
			JsdResourceDo assideResourceInfo = getAssetSideConfigInfo(appId);
			if(assideResourceInfo == null){
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_APPID_ERROR);
				return notifyRespBo;
			}
			//资产方及启用状态校验
			JsdAssetSideInfoDo afAssetSideInfoDo = jsdAssetSideInfoDao.getByAssetSideFlag(appId);
			if(afAssetSideInfoDo==null || YesNoStatus.NO.getCode().equals(afAssetSideInfoDo.getStatus()) ){
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.ASSET_SIDE_FROZEN);
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
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.PARSE_JSON_ERROR);
				return notifyRespBo;
			}

			String currSign = DigestUtil.MD5(realDataJson);
			if (!StringUtil.equals(currSign, sign)) {// 验签成功
				//验证签名失败
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_SIGNATURE_ERROR);
				return notifyRespBo;
			}

			//签名成功,业务处理
			if(NumberUtil.isNullOrZeroOrNegative(edspayGetCreditReqBo.getMoney())){
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}

			//校验当日限额
			int debtType = NumberUtil.objToIntDefault(edspayGetCreditReqBo.getDebtType(), 0);
			BigDecimal currDayHaveGetTotalBorrowAmount = jsdAssetPackageDao.getCurrDayHaveGetTotalBorrowAmount(afAssetSideInfoDo.getRid());
			BigDecimal currDayHaveGetTotalBorrowCashAmount = jsdAssetPackageDao.getCurrDayHaveGetTotalBorrowCashAmount(afAssetSideInfoDo.getRid());
			EdspayGetCreditDayLimit dayLimit = JSON.toJavaObject(JSON.parseObject(assideResourceInfo.getValue3()), EdspayGetCreditDayLimit.class);
			if (debtType == 1 && dayLimit != null && dayLimit.getBorrowDayLimit().compareTo(currDayHaveGetTotalBorrowAmount.add(edspayGetCreditReqBo.getMoney()))<=0) {
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.CREDIT_BORROW_AMOUNT_OVERRUN);
				return notifyRespBo;
			}
			if (debtType == 0 && dayLimit != null && dayLimit.getBorrowCashDayLimit().compareTo(currDayHaveGetTotalBorrowCashAmount.add(edspayGetCreditReqBo.getMoney()))<=0) {
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.CREDIT_BORROWCASH_AMOUNT_OVERRUN);
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
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			
			//具体获取债权明细(区分现金贷和消费分期)
			List<EdspayGetCreditRespBo> creditInfoList=new ArrayList<EdspayGetCreditRespBo>();
			if (debtType == 1) {
				//消费分期
				FanbeiBorrowBankInfoBo bankInfo = getAssetSideBankInfo(getAssetSideBankInfo(DebtType.BORROW.getCode()));
				if(bankInfo==null){
					notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_APPID_ERROR);
					return notifyRespBo;
				}
			//	creditInfoList = jsdAssetPackageDetailService.getBorrowBatchCreditInfo(bankInfo,afAssetSideInfoDo,edspayGetCreditReqBo.getMoney(), startTime, endTime);
			}else if (debtType == 0) {
				//现金贷
				FanbeiBorrowBankInfoBo bankInfo = getAssetSideBankInfo(getAssetSideBankInfo(DebtType.BORROWCASH.getCode()));
				if(bankInfo==null){
					notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_APPID_ERROR);
					return notifyRespBo;
				}
				//creditInfoList = jsdAssetPackageDetailService.getBorrowCashBatchCreditInfo(bankInfo,afAssetSideInfoDo,edspayGetCreditReqBo.getMoney(), startTime, endTime, minMoney);
			}else if (debtType == 2){
				//白领贷
				FanbeiBorrowBankInfoBo bankInfo = getAssetSideBankInfo(getAssetSideBankInfo(DebtType.LOAN.getCode()));
				if(bankInfo==null){
					notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_APPID_ERROR);
					return notifyRespBo;
				}
			//	creditInfoList = jsdAssetPackageDetailService.getLoanBatchCreditInfo(bankInfo,afAssetSideInfoDo,edspayGetCreditReqBo.getMoney(), startTime, endTime);
			}else if (debtType == 4 || debtType == 5){
				//西瓜极速贷2.0/极光贷
				FanbeiBorrowBankInfoBo bankInfo = getAssetSideBankInfo(getAssetSideBankInfo(DebtType.XGJSD.getCode()));
				if(bankInfo==null){
					notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_APPID_ERROR);
					return notifyRespBo;
				}
				creditInfoList = jsdAssetPackageDetailService.getXgJsdBatchCreditInfo(bankInfo,afAssetSideInfoDo,edspayGetCreditReqBo.getMoney(), startTime, endTime, minMoney);
			}

			if(creditInfoList!=null && creditInfoList.size()>0){
				String sourceJsonStr = JSON.toJSONString(creditInfoList);
				logger.info("EdspayController getBatchCreditInfo,appId="+appId+ ",returnJsonData=" + sourceJsonStr + ",sendTime=" + timestamp);
				notifyRespBo.setData(AesUtil.encryptToBase64(sourceJsonStr, assideResourceInfo.getValue2()));;
			}else{
				notifyRespBo.resetRespInfo(BizAssetSideRespCode.ASSET_SIDE_AMOUNT_NOTENOUGH);
				return notifyRespBo;
			}
		} catch (Exception e) {
			//系统异常
			logger.error("EdspayController getBatchCreditInfo error,appId="+appId+ ",sendTime=" + timestamp, e);
			notifyRespBo.resetRespInfo(BizAssetSideRespCode.APPLICATION_ERROR);
		}
		return notifyRespBo;
	}

	/**
	 * 获取资产方配置信息
	 * 如果资产方未启用或者配置未开启，则返回null，否则返回正常配置信息
	 * @param appId
	 * @return
	 */
	private JsdResourceDo getAssetSideConfigInfo(String appId){
		//资产方对应配置信息校验
		JsdResourceDo assideResourceInfo = jsdResourceService.getByTypeAngSecType(ResourceType.ASSET_SIDE_CONFIG.getCode(), appId);
		if(assideResourceInfo==null || !AfCounponStatus.O.getCode().equals(assideResourceInfo.getValue4()) ){
			return null;
		}
		return assideResourceInfo;
	}

	/**
	 * 获取资产方开户行信息
	 * @param
	 * @return
	 */
	public List<FanbeiBorrowBankInfoBo> getAssetSideBankInfo(String type) {
		List<FanbeiBorrowBankInfoBo> bankInfoList = new ArrayList<FanbeiBorrowBankInfoBo>();
		try {
			JsdResourceDo jsdResourceDo =null;
			if (type.equals(DebtType.BORROWCASH.getCode())) {
				jsdResourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.ASSET_SIDE_CONFIG.getCode(), ResourceType.BORROWCASH_BANK_INFOS.getCode());
			}else if (type.equals(DebtType.BORROW.getCode())) {
				jsdResourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.ASSET_SIDE_CONFIG.getCode(), ResourceType.BORROW_BANK_INFOS.getCode());
			}else if (type.equals(DebtType.LOAN.getCode())) {
				jsdResourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.ASSET_SIDE_CONFIG.getCode(), ResourceType.LOAN_BANK_INFOS.getCode());
			}else if (type.equals(DebtType.XGJSD.getCode())) {
				jsdResourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.ASSET_SIDE_CONFIG.getCode(), ResourceType.XGJSD_BANK_INFOS.getCode());
			}
			if(jsdResourceDo==null){
				return bankInfoList;
			}
			bankInfoList.add(JSON.toJavaObject(JSON.parseObject(jsdResourceDo.getValue()), FanbeiBorrowBankInfoBo.class));
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

}
