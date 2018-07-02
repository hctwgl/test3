package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.query.AfUserChangXiaoDo;

import java.util.List;

/**
 * @author 江荣波 2017年2月16日下午5:24:09
 * @类描述：类目service
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserChangXiaoService {

    int insertChangxiaoUser(AfUserChangXiaoDo afUserChangXiaoDo);

    AfUserChangXiaoDo getUser(AfUserChangXiaoDo afUserChangXiaoDo);

    AfUserChangXiaoDo getUserOpen(AfUserChangXiaoDo afUserChangXiaoDo);

    int updateUserOpen(AfUserChangXiaoDo afUserChangXiaoDo);

    int updateUser(AfUserChangXiaoDo afUserChangXiaoDo);


}
