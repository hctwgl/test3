package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;

/**
 * @类现描述：
 * @author caowu 2017年1月5日 下午17:25:56
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAuthStatusService {
    /**
     * 增加一个认证场景
     * @param afUserAuthStatusDo
     * @return
     */
    Long addAfUserAuthStatus(AfUserAuthStatusDo afUserAuthStatusDo);

    /**
     * 查询该场景 是否存在认证
     * @param userId
     * @param scene
     * @param status
     * @return
     */
    AfUserAuthStatusDo selectAfUserAuthStatusByUserIdAndScene(Long userId, String scene,String status);

}
