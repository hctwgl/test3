package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月18日上午10:08:37
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afGoodsService")
public class AfGoodsServiceImpl implements AfGoodsService{

	@Resource
	AfGoodsDao afGoodsDao;
	@Override
	public List<AfGoodsDo> getCateGoodsList(AfGoodsQuery query) {
		return afGoodsDao.getCateGoodsList(query);
	}
	@Override
	public AfGoodsDo getGoodsById(Long rid) {
		return afGoodsDao.getGoodsById(rid);
	}

}
