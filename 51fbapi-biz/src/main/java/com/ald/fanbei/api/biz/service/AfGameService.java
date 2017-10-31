package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfGameDo;

public interface AfGameService {
    /**
     * 通过编号查询
     * @param id
     * @return
     */
    AfGameDo getByCode(String code);
}
