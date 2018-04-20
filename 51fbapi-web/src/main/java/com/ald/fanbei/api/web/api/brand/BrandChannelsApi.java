/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBrandService;
import com.ald.fanbei.api.biz.service.AfResourceH5ItemService;
import com.ald.fanbei.api.biz.service.AfResourceH5Service;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBrandDo;
import com.ald.fanbei.api.dal.domain.dto.AfBrandDto;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5Dto;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5ItemDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBrandListVo;
import com.ald.fanbei.api.web.vo.AfShopVo;
/**
 * 爱尚街 分类品牌页面 app客户端
 * @author liutengyuan 
 * @date 2018年4月13日
 */
@Component("brandChannelsApi")
public class BrandChannelsApi implements ApiHandle {
	@Resource
	private AfBrandService afBrandService;
	@Resource
	AfResourceH5Service afResourceH5Service;
	@Resource
	AfResourceH5ItemService afResourceH5ItemService;
	@Resource
	private BizCacheUtil bizCacheUtil;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
	//	String tag = ObjectUtils.toString(request.getParameter("tag"), null);
		String tag = ObjectUtils.toString(requestDataVo.getParams().get("tag"), null);
		logger.info("/category/brandChannels params: id:" + request.getHeader(Constants.REQ_SYS_NODE_ID) + "requestParam tag:" + tag);
		if (tag == null || !"brandChal".equals(tag)){
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>) bizCacheUtil.getMap("ASJbrandChannels"+tag);
		if (data == null){
			data = new HashMap<String, Object>();
			List<AfResourceH5Dto> list = afResourceH5Service.selectByStatus(tag);
			String imageUrl = "";
			String h5LinkUrl = null;
			String[] brandIds = {};
			List<AfBrandDo> hotBrandList = new ArrayList<AfBrandDo>();
			List<AfBrandDto> allBrandList = new ArrayList<AfBrandDto>();
		//	Map<String, List<AfBrandDto>> allBrandInfo = new HashMap<String,List<AfBrandDto>>();
			List<AfBrandListVo> allBrandInfo2 = new ArrayList<AfBrandListVo>();
			if (CollectionUtil.isNotEmpty(list)){
				AfResourceH5Dto afResourceH5Dto = list.get(0);
				Long modelId = afResourceH5Dto.getId();
				List<AfResourceH5ItemDto> configList = afResourceH5ItemService.selectByModelId(modelId);
				if (CollectionUtil.isNotEmpty(configList)){
					for (AfResourceH5ItemDto afResourceH5ItemDto : configList) {
						String pageMark = afResourceH5ItemDto.getValue1();
						// zhu tui pin pai
						if (pageMark != null){
							pageMark = pageMark.trim();
						}
						if ("mainBrand".equalsIgnoreCase(pageMark )){
							imageUrl = afResourceH5ItemDto.getValue3();
							h5LinkUrl = afResourceH5ItemDto.getValue2();
						}else if("hotBrand".equalsIgnoreCase(pageMark)){
							String ids = afResourceH5ItemDto.getValue2();
							if (ids != null){
								String idStr = ids.trim();
								brandIds = idStr.split(",");
							}
						}
					}
					// query configed the hot brands
					/*for (String id :brandIds){
						AfBrandDo brandInfo = afBrandService.getById(NumberUtil.objToLongDefault(id, 0));
						if (brandInfo != null){
							hotBrandList.add(brandInfo);
						}
					}*/
					if (brandIds.length >0){
						hotBrandList = afBrandService.getHotBrands(brandIds);
					}
				}
				allBrandList = afBrandService.getAllAndNameSort();
				char[] str = new char[26];
				for (int i = 0; i < 26; i++) {
				str[i]= (char)(65 + i );
				}
				for (int i = 0; i < str.length; i++) {
					AfBrandListVo brandListVo = new AfBrandListVo(str[i]+"", new ArrayList<AfBrandDto>());
					allBrandInfo2.add(brandListVo);
				}
				for (AfBrandDto  afBrandDto :allBrandList){
					String initName = afBrandDto.getNameIndex();// get the first key of name
					for (AfBrandListVo brandListVo :allBrandInfo2){
						if (brandListVo.getKey() .equals(initName)){
							brandListVo.getBrandsList().add(afBrandDto);
						}
						
					}
					/*if (CollectionUtil.isNotEmpty(allBrandInfo.get(initName)) ){
						 List<AfBrandDto> relatedBrandList = allBrandInfo.get(initName);
						 relatedBrandList.add(afBrandDto);
						 allBrandInfo.put(initName,relatedBrandList );
					}else{
						ArrayList<AfBrandDto> relatedBrandList = new ArrayList<AfBrandDto>();
						relatedBrandList.add(afBrandDto);
						allBrandInfo.put(initName, relatedBrandList);
					}*/
				}
				
			}
			data.put("imageUrl", imageUrl);
			data.put("h5LinkUrl", h5LinkUrl);
			data.put("hotBrandList", hotBrandList);
			data.put("allBrandInfo", allBrandInfo2);
			bizCacheUtil.saveMap("ASJbrandChannels"+tag, data,Constants.MINITS_OF_FIVE);
		}
		resp.setResponseData(data);
		return resp;
	}
	
}
