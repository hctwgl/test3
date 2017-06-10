package com.ald.fanbei.api.biz.util;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.common.Constants;

/**
 *@类描述：
 *@author xiaotianjian 2017年6月10日下午3:24:10
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BorrowRateBoUtil {
	
	private static final String nper = "n";//分期数
	private static final String rate = "r";//分期利率
	private static final String poundageRate = "pr";//手续费率
	private static final String rangeBegin = "rb";//最低手续费
	private static final String rangeEnd = "re";//最高手续费
	private static final String overdueRate = "or";//逾期日利率
	private static final String overduePoundageRate = "opr";//滞纳金费率
	private static final String overdueRangeBegin = "orb";//最低滞纳金费率
	private static final String overdueRangeEnd = "ore";//最低滞纳金费率
	
	/**
	 * 数据库字符串转化为Bo
	 * borrowratebo data rule n:6;opr:0.06;orb:5;ore:50;pr:0.10;rb:2.5;re:50;r:0.12; 
	 * n//分期数  opr;//逾期日利率  orb;//最低滞纳金费率 ore;//最低滞纳金费率 opr;//滞纳金费率 pr;//手续费率 r;//分期利率 re;//最高手续费  rb;//最低手续费
	 * @param dataTableStr
	 * @return
	 */
	public static BorrowRateBo parseToBoFromDataTableStr(String dataTableStr) {
		BorrowRateBo bo = new BorrowRateBo();
		if (StringUtils.isEmpty(dataTableStr)) 
			return null;
		String[] array = dataTableStr.split(Constants.SEMICOLON);
		for (String str : array) {
			String[] valueArr = str.split(Constants.COMMA);
			String key = valueArr[0];
			String value = valueArr[1];
			switch(key) {
				case nper:
					bo.setNper(Integer.parseInt(value));
					break;
				case rate:
					bo.setRate(new BigDecimal(value));
					break;
				case poundageRate:
					bo.setPoundageRate(new BigDecimal(value));
					break;
				case rangeBegin:
					bo.setRangeBegin(new BigDecimal(value));
					break;
				case rangeEnd:
					bo.setRangeEnd(new BigDecimal(value));
					break;
				case overdueRate:
					bo.setOverdueRate(new BigDecimal(value));
					break;
				case overduePoundageRate:
					bo.setOverduePoundageRate(new BigDecimal(value));
					break;
				case overdueRangeBegin:
					bo.setOverdueRangeBegin(new BigDecimal(value));
					break;
				case overdueRangeEnd:
					bo.setOverdueRangeEnd(new BigDecimal(value));
					break;
				default :
					break;
			}
		}
		return bo;
	}
	
	/**
	 * bo转化为数据库字符串
	 * borrowratebo data rule n:6;opr:0.06;orb:5;ore:50;pr:0.10;rb:2.5;re:50;r:0.12; 
	 * @param dataTableStr
	 * @return
	 */
	public static String parseToDataTableStrFromBo(BorrowRateBo bo) {
		if (bo == null) 
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append(nper).append(Constants.COMMA).append(bo.getNper()).append(Constants.SEMICOLON);
		sb.append(rate).append(Constants.COMMA).append(bo.getRate()).append(Constants.SEMICOLON);
		sb.append(poundageRate).append(Constants.COMMA).append(bo.getPoundageRate()).append(Constants.SEMICOLON);
		sb.append(rangeBegin).append(Constants.COMMA).append(bo.getRangeBegin()).append(Constants.SEMICOLON);
		sb.append(rangeEnd).append(Constants.COMMA).append(bo.getRangeEnd()).append(Constants.SEMICOLON);
		sb.append(overdueRate).append(Constants.COMMA).append(bo.getOverdueRate()).append(Constants.SEMICOLON);
		sb.append(overduePoundageRate).append(Constants.COMMA).append(bo.getOverduePoundageRate()).append(Constants.SEMICOLON);
		sb.append(overdueRangeBegin).append(Constants.COMMA).append(bo.getOverdueRangeBegin()).append(Constants.SEMICOLON);
		sb.append(overdueRangeEnd).append(Constants.COMMA).append(bo.getOverdueRangeEnd()).append(Constants.SEMICOLON);
		return sb.toString();
	}
	
}
