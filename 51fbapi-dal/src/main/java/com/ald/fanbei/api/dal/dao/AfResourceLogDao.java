/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfResourceLogDo;

/**
 * @类描述：
 * @author suweili 2017年5月8日下午5:03:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfResourceLogDao {
	  /**
	    * 增加记录
	    * @param afResourceLogDo
	    * @return
	    */
	    int addResourceLog(AfResourceLogDo afResourceLogDo);
	   /**
	    * 更新记录
	    * @param afResourceLogDo
	    * @return
	    */
	    int updateResourceLog(AfResourceLogDo afResourceLogDo);
	    /**
	     * 获取日志资源修改日志
	     * @param type
	     * @param secType
	     * @param gmtCreate
	     * @return
	     */
	    AfResourceLogDo   selectResourceLogTypeAndSecType(@Param("type")String type,@Param("secType")String secType,@Param("gmtCreate")Date gmtCreate);
}
