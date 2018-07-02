package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfPushManageDo;

import java.util.List;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月22日下午2:26:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfPushManageService {

	int updatePushManage(AfPushManageDo afPushManageDo);

	AfPushManageDo getPushById(Long pushId);

}
