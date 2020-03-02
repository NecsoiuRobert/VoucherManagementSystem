package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        VoucherManagementSystem startSystem = VoucherManagementSystem.getInstance();
        try {


            File campaignsFile = new File("C:\\Users\\CartelDeCali\\Desktop\\VMStests\\test06\\input\\campaigns.txt");
            Scanner getCampaignsLines = new Scanner(campaignsFile);
            ArrayList<String> campaignLines = new ArrayList<String>();
            Integer numberOfCampaigns;
            //iau fiecare linie si o pun intr-un arraylist de linii
            while (getCampaignsLines.hasNextLine()) {
                String line = getCampaignsLines.nextLine();
                campaignLines.add(line);
            }
            //pregatesc formatter-ul pentru parsarea datei
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            //setez data aplicatiei
            LocalDateTime applicationDate = LocalDateTime.parse(campaignLines.get(1), formatter);
            //parcurg fiecare linie si folosesc metoda split cu delimitator ; pentru a lua fiecare field
            for (int i = 2; i < campaignLines.size(); i++) {
                String[] arrOfStr = campaignLines.get(i).split(";");
                Integer id = Integer.valueOf(arrOfStr[0]);
                LocalDateTime startDate = LocalDateTime.parse(arrOfStr[3], formatter);
                LocalDateTime endDate = LocalDateTime.parse(arrOfStr[4], formatter);
                Float aux = 1.2f;
                Float budget = aux.valueOf(arrOfStr[5]);
                String status = "NEW";
                //determin statusul campaniei
                if (endDate.isBefore(applicationDate)) {
                    status = "EXPIRED";
                } else if (startDate.isBefore(applicationDate) && endDate.isAfter(applicationDate)) {
                    status = "STARTED";
                } else if (startDate.isAfter(applicationDate) && endDate.isAfter(applicationDate)) {
                    status = "NEW";
                }
                Campaign newCampaign = new Campaign(id, arrOfStr[1], arrOfStr[2], startDate, endDate, budget, arrOfStr[6], status);
                startSystem.addCampaign(newCampaign);
            }
            File usersFile = new File("C:\\Users\\CartelDeCali\\Desktop\\VMStests\\test06\\input\\users.txt");
            Scanner getUsersLines = new Scanner(usersFile);
            ArrayList<String> usersLines = new ArrayList<String>();
            while (getUsersLines.hasNextLine()) {
                String line = getUsersLines.nextLine();
                usersLines.add(line);
            }
            for (int i = 1; i < usersLines.size(); i++) {
                String[] arrOfStr = usersLines.get(i).split(";");
                Integer id = Integer.valueOf(arrOfStr[0]);
                User newUser = new User(id, arrOfStr[1], arrOfStr[2], arrOfStr[3], arrOfStr[4]);
                startSystem.addUser(newUser);
            }
            File eventFile = new File("C:\\Users\\CartelDeCali\\Desktop\\VMStests\\test06\\input\\events.txt");
            Scanner getEventLines = new Scanner(eventFile);
            ArrayList<String> eventLines = new ArrayList<String>();
            while (getEventLines.hasNextLine()) {
                String line = getEventLines.nextLine();
                eventLines.add(line);
            }
            for (int i = 2; i < eventLines.size(); i++) {
                String[] arrOfStr = eventLines.get(i).split(";");
                if (arrOfStr[1].equals("addCampaign")) {
                    Integer userId = Integer.valueOf(arrOfStr[0]);
                    if (startSystem.getUser(userId) != null && startSystem.getUser(userId).getRank().equals("ADMIN")) {
                        Integer campaignId = Integer.valueOf(arrOfStr[2]);
                        LocalDateTime startDate = LocalDateTime.parse(arrOfStr[5], formatter);
                        LocalDateTime endDate = LocalDateTime.parse(arrOfStr[6], formatter);
                        Float budget = Float.valueOf(arrOfStr[7]);
                        String strategy = arrOfStr[8];
                        String status = "NEW";
                        if (endDate.isBefore(applicationDate)) {
                            status = "EXPIRED";
                        } else if (startDate.isBefore(applicationDate) && endDate.isAfter(applicationDate)) {
                            status = "STARTED";
                        } else if (startDate.isAfter(applicationDate) && endDate.isAfter(applicationDate)) {
                            status = "NEW";
                        }
                        Campaign newCampaign = new Campaign(campaignId, arrOfStr[3], arrOfStr[4], startDate, endDate, budget, strategy, status);
                        startSystem.addCampaign(newCampaign);
                    }
                }
                if (arrOfStr[1].equals("editCampaign")) {
                    Integer userId = Integer.valueOf(arrOfStr[0]);
                    if (startSystem.getUser(userId) != null && startSystem.getUser(userId).getRank().equals("ADMIN")) {
                        Integer campaignId = Integer.valueOf(arrOfStr[2]);
                        LocalDateTime startDate = LocalDateTime.parse(arrOfStr[5], formatter);
                        LocalDateTime endDate = LocalDateTime.parse(arrOfStr[6], formatter);
                        Float budget = Float.valueOf(arrOfStr[7]);
                        String strategy = "basic";
                        String status = "NEW";
                        if (endDate.isBefore(applicationDate)) {
                            status = "EXPIRED";
                        } else if (startDate.isBefore(applicationDate) && endDate.isAfter(applicationDate)) {
                            status = "STARTED";
                        } else if (startDate.isAfter(applicationDate) && endDate.isAfter(applicationDate)) {
                            status = "NEW";
                        }
                        Campaign newCampaign = new Campaign(campaignId, arrOfStr[3], arrOfStr[4], startDate, endDate, budget, strategy, status);
                        startSystem.updateCampaign(campaignId, newCampaign);
                    }
                }
                if (arrOfStr[1].equals("cancelCampaign")) {
                    Integer userId = Integer.valueOf(arrOfStr[0]);
                    if (startSystem.getUser(userId) != null && startSystem.getUser(userId).getRank().equals("ADMIN"))
                        startSystem.cancelCampaign(Integer.valueOf(arrOfStr[2]));
                }
                if (arrOfStr[1].equals("generateVoucher")) {
                    Integer userId = Integer.valueOf(arrOfStr[0]);
                    if (startSystem.getUser(userId) != null && startSystem.getUser(userId).getRank().equals("ADMIN")) {
                        if (startSystem.getUserMail(arrOfStr[3]) != null) {
                            if (startSystem.getCampaign(Integer.valueOf(arrOfStr[2])) != null) {
                                Campaign toBeEditedCampaign = startSystem.getCampaign(Integer.valueOf(arrOfStr[2]));
                                User user = startSystem.getUserMail(arrOfStr[3]);
                                if (toBeEditedCampaign.getNumberOfVouchers() > 0)
                                    if (user != null) {
                                        if (!toBeEditedCampaign.getObservers().contains(user)) {
                                            toBeEditedCampaign.addObserver(user);
                                        }
                                        toBeEditedCampaign.generateVoucher(arrOfStr[3], arrOfStr[4], Float.valueOf(arrOfStr[5]));
                                    }

                            }
                        }
                    }
                }
                if (arrOfStr[1].equals("redeemVoucher")) {
                    Integer userId = Integer.valueOf(arrOfStr[0]);
                    if (startSystem.getUser(userId) != null && startSystem.getUser(userId).getRank().equals("ADMIN")) {
                        Campaign campaignToBeEdited = startSystem.getCampaign(Integer.valueOf(arrOfStr[2]));
                        LocalDateTime usedAt = LocalDateTime.parse(arrOfStr[4], formatter);
                        campaignToBeEdited.redeemVoucher(Integer.valueOf(arrOfStr[3]), usedAt);
                    }
                }
                if (arrOfStr[1].equals("getVouchers")) {
                    if (startSystem.getUser(Integer.valueOf(arrOfStr[0])) != null)
                        System.out.println(startSystem.getUser(Integer.valueOf(arrOfStr[0])).getVouchers());
                }
                if (arrOfStr[1].equals("getObservers")) {
                    if (startSystem.getUser(Integer.valueOf(arrOfStr[0])) != null && startSystem.getUser(Integer.valueOf(arrOfStr[0])).getRank().equals("ADMIN"))
                        if (startSystem.getCampaign(Integer.valueOf(arrOfStr[2])) != null)
                            System.out.println(startSystem.getCampaign(Integer.valueOf(arrOfStr[2])).getObservers());
                }
                if (arrOfStr[1].equals("getNotifications")) {
                    if (startSystem.getUser(Integer.valueOf(arrOfStr[0])) != null)
                        System.out.println(startSystem.getUser(Integer.valueOf(arrOfStr[0])).getNotifications());
                }
            }
        } catch (IOException e)
        {// do something
            e.printStackTrace();
        }
    }
}
