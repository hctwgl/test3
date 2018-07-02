package com.ald.fanbei.api.biz.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.OrderSecType;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.dao.AfShopDao;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.query.AfShopQuery;

/**
 * @类描述：
 * @author xiaotianjian 2017年3月23日下午2:16:08
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afShopService")
public class AfShopServiceImpl extends BaseService implements AfShopService {

    @Resource
    AfShopDao afShopDao;

    @Override
    public AfShopDo getShopById(Long shopId) {
	return afShopDao.getShopById(shopId);
    }

    @Override
    public AfShopDo getShopByPlantNameAndTypeAndServiceProvider(String platformName, String type, String serviceProvider) {
	return afShopDao.getShopByPlantNameAndTypeAndServiceProvider(platformName, type, serviceProvider);
    }

    @Override
    public List<AfShopDo> getShopList(AfShopQuery query) {
	String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
	if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
	    return afShopDao.getShopList(query);
	} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
	    return afShopDao.getPreEnvShopList(query);
	}
	return null;
    }

    @Override
    public AfShopDo getShopInfoBySecType(AfShopDo afShopDo) {
	// TODO Auto-generated method stub
	return afShopDao.getShopInfoBySecType(afShopDo);
    }

    @Override
    public AfShopDo getShopInfoBySecTypeOpen(AfShopDo afShopDo) {
	// TODO Auto-generated method stub
	return afShopDao.getShopInfoBySecTypeOpen(afShopDo);
    }

    @Override
    public Long getWaiMainShopId() {

	return afShopDao.getWaiMainShopId();
    }

    // 根据测试，线上环境区别地址
    @Override
    public String parseBoluomeUrl(String shopUrl, String platform, String shopType, Long userId, String mobile) {
	logger.info("parseBoluomeUrl :" + shopUrl + " " + platform + " " + shopType);
	if (platform.equals("BOLUOME")) {
	    if (!OrderSecType.SUP_GAME.getCode().equals(shopType)) {
		String type = shopUrl.substring(shopUrl.lastIndexOf("/") + 1, shopUrl.length());
		if ("didi".equals(type)) {
		    type = "yongche/" + type;
		}
		shopUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_API_URL) + "/" + type + "?";

		Map<String, String> buildParams = new HashMap<String, String>();
		buildParams.put(BoluomeCore.CUSTOMER_USER_ID, userId + StringUtils.EMPTY);
		buildParams.put(BoluomeCore.CUSTOMER_USER_PHONE, mobile);
		buildParams.put(BoluomeCore.TIME_STAMP, System.currentTimeMillis() / 1000 + StringUtils.EMPTY);

		String sign = BoluomeCore.buildSignStr(buildParams);
		buildParams.put(BoluomeCore.SIGN, sign);
		String paramsStr = BoluomeCore.createLinkString(buildParams);

		return shopUrl + paramsStr;
	    }
	}

	return shopUrl;
    }
}