package com.ald.fanbei.unit.test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ald.fanbei.api.dal.dao.JsdUserDao;
import com.ald.fanbei.api.dal.domain.JsdUserDo;

public class DemoTest extends JunitBaseTest {

    @Autowired
    private JsdUserDao userDao;

    @Test
    public void getUserInfo(){
        JsdUserDo userDo = userDao.getById(23213L);
        System.out.println(userDo.getRid());
    }

}
