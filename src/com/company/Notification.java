package com.company;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Notification {
    private NotificationType nType;
    private LocalDateTime data;
    private Integer campaignId;
    private ArrayList<Voucher> vouchers;
    private ArrayList<String> voucherCodes = new ArrayList<>();
    public Notification(LocalDateTime data, Integer campaignId,String status){
            this.data = data;
            this.campaignId = campaignId;
            this.nType = NotificationType.valueOf(status);
    }

    public ArrayList<Voucher> getVouchers() {
        return this.vouchers;
    }
    //clasa care seteaza lista de coduri de vouchere si mentine si o lista de vouchere
    public void setVouchers(ArrayList<Voucher> vouchers)
    {
        ArrayList<String> aux = new ArrayList<>();
        this.vouchers = vouchers;
        if(vouchers != null)
        {
            for(Voucher voucher : vouchers)
                aux.add(voucher.getCode());
            this.voucherCodes = aux;
        }

    }
    @Override
    public String toString() {
        String type = this.nType.toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String data = this.data.format(formatter);
            return "Campania cu id " +campaignId.toString()+ " are statusul: " + this.nType.toString()+"\n Trimisa la: " + data + "\n";


    }
}
