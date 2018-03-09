package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfAbtestDeviceNewDo;

/**
 * 用户设备号记录表Service
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-03-06 19:59:03
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAbtestDeviceNewService extends ParentService<AfAbtestDeviceNewDo, Long>{

    void addUserDeviceInfo(AfAbtestDeviceNewDo abTestDeviceDo);

}
