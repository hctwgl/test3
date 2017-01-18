package com.ald.fanbei.api.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.filter.OncePerRequestFilter;



/**
 * 
 *@类描述：h5页面过滤器
 *@author 陈金虎 2017年1月17日 下午6:15:21
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class H5Filter extends OncePerRequestFilter{
	
//	private static String WX_URL_PRE = "/h5/wx/";
//	private static int CURRENT_VERSION_CODE = 130;
//	
//	private List<String> pubAccessUrl;

//	public void setPubAccessUrl(String pubAccessUrl) {
//		this.pubAccessUrl = Arrays.asList(pubAccessUrl.split(","));
//	}

	/**
	 * 如果URI在排除列表内，不需过滤
	 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        logger.debug("H5Filter:" + request.getRequestURI());
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    		return;
    }
    
    
}
