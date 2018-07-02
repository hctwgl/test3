/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfFeedBackService;
import com.ald.fanbei.api.dal.dao.AfFeedBackDao;
import com.ald.fanbei.api.dal.domain.AfFeedBackDo;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午1:39:59
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afFeedBackService")
public class AfFeedBackServiceImpl implements AfFeedBackService {
 
	@Resource
	AfFeedBackDao afFeedBackDao;
	@Override
	public int addFeedBack(AfFeedBackDo afFeedBackDo) {
		return afFeedBackDao.addFeedBack(afFeedBackDo);
	}

}
