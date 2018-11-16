package com.ald.fanbei.api.biz.util;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.AssetSideRespBo;
import com.ald.fanbei.api.biz.bo.aassetside.edspay.EdspayBackPdfReqBo;
import com.ald.fanbei.api.biz.bo.aassetside.edspay.EdspayBackSealReqBo;
import com.ald.fanbei.api.biz.bo.aassetside.edspay.EdspayInvestorInfoBo;

import com.ald.fanbei.api.biz.service.JsdESdkService;
import com.ald.fanbei.api.biz.service.JsdLegalContractPdfCreateService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.third.AbstractThird;

import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.BizAssetSideRespCode;

import com.ald.fanbei.api.common.util.*;

import com.ald.fanbei.api.dal.dao.JsdUserSealDao;

import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.dal.domain.JsdUserSealDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.sf.json.JSONArray;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chengkang 2017年11月29日 16:55:23
 * @类描述：和资产方系统调用工具类
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("eProtocolUtil")
public class EdsPayProtocolUtil extends AbstractThird {

    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdLegalContractPdfCreateService jsdLegalContractPdfCreateService;
    @Resource
    JsdUserSealDao jsdUserSealDao;
    @Resource
    JsdESdkService jsdESdkService;
    @Resource
    RedisTemplate redisTemplate;

    public AssetSideRespBo giveBackPdfInfo(String timestamp, String data, String sign, String appId) {
        // 响应数据,默认成功
        AssetSideRespBo notifyRespBo = new AssetSideRespBo();
        EdspayBackPdfReqBo edspayBackPdfReqBo = null;
        try {
            //获取对应资产方配置信息
            JsdResourceDo assideResourceInfo = getAssetSideConfigInfo(appId);
            if (assideResourceInfo == null) {
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_APPID_ERROR);
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
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_TIMESTAMP_ERROR);
                return notifyRespBo;
            }
            //签名验证相关值处理
            String realDataJson = "";

            try {
                realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
                edspayBackPdfReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayBackPdfReqBo.class);
            } catch (Exception e) {
                logger.error("eProtocolUtil giveBackPdfInfo parseJosn error", e);
            } finally {
                logger.info("eProtocolUtil giveBackPdfInfo,appId=" + appId + ",reqJsonData=" + realDataJson + ",sendTime=" + timestamp);
            }
            if (edspayBackPdfReqBo == null) {
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
            String orderNo = edspayBackPdfReqBo.getOrderNo();
            String protocolUrl = edspayBackPdfReqBo.getProtocolUrl();
            Integer debtType = edspayBackPdfReqBo.getDebtType();
            String borrowerName = edspayBackPdfReqBo.getBorrowerName();
            List<EdspayInvestorInfoBo> investorList = edspayBackPdfReqBo.getInvestorList();
            List<EdspayInvestorInfoBo> list = JSONArray.toList(JSONArray.fromObject(investorList), EdspayInvestorInfoBo.class);
            if (orderNo == null) {
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            if (borrowerName == null) {
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            if (protocolUrl == null) {
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            if (debtType == null) {
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            if (protocolUrl == null) {
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            //具体操作
//            String url = afLegalContractPdfCreateServiceV2.getProtocalLegalByType(debtType, orderNo, protocolUrl, borrowerName, list);
            String url = jsdLegalContractPdfCreateService.getProtocalLegalWithOutLenderByType(debtType, orderNo, protocolUrl, borrowerName, list);
            if (url == null || "".equals(url)) {
                logger.error("eProtocolUtil giveBackPdfInfo url exist error records,appId=" + appId + ",data=" + data +",orderNo = "+ orderNo +",debtType =" + debtType +",protocolUrl =" + protocolUrl +",borrowerName = "+borrowerName+",sendTime=" + timestamp);
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.APPLICATION_ERROR);
                return notifyRespBo;
            }
            notifyRespBo.setData(url);
            notifyRespBo.resetRespInfo(BizAssetSideRespCode.SUCCESS);
            logger.info("eProtocolUtil giveBackPdfInfo url=" + url + ",appId=" + appId + ",sendTime=" + timestamp);
        } catch (Exception e) {
            //系统异常
            logger.error("eProtocolUtil giveBackPdfInfo error,appId=" + appId +",data=" + JSONObject.toJSONString(edspayBackPdfReqBo) +",sendTime=" + timestamp, e);
            notifyRespBo.resetRespInfo(BizAssetSideRespCode.APPLICATION_ERROR);
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
    private JsdResourceDo getAssetSideConfigInfo(String appId) {
        //资产方对应配置信息校验
        JsdResourceDo assideResourceInfo = jsdResourceService.getByTypeAngSecType(ResourceType.ASSET_SIDE_CONFIG.getCode(), appId);
        if (assideResourceInfo == null || !"O".equals(assideResourceInfo.getValue4())) {
            return null;
        }
        return assideResourceInfo;
    }

    public AssetSideRespBo giveBackSealInfo(String timestamp, String data, String sign, String appId) {
        // 响应数据,默认成功
        AssetSideRespBo notifyRespBo = new AssetSideRespBo();
        try {
            //获取对应资产方配置信息
            JsdResourceDo assideResourceInfo = getAssetSideConfigInfo(appId);
            if (assideResourceInfo == null) {
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.VALIDATE_APPID_ERROR);
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
            List<EdspayBackSealReqBo> edspayBackSealReqBoList = null;
            try {
                realDataJson = AesUtil.decryptFromBase64(data, assideResourceInfo.getValue2());
                edspayBackSealReqBoList = JSONArray.toList(JSONArray.fromObject(realDataJson), EdspayBackSealReqBo.class);
//                edspayBackSealReqBo = JSON.toJavaObject(JSON.parseObject(realDataJson), EdspayBackSealReqBo.class);
            } catch (Exception e) {
                logger.error("eProtocolUtil giveBackSealInfo parseJosn error", e);
            } finally {
                logger.info("eProtocolUtil giveBackSealInfo,appId=" + appId + ",reqJsonData=" + realDataJson + ",sendTime=" + timestamp);
            }
            if (edspayBackSealReqBoList == null) {
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
//            List<AfUserSealDo> afUserSealDoList = edspayBackSealReqBo.getAfUserSealDoList();
//            List<AfUserSealDo> list = JSONArray.toList(JSONArray.fromObject(afUserSealDoList), AfUserSealDo.class);
            if (edspayBackSealReqBoList == null || edspayBackSealReqBoList.size() <= 0) {
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.INVALID_PARAMETER);
                return notifyRespBo;
            }
            List<JsdUserSealDo> newSealList = new ArrayList<>();
            //具体操作
            for (EdspayBackSealReqBo seal : edspayBackSealReqBoList) {
                JsdUserSealDo userSealDo = new JsdUserSealDo();
                userSealDo.setEdspayUserCardId(seal.getEdspayUserCardId());
                userSealDo.setUserType(seal.getUserType());
                JsdUserSealDo sealDo = jsdUserSealDao.selectBySealInfo(userSealDo);
                if (sealDo != null) {
                    newSealList.add(sealDo);
                } else {
                    JsdUserDo investorUserDo = new JsdUserDo();
                    investorUserDo.setMobile(seal.getMobile());
                    //          investorUserDo.setRid((Long)map.get("investorCardId"));
                    investorUserDo.setRealName(seal.getRealName());
                    investorUserDo.setMajiabaoName("edspay");
                    investorUserDo.setIdNumber(seal.getEdspayUserCardId());
                    investorUserDo.setRealName(seal.getRealName());
                    JsdUserSealDo afUserSealDo = jsdESdkService.getSealPersonal(investorUserDo);
                    if (afUserSealDo != null) {
                        newSealList.add(afUserSealDo);
                    }
                }
            }
            if (newSealList.size() <= 0) {
                logger.error("eProtocolUtil giveBackSealInfo newSealList exist error records,appId=" + appId + ",sendTime=" + timestamp);
                notifyRespBo.resetRespInfo(BizAssetSideRespCode.APPLICATION_ERROR);
                return notifyRespBo;
            }
            logger.info("eProtocolUtil giveBackSealInfo newSealList=", JSON.toJSONString(newSealList) + ",appId=" + appId + ",sendTime=" + timestamp);
            notifyRespBo.setData(JSON.toJSONString(newSealList));
            notifyRespBo.resetRespInfo(BizAssetSideRespCode.SUCCESS);
        } catch (Exception e) {
            //系统异常
            logger.error("eProtocolUtil giveBackSealInfo error,appId=" + appId + ",sendTime=" + timestamp, e);
            notifyRespBo.resetRespInfo(BizAssetSideRespCode.APPLICATION_ERROR);
        }
        return notifyRespBo;
    }

}
