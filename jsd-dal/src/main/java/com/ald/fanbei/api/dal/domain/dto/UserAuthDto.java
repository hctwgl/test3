package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;

public class UserAuthDto extends JsdUserAuthDo{
    private String realName;//申请的真实姓名

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
