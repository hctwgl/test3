/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfTdFraudSerVice;
import com.ald.fanbei.api.dal.dao.AfTdFraudDao;
import com.ald.fanbei.api.dal.domain.AfTdFraudDo;

/**
 * @类描述：
 * @author suweili 2017年3月31日下午5:02:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afTdFraudSerVice")
public class AfTdFraudSerViceImpl implements AfTdFraudSerVice {

	@Resource
	AfTdFraudDao afTdFraudDao;
	
	@Override
	public int addTdFraud(AfTdFraudDo afTdFraudDo) {
		return afTdFraudDao.addTdFraud(afTdFraudDo);
	}

	
	@Override
	public int updateTdFraud(AfTdFraudDo afTdFraudDo) {
		return afTdFraudDao.updateTdFraud(afTdFraudDo);
	}

}
