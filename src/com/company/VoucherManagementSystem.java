package com.company;

import java.util.ArrayList;

public class VoucherManagementSystem {
    private ArrayList<Campaign>campaigns = new ArrayList<Campaign>();
    private ArrayList<User>users = new ArrayList<User>();
    /*
    clasa interna SingletonHelper folosita pentru a crea o singura instanta a clasei VoucherManagementSystem
     */
   private static class SingletonHelper{
       private static final VoucherManagementSystem instance =  new VoucherManagementSystem();
   }
   //metoda ce returneaza instanta clasei VoucherManagementSystem
    private VoucherManagementSystem(){};
   public static VoucherManagementSystem getInstance(){
       return SingletonHelper.instance;
   }
   public ArrayList<Campaign> getCampaigns(){
       return this.campaigns;
    }
    //metoda care returneaza o campanie pe care o cauta dupa id
    public Campaign getCampaign(Integer id){
       for( Campaign campaign : campaigns)
       {
           if(campaign.getId().equals(id))
           {
               return campaign;
           }
       }
       return null;
    }
    //method overloading pe metoda getCampaign, acum pot sa caut si dupa numele campaniei
    public Campaign getCampaign(String nume){
        for( Campaign campaign : campaigns)
        {
            if(campaign.getCampaignName().equals(nume))
            {
                return campaign;
            }
        }
        return null;
    }
    public void addCampaign(Campaign campaign)
    {
        this.campaigns.add(campaign);
    }
    public ArrayList<User> getUsers(){
       return this.users;
    }
    public boolean addUser(User user){
       return this.users.add(user);
    }
    //metoda care imi returneaza un user cautat dupa id
    public User getUser(Integer id){
        for(User user : this.users)
        {
            if(user.getId().equals(id))
            {
                return user;
            }
        }
        return null;
    }
    //method overloading pe getUser, se cauta dupa username si se returneaza
    public User getUser(String username){
        for(User user : this.users)
        {
            if(user.getName().equals(username))
            {
                return user;
            }
        }
        return null;
    }
    public User getUserMail(String mail){
        for(User user : this.users)
        {
            if(user.getMail().equals(mail))
            {
                return user;
            }
        }
        return null;
    }
    //metoda ce verifica daca o campanie poate fi editata, verifica statusul si campurile ce au fost schimbate
    private boolean isValidCampaign(Campaign toBeEditedCampaign, Campaign newCampaign)
    {
        if(toBeEditedCampaign.getStatus().equals("NEW"))
        {
            return true;

        }
        else if(toBeEditedCampaign.getStatus().equals("STARTED")){
                if(toBeEditedCampaign.getStartDate().isEqual(newCampaign.getStartDate()))
                {
                    if (toBeEditedCampaign.getCampaignName().equals(newCampaign.getCampaignName()))
                    {
                        return toBeEditedCampaign.getDescription().equals(newCampaign.getDescription());
                    }
                }
        }
        return false;
    }
    //metoda ce seteaza statusul unei campanii ca si Cancelled
    public boolean cancelCampaign(Integer id){
       if(getCampaign(id)!=null)
       {
        Campaign closedCampaign = getCampaign(id);
        closedCampaign.setCanceled();
        return true;
       }
       return false;
    }
    /*
        metoda ce primeste ca parametrii id-ul campaniei ce trebuie editata si campania cu care va fi editata
        se verifica daca campania se poate edita si daca modificarile sunt valide si se editeaza campania apoi se
        trimite notificare
     */
    public boolean updateCampaign(Integer id, Campaign newCampaign)
    {
        if(getCampaign(id)!=null)
        {
            Campaign toBeEditedCampaign = getCampaign(id);
            if(isValidCampaign(toBeEditedCampaign,newCampaign))
            {
                if (toBeEditedCampaign.getStatus().equals("NEW"))
                {
                    toBeEditedCampaign.editNewCampaign(newCampaign.getBudget(),newCampaign.getDescription(),
                            newCampaign.getStartDate(),newCampaign.getEndDate(),newCampaign.getStatus());
                    toBeEditedCampaign.notifEdited();
                    return true;
                }
                toBeEditedCampaign.editStartedCampaign(newCampaign.getBudget(),newCampaign.getEndDate(),newCampaign.getStatus());
                toBeEditedCampaign.notifEdited();
                return true;
            }
        }
        return false;
    }
}
