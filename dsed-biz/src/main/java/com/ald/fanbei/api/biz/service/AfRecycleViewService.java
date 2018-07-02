package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfRecycleViewDo;
import com.ald.fanbei.api.dal.domain.AfRecycleViewStatisticsDo;
import com.ald.fanbei.api.dal.domain.query.AfRecycleViewQuery;

/**
 * 
 * @类描述： 有得卖  回收业务 页面访问
 * @author weiqingeng 2018年2月27日上午9:55:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRecycleViewService {

	Integer addRecycleView(AfRecycleViewQuery afRecycleviewQuery);//新增用户访问记录

	Integer updateRecycleView(AfRecycleViewQuery afRecycleviewQuery);//修改用户访问记录

	AfRecycleViewDo getRecycleViewByUid(AfRecycleViewQuery afRecycleViewQuery);//查看用户是否存在页面访问记录

}
