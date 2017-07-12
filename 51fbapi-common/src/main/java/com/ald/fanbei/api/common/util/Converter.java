package com.ald.fanbei.api.common.util;

/**
 * 
 *@类Converter.java 的实现描述：转换器接口
 *@author 陈金虎 2017年1月16日 下午11:38:15
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface Converter<S, T> {

    /**
     * Convert the source of type S to target type T.
     * 
     * @param source
     * @return
     */
    T convert(S source);

}
