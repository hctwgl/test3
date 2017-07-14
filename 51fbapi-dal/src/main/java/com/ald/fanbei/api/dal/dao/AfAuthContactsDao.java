package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfAuthContactsDo;

/**
 * @类现描述：
 * 
 * @author chenjinhu 2017年2月16日 下午2:23:44
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAuthContactsDao {
    /**
     * 增加记录
     * 
     * @param afAuthContactsDo
     * @return
     */
    int addAuthContacts(@Param("afAuthContactsDos") List<AfAuthContactsDo> afAuthContactsDos);

    /**
     * @方法描述： 根据用户Id查询通讯录
     * 
     * @author huyang 2017年4月5日下午1:59:47
     * @param userId
     *            --用户主键
     * @return
     * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
     */
    List<AfAuthContactsDo> getContactsByUserId(@Param("userId") Long userId);
}
