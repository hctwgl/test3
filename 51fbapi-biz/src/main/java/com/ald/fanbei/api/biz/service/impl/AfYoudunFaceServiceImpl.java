package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfYoudunFaceService;
import com.ald.fanbei.api.dal.dao.AfYoudunFaceDao;
import com.ald.fanbei.api.dal.domain.AfYoudunFaceDo;

/**
 *@类描述：
 *@author 陈金虎 2017年1月21日 下午3:57:23
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
@Service("afYoudunFaceService")
public class AfYoudunFaceServiceImpl implements AfYoudunFaceService {

	@Resource
	AfYoudunFaceDao afYoudunFaceDao;
	
	@Override
	public void addYoudunFace(AfYoudunFaceDo afYoudunFaceDo) {
		afYoudunFaceDao.addYoudunFace(afYoudunFaceDo);
	}

}
