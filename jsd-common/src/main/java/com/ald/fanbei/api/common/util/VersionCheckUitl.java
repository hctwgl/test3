package com.ald.fanbei.api.common.util;

/**
 * @author honghzengpei 2017/12/12 15:36
 * @类描述：版本号控制
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class VersionCheckUitl {
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>();
    public static void setVersion(Integer v){
        threadLocal.set(v);
    }

    public static Integer getVersion(){
        return threadLocal.get();
    }

    public final static Integer VersionZhangDanSecond = 404;
}
