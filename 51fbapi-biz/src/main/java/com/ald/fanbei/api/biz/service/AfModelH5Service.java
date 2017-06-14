/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfModelH5Do;

/**
 * @类描述：
 * @author suweili 2017年6月14日下午3:08:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfModelH5Service {
	  /**
	    * 增加记录
	    * @param afModelH5Do
	    * @return
	    */
	    int addModelH5(AfModelH5Do afModelH5Do);
	   /**
	    * 更新记录
	    * @param afModelH5Do
	    * @return
	    */
	    int updateModelH5(AfModelH5Do afModelH5Do);
	    
	    AfModelH5Do selectMordelH5ById(Long rid);
}
