package com.ald.fanbei.api.web.apph5.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfFacescoreRedConfigService;
import com.ald.fanbei.api.biz.service.AfFacescoreRedService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.dal.domain.AfFacescoreImgDo;
import com.ald.fanbei.api.dal.domain.AfFacescoreRedConfigDo;
import com.ald.fanbei.api.dal.domain.AfFacescoreRedDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述: 颜值测试接口
 * @author :liutengyuan
 * @version :2018年3月12日 下午4:58:43
 * @注意：本内本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class AppH5FaceTestController extends BaseController {

	@Resource
	private AfFacescoreRedConfigService afFacescoreRedConfigService;

	@Resource
	private AfFacescoreRedService afFacescoreRedService;

	@Resource
	private AfUserService afUserService;
	@Resource
	private BizCacheUtil bizCacheUtil;

	private List<AfFacescoreImgDo> imgList;// 分享图片列表
	
	@ResponseBody
	@RequestMapping(value = "/fanbei_api/faceScore", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String faceScore(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			AfFacescoreRedDo redDo = new AfFacescoreRedDo();
			logger.info("/fanbeiapi/faceScore params: ");
			List<AfFacescoreRedConfigDo> redConfigList =  bizCacheUtil.getObjectList(Constants.FACE_GAME_RED_CONFIG);
			if (redConfigList == null){
				redConfigList = afFacescoreRedConfigService.findAll();
				bizCacheUtil.saveObjectListExpire(Constants.FACE_GAME_RED_CONFIG, redConfigList, Constants.MINITS_OF_FIVE);
			}
			if (imgList == null){
				imgList = afFacescoreRedService.findRedImg();
			}
			if (redConfigList == null || redConfigList.size() == 0) {
				return H5CommonResponse.getNewInstance(false, "颜值测试活动已经结束", "", null).toString();
			} else {
				Random random = new Random();
				int a = random.nextInt(100);
				// 根据随机概率获取对应等级红包的配置对象
				AfFacescoreRedConfigDo redConfigVo = generateRedDegree(a,
						redConfigList);
				if (redConfigVo != null) {
					BigDecimal maxmoney = redConfigVo.getMaxmoney();
					BigDecimal minmoney = redConfigVo.getMinmoney();
					// 确定红包的金额
					int value = random.nextInt((maxmoney.intValue() - minmoney.intValue()) * 100);
					BigDecimal bigDecimal = new BigDecimal(value * 0.01).setScale(2, BigDecimal.ROUND_DOWN);
					BigDecimal amout = minmoney.add(bigDecimal);
					redDo.setAmount(amout);
					redDo.setConfigId(redConfigVo.getRid());
					if (CollectionUtil.isNotEmpty(imgList)){
						int index = random.nextInt(imgList.size());
						String imgUrl = imgList.get(index).getUrl();
						redDo.setImageurl(imgUrl);
					}
				} else {
					logger.error("颜值测试红包配置信息类异常...", redConfigVo==null? "" : redConfigVo.getRid());
					return H5CommonResponse.getNewInstance(false, "红包结果初始化失败..", "", null).toString();
				}
				// 保存红包到颜值红包表
				afFacescoreRedService.addRed(redDo);
				return H5CommonResponse.getNewInstance(true, "颜值测试成功", "", redDo).toString();
			}
		} catch (Exception e) {
			String result =  H5CommonResponse.getNewInstance(false, "红包结果初始化失败..", "", e.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", e, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
			return result;
		}
	}

	/**
	 * 根据配置随机产生等级的方法
	 * 
	 * @param a
	 *            随机数
	 * @param redConfigList
	 * @return
	 */
	private AfFacescoreRedConfigDo generateRedDegree(int a,
			List<AfFacescoreRedConfigDo> redConfigList) {
		AfFacescoreRedConfigDo configDo = new AfFacescoreRedConfigDo();
		try {
			for (AfFacescoreRedConfigDo redConfigDo : redConfigList) {
				if (redConfigDo.getProbabilityAreaStart() <= a && a < redConfigDo.getProbabilityAreaEnd()){
					configDo.setRid(redConfigDo.getRid());
					configDo.setMaxmoney(redConfigDo.getMaxmoney());
					configDo.setMinmoney(redConfigDo.getMinmoney());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("/fanbeiapi/faceScoreTest method: generateRedDegree",e.getMessage());
		}
		return configDo;
	}

	/**
	 * 将文件转换成Byte数组
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] getBytesByFile(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			byte[] data = bos.toByteArray();
			bos.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		// TODO Auto-generated method stub·
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(),
					FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
