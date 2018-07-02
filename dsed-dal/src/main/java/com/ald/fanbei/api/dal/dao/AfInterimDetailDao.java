package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfInterimDetailDo;

/**
 * @author zhourui on 2017年11月17日 17:44
 * @类描述：用户临时额度使用记录
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfInterimDetailDao {
    /**
     * 增加记录
     * @param afInterimDetailDo
     * @return
     */
    int addAfInterimDetail(AfInterimDetailDo afInterimDetailDo);
}
