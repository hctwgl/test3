/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;


import com.ald.fanbei.api.dal.domain.AfUserAddressDo;

/**
 * @类描述：
 * @author suweili 2017年4月17日下午9:00:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAddressService {
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
	    int deleteUserAddress(Long rid);
	    /**
	     * 查询地址列表
	     * @param userId
	     * @return
	     */
	   List<AfUserAddressDo> selectUserAddressByUserId(Long userId);
	   /**
	    * 根据rid查询地址
	    * @param rid
	    * @return
	    */
	   AfUserAddressDo selectUserAddressByrid(Long rid);
	   /**
	    * 获取默认地址
	    * @param userId
	    * @return
	    */
	   AfUserAddressDo selectUserAddressDefaultByUserId(Long userId);
	   
	   /**
	    * 默认地址被删除 , 重新设置默认地址
	    */
	   int reselectTheDefaultAddress(Long userId);
	   
	   /**
	    * 通过userId获取未被删地址的数量
	    * @param userId
	    * @return
	    */
	   int getCountOfAddressByUserId(Long userId);
}
