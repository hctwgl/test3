package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserSearchService;
import com.ald.fanbei.api.dal.dao.AfUserSearchDao;
import com.ald.fanbei.api.dal.domain.AfUserSearchDo;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月21日下午16:20:46
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("afUserSearchService")
public class AfUserSearchServiceImpl implements AfUserSearchService{

	@Resource
	private AfUserSearchDao afUserSearchDao;
	
	@Override
	public int addUserSearch(AfUserSearchDo afUserSearchDo) {
		return afUserSearchDao.addUserSearch(afUserSearchDo);
	}

}
