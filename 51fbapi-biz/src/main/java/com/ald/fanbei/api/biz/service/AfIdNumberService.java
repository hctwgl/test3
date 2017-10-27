/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfIdNumberDo;

/**
 * @类描述：
 * @author suweili 2017年4月17日下午6:36:44
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfIdNumberService {
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
	    
	    /**
	     * 根据userId获取省份证的信息
	     */
	    AfIdNumberDo selectUserIdNumberByUserId(Long userId);

}
