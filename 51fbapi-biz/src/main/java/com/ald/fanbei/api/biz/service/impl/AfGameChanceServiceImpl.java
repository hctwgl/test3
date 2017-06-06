/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfGameChanceService;
import com.ald.fanbei.api.biz.service.AfGameService;
import com.ald.fanbei.api.common.enums.AfGameChanceType;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfGameChanceDao;
import com.ald.fanbei.api.dal.domain.AfGameChanceDo;
import com.ald.fanbei.api.dal.domain.AfGameDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年6月3日 下午6:43:19
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afGameChanceService")
public class AfGameChanceServiceImpl implements AfGameChanceService {

	@Resource
	AfGameChanceDao afGameChanceDao;
	@Resource
	AfGameService afGameService;
	
	@Override
	public int updateGameChance(AfGameChanceDo afGameChanceDo) {
		afGameChanceDao.updateGameChance(afGameChanceDo);
		return 0;
	}

	@Override
	public List<AfGameChanceDo> getByUserId(Long gameId,Long userId, String day) {
		
		List<AfGameChanceDo> chanceList = afGameChanceDao.getByUserId(userId, day);
		boolean isInitDayChanceCount = false;
		if(chanceList != null){
			for(AfGameChanceDo item:chanceList){
				if(AfGameChanceType.DAY.getCode().equals(item.getType())){
					isInitDayChanceCount = true;
					break;
				}
			}
		}
		if(isInitDayChanceCount){
			return chanceList;
		}
		//初始化当天抓娃娃次数
		initCurrentDayChance(gameId,userId, day);
		return afGameChanceDao.getByUserId(userId, day);
	}
	
	/**
	 * 初始化
	 * @param userId
	 * @param day
	 */
	private void initCurrentDayChance(Long gameId,Long userId,String day){
		AfGameChanceDo afGameChanceDo = new AfGameChanceDo();
		Set<String> codesSet = new HashSet<String>(); 
		while(codesSet.size()< 3){
			codesSet.add(AfGameChanceType.DAY.getCode() + CommonUtil.getRandomCharacter(5));
		}
		afGameChanceDo.setCodes(StringUtil.turnListToStr(codesSet, ","));
		afGameChanceDo.setDay(day);
		afGameChanceDo.setTotalCount(3);
		afGameChanceDo.setType(AfGameChanceType.DAY.getCode());
		afGameChanceDo.setUsedCount(0);
		afGameChanceDo.setUserId(userId);
		afGameChanceDo.setGameId(gameId);
		afGameChanceDao.addGameChance(afGameChanceDo);
	}
	
	@Override
	public int updateInviteChance(Long userId) {

		AfGameDo gameDo = afGameService.getByCode("catch_doll");
		List<AfGameChanceDo> chanceList = afGameChanceDao.getByUserId(userId, DateUtil.formatDate(new Date(), DateUtil.DEFAULT_PATTERN.substring(2)));
		boolean isHasInviteRecord = false;
		AfGameChanceDo inviteChanceDo = null;
		if(chanceList != null){
			for(AfGameChanceDo item:chanceList){
				if(AfGameChanceType.INVITE.getCode().equals(item.getType())){
					isHasInviteRecord = true;
					inviteChanceDo = item;
					break;
				}
			}
		}
		if(isHasInviteRecord){
			AfGameChanceDo afGameChanceDo = new AfGameChanceDo();
			Set<String> codesSet = new HashSet<String>(); 
			while(codesSet.size()< 1){
				codesSet.add(AfGameChanceType.DAY.getCode() + CommonUtil.getRandomCharacter(5));
			}
			afGameChanceDo.setCodes(StringUtil.turnListToStr(codesSet, ","));
			afGameChanceDo.setDay("");
			afGameChanceDo.setTotalCount(1);
			afGameChanceDo.setType(AfGameChanceType.INVITE.getCode());
			afGameChanceDo.setUsedCount(0);
			afGameChanceDo.setUserId(userId);
			afGameChanceDo.setGameId(gameDo.getRid());
			afGameChanceDao.addGameChance(afGameChanceDo);
		}else{
			String addCode = AfGameChanceType.INVITE.getCode() + CommonUtil.getRandomCharacter(5);
			if(StringUtil.isNotBlank(inviteChanceDo.getCodes())){
				List<String> codes = CommonUtil.turnStringToList(inviteChanceDo.getCodes(), ",");
				while(codes.contains(addCode)){
					addCode = AfGameChanceType.INVITE.getCode() + CommonUtil.getRandomCharacter(5);
					break;
				}
			}
			afGameChanceDao.updateInviteGameChance(inviteChanceDo.getRid(), 1, addCode);
		}
		return 0;
	}
}
