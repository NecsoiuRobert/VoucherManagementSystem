package com.company;

import java.util.ArrayList;

public class User implements Observer {
    private Integer id;
    private String name;
    private String mail;
    private String password;
    private UserType rank;
    private UserVoucherMap vouchers;
    private ArrayList<Notification> notifications;
    public User(Integer id,String name,String password,String mail,String rank)
    {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.rank = UserType.valueOf(rank);
        this.notifications = new ArrayList<Notification>();
        this.vouchers = new UserVoucherMap();
    }
    public String getMail(){
        return this.mail;
    }
    void addVoucher(Voucher voucher)
    {
        this.vouchers.addVoucher(voucher);
    }
    @Override
    public void update(Notification notification) {
        this.notifications.add(notification);
    }
    public String toString(){
        return this.id.toString() +":" + this.name + ":" + this.mail + ":" + this.rank.toString();
    }
    public Integer getId() {
        return this.id;
    }
    public String getRank(){
        return this.rank.toString();
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public UserVoucherMap getVouchers() {
        return vouchers;
    }
    public ArrayList<Voucher> getUserVoucher(Integer id)
    {
        return vouchers.get(id);
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
