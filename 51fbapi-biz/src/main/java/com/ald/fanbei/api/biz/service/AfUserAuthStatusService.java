package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;

/**
 * @类现描述：
 * @author caowu 2017年1月5日 下午17:25:56
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAuthStatusService {
    /**
     * 增加或更新一个认证场景
     * @param afUserAuthStatusDo
     * @return
     */
    void addOrUpdateAfUserAuthStatus(AfUserAuthStatusDo afUserAuthStatusDo);

    /**
     * 查询该场景 是否存在认证
     * @param userId
     * @param scene
     * @param status
     * @return
     */
    AfUserAuthStatusDo selectAfUserAuthStatusByCondition(Long userId, String scene,String status);

    /**
     * 查询场景认证
     * @param userId
     * @param scene
     * @return
     */
    AfUserAuthStatusDo getAfUserAuthStatusByUserIdAndScene(Long userId, String scene);

    List<AfUserAuthStatusDo> selectAfUserAuthStatusByUserIdAndStatus(Long userId, String status);    

    List<AfUserAuthStatusDo> selectAfUserAuthStatusByUserId(Long userId);

    /**
     * 检测指定场景下用户是否通过信用认证
     */
    boolean isPass(String scene, Long userId);

	String getBldOpenStatus(Long userId);

    /**
     * 更新场景状态
     * @Param userId
     * @Param scene
     * @Param status
     * @return
     * **/
    int updateAfUserAuthStatusByUserId(Long userId, String scene, String status);
    
    /**
     * 增加场景状态
     * @Param userId
     * @Param scene
     * @Param status
     * @return
     * **/
    int updateAfUserAuthStatus(Long userId, String scene, String status);
}
