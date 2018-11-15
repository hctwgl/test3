package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.dal.domain.JsdUserSealDo;
import com.timevale.esign.sdk.tech.bean.result.AddAccountResult;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import com.timevale.esign.sdk.tech.bean.result.GetAccountProfileResult;

import java.util.List;
import java.util.Map;

/**
 * @author guoshuaiqiang 2017年10月27日下午2:26:04
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdESdkService {

//    Result init(Map<String,Object> map);//初始化

    FileDigestSignResult userStreamSign(Map<String, Object> map, byte[] stream);//签章

    FileDigestSignResult secondPartySign(Map<String, Object> map, byte[] secondStream);//乙方签章

    FileDigestSignResult secondStreamSign(Map<String, Object> map, byte[] secondStream);//签章

    FileDigestSignResult thirdStreamSign(Map<String, Object> map, byte[] stream);//签章

    FileDigestSignResult selfStreamSign(Map<String, Object> map, byte[] stream);//签章

    AddAccountResult addUserSealAccount(Map<String, String> map);//创建个人账户

    GetAccountProfileResult getUserSealAccount(Map<String, String> map);//创建个人账户

    JsdUserSealDo selectUserSealByUserId(Long id);

    JsdUserSealDo selectUserSealByCardId(String id);

    List<JsdUserSealDo> selectByUserType(String type);

    AddSealResult createOrganizeSeal(String accountId, String templateType, String color, String hText, String qText);

    AddSealResult createUserSeal(String accountId, String templateType, String color);

    JsdUserSealDo getSealPersonal(JsdUserDo dsedUserDo);

    FileDigestSignResult userSign(Map<String, Object> map);//签章

    FileDigestSignResult selfStreamSign(String fileName, String type, String accountId, int posType, float width, String key, String posPage, boolean isQrcodeSign, byte[] stream);//签章

    FileDigestSignResult streamSign(String fileName, String type, String sealData, String accountId, int posType, float width, String key, String posPage, boolean isQrcodeSign, byte[] stream);//签章

}
