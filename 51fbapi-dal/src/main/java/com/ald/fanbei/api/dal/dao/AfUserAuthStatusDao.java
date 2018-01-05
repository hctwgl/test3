package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import org.apache.ibatis.annotations.Param;

/**
 *@类现描述：
 *@author caowu 2018年1月5日 下午15:34:01
 *@version
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAuthStatusDao {
    /**
     * 新增一个用户认证信息场景
     * @param afUserAuthStatusDo
     * @return
     */
    Long addAfUserAuthStatus(AfUserAuthStatusDo afUserAuthStatusDo);

    /**
     * 查询一个用户认证场景
     * @param userId
     * @param scene
     * @return
     */
    AfUserAuthStatusDo selectAfUserAuthStatusByUserIdAndScene(@Param("userId") Long userId, @Param("scene") String scene,@Param("status") String status);
}
