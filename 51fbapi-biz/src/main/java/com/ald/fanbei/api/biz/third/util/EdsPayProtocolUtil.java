package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.bo.assetside.AssetSideRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayBackPdfReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayInvestorInfoBo;
import com.ald.fanbei.api.biz.service.AfLegalContractPdfCreateService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiAssetSideRespCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDao;
import com.ald.fanbei.api.dal.dao.AfAssetSideInfoDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSON;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chengkang 2017年11月29日 16:55:23
 * @类描述：和资产方系统调用工具类
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("eProtocolUtil")
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
            if (assideResourceInfo == null) {
                notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_APPID_ERROR);
                return notifyRespBo;
            }
            //资产方及启用状态校验
//			AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoDao.getByAssetSideFlag(appId);
            /*if(afAssetSideInfoDo==null || YesNoStatus.NO.getCode().equals(afAssetSideInfoDo.getStatus()) ){
				notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.ASSET_SIDE_FROZEN);
				return notifyRespBo;
			}*/
            //请求时间校验
            Long reqTimeStamp = NumberUtil.objToLongDefault(timestamp, 0L);
            int result = DateUtil.judgeDiffTimeStamp(reqTimeStamp, DateUtil.getCurrSecondTimeStamp(), 60);
            if (result > 0) {
                notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
                return notifyRespBo;
            }
            //签名验证相关值处理
            String realDataJson = "";
            EdspayBackPdfReqBo edspayBackPdfReqBo = null;
            try {
                realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
                edspayBackPdfReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayBackPdfReqBo.class);
            } catch (Exception e) {
                logger.error("EdspayController giveBackCreditInfo parseJosn error", e);
            } finally {
                logger.info("EdspayController giveBackCreditInfo,appId=" + appId + ",reqJsonData=" + realDataJson + ",sendTime=" + timestamp);
            }
            if (edspayBackPdfReqBo == null) {
                notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.PARSE_JSON_ERROR);
                return notifyRespBo;
            }

            String currSign = DigestUtil.MD5(realDataJson);
            if (!StringUtil.equals(currSign, sign)) {// 验签成功
                //验证签名失败
                notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.VALIDATE_SIGNATURE_ERROR);
                return notifyRespBo;
            }
//			edspayBackPdfReqBo = new EdspayBackPdfReqBo();
            //签名成功,业务处理
            String orderNo = edspayBackPdfReqBo.getOrderNo();
            String protocolUrl = edspayBackPdfReqBo.getProtocolUrl();
            Integer debtType = edspayBackPdfReqBo.getDebtType();
            String borrowerName = edspayBackPdfReqBo.getBorrowerName();
            List<EdspayInvestorInfoBo> investorList = edspayBackPdfReqBo.getInvestorList();
            List<EdspayInvestorInfoBo> list = JSONArray.toList(JSONArray.fromObject(investorList), EdspayInvestorInfoBo.class);
			/*orderNo="jk2018010914473300001";
			debtType=2;
			EdspayInvestorInfoBo infoBo = new EdspayInvestorInfoBo();
			investorName="杨海龙";
			investorCardId="342522199401124538";
			investorPhone="18268005632";
			borrowerName="何文艺";
			infoBo.setInvestorName(investorName);
			infoBo.setInvestorCardId(investorCardId);
			infoBo.setInvestorPhone(investorPhone);
			investorList = new ArrayList<>();
			investorList.add(infoBo);
			EdspayInvestorInfoBo infoBo2 = new EdspayInvestorInfoBo();
			investorName="郭帅强";
			investorCardId="330724199211254817";
			investorPhone="13018933980";
			infoBo2.setInvestorName(investorName);
			infoBo2.setInvestorCardId(investorCardId);
			infoBo2.setInvestorPhone(investorPhone);
			investorList.add(infoBo2);
			protocolUrl="http://edspay.oss-cn-qdjbp-a.aliyuncs.com/protocol/loanProtocol1000001675551827.pdf";*/
            if (orderNo == null) {
                notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            if (borrowerName == null) {
                notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            if (protocolUrl == null) {
                notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            if (debtType == null && debtType != 1 && debtType != 0) {
                notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            if (protocolUrl == null) {
                notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            //具体操作
            String url = afLegalContractPdfCreateService.getProtocalLegalByType(debtType, orderNo, protocolUrl, borrowerName, list);
//			int resultValue = afAssetPackageDetailService.batchGiveBackCreditInfo(afAssetSideInfoDo,orderNos,debtType);
            if (url == null || "".equals(url)) {
                logger.error("EdspayController giveBackCreditInfo url exist error records,appId=" + appId + ",sendTime=" + timestamp);
                notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.APPLICATION_ERROR);
                return notifyRespBo;
            }
            notifyRespBo.setData(url);
            notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.SUCCESS);
        } catch (Exception e) {
            //系统异常
            logger.error("EdspayController giveBackCreditInfo error,appId=" + appId + ",sendTime=" + timestamp, e);
            notifyRespBo.resetRespInfo(FanbeiAssetSideRespCode.APPLICATION_ERROR);
        }
        return notifyRespBo;
    }

    /**
     * 获取资产方配置信息
     * 如果资产方未启用或者配置未开启，则返回null，否则返回正常配置信息
     *
     * @param appId
     * @return
     */
    private AfResourceDo getAssetSideConfigInfo(String appId) {
        //资产方对应配置信息校验
        AfResourceDo assideResourceInfo = afResourceService.getConfigByTypesAndSecType(AfResourceType.ASSET_SIDE_CONFIG.getCode(), appId);
        if (assideResourceInfo == null || !AfCounponStatus.O.getCode().equals(assideResourceInfo.getValue4())) {
            return null;
        }
        return assideResourceInfo;
    }
}
