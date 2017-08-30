package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfSubjectGoodsService;
import com.ald.fanbei.api.dal.dao.AfSubjectGoodsDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfSubjectGoodsDo;
import com.ald.fanbei.api.dal.domain.query.AfSubjectGoodsQuery;

/**
 * 
 *@类描述：AfSubjectServiceImpl
 *@author 何鑫 2017年1月18日  12:51:33
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afSubjectGoodsService")
public class AfSubjectGoodsServiceImpl implements AfSubjectGoodsService{

	@Resource
	private AfSubjectGoodsDao afSubjectGoodsDao;

	@Override
	public int addSubjectGoods(AfSubjectGoodsDo afSubjectGoodsDo) {
		
		return afSubjectGoodsDao.addSubjectGoods(afSubjectGoodsDo);
	}

	@Override
	public List<AfGoodsDo> listAllSubjectGoods(AfSubjectGoodsQuery query) {
		
		return afSubjectGoodsDao.listAllSubjectGoods(query);
	}

	@Override
	public List<AfGoodsDo> listQualitySubjectGoods() {
		return afSubjectGoodsDao.listQualitySubjectGoods();
	}

	@Override
	public List<AfGoodsDo> listQualitySubjectGoodsByParentId(Long parentId) {
		return afSubjectGoodsDao.listQualitySubjectGoodsByParentId(parentId);
	}
	
	@Override
	public List<AfSubjectGoodsDo> getSubjectGoodsByGoodsId(Long goodsId) {
		return afSubjectGoodsDao.getSubjectGoodsByGoodsId(goodsId);
	}

}
