package com.ald.fanbei.api.biz.third.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfYoudunFaceService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.dal.domain.AfYoudunFaceDo;

/**
 *@类描述：有盾工具类
 *@author 陈金虎 2017年1月20日 下午9:26:28
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
@Component("youdunUtil")
public class YoudunUtil extends AbstractThird{
	
	@Resource
	private AfYoudunFaceService afYoudunFaceService;
	
	/**
	 * 处理有盾异步通知
	 * @param youdunNotifyBo
	 */
	public void dealYoudunNotify(AfYoudunFaceDo afYoudunFaceDo){
		String frontCard = afYoudunFaceDo.getFrontCard();
		String backCard = afYoudunFaceDo.getBackCard();
		String photoGet = afYoudunFaceDo.getPhotoGet();
		String photoGrid = afYoudunFaceDo.getPhotoGet();
		String photoLiving = afYoudunFaceDo.getPhotoLiving();
		String userId = afYoudunFaceDo.getUserId();
		
		try {
			frontCard = this.uploadYoudunFile(frontCard, userId, 1);//TODO 
			afYoudunFaceDo.setFrontCard(frontCard);
			backCard = this.uploadYoudunFile(backCard, userId, 2);
			afYoudunFaceDo.setBackCard(backCard);
			photoGet = this.uploadYoudunFile(photoGet, userId, 3);
			afYoudunFaceDo.setPhotoGet(photoGet);
			photoGrid = this.uploadYoudunFile(photoGrid, userId, 4);
			afYoudunFaceDo.setPhotoGrid(photoGrid);
			photoLiving = this.uploadYoudunFile(photoLiving, userId, 5);
			afYoudunFaceDo.setPhotoLiving(photoLiving);
		} catch (IOException e) {
			throw new FanbeiException("deal youdun notify error",FanbeiExceptionCode.DEALWITH_YOUDUN_NOTIFY_ERROR,e);
		}
		
		afYoudunFaceService.addYoudunFace(afYoudunFaceDo);
		
	}
	
	private String uploadYoudunFile(String base64Str,String userId,int type) throws IOException{//TODO 
		FileOutputStream out = null;
		try{
			byte[] imageBytes = Base64.decode(base64Str);
			String dir = "d:/home/file" + userId + type + ".jpg";//TODO
			out = new FileOutputStream(new File(dir));
			out.write(imageBytes);
			return dir;
		}catch(Exception e){
			logger.error("uploadYoudunFile error",e);
			return "";
		}finally{
			if(out != null){
				out.close();
			}
		}
	}
}
