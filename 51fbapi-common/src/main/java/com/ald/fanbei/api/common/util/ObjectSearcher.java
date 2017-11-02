package com.ald.fanbei.api.common.util;

/**
 * 
 *@类描述：
 *@author 陈金虎 2017年1月16日 下午11:40:27
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface ObjectSearcher<S, T, D> {

    T search(S source, D value);
}
