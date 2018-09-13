package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.dal.domain.JsdUserSealDo;

/**
 * @author guoshuaiqiang 2017年10月27日下午2:26:04
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdUserSealService {

    JsdUserSealDo getUserSealById(Long id);

    JsdUserSealDo getUserSealByUserName(String userName);

    JsdUserSealDo getUserSeal(JsdUserDo afUserDo);

    int insertUserSeal(JsdUserSealDo record);

    int updateUserSealByUserId(JsdUserSealDo record);

}
