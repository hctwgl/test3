package com.ald.fanbei.api.biz.bo.barlyClearance;

import org.omg.CORBA.OBJECT_NOT_EXIST;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author honghzengpei 2017/11/28 16:33
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AllBarlyClearanceBo {
    private Long borrowId;        //借款编号
    private String title;        //标题
    private int startNper=0;      //开始还的期数
    private int endNper=0;        //结止期数
    private int nper=0;            //总期数
    private BigDecimal amount =BigDecimal.ZERO;   //总金额

    private List<AllBarlyClearanceDetailBo> detailList;  //详情



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStartNper() {
        return startNper;
    }

    public void setStartNper(int startNper) {
        this.startNper = startNper;
    }

    public int getEndNper() {
        return endNper;
    }

    public void setEndNper(int endNper) {
        this.endNper = endNper;
    }

    public int getNper() {
        return nper;
    }

    public void setNper(int nper) {
        this.nper = nper;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<AllBarlyClearanceDetailBo> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<AllBarlyClearanceDetailBo> detailList) {
        this.detailList = detailList;
    }

    public Long getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Long borrowId) {
        this.borrowId = borrowId;
    }

    public void setMinAdnMaxNper(int _nper){
        if(startNper ==0 || endNper ==0){
            startNper = _nper;
            endNper = _nper;
        }
        else{
            if(startNper > _nper){
                startNper = _nper;
            }
            if(endNper <_nper){
                endNper = _nper;
            }
        }
    }
}
