package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfSupGameService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.GameGoodsType;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.dao.AfSupGameDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfSupGameDo;
import com.ald.fanbei.api.dal.domain.dto.GameGoods;
import com.ald.fanbei.api.dal.domain.dto.GameGoodsGroup;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * 新人专享ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-22 13:57:28 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afSupGameService")
public class AfSupGameServiceImpl extends ParentServiceImpl<AfSupGameDo, Long> implements AfSupGameService {

    private static final Logger logger = LoggerFactory.getLogger(AfSupGameServiceImpl.class);

    @Resource
    private AfSupGameDao afSupGameDao;

    @Override
    public BaseDao<AfSupGameDo, Long> getDao() {
	return afSupGameDao;
    }

    @Override
    public List<GameGoodsGroup> getGoodsList(String type) {

	if (type.equals(GameGoodsType.GAME.getCode()))
	    return afSupGameDao.getGameGoodsList(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE));
	else if (type.equals(GameGoodsType.AMUSEMENT.getCode()))
	    return afSupGameDao.getAmusementGoodsList(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE));
	else {
	    logger.error("Game pay type is not support :" + type);
	    return null;
	}
    }

    @Override
    public List<GameGoods> getHotGoodsList(String type) {

	return afSupGameDao.getHotGoodsList(type, ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE));
    }
}