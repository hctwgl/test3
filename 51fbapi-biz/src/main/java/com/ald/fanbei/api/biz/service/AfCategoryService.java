package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfCategoryDo;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月17日下午21:49:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCategoryService {

	/**
	 * 获取三级类目显示列表
	 * @return
	 */
	List<AfCategoryDo> getCategoryList();
}
