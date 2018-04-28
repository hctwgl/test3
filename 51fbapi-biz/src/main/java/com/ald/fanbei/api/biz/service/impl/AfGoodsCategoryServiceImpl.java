/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfActivityModelService;
import com.ald.fanbei.api.biz.service.AfGoodsCategoryService;
import com.ald.fanbei.api.dal.dao.AfActivityModelDao;
import com.ald.fanbei.api.dal.dao.AfGoodsCategoryDao;
import com.ald.fanbei.api.dal.domain.AfActivityModelDo;
import com.ald.fanbei.api.dal.domain.AfGoodsCategoryDo;
import com.ald.fanbei.api.dal.domain.dto.AfGoodsCategoryDto;
import com.ald.fanbei.api.dal.domain.query.AfGoodsCategoryQuery;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

/**
 * @类描述：
 * @author chefeipeng 2017年6月20日下午4:47:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afGoodsCategoryService")
public class AfGoodsCategoryServiceImpl implements AfGoodsCategoryService {

	@Resource
	AfGoodsCategoryDao afGoodsCategoryDao;

	@Override
	public int addGoodsCategory(AfGoodsCategoryDo afGoodsCategoryDo){
		return afGoodsCategoryDao.addGoodsCategory(afGoodsCategoryDo);
	}
	
	@Override
	public int updateGoodsCategory(AfGoodsCategoryDo afGoodsCategoryDo){
		return afGoodsCategoryDao.updateGoodsCategory(afGoodsCategoryDo);
	}

	@Override
	public List<AfGoodsCategoryDo> selectOneLevel(){
		return afGoodsCategoryDao.selectOneLevel();
	}

	@Override
	public List<AfGoodsCategoryDo> selectSecondLevel(Long rid){
		return afGoodsCategoryDao.selectSecondLevel(rid);
	}

	@Override
	public List<AfGoodsCategoryDo> selectThirdLevel(Long rid){
		return afGoodsCategoryDao.selectThirdLevel(rid);
	}

	@Override
	public List<AfGoodsCategoryDto> selectGoodsInformation(AfGoodsCategoryQuery query) {return afGoodsCategoryDao.selectGoodsInformation(query);}

	@Override
	public AfGoodsCategoryDo getParentDirectoryByName(String name) {
	    // TODO Auto-generated method stub
	    	return afGoodsCategoryDao.getParentDirectoryByName(name);
	}

	@Override
	public List<AfGoodsCategoryDo> listByParentIdAndLevel(AfGoodsCategoryDo queryAfGoodsCategory) {
	    // TODO Auto-generated method stub
	    	return afGoodsCategoryDao.listByParentIdAndLevel(queryAfGoodsCategory);
	}

	@Override
	public AfGoodsCategoryDo getGoodsCategoryById(Long categoryId) {
		return afGoodsCategoryDao.getGoodsCategoryById(categoryId);
	}
}
