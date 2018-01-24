package com.ald.fanbei.api.biz.bo.assetside.edspay;

import com.ald.fanbei.api.dal.domain.AfUserSealDo;

import java.io.Serializable;
import java.util.List;

/**
 * @author chengkang 2017年11月29日 14:29:12
 * @类现描述：钱包平台退回债权请求实体
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayBackSealReqBo implements Serializable {

    private static final long serialVersionUID = 4347678991772430075L;

    private List<AfUserSealDo> afUserSealDoList;

    public List<AfUserSealDo> getAfUserSealDoList() {
        return afUserSealDoList;
    }

    public void setAfUserSealDoList(List<AfUserSealDo> afUserSealDoList) {
        this.afUserSealDoList = afUserSealDoList;
    }
}
