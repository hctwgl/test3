package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfUserMaJiaBaoType {

    APP("www", "51"),
    H5(null, "H5"),
    BORROWMONEY("borrowMoney", "borrowMoney"),
    BORROWSUPERMAN("borrowSuperman", "借款超人"),
    JIEDAIBAO("jiedaibao", "借贷宝"),
    HUANKADAI("huankadai", "还卡贷"),
    XIANGQIANDAIKUAN("xiangqiandaikuan", "向钱贷款"),
    XIANGQIANDAIHUAWEI("xiangqiandaihuawei", "xiangqiandaihuawei"),
    SUDAIHUAWEI("sudaihuawei", "sudaihuawei");
    

    private String code;
    private String name;
    
    private static Map<String,AfUserMaJiaBaoType> codeRoleTypeMap = null;

    AfUserMaJiaBaoType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfUserMaJiaBaoType findRoleTypeByCode(String code) {
        for (AfUserMaJiaBaoType roleType : AfUserMaJiaBaoType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,AfUserMaJiaBaoType> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, AfUserMaJiaBaoType>();
        for(AfUserMaJiaBaoType item:AfUserMaJiaBaoType.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
    }


	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}


}
