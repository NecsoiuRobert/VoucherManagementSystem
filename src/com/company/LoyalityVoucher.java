package com.company;

import java.time.LocalDateTime;

//clasa GiftVoucher extinde clasa Voucher si implementeaza constructorul si metodele aferente
public class LoyalityVoucher extends Voucher{
    private Float discount;
    public LoyalityVoucher(Integer id, String mail, Integer campaignId, Float discount)
    {
        vType = VoucherStatusType.UNUSED;
        this.campaignId = campaignId;
        this.mail = mail;
        setId(id);
        this.discount = discount;

    }
    public void setId(Integer id)
    {
        this.code = id.toString();
        this.id = id;
    }
    public Integer getId(){
        return this.id;
    }

    public Integer getCampaignId() {
        return this.campaignId;
    }

    public LocalDateTime getData() {
        return this.usedAt;
    }

    public String getMail() {
        return this.mail;
    }
    public void setUsedAt(LocalDateTime usedAt)
    {
        this.usedAt = usedAt;
        this.vType = VoucherStatusType.USED;
    }
    public String getCode()
    {
        return this.code;
    }
    public String getStatus(){
        return this.vType.toString();
    }
    @Override
    public String toString() {
        return "Voucher code : " + this.code + " Status: " + vType.toString() +" Campaign:"+ this.campaignId.toString()+"\n";
    }
}
