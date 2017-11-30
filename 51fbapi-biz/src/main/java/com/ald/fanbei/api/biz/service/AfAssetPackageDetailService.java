package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfAssetPackageDetailDo;

/**
 * 资产包与债权记录关系Service
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAssetPackageDetailService extends ParentService<AfAssetPackageDetailDo, Long>{

	/**
	 * 批量债权包明细撤回操作
	 * @param orderNos
	 */
	void batchGiveBackCreditInfo(List<String> orderNos);

}
