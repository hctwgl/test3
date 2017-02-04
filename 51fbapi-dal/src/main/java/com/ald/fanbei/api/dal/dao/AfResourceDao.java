package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfResourceDo;

public interface AfResourceDao {
	/**
	 * 获取首页配置信息
	 * @param allTypes
	 * @return
	 */
	 List<AfResourceDo> selectHomeConfigByAllTypes();
	 
	 /**
	  * 获取type类型的配置信息
	  * @param type
	  * @return
	  */
	 
	 List<AfResourceDo> getConfigByTypes(@Param("type")String type);
 
}
