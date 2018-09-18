package com.ald.fanbei.api.common.page;

/**
 * 
 *@类描述：分页接口
 *@author 陈金虎 2017年1月16日 下午11:47:31
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface Paginable<T> {

    /** 总记录数 */
    int getTotalCount();

    /** 总页数 */
    int getTotalPage();

    /** 每页记录数 */
    int getPageSize();

    /** 当前页号 */
    int getPageIndex();

}
