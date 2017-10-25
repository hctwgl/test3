package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfGameDo;
/**
 * 
 *@类现描述：
 *@author chenjinhu 2017年6月3日 下午5:14:20
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGameDao {
	   /**
	    * 增加记录
	    * @param afGameDo
	    * @return
	    */
	    int addGame(AfGameDo afGameDo);
	    
	    /**
	     * 通过id查询
	     * @param id
	     * @return
	     */
	    AfGameDo getById(Long id);
	    
	    /**
	     * 通过编号查询
	     * @param id
	     * @return
	     */
	    AfGameDo getByCode(String code);
}
