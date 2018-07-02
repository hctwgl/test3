package com.ald.fanbei.api.dal.dao;



import com.ald.fanbei.api.dal.domain.AfPushManageDo;

import java.util.List;

public interface AfPushManageDao extends BaseDao<AfPushManageDo, Long>{

    List<AfPushManageDo> selectListByStatus(String status);

    int addPushManage(AfPushManageDo afPushManageDo);

    int deleteById(Long id);

}