package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfContactsOldService;
import com.ald.fanbei.api.dal.dao.AfContactsOldDao;
import com.ald.fanbei.api.dal.domain.AfContactsOldDo;

/**
 * @类现描述：
 * @author fumeiai 2017年4月19日 下午4:15:39
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afContactsOldService")
public class AfContactsOldServiceImpl implements AfContactsOldService {

	@Resource
	AfContactsOldDao afContactsOldDao;
	
	@Override
	public AfContactsOldDo getAfContactsByUserId(Long userId) {
		return afContactsOldDao.getAfContactsByUserId(userId);
	}

}
