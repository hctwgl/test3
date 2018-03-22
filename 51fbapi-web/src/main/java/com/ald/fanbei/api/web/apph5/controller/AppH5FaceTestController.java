package com.ald.fanbei.api.web.apph5.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
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
 * @类描述:
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

	private ConcurrentHashMap<ArrayList<Integer>, Long> map;

	

	@ResponseBody
	@RequestMapping(value = "/fanbei_api/faceScore", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String faceScore(HttpServletRequest request,
			HttpServletResponse response) {
		AfFacescoreRedDo redDo = new AfFacescoreRedDo();
		FanbeiWebContext context = new FanbeiWebContext();
		// 和登录有关的
		context = doWebCheck(request, false);
		logger.info("/fanbeiapi/faceScore params: ");
		List<AfFacescoreRedConfigDo> redConfigList = afFacescoreRedConfigService.findAll();
		if (redConfigList == null || redConfigList.size() == 0) {
			return H5CommonResponse.getNewInstance(false, "颜值测试活动已经结束", "", null).toString();
		} else {
			Random random = new Random();
			int a = random.nextInt(100);
			// 根据随机概率获取对应等级红包的配置对象
			AfFacescoreRedConfigDo redConfig = generateRedDegree(a,
					redConfigList);
			Long redConfigId = redConfig.getRid();
			AfFacescoreRedConfigDo redConfigDo = afFacescoreRedConfigService.getById(redConfigId);
			if (redConfigDo != null) {
				BigDecimal maxmoney = redConfigDo.getMaxmoney();
				BigDecimal minmoney = redConfigDo.getMinmoney();
				// 确定红包的金额
				int value = random.nextInt((maxmoney.intValue() - minmoney.intValue()) * 100);
				BigDecimal bigDecimal = new BigDecimal(value * 0.01).setScale(2, BigDecimal.ROUND_DOWN);
				BigDecimal amout = minmoney.add(bigDecimal);
				redDo.setAmount(amout);
				redDo.setConfigId(redConfigId);
				List<AfFacescoreImgDo> imgList = afFacescoreRedService.findRedImg();
				if (CollectionUtil.isNotEmpty(imgList)){
					int index = random.nextInt(imgList.size());
					String imgUrl = imgList.get(index).getImgUrl();
					redDo.setImageurl(imgUrl);
				}
			} else {
				logger.error("颜值测试红包配置信息类异常...", redConfigId);
				return H5CommonResponse.getNewInstance(false, "", "", null).toString();
			}
			// 保存红包到颜值红包表
			afFacescoreRedService.addRed(redDo);
			return H5CommonResponse.getNewInstance(true, "颜值测试成功", "", redDo).toString();
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
			if (map == null) {
				map = new ConcurrentHashMap<ArrayList<Integer>, Long>();
				if (CollectionUtil.isNotEmpty(redConfigList)) {
					for (AfFacescoreRedConfigDo facescoreRedConfigDo : redConfigList) {
						ArrayList<Integer> list = new ArrayList<Integer>();
						list.add(facescoreRedConfigDo.getProbabilityAreaStart());
						list.add(facescoreRedConfigDo.getProbabilityAreaEnd());
						map.put(list, facescoreRedConfigDo.getRid());
					}
				}
			}
			Set<ArrayList<Integer>> set = map.keySet();
			for (ArrayList<Integer> scopeList : set) {
				if (a >= scopeList.get(0) && a < scopeList.get(1)) {
					configDo.setRid(map.get(scopeList));
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
