package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.assetside.AssetSideRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayBackCreditReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayCreditDetailInfo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.FanbeiBorrowBankInfoBo;
import com.ald.fanbei.api.biz.service.AfAssetPackageDetailService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiAssetSideRespCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfAssetSideInfoDao;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSON;

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
			String realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
			EdspayBackCreditReqBo ddspayBackCreditReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayBackCreditReqBo.class);
			if(ddspayBackCreditReqBo==null){
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
			List<String> orderNos = ddspayBackCreditReqBo.parseOrderNoLists();
			if(orderNos==null || orderNos.size()==0 || orderNos.size()>100){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			
			//TODO 待完善实现
			afAssetPackageDetailService.batchGiveBackCreditInfo(afAssetSideInfoDo,orderNos);
			
		} catch (Exception e) {
			//系统异常
			logger.error("giveBackCreditInfo error", e);
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
			String realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
			EdspayGetCreditReqBo edspayGetCreditReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayGetCreditReqBo.class);
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
			Date nowDate = new Date();
			Date startTime = DateUtil.getSpecDateBySecondDefault(edspayGetCreditReqBo.getLoanStartTime(),DateUtil.getStartOfDate(nowDate));
			Date endTime = DateUtil.getSpecDateBySecondDefault(edspayGetCreditReqBo.getLoanEndTime(),DateUtil.getStartOfDate(nowDate));
			BigDecimal sevenMoney = null;
			BigDecimal fourteenMoney = null;
			EdspayCreditDetailInfo detailInfo = edspayGetCreditReqBo.getCreditDetails();
			if(detailInfo != null && !NumberUtil.isNullOrZeroOrNegative(detailInfo.getSEVEN()) && !NumberUtil.isNullOrZeroOrNegative(detailInfo.getFOURTEEN())){
				sevenMoney = detailInfo.getSEVEN();
				fourteenMoney = detailInfo.getFOURTEEN();
			}
			if(sevenMoney!=null && fourteenMoney!=null && sevenMoney.add(fourteenMoney).compareTo(edspayGetCreditReqBo.getMoney())!=0){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			
			//TODO 待完善实现
			List<EdspayGetCreditRespBo> creditInfoList = afAssetPackageDetailService.getBatchCreditInfo(afAssetSideInfoDo,edspayGetCreditReqBo.getMoney(), startTime, endTime, sevenMoney);
			if(creditInfoList!=null && creditInfoList.size()>0){
				notifyRespBo.setData(AesUtil.encryptToBase64(JSON.toJSONString(creditInfoList), assideResourceInfo.getValue2()));;
			}else{
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.ASSET_SIDE_AMOUNT_NOTENOUGH);
				return notifyRespBo;
			}
		} catch (Exception e) {
			//系统异常
			logger.error("giveBackCreditInfo error", e);
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
	public List<FanbeiBorrowBankInfoBo> getAssetSideBankInfo(String assetSideFlag) {
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
}
