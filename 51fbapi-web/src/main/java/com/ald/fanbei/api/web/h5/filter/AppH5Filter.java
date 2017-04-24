package com.ald.fanbei.api.web.h5.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @类描述：h5页面过滤器
 * @author 陈金虎 2017年1月17日 下午6:15:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class AppH5Filter extends OncePerRequestFilter {

	private static String WX_URL_PRE = "/h5/app/";   //app中h5前缀
	private List<String> appPubAccessUrl;      //app h5不需要验证权限的页面

	public void setAppPubAccessUrl(String appPubAccessUrl) {
		this.appPubAccessUrl = Arrays.asList(appPubAccessUrl.split(","));
	}

	/**
	 * 如果URI在排除列表内，不需过滤
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request)
			throws ServletException {
		logger.debug("H5 App Filter:" + request.getRequestURI());
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		//非app h5的请求
    	if(request.getRequestURI().indexOf(WX_URL_PRE) != 0){
    		filterChain.doFilter(request, response);
    		return;
    	}
		//不需要验证权限
    	if(checkUrlAccess(request.getRequestURI())){
    		filterChain.doFilter(request, response);
    	}
		//验证权限，验签名
    	
		

		filterChain.doFilter(request, response);
		return;
	}
    
	/**
	 * 检查该页面是否需要验证权限
	 * @param url
	 * @return
	 */
    private boolean checkUrlAccess(String url){
    	for(String item:appPubAccessUrl){
    		if(url.indexOf(item) ==0){
    			return true;
    		}
    	}
    	return false;
    }

}
