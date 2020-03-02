package com.company;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Campaign implements Subject{
    private Integer voucherIdCounter = 0;
    private Integer id;
    private Float budget;
    private String strategy;
    private Float numberOfVouchers;
    private String campaignName;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ArrayList<User> users;
    private ArrayList<Voucher> vouchers = new ArrayList<Voucher>();
    private CampaignVoucherMap voucherMap = new CampaignVoucherMap(); ;
    private CampaignStatusType status;
    public CampaignVoucherMap getVouchers(){
        return this.voucherMap;
    }
    public Campaign(Integer id,String name,String description,LocalDateTime startDate, LocalDateTime endDate,
                    Float budget, String strategy,String status) {
        this.id = id;
        this.campaignName = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.numberOfVouchers = budget;
        this.strategy = strategy;
        this.status = CampaignStatusType.valueOf(status);
        this.users = new ArrayList<User>();
    }
    //metoda folosita la editarea unei campanii noi, ea inlocuieste doar field-urile ce trebuie inlocuite in cazul unei
    //campanii noi
    public void editNewCampaign(Float budget,String description, LocalDateTime startDate, LocalDateTime endDate, String status)
    {
        this.budget = budget;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = CampaignStatusType.valueOf(status);
    }
    //metoda folosita in editarea unei campanii cu statul "STARTED", ea inlocuieste doar field-urile necesare
    public void editStartedCampaign(Float budget, LocalDateTime endDate,String status)
    {
        this.budget = budget;
        this.endDate = endDate;
        this.status = CampaignStatusType.valueOf(status);
    }
    public String getStatus(){
        return status.toString();
    }
    /*
    * metoda ce genereaza un voucher, aceasta verifica daca numarul de vouchere este mai mare decat 0, daca da
    * se decrementeaza numarul de vouchere ce mai pot fi generate
    * se creste counterul id-ului, counter folosit pentru a asigura unicitatea id-urilor in fiecare campanie
    * se verifica tipul de voucher si se adauga in voucherMap-ul campaniei, si in colectia utilizatorului de vouchere
    * */
    public boolean generateVoucher(String email, String voucherType, Float value)
    {
        if(this.numberOfVouchers > 0)
        {
            this.numberOfVouchers--;
            this.voucherIdCounter++;
            boolean exists = false;
            if(voucherType.equals("GiftVoucher"))
            {
                GiftVoucher newVoucher = new GiftVoucher(this.voucherIdCounter,email,this.id,value);
                this.voucherMap.addVoucher(newVoucher);
                this.vouchers.add(newVoucher);
                for(User user : this.users)
                {
                    if(user.getMail().equals(email))
                    {
                        user.addVoucher(newVoucher);
                    }
                }
            }else{
                LoyalityVoucher newVoucher = new LoyalityVoucher(this.voucherIdCounter,email,this.id,value);
                this.voucherMap.addVoucher(newVoucher);
                this.vouchers.add(newVoucher);
                for(User user : users)
                {
                    if(user.getMail().equals(email))
                    {
                        user.addVoucher(newVoucher);
                    }
                }
            }
            return true;
        }
        return false;
    }
    /* metoda setCanceled este o metoda care seteaza statusul campaniei cu CANCELLED si trimite o notificare
    observatorilor
     */
    public void setCanceled()
    {
        this.status = CampaignStatusType.CANCELLED;
        String status = "CANCEL";
        Notification notification = new Notification(LocalDateTime.now(),this.id,status);
        notifyAllObservers(notification);
    }
    //metoda ce returneaza un voucher gasit dupa id, se cauta in arraylist-ul de vouchere al campaniei
    public Voucher getVoucher(Integer id)
    {
        for(Voucher voucher : this.vouchers)
        {
            if(voucher.getId().equals(id))
            {
                return  voucher;
            }
        }
        return null;
    }
    /*metoda ce seteaza un voucher ca used, aceasta ia voucher-ul si apeleaza seter-ul care seteaza statusul ca
    used si data cand a fost folosit, aceasta returneaza fals daca nu a reusit sa gaseasca niciun voucher
    */
    public boolean redeemVoucher(Integer voucherId, LocalDateTime usedAt){
                    if(getVoucher(voucherId)!=null) {
                        getVoucher(voucherId).setUsedAt(usedAt);
                        String mail = getVoucher(voucherId).getMail();
                        this.vouchers.remove(getVoucher(voucherId));
                        return true;
                    }
                    return false;
    }
    /*
    metoda care creaza si trimite o notificare atunci cand o campanie a fost editata
     */
    public void notifEdited(){
        String status = "EDIT";
        Notification notification = new Notification(LocalDateTime.now(),this.id,status);
        notifyAllObservers(notification);
    }
    public void addObserver(User user)
    {
        this.users.add(user);
    }

    @Override
    public void removeObserver(User user) {
        this.users.remove(user);
    }

    @Override
    public void notifyAllObservers(Notification notification) {
        for ( User user: users){
            notification.setVouchers(this.voucherMap.get(user.getMail()));
            user.update(notification);
        }
    }
    public ArrayList<User> getObservers()
    {
        return this.users;
    }


    public Integer getId(){
        return this.id;
    }

    public String getStrategy() {
        return strategy;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public Float getBudget() {
        return budget;
    }

    public String getDescription() {
        return description;
    }

    public Float getNumberOfVouchers() {
        return numberOfVouchers;
    }
    public ArrayList<Voucher>getArrayVoucher(){
        return this.vouchers;
    }

}
