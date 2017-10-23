package com.ald.fanbei.api.biz.service;


import com.ald.fanbei.api.dal.domain.AfGoodsCategoryDo;

import java.util.List;

/**
 * 
 * @类描述：类目service
 * @author chefeipeng 2017年2月16日下午5:24:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsCategoryService {

	/**
	 * 增加记录
	 * @param afGoodsCategoryDo
	 * @return
	 */
	int addGoodsCategory(AfGoodsCategoryDo afGoodsCategoryDo);
	/**
	 * 更新记录
	 * @param afGoodsCategoryDo
	 * @return
	 */
	int updateGoodsCategory(AfGoodsCategoryDo afGoodsCategoryDo);

	/**
	 * 查询一级商品类别
	 * @return
	 */
	List<AfGoodsCategoryDo> selectOneLevel();

	/**
	 * 查询二级商品类别
	 * @param rid
	 * @return
	 */
	List<AfGoodsCategoryDo> selectSecondLevel(Long rid);

	/**
	 * 查询三级商品类别
	 * @param rid
	 * @return
	 */
	List<AfGoodsCategoryDo> selectThirdLevel(Long rid);
	
}
