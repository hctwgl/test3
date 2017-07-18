/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfIdNumberDo;

/**
 * @类描述：
 * @author suweili 2017年4月17日下午6:32:22
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfIdNumberDao {
	  /**
	    * 增加记录
	    * @param afIdNumberDo
	    * @return
	    */
	    int addIdNumber(AfIdNumberDo afIdNumberDo);
	   /**
	    * 更新记录
	    * @param afIdNumberDo
	    * @return
	    */
	    int updateIdNumber(AfIdNumberDo afIdNumberDo);
	    
	    AfIdNumberDo selectUserIdNumberByUserId(@Param("userId")Long userId);
}
