package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserSealDo;
import com.timevale.esign.sdk.tech.bean.result.AddAccountResult;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import com.timevale.esign.sdk.tech.bean.result.GetAccountProfileResult;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author guoshuaiqiang 2017年10月27日下午2:26:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfESdkService {

//    Result init(Map<String,String> map);//初始化

    FileDigestSignResult userSign(Map<String,String> map);//签章

    FileDigestSignResult secondSign(Map<String,String> map);//签章

    FileDigestSignResult selfSign(Map<String,String> map);//签章

    AddAccountResult addPerson(Map<String,String> map);//创建个人账户

    GetAccountProfileResult getPerson(Map<String,String> map);//创建个人账户

    AfUserSealDo selectUserSealByUserId(Long id);

    List<AfUserSealDo> selectByUserType(String type);

    AddSealResult createSealOrganize(String accountId, String templateType, String color, String hText, String qText);

    AddSealResult createSealPersonal( String accountId, String templateType, String color);

    int insertUserSeal(AfUserSealDo record);

    int updateUserSealByUserId(AfUserSealDo record);

    AfUserSealDo getSealPersonal(AfUserDo afUserDo, AfUserAccountDo accountDo);

    void GetSeal(ModelMap model, AfUserDo afUserDo, AfUserAccountDo accountDo);

}
