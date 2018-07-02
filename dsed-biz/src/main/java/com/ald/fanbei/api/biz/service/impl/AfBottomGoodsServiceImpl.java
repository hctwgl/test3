/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfBottomGoodsService;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBottomGoodsDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBottomGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfBottomGoodsDto;
import com.ald.fanbei.api.dal.domain.query.AfBottomGoodsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *
 * @author wangli
 * @date 2018/4/11 8:18
 */
@Service("afBottomGoodsService")
public class AfBottomGoodsServiceImpl extends ParentServiceImpl<AfBottomGoodsDo, Long>
		implements AfBottomGoodsService {

	@Autowired
	private AfBottomGoodsDao afBottomGoodsDao;

	@Override
	public List<AfBottomGoodsDto> findGoodsByPageFlag(AfBottomGoodsQuery query) {
		if (StringUtil.isBlank(query.getPageFlag())) {
			throw new IllegalArgumentException("pageFlag cannot be null");
		}
		return afBottomGoodsDao.findGoodsByPageFlag(query);
	}

	@Override
	public BaseDao<AfBottomGoodsDo, Long> getDao() {
		return afBottomGoodsDao;
	}
}
