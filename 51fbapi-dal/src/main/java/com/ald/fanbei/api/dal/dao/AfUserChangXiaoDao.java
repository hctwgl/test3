package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.query.AfUserChangXiaoDo;

import java.util.List;


/**
 * @类描述：畅效广告平台与广告主对接接口
 * @author weiqineng
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserChangXiaoDao {

	int insertChangXiaoUser(AfUserChangXiaoDo afUserChangXiaoDo);

	AfUserChangXiaoDo getUser(AfUserChangXiaoDo afUserChangXiaoDo);

	AfUserChangXiaoDo getUserOpen(AfUserChangXiaoDo afUserChangXiaoDo);

	int updateUserOpen(AfUserChangXiaoDo afUserChangXiaoDo);

	int updateUser(AfUserChangXiaoDo afUserChangXiaoDo);
}
