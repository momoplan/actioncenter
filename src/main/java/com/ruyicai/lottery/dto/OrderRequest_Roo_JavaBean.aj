// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.lottery.dto;

import com.ruyicai.actioncenter.consts.SubaccountType;
import com.ruyicai.lottery.dto.BetRequest;
import com.ruyicai.lottery.dto.CaseLotRequest;
import com.ruyicai.lottery.dto.SubscribeRequest;
import java.lang.String;
import java.math.BigDecimal;
import java.util.List;

privileged aspect OrderRequest_Roo_JavaBean {
    
    public String OrderRequest.getBatchcode() {
        return this.batchcode;
    }
    
    public void OrderRequest.setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }
    
    public String OrderRequest.getLotno() {
        return this.lotno;
    }
    
    public void OrderRequest.setLotno(String lotno) {
        this.lotno = lotno;
    }
    
    public BigDecimal OrderRequest.getAmt() {
        return this.amt;
    }
    
    public void OrderRequest.setAmt(BigDecimal amt) {
        this.amt = amt;
    }
    
    public BigDecimal OrderRequest.getBettype() {
        return this.bettype;
    }
    
    public void OrderRequest.setBettype(BigDecimal bettype) {
        this.bettype = bettype;
    }
    
    public String OrderRequest.getUserno() {
        return this.userno;
    }
    
    public void OrderRequest.setUserno(String userno) {
        this.userno = userno;
    }
    
    public BigDecimal OrderRequest.getLotmulti() {
        return this.lotmulti;
    }
    
    public void OrderRequest.setLotmulti(BigDecimal lotmulti) {
        this.lotmulti = lotmulti;
    }
    
    public String OrderRequest.getBuyuserno() {
        return this.buyuserno;
    }
    
    public void OrderRequest.setBuyuserno(String buyuserno) {
        this.buyuserno = buyuserno;
    }
    
    public String OrderRequest.getChannel() {
        return this.channel;
    }
    
    public void OrderRequest.setChannel(String channel) {
        this.channel = channel;
    }
    
    public String OrderRequest.getSubchannel() {
        return this.subchannel;
    }
    
    public void OrderRequest.setSubchannel(String subchannel) {
        this.subchannel = subchannel;
    }
    
    public SubaccountType OrderRequest.getSubaccount() {
        return this.subaccount;
    }
    
    public void OrderRequest.setSubaccount(SubaccountType subaccount) {
        this.subaccount = subaccount;
    }
    
    public BigDecimal OrderRequest.getPaytype() {
        return this.paytype;
    }
    
    public void OrderRequest.setPaytype(BigDecimal paytype) {
        this.paytype = paytype;
    }
    
    public BigDecimal OrderRequest.getOneamount() {
        return this.oneamount;
    }
    
    public void OrderRequest.setOneamount(BigDecimal oneamount) {
        this.oneamount = oneamount;
    }
    
    public String OrderRequest.getMemo() {
        return this.memo;
    }
    
    public void OrderRequest.setMemo(String memo) {
        this.memo = memo;
    }
    
    public String OrderRequest.getDesc() {
        return this.desc;
    }
    
    public void OrderRequest.setDesc(String desc) {
        this.desc = desc;
    }
    
    public List<BetRequest> OrderRequest.getBetRequests() {
        return this.betRequests;
    }
    
    public void OrderRequest.setBetRequests(List<BetRequest> betRequests) {
        this.betRequests = betRequests;
    }
    
    public BigDecimal OrderRequest.getPrizeend() {
        return this.prizeend;
    }
    
    public void OrderRequest.setPrizeend(BigDecimal prizeend) {
        this.prizeend = prizeend;
    }
    
    public BigDecimal OrderRequest.getPrizeendamt() {
        return this.prizeendamt;
    }
    
    public void OrderRequest.setPrizeendamt(BigDecimal prizeendamt) {
        this.prizeendamt = prizeendamt;
    }
    
    public BigDecimal OrderRequest.getLeijiprizeendamt() {
        return this.leijiprizeendamt;
    }
    
    public void OrderRequest.setLeijiprizeendamt(BigDecimal leijiprizeendamt) {
        this.leijiprizeendamt = leijiprizeendamt;
    }
    
    public BigDecimal OrderRequest.getAccountnomoneysms() {
        return this.accountnomoneysms;
    }
    
    public void OrderRequest.setAccountnomoneysms(BigDecimal accountnomoneysms) {
        this.accountnomoneysms = accountnomoneysms;
    }
    
    public List<SubscribeRequest> OrderRequest.getSubscribeRequests() {
        return this.subscribeRequests;
    }
    
    public void OrderRequest.setSubscribeRequests(List<SubscribeRequest> subscribeRequests) {
        this.subscribeRequests = subscribeRequests;
    }
    
    public CaseLotRequest OrderRequest.getCaseLotRequest() {
        return this.caseLotRequest;
    }
    
    public void OrderRequest.setCaseLotRequest(CaseLotRequest caseLotRequest) {
        this.caseLotRequest = caseLotRequest;
    }
    
    public BigDecimal OrderRequest.getLotsType() {
        return this.lotsType;
    }
    
    public void OrderRequest.setLotsType(BigDecimal lotsType) {
        this.lotsType = lotsType;
    }
    
    public BigDecimal OrderRequest.getDeduct() {
        return this.deduct;
    }
    
    public void OrderRequest.setDeduct(BigDecimal deduct) {
        this.deduct = deduct;
    }
    
    public String OrderRequest.getAgencyno() {
        return this.agencyno;
    }
    
    public void OrderRequest.setAgencyno(String agencyno) {
        this.agencyno = agencyno;
    }
    
    public BigDecimal OrderRequest.getEndsms() {
        return this.endsms;
    }
    
    public void OrderRequest.setEndsms(BigDecimal endsms) {
        this.endsms = endsms;
    }
    
    public BigDecimal OrderRequest.getCancancel() {
        return this.cancancel;
    }
    
    public void OrderRequest.setCancancel(BigDecimal cancancel) {
        this.cancancel = cancancel;
    }
    
    public String OrderRequest.getBlessing() {
        return this.Blessing;
    }
    
    public void OrderRequest.setBlessing(String Blessing) {
        this.Blessing = Blessing;
    }
    
}
