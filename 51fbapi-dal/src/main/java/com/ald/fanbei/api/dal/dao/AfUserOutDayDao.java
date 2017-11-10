package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserOutDayDo;
import org.apache.ibatis.annotations.Param;

/**
 * 
* @ClassName: AfUserOutDayDao 
* @Description: 
* @author yuyue
* @date 2017年11月9日 下午5:44:03 
*
 */
public interface AfUserOutDayDao {
	
    int addserOutDay(AfUserOutDayDo afUserOutDayDo);
    
    AfUserOutDayDo getUserOutDayByUserId(@Param("userId") long userId);
    
    int countUserOutDayLogByUserId(@Param("userId") long userId);
}
