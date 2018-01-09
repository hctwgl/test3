package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.bo.assetside.AssetSideRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.*;
import com.ald.fanbei.api.biz.service.AfAssetPackageDetailService;
import com.ald.fanbei.api.biz.service.AfLegalContractPdfCreateService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiAssetSideRespCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDao;
import com.ald.fanbei.api.dal.dao.AfAssetSideInfoDao;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @类描述：和资产方系统调用工具类
 * @author chengkang 2017年11月29日 16:55:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("EProtocolUtil")
public class EdsPayProtocolUtil extends AbstractThird {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfAssetSideInfoDao afAssetSideInfoDao;
	@Resource
	AfAssetPackageDao afAssetPackageDao;
	@Resource
	AfLegalContractPdfCreateService afLegalContractPdfCreateService;

	public AssetSideRespBo giveBackPdfInfo(String timestamp, String data, String sign, String appId) {
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
			EdspayBackPdfReqBo edspayBackPdfReqBo  = null;
			try {
				realDataJson = AesUtil.decryptFromBase64(data, "2KA4WGA857FFCC65");
				edspayBackPdfReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayBackPdfReqBo.class);
			} catch (Exception e) {
				logger.error("EdspayController giveBackCreditInfo parseJosn error", e);
			}finally{
				logger.info("EdspayController giveBackCreditInfo,appId="+appId+ ",reqJsonData=" + realDataJson + ",sendTime=" + timestamp);
			}
			if(edspayBackPdfReqBo==null){
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
			String orderNo = edspayBackPdfReqBo.getOrderNo();
			String protocolUrl = edspayBackPdfReqBo.getProtocolUrl();
			Integer debtType = edspayBackPdfReqBo.getDebtType();
			String investorName = edspayBackPdfReqBo.getInvestorName();
			String investorCardId = edspayBackPdfReqBo.getInvestorCardId();
			String investorPhone = edspayBackPdfReqBo.getInvestorPhone();
			/*orderNo="jk2018010914473300001";
			debtType=2;
			investorName="杨海龙";
			investorCardId="342522199401124538";
			investorPhone="18268005632";
			protocolUrl="http://edspay.oss-cn-qdjbp-a.aliyuncs.com/protocol/loanProtocol1000000059960459.pdf";*/
			if(orderNo==null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			if(protocolUrl==null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			if(debtType==null){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
				return notifyRespBo;
			}
			//具体操作
			String url = afLegalContractPdfCreateService.getProtocalLegalByType(debtType,orderNo,protocolUrl,investorPhone,
					investorName,investorCardId);
//			int resultValue = afAssetPackageDetailService.batchGiveBackCreditInfo(afAssetSideInfoDo,orderNos,debtType);
			if(url == null){
				logger.error("EdspayController giveBackCreditInfo exist error records,appId="+appId+ ",sendTime=" + timestamp);
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.APPLICATION_ERROR);
				return notifyRespBo;
			}
			notifyRespBo.setData(url);
			notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.SUCCESS);
		}catch (Exception e) {
			//系统异常
			logger.error("EdspayController giveBackCreditInfo error,appId="+appId+ ",sendTime=" + timestamp, e);
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
}
