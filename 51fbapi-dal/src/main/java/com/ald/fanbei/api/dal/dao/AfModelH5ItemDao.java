/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfModelH5ItemDo;

/**
 * @类描述：
 * @author suweili 2017年2月23日下午10:32:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfModelH5ItemDao {
	List<AfModelH5ItemDo> getModelH5ItemListByModelIdAndModelType(@Param("modelId")Long modelId,@Param("modelType")Long modelType);

}
