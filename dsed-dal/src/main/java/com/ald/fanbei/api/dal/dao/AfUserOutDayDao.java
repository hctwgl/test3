package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBorrowDo;
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

	int insertUserOutDay(@Param("userId")long userId, @Param("outDay")int outDay, @Param("payDay")int payDay);

	int insertUserOutDayLog(@Param("userId")long userId, @Param("outDay")int outDay);

	int updateUserOutDay(@Param("userId")long userId, @Param("outDay")int outDay, @Param("payDay")int payDay);

}
