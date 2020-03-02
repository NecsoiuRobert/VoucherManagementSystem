package com.company;

import java.util.ArrayList;

public class CampaignVoucherMap extends ArrayMap<String, ArrayList<Voucher>> {
    /*metoda ce verifica daca in CampaignVoucherMap exista un voucher si daca nu exista il adauga
     metoda foloseste metodele containsKey sa decida daca exista voucherul, si get sa obtina lista de Vouchere si sa il
     adauge sau sa creeze o lista noua si sa o adauge ca entry daca nu exista niciun entry cu mailul dat*/
    public boolean addVoucher(Voucher v)
    {
        if(containsKey(v.getMail())) {
           get(v.getMail()).add(v);
            return true;
        }
        ArrayList<Voucher> Vouchers = new ArrayList<Voucher>();
        Vouchers.add(v);
        put(v.getMail(),Vouchers);
        return true;
    }
}

