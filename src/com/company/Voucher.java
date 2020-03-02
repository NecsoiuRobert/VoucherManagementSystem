package com.company;

import java.time.LocalDateTime;
import java.util.Date;

//Clasa abstracta Voucher ce are ca metode getteri si setteri
public abstract class Voucher {
    Integer id;
    String code;
    VoucherStatusType vType;
    LocalDateTime usedAt;
    String mail;
    Integer campaignId;
    public abstract void setId(Integer id);

    public abstract Integer getId();

    public abstract Integer getCampaignId();

    public abstract LocalDateTime getData();

    public abstract String getMail();
    public abstract void setUsedAt(LocalDateTime usedAt);
    public abstract String getCode();
    public abstract String getStatus();
    @Override
    public abstract String toString();
}

