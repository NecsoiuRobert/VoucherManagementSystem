package com.company;


import java.util.ArrayList;
/*metoda ce verifica daca in UserVoucherMap exista un voucher si daca nu exista il adauga
   metoda foloseste metodele containsKey sa decida daca exista voucherul, si get sa obtina lista de Vouchere si sa il
   adauge sau sa creeze o lista noua si sa o adauge ca entry daca nu exista niciun entry cu id-ul campaniei dat*/
public class UserVoucherMap extends ArrayMap<Integer, ArrayList<Voucher>> {
       public boolean addVoucher(Voucher v)
        {
            if(containsKey(v.getCampaignId())) {
               get(v.getCampaignId()).add(v);
                return true;
            }

                ArrayList<Voucher> Vouchers = new ArrayList<Voucher>();
                Vouchers.add(v);
                put(v.getCampaignId(),Vouchers);
                return true;
        }
}
