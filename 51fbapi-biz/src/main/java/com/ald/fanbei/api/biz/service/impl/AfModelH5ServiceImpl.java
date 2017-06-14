/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfModelH5Service;
import com.ald.fanbei.api.dal.dao.AfModelH5Dao;
import com.ald.fanbei.api.dal.domain.AfModelH5Do;

/**
 * @类描述：
 * @author suweili 2017年6月14日下午3:09:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afModelH5Service")
public class AfModelH5ServiceImpl implements AfModelH5Service {

	@Resource
	AfModelH5Dao afModelH5Dao;
	@Override
	public int addModelH5(AfModelH5Do afModelH5Do) {
		return afModelH5Dao.addModelH5(afModelH5Do);
	}

	
	@Override
	public int updateModelH5(AfModelH5Do afModelH5Do) {
		return afModelH5Dao.updateModelH5(afModelH5Do);
	}

	@Override
	public AfModelH5Do selectMordelH5ById(Long rid) {
		return afModelH5Dao.selectMordelH5ById(rid);
	}

}
