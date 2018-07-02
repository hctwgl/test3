package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfGoodsTbkDo;


/**
 * @类描述：
 * @author chefeipeng  2017年6月20日上午9:49:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsTbkDao {

	/**
	 * 增加记录
	 * @param afGoodsTbkDo
	 * @return
	 */
	int addGoodsTbk(AfGoodsTbkDo afGoodsTbkDo);
	/**
	 * 更新记录
	 * @param afGoodsTbkDo
	 * @return
	 */
	int updateGoodsTbk(AfGoodsTbkDo afGoodsTbkDo);

}
