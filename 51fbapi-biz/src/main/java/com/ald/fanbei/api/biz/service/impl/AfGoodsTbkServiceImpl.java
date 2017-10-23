package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfGoodsTbkService;
import com.ald.fanbei.api.dal.domain.AfGoodsTbkDo;
import org.springframework.stereotype.Service;

/**
 * 
 * @类描述：类目service
 * @author chefeipeng 2017年2月16日下午5:24:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("AfGoodsTbkService")
public class AfGoodsTbkServiceImpl implements AfGoodsTbkService {

	/**
	 * 增加记录
	 * @param afGoodsTbkDo
	 * @return
	 */
	@Override
	public int addGoodsTbk(AfGoodsTbkDo afGoodsTbkDo){
		return 1;
	}
	/**
	 * 更新记录
	 * @param afGoodsTbkDo
	 * @return
	 */
	@Override
	public int updateGoodsTbk(AfGoodsTbkDo afGoodsTbkDo){
		return 1;
	}

	
}
