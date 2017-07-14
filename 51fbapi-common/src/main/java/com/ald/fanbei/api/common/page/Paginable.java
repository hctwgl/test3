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
    int getPageNo();

    /** 是否第一页 */
    boolean isFirstPage();

    /** 是否最后一页 */
    boolean isLastPage();

    /** 返回下页的页号 */
    int getNextPage();

    /** 返回上页的页号 */
    int getPrePage();

    /** 取得当前页显示的项的起始序号 */
    int getBeginIndex();

    /** 取得当前页显示的末项序号 */
    int getEndIndex();

    int getBeginPage();

    int getEndPage();
}
