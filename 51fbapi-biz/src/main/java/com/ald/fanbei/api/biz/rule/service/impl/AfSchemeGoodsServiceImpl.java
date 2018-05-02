package com.ald.fanbei.api.biz.rule.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.dal.dao.AfSchemeGoodsDao;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年6月6日下午1:59:28
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afSchemeGoodsService")
public class AfSchemeGoodsServiceImpl implements AfSchemeGoodsService {

	@Resource
	AfSchemeGoodsDao afSchemeGoodsDao;
	
	@Resource
	BizCacheUtil bizCacheUtil;
	
	
	@Override
	public AfSchemeGoodsDo getSchemeGoodsByGoodsId(Long goodsId) {
		// 每10分钟清除一次缓存
		String schemeGoodsKey = "GET_SCHEME_GOODS_BY_GOODS_ID_" + goodsId;
		AfSchemeGoodsDo schemeGoodsDo = (AfSchemeGoodsDo)bizCacheUtil.getObject(schemeGoodsKey);
		if(schemeGoodsDo == null) {
			schemeGoodsDo = afSchemeGoodsDao.getSchemeGoodsByGoodsId(goodsId);
			bizCacheUtil.saveObject(schemeGoodsKey, schemeGoodsDo);
		}
		return schemeGoodsDo;
	}

}
