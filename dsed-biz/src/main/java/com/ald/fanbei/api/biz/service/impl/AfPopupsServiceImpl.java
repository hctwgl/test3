/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfPopupsService;
import com.ald.fanbei.api.dal.dao.AfPopupsDao;
import com.ald.fanbei.api.dal.domain.AfPopupsDo;


/**
 * @类描述：
 * @author 车飞鹏 2017年6月20日下午4:47:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afPopupsService")
public class AfPopupsServiceImpl  implements AfPopupsService {

	@Resource
	AfPopupsDao afPopupsDao;

	@Override
	public AfPopupsDo selectPopups(Long id){
		return afPopupsDao.selectPopups(id);
	}


	@Override
	public int updatePopups(AfPopupsDo afPopupsDo){
		return afPopupsDao.updatePopups(afPopupsDo);
	}

	@Override
	public int updatePopupsReachAmount(Long id) {
		return afPopupsDao.updatePopupsReachAmount(id);
	}

}
