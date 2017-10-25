package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserSealDo;
import com.timevale.esign.sdk.tech.bean.result.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月22日下午2:26:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfESdkService {

//    Result init(Map<String,String> map);//初始化

    FileDigestSignResult sign(HttpServletRequest req, HttpServletResponse response);//签章

    AddAccountResult addPerson(Map<String,String> map);//创建个人账户

    GetAccountProfileResult getPerson(Map<String,String> map);//创建个人账户

    AfUserSealDo selectUserSealByUserId(Long id);

    AfUserSealDo selectByUserType(String type);

    AddSealResult createSealOrganize(String accountId, String templateType, String color, String hText, String qText);

    AddSealResult createSealPersonal( String accountId, String templateType, String color);

    int insertUserSeal(AfUserSealDo record);

    int updateUserSealByUserId(AfUserSealDo record);

}
