package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfCategoryService;
import com.ald.fanbei.api.dal.dao.AfCategoryDao;
import com.ald.fanbei.api.dal.domain.AfCategoryDo;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月17日下午21:52:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afCategoryService")
public class AfCategoryServiceImpl implements AfCategoryService{

	@Resource
	private AfCategoryDao afCategoryDao;
	
	@Override
	public List<AfCategoryDo> getCategoryList() {
		return afCategoryDao.getCategoryList();
	}

}
