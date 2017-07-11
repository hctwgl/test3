/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserAddressDo;

/**
 * @类描述：
 * @author suweili 2017年4月17日下午6:46:27
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAddressDao {
	   /**
	    * 增加记录
	    * @param afUserAddressDo
	    * @return
	    */
	    int addUserAddress(AfUserAddressDo afUserAddressDo);
	   /**
	    * 更新记录
	    * @param afUserAddressDo
	    * @return
	    */
	    int updateUserAddress(AfUserAddressDo afUserAddressDo);
	    
	    /**
	     * 删除地址
	     * @param rid
	     * @return
	     */
	    int deleteUserAddress(@Param("rid")Long rid);

	    /**
	     * 查询地址列表
	     * @param userId
	     * @return
	     */
	   List<AfUserAddressDo> selectUserAddressByUserId(@Param("userId")Long userId);
	   /**
	    * 根据rid查询地址
	    * @param rid
	    * @return
	    */
	   AfUserAddressDo selectUserAddressByrid(@Param("rid")Long rid);
	   /**
	    * 获取默认地址
	    * @param userId
	    * @return
	    */
	   AfUserAddressDo selectUserAddressDefaultByUserId(@Param("userId")Long userId);
	   
	   /**
	    * 删除默认地址的时候, 重新设置默认地址
	    * @return
	    */
	   int reselectTheDefaultAddress(@Param("userId") Long userId);
	   
	   /**
	    * 通过userId获取用户未被删除掉的地址数量
	    * @param userId
	    * @return
	    */
	   int getCountOfAddressByUserId(@Param("userId") Long userId);

	    
}
