package com.ald.fanbei.api.dal.domain.dto;

import java.util.List;

public class GameGoodsGroup {
    private String groupName;
    private List<GameGoods> goodsList;

    public String getGroupName() {
	return groupName;
    }

    public void setGroupName(String groupName) {
	this.groupName = groupName;
    }

    public List<GameGoods> getGoodsList() {
	return goodsList;
    }

    public void setGoodsList(List<GameGoods> goodsList) {
	this.goodsList = goodsList;
    }

    @Override
    public String toString() {
	return "GameGoodsGroup [groupName=" + groupName + ", goodsList=" + goodsList + "]";
    }

}
