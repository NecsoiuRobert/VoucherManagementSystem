/*
 * Created by JFormDesigner on Sun Dec 29 18:19:34 EET 2019
 */

package com.company;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
import javax.xml.catalog.CatalogManager;

/**
 * @author Necșoiu Robert-Emanoil
 */
public class Home2 extends JFrame {
    File userPath;
   File campaignPath;
   User thisUser;
   String campaignName;
   VoucherManagementSystem startSystem = VoucherManagementSystem.getInstance();
    public Home2() {
        initComponents();
        //scot din aplicatie toate panel-urile mai putin primul panel, fiecare panel reprezinta o pagina
        this.remove(panel2);
        this.remove(normalUserPage);
        this.remove(normalUserVoucherPage);
        this.remove(AdminUserPage);
        this.remove(AdminCampaignPage);
        this.remove(addCampaignPage);
        this.remove(editCampaignPage);
        this.remove(closeCampaignPage);
        this.remove(beforeVoucherAdminPage);
        this.remove(adminVoucherPage);
        this.remove(redeemVoucherPage);
        this.remove(generateVoucherPage);
        this.repaint();
        this.revalidate();
        layeredPane1.setLayout(new CardLayout());//holder pentru paginile mele
        switchPanel(panel1);//pun primul panel

    }
    //actionEvent pentru butonul de upload pentru fisierul users, deschide un JFileChooser si seteaza fisierul cu users
    private void UsersActionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            this.userPath = selectedFile;
        }

    }
    /*
    metoda loadData este o metoda ce parseaza fisierele si introduce datele in startSystem, functioneaza asemanator cu clasa test
    adica creeaza cate un arraylist cu liniile din fiecare fisier si apeleaza split pe fiecare linie cu delimitator ";"
    pentru a lua fiecare camp si a crea utilizatori sau campanii
     */
    public void loadData() throws FileNotFoundException {
        File campaignsFile = this.campaignPath;
        Scanner getCampaignsLines = new Scanner(campaignsFile);
        ArrayList<String> campaignLines = new ArrayList<String>();
        Integer numberOfCampaigns;
        while (getCampaignsLines.hasNextLine())
        {
            String line = getCampaignsLines.nextLine();
            campaignLines.add(line);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String dateForParse = LocalDateTime.now().format(formatter);
        LocalDateTime applicationDate = LocalDateTime.parse(dateForParse, formatter);
        for(int i = 2; i < campaignLines.size();i++)
        {
            String[] arrOfStr = campaignLines.get(i).split(";");
            Integer id = Integer.valueOf(arrOfStr[0]);
            LocalDateTime startDate = LocalDateTime.parse(arrOfStr[3], formatter);
            LocalDateTime endDate = LocalDateTime.parse(arrOfStr[4],formatter);
            Float aux = 1.2f;
            Float budget = aux.valueOf(arrOfStr[5]);
            String status = "NEW";
            if(endDate.isBefore(applicationDate))
            {
                status = "EXPIRED";
            }else if(startDate.isBefore(applicationDate) && endDate.isAfter(applicationDate))
            {
                status = "STARTED";
            }else if(startDate.isAfter(applicationDate) && endDate.isAfter(applicationDate))
            {
                status = "NEW";
            }
            Campaign newCampaign = new Campaign(id,arrOfStr[1],arrOfStr[2],startDate,endDate,budget,arrOfStr[6],status);
            startSystem.addCampaign(newCampaign);
        }
        File usersFile = this.userPath;
        Scanner getUsersLines = new Scanner(usersFile);
        ArrayList<String> usersLines = new ArrayList<String>();
        while (getUsersLines.hasNextLine())
        {
            String line = getUsersLines.nextLine();
            usersLines.add(line);
        }
        for(int i = 1; i < usersLines.size(); i++)
        {
            String[] arrOfStr = usersLines.get(i).split(";");
            Integer id = Integer.valueOf(arrOfStr[0]);
            User newUser = new User(id,arrOfStr[1],arrOfStr[2],arrOfStr[3],arrOfStr[4]);
            startSystem.addUser(newUser);
        }
    }
    //actionEvent pentru butonul de upload pentru fisierul campaigns, deschide un JFileChooser si seteaza fisierul cu campaigns
    private void CampaignsActionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.campaignPath = fileChooser.getSelectedFile();
        }
    }
/*
action listener pentru butonul de start, determina daca au fost alese fisierele, in caz pozitiv incarca data si
trece la panel-ul 2, daca nu seteaza o alerta (coloreaza text-ul pe label-uri)
 */
    private void button1ActionPerformed(ActionEvent e) {
        if(this.campaignPath!=null && this.userPath != null)
        {
            try {
                loadData();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            switchPanel(panel2);

        }
        else{
            if(this.campaignPath == null)
            {
                label2.setForeground(Color.red);
            }
            if(this.userPath == null)
            {
                label1.setForeground(Color.red);
            }
        }
    }
    /*metoda folosita pentru a schimba un panel, aceasta scoate toate panel-urile din layeredpanel si il adauga pe cel
    primti ca parametru
     */
    public void switchPanel(JPanel panel)
    {
        layeredPane1.removeAll();
        layeredPane1.add(panel);
        layeredPane1.repaint();
        layeredPane1.revalidate();
    }
/*
actionListener ce verifica daca field-urile de login au fost completate, daca da se verifica daca sunt valide si
se trece fie la panel-ul de administrator daca utilizatorul este admin sau la panelul de guest, in caz ca datele sunt
invalide se emit avertizari
 */
    private void button2ActionPerformed(ActionEvent e) {
            if(this.Username.getText()!=null && this.Password.getText()!=null)
            {
               User user = startSystem.getUser(this.Username.getText());
               if(user!=null)
                {
                    if(user.getPassword().equals(this.Password.getText()))
                    {

                        if(user.getRank().equals("GUEST")){
                            this.Username2.setText(this.Username.getText());
                            this.thisUser = user;
                            switchPanel(normalUserPage);
                        }else{

                            this.Username4.setText(user.getName());
                            this.thisUser = user;
                            switchPanel(AdminUserPage);
                        }

                    }else{
                        this.Password.setBackground(Color.red);
                    }
                }else{
                   this.Username.setBackground(Color.red);
               }
            }else{
                if(this.Username.getText() == null)
                {
                    this.label2.setForeground(Color.red);
                    this.Username.setBackground(Color.red);
                }
                if(this.Username.getText() == null)
                {
                    this.label1.setForeground(Color.red);
                    this.Password.setBackground(Color.red);
                }
            }

    }
    /*
    metoda ce populeaza un tabel cu toate voucherele unui utilizator normal, se foloseste un jtable si
    defaultTableModel
     */
    private void populateJTable()
    {
        this.Username3.setText(thisUser.getName());
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.setColumnIdentifiers(new Object[]{"Cod", "Campaign Id", "Status"});
        table1.setModel(dtm);
        UserVoucherMap voucherMap = thisUser.getVouchers();
        ArrayList<Campaign> campaigns = startSystem.getCampaigns();
        for(Campaign campaign : campaigns)
        {
            if(voucherMap.get(campaign.getId())!=null)
            {
                ArrayList<Voucher> vouchers = voucherMap.get(campaign.getId());
                for(Voucher voucher : vouchers)
                {
                    dtm.addRow(new Object[]{voucher.getCode(),voucher.getCampaignId(),voucher.getStatus()});

                }
            }
        }
    }
    //actionListener ce populeaza tabelul cu vouchere si trece la pagina ce le afiseaza
    private void seeVouchersActionPerformed(ActionEvent e) {
        populateJTable();
        switchPanel(normalUserVoucherPage);
    }
//actionListener folosit pentru a schimba panel-ul si a simula un buton de "back"
    private void InapoiActionPerformed(ActionEvent e) {
        this.Username2.setText(thisUser.getName());
        switchPanel(normalUserPage);
    }
//actionListener ce simuleaza un buton de logout
    private void button4ActionPerformed(ActionEvent e) {
        thisUser = null;
        switchPanel(panel2);
    }
    //metoda ce adauga toate campaniile din aplicatie in table1, foloseste Jtable si DefaultTableModel
    private void pupulateJTableCampaigns(){
        this.Username3.setText(thisUser.getName());
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.setColumnIdentifiers(new Object[]{"Id", "Nume", "Descriere", "Data Start"});
        table1.setModel(dtm);
        ArrayList<Campaign> campaigns = startSystem.getCampaigns();
        for(Campaign campaign : campaigns){
            dtm.addRow(new Object[]{campaign.getId(),campaign.getCampaignName(),campaign.getDescription(),campaign.getStartDate()});
        }
    }
    //metoda ce adauga sortat sau nu intr-un tabel, in functie de parametrii, pentru a adauga sortat foloseste comparatori
    //ca al doilea argument al metodei sort al arraylist-ului de campanii
    private void CampaignsAdmin(String sortType1,String sortType2){
        this.Username5.setText(thisUser.getName());
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.setColumnIdentifiers(new Object[]{"Id", "Nume", "Descriere", "Data Start","Status"});
        table2.setModel(dtm);
        ArrayList<Campaign> campaigns = startSystem.getCampaigns();
        if(sortType1.equals("Nume"))
        {
            campaigns.sort(new Comparator<Campaign>() {
                @Override
                public int compare(Campaign campaign, Campaign t1) {
                    return campaign.getCampaignName().compareTo(t1.getCampaignName());
                }
            });
            if(sortType2.equals("Descending"))
            {
                Collections.reverse(campaigns);
            }
        }else if(sortType1.equals("Data")){
            campaigns.sort(new Comparator<Campaign>() {
                @Override
                public int compare(Campaign campaign, Campaign t1) {
                    return campaign.getStartDate().compareTo(t1.getStartDate());
                }
            });
            if(sortType2.equals("Descending"))
            {
                Collections.reverse(campaigns);
            }
        }
        for(Campaign campaign : campaigns){
            dtm.addRow(new Object[]{campaign.getId(),campaign.getCampaignName(),campaign.getDescription(),campaign.getStartDate(),campaign.getStatus()});
        }
    }

    private void seeCampaignsActionPerformed(ActionEvent e) {
        pupulateJTableCampaigns();
        switchPanel(normalUserVoucherPage);
    }

    private void button3ActionPerformed(ActionEvent e) {
        thisUser = null;
        switchPanel(panel2);
    }

    private void button5ActionPerformed(ActionEvent e) {
        thisUser = null;
        switchPanel(panel2);
    }

    private void button6ActionPerformed(ActionEvent e) {
        CampaignsAdmin("simple","no");
        switchPanel(AdminCampaignPage);
    }

    private void Inapoi2ActionPerformed(ActionEvent e) {
        switchPanel(AdminUserPage);
    }

    private void button8ActionPerformed(ActionEvent e) {
        thisUser = null;
        switchPanel(panel2);
    }
//actionListener pentru butonul de sortare ascendenta dupa nume
    private void button9ActionPerformed(ActionEvent e) {
        CampaignsAdmin("Nume","ASC");
    }

    private void button10ActionPerformed(ActionEvent e) {
        CampaignsAdmin("Nume","Descending");
    }

    private void button15ActionPerformed(ActionEvent e) {
        CampaignsAdmin("Data","Descending");
    }

    private void button16ActionPerformed(ActionEvent e) {
        CampaignsAdmin("Data","ASC");
    }

    private void button11ActionPerformed(ActionEvent e) {
        Username6.setText(thisUser.getName());
        label19.setForeground(Color.black);
        label19.setText("Completeaza toate campurile");
        switchPanel(addCampaignPage);
    }

    private void Inapoi3ActionPerformed(ActionEvent e) {
        CampaignsAdmin("simple","no");
        switchPanel(AdminCampaignPage);
    }
/*action listener pentru butonul de adaugare a unei campanii, acesta verifica daca toate field-urile au fost
completate si creeaza o campanie noua pe care o adauga
 */

    private void addCampaignActionPerformed(ActionEvent e) {
        if(numeCampanie.getText() != null && descriereCampanie.getText()!=null
            && dataStart.getText() != null && dataFinalizare.getText() != null
                && addBuget.getText() != null)
        {
            if(startSystem.getCampaign(numeCampanie.getText()) == null)
            {
                Integer campaignId = startSystem.getCampaigns().size() + 1;
                String nume = numeCampanie.getText();
                String descriere = descriereCampanie.getText();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String startDataToParse = dataStart.getText();
                LocalDateTime startDate = LocalDateTime.parse(startDataToParse,formatter);
                String endDataToParse = dataFinalizare.getText();
                LocalDateTime endDate = LocalDateTime.parse(endDataToParse,formatter);
                Float budget = Float.valueOf(addBuget.getText());
                String strategy = "NO";
                String dateForParse = LocalDateTime.now().format(formatter);
                LocalDateTime applicationDate = LocalDateTime.parse(dateForParse, formatter);
                String status = "NEW";
                if(endDate.isBefore(applicationDate))
                {
                    status = "EXPIRED";
                }else if(startDate.isBefore(applicationDate) && endDate.isAfter(applicationDate))
                {
                    status = "STARTED";
                }else if(startDate.isAfter(applicationDate) && endDate.isAfter(applicationDate))
                {
                    status = "NEW";
                }
                Campaign newCampaign = new Campaign(campaignId,nume,descriere,startDate,endDate,budget,strategy,status);
                startSystem.addCampaign(newCampaign);
                label19.setText("Campania a fost adaugata cu success");
                label19.setForeground(Color.green);
                numeCampanie.setText("");
                descriereCampanie.setText("");
                dataStart.setText("");
                dataFinalizare.setText("");
                addBuget.setText("");
            }else{
                label19.setText("Campania deja exista");
                label19.setForeground(Color.red);
            }
        }else{
            label19.setForeground(Color.red);
        }

    }

    private void button12ActionPerformed(ActionEvent e) {
        Username7.setText(thisUser.getName());
        numeCampanie2.setEditable(true);
        numeCampanie2.setEnabled(true);
        numeCampanie2.setText("");
        newDataStart.setEnabled(true);
        newDataStart.setEditable(true);
        newDataStart.setText("");
        newDescriereCampanie.setEditable(true);
        newDescriereCampanie.setEnabled(true);
        newDescriereCampanie.setText("");
        editCampaign.setEnabled(true);
        label20.setText("Completeaza toate campurile");
        label20.setForeground(Color.black);
        editBuget.setText("");
        newDataFinalizare.setText("");
        switchPanel(editCampaignPage);
    }
/*
    actionListener pentru butonul de cautare a unei campanii din pagina de editare, daca o campanie poate fi editata
    se seteaza ca needitabile campurile ce nu pot fi editate(in cazul Started sau NEW)
    ca o masura de siguranta se seteaza needitabil si campul de cautare
 */
    private void searchCampaignActionPerformed(ActionEvent e) {
        if(numeCampanie2.getText()!=null)
        {
            if(startSystem.getCampaign(numeCampanie2.getText())!=null)
            {
                numeCampanie2.setEditable(false);
                numeCampanie2.setEnabled(false);
                Campaign campaign = startSystem.getCampaign(numeCampanie2.getText());
                if(campaign.getStatus().equals("STARTED"))
                {
                    newDataStart.setEnabled(false);
                    newDataStart.setEditable(false);
                    newDescriereCampanie.setEditable(false);
                    newDescriereCampanie.setEnabled(false);
                }
                if(campaign.getStatus().equals("EXPIRED") || campaign.getStatus().equals("CANCELLED"))
                {
                    label20.setText("Campania nu este editabila");
                    label20.setForeground(Color.red);
                    numeCampanie2.setEditable(true);
                    numeCampanie2.setEnabled(true);
                }
            }
        }
    }
/*
    metoda ce creeaza o campanie cu noile atribute si o inlocuieste pe cea veche, se verifica daca a fost gasita o
    campanie ce poate fi editate, se preiau campurile in functie de statusul campaniei si se creeaza o campanie
    noua folosind campania veche apoi se apeleaza metoda de update a campaniei
 */
    private void editCampaignActionPerformed(ActionEvent e) {
        if(!searchCampaign.isEnabled())
        {
            Campaign campaign = startSystem.getCampaign(numeCampanie2.getText());
            if(campaign.getStatus().equals("NEW"))
            {
                if(newDescriereCampanie.getText() != null && newDataFinalizare.getText() != null
                && newDataStart.getText() != null && editBuget.getText() != null) {
                    String description = newDescriereCampanie.getText();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String startDataToParse = newDataStart.getText();
                    LocalDateTime startDate = LocalDateTime.parse(startDataToParse,formatter);
                    String endDataToParse = newDataFinalizare.getText();
                    LocalDateTime endDate = LocalDateTime.parse(endDataToParse,formatter);
                    Float budget = Float.valueOf(editBuget.getText());
                    String dateForParse = LocalDateTime.now().format(formatter);
                    LocalDateTime applicationDate = LocalDateTime.parse(dateForParse, formatter);
                    String status = "NEW";
                    if(endDate.isBefore(applicationDate))
                    {
                        status = "EXPIRED";
                    }else if(startDate.isBefore(applicationDate) && endDate.isAfter(applicationDate))
                    {
                        status = "STARTED";
                    }else if(startDate.isAfter(applicationDate) && endDate.isAfter(applicationDate))
                    {
                        status = "NEW";
                    }
                    Campaign newCampaign = new Campaign(campaign.getId(),campaign.getCampaignName(),description,startDate,endDate,budget,campaign.getStrategy(),status);
                    startSystem.updateCampaign(campaign.getId(),newCampaign);
                }
            }else{
                if(newDataFinalizare.getText()!=null && editBuget.getText()!=null)
                {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String endDataToParse = newDataFinalizare.getText();
                    LocalDateTime startDate = campaign.getStartDate();
                    String description = campaign.getDescription();
                    Float budget = Float.valueOf(editBuget.getText());
                    LocalDateTime endDate = LocalDateTime.parse(endDataToParse,formatter);
                    String dateForParse = LocalDateTime.now().format(formatter);
                    LocalDateTime applicationDate = LocalDateTime.parse(dateForParse, formatter);
                    String status = "NEW";
                    if(endDate.isBefore(applicationDate))
                    {
                        status = "EXPIRED";
                    }else if(startDate.isBefore(applicationDate) && endDate.isAfter(applicationDate))
                    {
                        status = "STARTED";
                    }else if(startDate.isAfter(applicationDate) && endDate.isAfter(applicationDate))
                    {
                        status = "NEW";
                    }
                    Campaign newCampaign = new Campaign(campaign.getId(),campaign.getCampaignName(),description,startDate,endDate,budget,campaign.getStrategy(),status);
                    startSystem.updateCampaign(campaign.getId(),newCampaign);
                }
            }
        }
    }
/*actionListener pentru butonul de cautare a campaniei, din pagina de detalii, daca campania a fost gasita se seteaz
    field-ul corespunzator cu valoarea din field-ul campaniei
 */
    private void searchCampaign2ActionPerformed(ActionEvent e) {
        if(numeCampanie3.getText()!=null){
            if(startSystem.getCampaign(numeCampanie3.getText()) != null)
            {
                Campaign campaign = startSystem.getCampaign(numeCampanie3.getText());
                newDataStart2.setText(campaign.getStartDate().toString());
                newDataFinalizare2.setText(campaign.getEndDate().toString());
                newDescriereCampanie2.setText(campaign.getDescription());
                editBuget2.setText(campaign.getBudget().toString());
            }
        }
    }

    private void button14ActionPerformed(ActionEvent e) {
        Username8.setText(thisUser.getName());
        label21.setForeground(Color.black);
        label21.setText("Vezi detaliile campaniei sau inchide campania");
        newDescriereCampanie2.setEnabled(false);
        newDataFinalizare2.setEnabled(false);
        newDataStart2.setEnabled(false);
        newDescriereCampanie2.setEditable(false);
        newDataFinalizare2.setEditable(false);
        newDataStart2.setEditable(false);
        editBuget2.setEditable(false);
        editBuget2.setEnabled(false);
        switchPanel(closeCampaignPage);
    }
/*action listener pentru setarea statusului CANCELED al unei campanii, se ia numele campaniei, se gaseste campania
si se apeleaza metoda cancelCampaign
 */
    private void editCampaign2ActionPerformed(ActionEvent e) {
        if(numeCampanie3.getText()!=null){
            if(startSystem.getCampaign(numeCampanie3.getText()) != null)
            {
                Campaign campaign = startSystem.getCampaign(numeCampanie3.getText());
                if(!campaign.getStatus().equals("CANCELLED"))
                {
                    startSystem.cancelCampaign(campaign.getId());
                }
            }
        }
    }

    private void button7ActionPerformed(ActionEvent e) {
        Username9.setText(thisUser.getName());
        switchPanel(beforeVoucherAdminPage);
    }
    //metoda care adauga voucherele dintr-o campanie intr-un tabel, primeste ca parametru campania corespunzatoare
    private void populateVouchers(Campaign campaign)
    {
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.setColumnIdentifiers(new Object[]{"Cod","User Mail", "Status"});
        table3.setModel(dtm);
        ArrayList<Voucher> vouchers = campaign.getArrayVoucher();
        for(Voucher voucher : vouchers)
        {
            dtm.addRow(new Object[]{voucher.getCode(),voucher.getMail(),voucher.getStatus()});

        }
    }
/*
action listener pentru butonul care cauta o campanie si trece la pagina de afisare a voucherelor
 */
    private void searchCampaign3ActionPerformed(ActionEvent e) {
        if(numeCampanie4.getText()!=null)
        {
            if(startSystem.getCampaign(numeCampanie4.getText())!=null)
            {
                Username10.setText(thisUser.getName());
                populateVouchers(startSystem.getCampaign(numeCampanie4.getText()));
                switchPanel(adminVoucherPage);
                campaignName = numeCampanie4.getText();
            }
        }
    }

    private void Inapoi7ActionPerformed(ActionEvent e) {
        Username9.setText(thisUser.getName());
        switchPanel(beforeVoucherAdminPage);
    }

    private void Inapoi8ActionPerformed(ActionEvent e) {
        switchPanel(adminVoucherPage);
    }

    private void redeemActionPerformed(ActionEvent e) {
        Username11.setText(thisUser.getName());
        switchPanel(redeemVoucherPage);
    }
/*actionListener pentru butonul de redeem voucher din pagina de cautare, acesta verifica daca campul este completat,
in caz pozitiv cauta voucher-ul in sistem folosind metoda getCampaign pentru a lua campania curenta, iar in
campania curenta cauta folosind metoda getVoucher, se foloseste setter-ul setUsedAt, si se seteaza cu data curenta
 */

    private void redeemVoucherActionPerformed(ActionEvent e) {
        if(VoucherCode.getText()!=null)
        {
            if(startSystem.getCampaign(campaignName).getVoucher(Integer.valueOf(VoucherCode.getText()))!=null)
            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String dateForParse = LocalDateTime.now().format(formatter);
                LocalDateTime applicationDate = LocalDateTime.parse(dateForParse, formatter);
                Voucher voucher = startSystem.getCampaign(campaignName).getVoucher(Integer.valueOf(VoucherCode.getText()));
                voucher.setUsedAt(applicationDate);
                populateVouchers(startSystem.getCampaign(campaignName));
            }
        }
    }

    private void generareActionPerformed(ActionEvent e) {
        Username12.setText(thisUser.getName());
        switchPanel(generateVoucherPage);
    }
/*
action Listener pentru butonul de generare din pagina de generare a unui voucher, acesta
verifica daca toate campurile sunt completate, in caz afirmativ se verifica email-ul utilizatorului si
se apeleaza metoda getUserMail pentru a lua utilizatorul din sistem, metoda getCampaign pentru a lua campania
se verifica daca utilizatorul este deja observator al campaniei, daca nu este se adauga
se verifica ce fel de voucher trebuie creat si se apeleaza metoda generateVoucher al campaniei .
 */
    private void generateVoucherActionPerformed(ActionEvent e) {
        if(userMail.getText()!=null && voucherType.getText() != null && value.getText() != null)
        {
            if(startSystem.getUserMail(userMail.getText())!=null)
            {
                User observer = startSystem.getUserMail(userMail.getText());
                Campaign campaign = startSystem.getCampaign(campaignName);
                if(!campaign.getObservers().contains(observer))
                {
                    campaign.addObserver(observer);
                }
                String voucherTYPE = "GiftVoucher";
                if(voucherType.getText().equals("Loyality"))
                    voucherTYPE = "LoyalityVoucher";
                campaign.generateVoucher(userMail.getText(),voucherTYPE,Float.valueOf(value.getText()));
                userMail.setText("");
                voucherType.setText("");
                value.setText("");
                populateVouchers(startSystem.getCampaign(campaignName));
            }
        }
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Necșoiu Robert-Emanoil
        layeredPane1 = new JLayeredPane();
        panel1 = new JPanel();
        label3 = new JLabel();
        label1 = new JLabel();
        Users = new JButton();
        label2 = new JLabel();
        Campaigns = new JButton();
        button1 = new JButton();
        panel2 = new JPanel();
        label4 = new JLabel();
        label5 = new JLabel();
        Username = new JTextField();
        Password = new JTextField();
        label6 = new JLabel();
        button2 = new JButton();
        normalUserVoucherPage = new JPanel();
        Username3 = new JLabel();
        label9 = new JLabel();
        label10 = new JLabel();
        normalUserVoucherHolder = new JScrollPane();
        table1 = new JTable();
        Inapoi = new JButton();
        button3 = new JButton();
        normalUserPage = new JPanel();
        Username2 = new JLabel();
        label7 = new JLabel();
        label8 = new JLabel();
        seeCampaigns = new JButton();
        seeVouchers = new JButton();
        button4 = new JButton();
        AdminUserPage = new JPanel();
        Username4 = new JLabel();
        label11 = new JLabel();
        label12 = new JLabel();
        button5 = new JButton();
        button6 = new JButton();
        button7 = new JButton();
        AdminCampaignPage = new JPanel();
        Username5 = new JLabel();
        label13 = new JLabel();
        normalUserVoucherHolder2 = new JScrollPane();
        table2 = new JTable();
        Inapoi2 = new JButton();
        button8 = new JButton();
        button9 = new JButton();
        button10 = new JButton();
        button11 = new JButton();
        button12 = new JButton();
        button14 = new JButton();
        button15 = new JButton();
        button16 = new JButton();
        addCampaignPage = new JPanel();
        Username6 = new JLabel();
        label14 = new JLabel();
        Inapoi3 = new JButton();
        button17 = new JButton();
        numeCampanie = new JTextField();
        label15 = new JLabel();
        label16 = new JLabel();
        descriereCampanie = new JTextField();
        label17 = new JLabel();
        dataStart = new JTextField();
        dataFinalizare = new JTextField();
        label18 = new JLabel();
        addCampaign = new JButton();
        label19 = new JLabel();
        label20 = new JLabel();
        addBuget = new JTextField();
        editCampaignPage = new JPanel();
        Username7 = new JLabel();
        label21 = new JLabel();
        Inapoi4 = new JButton();
        button18 = new JButton();
        numeCampanie2 = new JTextField();
        label22 = new JLabel();
        label23 = new JLabel();
        newDescriereCampanie = new JTextField();
        label24 = new JLabel();
        newDataStart = new JTextField();
        newDataFinalizare = new JTextField();
        label25 = new JLabel();
        editCampaign = new JButton();
        label26 = new JLabel();
        label27 = new JLabel();
        editBuget = new JTextField();
        searchCampaign = new JButton();
        closeCampaignPage = new JPanel();
        Username8 = new JLabel();
        label28 = new JLabel();
        Inapoi5 = new JButton();
        button19 = new JButton();
        numeCampanie3 = new JTextField();
        label29 = new JLabel();
        label30 = new JLabel();
        newDescriereCampanie2 = new JTextField();
        label31 = new JLabel();
        newDataStart2 = new JTextField();
        newDataFinalizare2 = new JTextField();
        label32 = new JLabel();
        editCampaign2 = new JButton();
        label33 = new JLabel();
        editBuget2 = new JTextField();
        searchCampaign2 = new JButton();
        beforeVoucherAdminPage = new JPanel();
        Username9 = new JLabel();
        label34 = new JLabel();
        Inapoi6 = new JButton();
        button13 = new JButton();
        numeCampanie4 = new JTextField();
        label35 = new JLabel();
        searchCampaign3 = new JButton();
        adminVoucherPage = new JPanel();
        Username10 = new JLabel();
        label36 = new JLabel();
        label37 = new JLabel();
        normalUserVoucherHolder3 = new JScrollPane();
        table3 = new JTable();
        Inapoi7 = new JButton();
        button20 = new JButton();
        redeem = new JButton();
        generare = new JButton();
        redeemVoucherPage = new JPanel();
        Username11 = new JLabel();
        label38 = new JLabel();
        Inapoi8 = new JButton();
        button21 = new JButton();
        VoucherCode = new JTextField();
        label39 = new JLabel();
        redeemVoucher = new JButton();
        generateVoucherPage = new JPanel();
        Username12 = new JLabel();
        label40 = new JLabel();
        Inapoi9 = new JButton();
        button22 = new JButton();
        userMail = new JTextField();
        label41 = new JLabel();
        generateVoucher = new JButton();
        label42 = new JLabel();
        voucherType = new JTextField();
        label43 = new JLabel();
        value = new JTextField();

        //======== this ========
        setTitle("Voucher Management System");
        var contentPane = getContentPane();

        //======== layeredPane1 ========
        {

            //======== panel1 ========
            {
                panel1.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border. EmptyBorder
                ( 0, 0, 0, 0) , "JF\u006frmDes\u0069gner \u0045valua\u0074ion", javax. swing. border. TitledBorder. CENTER, javax. swing. border
                . TitledBorder. BOTTOM, new java .awt .Font ("D\u0069alog" ,java .awt .Font .BOLD ,12 ), java. awt
                . Color. red) ,panel1. getBorder( )) ); panel1. addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void
                propertyChange (java .beans .PropertyChangeEvent e) {if ("\u0062order" .equals (e .getPropertyName () )) throw new RuntimeException( )
                ; }} );

                //---- label3 ----
                label3.setText("Voucher Management System");

                //---- label1 ----
                label1.setText("Te rog alege fisierul Users");

                //---- Users ----
                Users.setText("Upload");
                Users.addActionListener(e -> UsersActionPerformed(e));

                //---- label2 ----
                label2.setText("Te rog alege fisierul Campaigns");

                //---- Campaigns ----
                Campaigns.setText("Upload");
                Campaigns.addActionListener(e -> CampaignsActionPerformed(e));

                //---- button1 ----
                button1.setText("START");
                button1.addActionListener(e -> button1ActionPerformed(e));

                GroupLayout panel1Layout = new GroupLayout(panel1);
                panel1.setLayout(panel1Layout);
                panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                        .addGroup(panel1Layout.createSequentialGroup()
                            .addGap(0, 210, Short.MAX_VALUE)
                            .addGroup(panel1Layout.createParallelGroup()
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addGap(41, 41, 41)
                                    .addComponent(label3, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE))
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addComponent(label1, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
                                    .addGap(6, 6, 6)
                                    .addComponent(Users))
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addComponent(label2, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
                                    .addGap(6, 6, 6)
                                    .addComponent(Campaigns))
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addGap(93, 93, 93)
                                    .addComponent(button1, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)))
                            .addGap(0, 205, Short.MAX_VALUE))
                );
                panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                        .addGroup(panel1Layout.createSequentialGroup()
                            .addGap(0, 96, Short.MAX_VALUE)
                            .addComponent(label3, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addGroup(panel1Layout.createParallelGroup()
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addComponent(label1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                                .addComponent(Users))
                            .addGap(18, 18, 18)
                            .addGroup(panel1Layout.createParallelGroup()
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addComponent(label2, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                                .addComponent(Campaigns))
                            .addGap(34, 34, 34)
                            .addComponent(button1)
                            .addGap(0, 91, Short.MAX_VALUE))
                );
            }
            layeredPane1.add(panel1, JLayeredPane.DEFAULT_LAYER);
            panel1.setBounds(0, 0, 710, 375);
        }

        //======== panel2 ========
        {

            //---- label4 ----
            label4.setText("Username");
            label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() - 2f));

            //---- label5 ----
            label5.setText("Password");
            label5.setFont(label5.getFont().deriveFont(label5.getFont().getSize() - 2f));

            //---- label6 ----
            label6.setText("Login");
            label6.setEnabled(false);
            label6.setFont(label6.getFont().deriveFont(label6.getFont().getStyle() | Font.BOLD, label6.getFont().getSize() + 10f));

            //---- button2 ----
            button2.setText("Login");
            button2.addActionListener(e -> button2ActionPerformed(e));

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createParallelGroup()
                        .addGroup(panel2Layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addGroup(panel2Layout.createParallelGroup()
                                .addGroup(panel2Layout.createSequentialGroup()
                                    .addGap(64, 64, 64)
                                    .addComponent(label6, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE))
                                .addComponent(label4, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                .addComponent(Username, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label5, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                .addComponent(Password, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE)
                                .addGroup(panel2Layout.createSequentialGroup()
                                    .addGap(52, 52, 52)
                                    .addComponent(button2, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)))
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addGap(0, 710, Short.MAX_VALUE)
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createParallelGroup()
                        .addGroup(panel2Layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(label6)
                            .addGap(40, 40, 40)
                            .addComponent(label4)
                            .addGap(6, 6, 6)
                            .addComponent(Username, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12)
                            .addComponent(label5)
                            .addGap(6, 6, 6)
                            .addComponent(Password, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(button2)
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addGap(0, 375, Short.MAX_VALUE)
            );
        }

        //======== normalUserVoucherPage ========
        {

            //---- Username3 ----
            Username3.setText("Username");

            //---- label9 ----
            label9.setText("Voucher Management System");

            //---- label10 ----
            label10.setText("Aici sunt Voucherele tale");
            label10.setHorizontalAlignment(SwingConstants.CENTER);
            label10.setFont(label10.getFont().deriveFont(label10.getFont().getSize() + 4f));

            //======== normalUserVoucherHolder ========
            {
                normalUserVoucherHolder.setViewportView(table1);
            }

            //---- Inapoi ----
            Inapoi.setText("Inapoi ");
            Inapoi.addActionListener(e -> InapoiActionPerformed(e));

            //---- button3 ----
            button3.setText("LOGOUT");
            button3.addActionListener(e -> button3ActionPerformed(e));

            GroupLayout normalUserVoucherPageLayout = new GroupLayout(normalUserVoucherPage);
            normalUserVoucherPage.setLayout(normalUserVoucherPageLayout);
            normalUserVoucherPageLayout.setHorizontalGroup(
                normalUserVoucherPageLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, normalUserVoucherPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label9, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                        .addComponent(button3)
                        .addGap(86, 86, 86)
                        .addComponent(Username3, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(normalUserVoucherPageLayout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(label10, GroupLayout.PREFERRED_SIZE, 353, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(135, Short.MAX_VALUE))
                    .addGroup(GroupLayout.Alignment.TRAILING, normalUserVoucherPageLayout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(normalUserVoucherHolder, GroupLayout.PREFERRED_SIZE, 507, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(85, Short.MAX_VALUE))
                    .addGroup(normalUserVoucherPageLayout.createSequentialGroup()
                        .addGap(231, 231, 231)
                        .addComponent(Inapoi, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(250, Short.MAX_VALUE))
            );
            normalUserVoucherPageLayout.setVerticalGroup(
                normalUserVoucherPageLayout.createParallelGroup()
                    .addGroup(normalUserVoucherPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(normalUserVoucherPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Username3, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label9)
                            .addComponent(button3))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label10, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(normalUserVoucherHolder, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Inapoi)
                        .addContainerGap(37, Short.MAX_VALUE))
            );
        }

        //======== normalUserPage ========
        {

            //---- Username2 ----
            Username2.setText("Username");

            //---- label7 ----
            label7.setText("Voucher Management System");

            //---- label8 ----
            label8.setText("Bine ai venit, cu ce te putem ajuta?");
            label8.setHorizontalAlignment(SwingConstants.CENTER);
            label8.setFont(label8.getFont().deriveFont(label8.getFont().getSize() + 4f));

            //---- seeCampaigns ----
            seeCampaigns.setText("Vezi campanii");
            seeCampaigns.addActionListener(e -> seeCampaignsActionPerformed(e));

            //---- seeVouchers ----
            seeVouchers.setText("Vezi vouchere");
            seeVouchers.addActionListener(e -> seeVouchersActionPerformed(e));

            //---- button4 ----
            button4.setText("LOGOUT");
            button4.addActionListener(e -> button4ActionPerformed(e));

            GroupLayout normalUserPageLayout = new GroupLayout(normalUserPage);
            normalUserPage.setLayout(normalUserPageLayout);
            normalUserPageLayout.setHorizontalGroup(
                normalUserPageLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, normalUserPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label7, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button4)
                        .addGap(108, 108, 108)
                        .addComponent(Username2, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(normalUserPageLayout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addGroup(normalUserPageLayout.createParallelGroup()
                            .addComponent(label8, GroupLayout.PREFERRED_SIZE, 353, GroupLayout.PREFERRED_SIZE)
                            .addGroup(normalUserPageLayout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(seeCampaigns, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41)
                                .addComponent(seeVouchers)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            normalUserPageLayout.setVerticalGroup(
                normalUserPageLayout.createParallelGroup()
                    .addGroup(normalUserPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(normalUserPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Username2, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label7)
                            .addComponent(button4))
                        .addGap(34, 34, 34)
                        .addComponent(label8, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addGroup(normalUserPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(seeVouchers)
                            .addComponent(seeCampaigns))
                        .addContainerGap(157, Short.MAX_VALUE))
            );
        }

        //======== AdminUserPage ========
        {

            //---- Username4 ----
            Username4.setText("Username");

            //---- label11 ----
            label11.setText("Voucher Management System");

            //---- label12 ----
            label12.setText("Bine ai venit in panoul de administrare!");
            label12.setHorizontalAlignment(SwingConstants.CENTER);
            label12.setFont(label12.getFont().deriveFont(label12.getFont().getSize() + 4f));

            //---- button5 ----
            button5.setText("LOGOUT");
            button5.addActionListener(e -> button5ActionPerformed(e));

            //---- button6 ----
            button6.setText("Administreaza Campanii");
            button6.addActionListener(e -> button6ActionPerformed(e));

            //---- button7 ----
            button7.setText("Administreaza Vouchere Campanie");
            button7.addActionListener(e -> button7ActionPerformed(e));

            GroupLayout AdminUserPageLayout = new GroupLayout(AdminUserPage);
            AdminUserPage.setLayout(AdminUserPageLayout);
            AdminUserPageLayout.setHorizontalGroup(
                AdminUserPageLayout.createParallelGroup()
                    .addGroup(AdminUserPageLayout.createSequentialGroup()
                        .addGroup(AdminUserPageLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, AdminUserPageLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label11, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                .addGap(68, 68, 68)
                                .addComponent(button5)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Username4, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE))
                            .addGroup(AdminUserPageLayout.createSequentialGroup()
                                .addGroup(AdminUserPageLayout.createParallelGroup()
                                    .addGroup(AdminUserPageLayout.createSequentialGroup()
                                        .addGap(123, 123, 123)
                                        .addComponent(label12, GroupLayout.PREFERRED_SIZE, 353, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(AdminUserPageLayout.createSequentialGroup()
                                        .addGap(72, 72, 72)
                                        .addComponent(button6, GroupLayout.PREFERRED_SIZE, 173, GroupLayout.PREFERRED_SIZE)
                                        .addGap(41, 41, 41)
                                        .addComponent(button7, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
            );
            AdminUserPageLayout.setVerticalGroup(
                AdminUserPageLayout.createParallelGroup()
                    .addGroup(AdminUserPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(AdminUserPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label11)
                            .addComponent(button5)
                            .addComponent(Username4, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addComponent(label12, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58)
                        .addGroup(AdminUserPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(button7)
                            .addComponent(button6))
                        .addContainerGap(161, Short.MAX_VALUE))
            );
        }

        //======== AdminCampaignPage ========
        {

            //---- Username5 ----
            Username5.setText("Username");

            //---- label13 ----
            label13.setText("Voucher Management System");

            //======== normalUserVoucherHolder2 ========
            {
                normalUserVoucherHolder2.setViewportView(table2);
            }

            //---- Inapoi2 ----
            Inapoi2.setText("Inapoi ");
            Inapoi2.addActionListener(e -> {
			InapoiActionPerformed(e);
			Inapoi2ActionPerformed(e);
		});

            //---- button8 ----
            button8.setText("LOGOUT");
            button8.addActionListener(e -> {
			button3ActionPerformed(e);
			button8ActionPerformed(e);
		});

            //---- button9 ----
            button9.setText("Sort Cresc Nume");
            button9.addActionListener(e -> button9ActionPerformed(e));

            //---- button10 ----
            button10.setText("Sort desc Nume");
            button10.addActionListener(e -> button10ActionPerformed(e));

            //---- button11 ----
            button11.setText("Adauga Campanie");
            button11.addActionListener(e -> button11ActionPerformed(e));

            //---- button12 ----
            button12.setText("Editeaza campanie");
            button12.addActionListener(e -> button12ActionPerformed(e));

            //---- button14 ----
            button14.setText("Vezi detalii despre o campanie");
            button14.addActionListener(e -> button14ActionPerformed(e));

            //---- button15 ----
            button15.setText("Sort desc Data");
            button15.addActionListener(e -> button15ActionPerformed(e));

            //---- button16 ----
            button16.setText("Sort Cresc Data");
            button16.addActionListener(e -> button16ActionPerformed(e));

            GroupLayout AdminCampaignPageLayout = new GroupLayout(AdminCampaignPage);
            AdminCampaignPage.setLayout(AdminCampaignPageLayout);
            AdminCampaignPageLayout.setHorizontalGroup(
                AdminCampaignPageLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, AdminCampaignPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label13, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Inapoi2, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button8)
                        .addGap(38, 38, 38)
                        .addComponent(Username5, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(AdminCampaignPageLayout.createSequentialGroup()
                        .addGroup(AdminCampaignPageLayout.createParallelGroup()
                            .addGroup(AdminCampaignPageLayout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addGroup(AdminCampaignPageLayout.createParallelGroup()
                                    .addComponent(normalUserVoucherHolder2, GroupLayout.PREFERRED_SIZE, 520, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button11)
                                    .addGroup(AdminCampaignPageLayout.createSequentialGroup()
                                        .addGap(196, 196, 196)
                                        .addComponent(button12))
                                    .addGroup(AdminCampaignPageLayout.createSequentialGroup()
                                        .addComponent(button9, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(button10, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(button16, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(button15, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE))))
                            .addGroup(AdminCampaignPageLayout.createSequentialGroup()
                                .addGap(207, 207, 207)
                                .addComponent(button14)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            AdminCampaignPageLayout.setVerticalGroup(
                AdminCampaignPageLayout.createParallelGroup()
                    .addGroup(AdminCampaignPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(AdminCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Username5, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label13)
                            .addComponent(button8)
                            .addComponent(Inapoi2))
                        .addGap(12, 12, 12)
                        .addGroup(AdminCampaignPageLayout.createParallelGroup()
                            .addGroup(AdminCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(button9)
                                .addComponent(button10))
                            .addComponent(button16)
                            .addComponent(button15))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(normalUserVoucherHolder2, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(AdminCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(button11)
                            .addComponent(button12))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(button14)
                        .addContainerGap(7, Short.MAX_VALUE))
            );
        }

        //======== addCampaignPage ========
        {

            //---- Username6 ----
            Username6.setText("Username");

            //---- label14 ----
            label14.setText("Voucher Management System");

            //---- Inapoi3 ----
            Inapoi3.setText("Inapoi ");
            Inapoi3.addActionListener(e -> {
			InapoiActionPerformed(e);
			Inapoi3ActionPerformed(e);
		});

            //---- button17 ----
            button17.setText("LOGOUT");
            button17.addActionListener(e -> button3ActionPerformed(e));

            //---- label15 ----
            label15.setText("Nume Campanie");

            //---- label16 ----
            label16.setText("Descriere Campanie");

            //---- label17 ----
            label17.setText("Data Start");

            //---- label18 ----
            label18.setText("Data Finalizare");

            //---- addCampaign ----
            addCampaign.setText("Adauga Campanie");
            addCampaign.addActionListener(e -> addCampaignActionPerformed(e));

            //---- label19 ----
            label19.setText("Completeaza toate campurile");

            //---- label20 ----
            label20.setText("Buget");

            GroupLayout addCampaignPageLayout = new GroupLayout(addCampaignPage);
            addCampaignPage.setLayout(addCampaignPageLayout);
            addCampaignPageLayout.setHorizontalGroup(
                addCampaignPageLayout.createParallelGroup()
                    .addGroup(addCampaignPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label14, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Inapoi3, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button17)
                        .addGap(38, 38, 38)
                        .addComponent(Username6, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(addCampaignPageLayout.createSequentialGroup()
                        .addGroup(addCampaignPageLayout.createParallelGroup()
                            .addGroup(addCampaignPageLayout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addGroup(addCampaignPageLayout.createParallelGroup()
                                    .addComponent(label15, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(numeCampanie, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label16, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(descriereCampanie, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label20, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(addBuget, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE))
                                .addGap(63, 63, 63)
                                .addGroup(addCampaignPageLayout.createParallelGroup()
                                    .addComponent(dataFinalizare, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label17, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dataStart, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label18, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(addCampaignPageLayout.createSequentialGroup()
                                .addGap(179, 179, 179)
                                .addComponent(label19, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE))
                            .addGroup(addCampaignPageLayout.createSequentialGroup()
                                .addGap(187, 187, 187)
                                .addComponent(addCampaign, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            addCampaignPageLayout.setVerticalGroup(
                addCampaignPageLayout.createParallelGroup()
                    .addGroup(addCampaignPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(addCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Username6, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label14)
                            .addComponent(button17)
                            .addComponent(Inapoi3))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(addCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label15)
                            .addComponent(label17))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(addCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(numeCampanie, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(dataStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(addCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label16)
                            .addComponent(label18))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(addCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(descriereCampanie, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(dataFinalizare, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label20)
                        .addGap(6, 6, 6)
                        .addComponent(addBuget, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(label19)
                        .addGap(18, 18, 18)
                        .addComponent(addCampaign)
                        .addContainerGap(32, Short.MAX_VALUE))
            );
        }

        //======== editCampaignPage ========
        {

            //---- Username7 ----
            Username7.setText("Username");

            //---- label21 ----
            label21.setText("Voucher Management System");

            //---- Inapoi4 ----
            Inapoi4.setText("Inapoi ");
            Inapoi4.addActionListener(e -> {
			InapoiActionPerformed(e);
			Inapoi3ActionPerformed(e);
		});

            //---- button18 ----
            button18.setText("LOGOUT");
            button18.addActionListener(e -> button3ActionPerformed(e));

            //---- label22 ----
            label22.setText("Nume Campanie");

            //---- label23 ----
            label23.setText("Descriere Campanie");

            //---- label24 ----
            label24.setText("Data Start");

            //---- label25 ----
            label25.setText("Data Finalizare");

            //---- editCampaign ----
            editCampaign.setText("Modifica Campanie");
            editCampaign.addActionListener(e -> editCampaignActionPerformed(e));

            //---- label26 ----
            label26.setText("Completeaza toate campurile");

            //---- label27 ----
            label27.setText("Buget");

            //---- searchCampaign ----
            searchCampaign.setText("Cauta campanie");
            searchCampaign.addActionListener(e -> searchCampaignActionPerformed(e));

            GroupLayout editCampaignPageLayout = new GroupLayout(editCampaignPage);
            editCampaignPage.setLayout(editCampaignPageLayout);
            editCampaignPageLayout.setHorizontalGroup(
                editCampaignPageLayout.createParallelGroup()
                    .addGroup(editCampaignPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label21, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Inapoi4, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button18)
                        .addGap(38, 38, 38)
                        .addComponent(Username7, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(editCampaignPageLayout.createSequentialGroup()
                        .addGroup(editCampaignPageLayout.createParallelGroup()
                            .addGroup(editCampaignPageLayout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addGroup(editCampaignPageLayout.createParallelGroup()
                                    .addComponent(label22, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(numeCampanie2, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label23, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(newDescriereCampanie, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label27, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(editBuget, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE))
                                .addGroup(editCampaignPageLayout.createParallelGroup()
                                    .addGroup(editCampaignPageLayout.createSequentialGroup()
                                        .addGap(63, 63, 63)
                                        .addGroup(editCampaignPageLayout.createParallelGroup()
                                            .addComponent(newDataFinalizare, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label25, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(newDataStart, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label24, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(editCampaignPageLayout.createSequentialGroup()
                                        .addGap(49, 49, 49)
                                        .addComponent(searchCampaign, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE))))
                            .addGroup(editCampaignPageLayout.createSequentialGroup()
                                .addGap(187, 187, 187)
                                .addGroup(editCampaignPageLayout.createParallelGroup()
                                    .addComponent(editCampaign, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label26, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            editCampaignPageLayout.setVerticalGroup(
                editCampaignPageLayout.createParallelGroup()
                    .addGroup(editCampaignPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(editCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Username7, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label21)
                            .addComponent(button18)
                            .addComponent(Inapoi4))
                        .addGap(18, 18, 18)
                        .addGroup(editCampaignPageLayout.createParallelGroup()
                            .addGroup(editCampaignPageLayout.createSequentialGroup()
                                .addComponent(label22)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numeCampanie2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(searchCampaign))
                        .addGap(18, 18, 18)
                        .addGroup(editCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label23)
                            .addComponent(label24))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(newDescriereCampanie, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(newDataStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(editCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label27)
                            .addComponent(label25))
                        .addGap(6, 6, 6)
                        .addGroup(editCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(editBuget, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(newDataFinalizare, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addComponent(label26)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editCampaign)
                        .addContainerGap(14, Short.MAX_VALUE))
            );
        }

        //======== closeCampaignPage ========
        {

            //---- Username8 ----
            Username8.setText("Username");

            //---- label28 ----
            label28.setText("Voucher Management System");

            //---- Inapoi5 ----
            Inapoi5.setText("Inapoi ");
            Inapoi5.addActionListener(e -> {
			InapoiActionPerformed(e);
			Inapoi3ActionPerformed(e);
		});

            //---- button19 ----
            button19.setText("LOGOUT");
            button19.addActionListener(e -> button3ActionPerformed(e));

            //---- label29 ----
            label29.setText("Nume Campanie");

            //---- label30 ----
            label30.setText("Descriere Campanie");

            //---- label31 ----
            label31.setText("Data Start");

            //---- label32 ----
            label32.setText("Data Finalizare");

            //---- editCampaign2 ----
            editCampaign2.setText("Inchide Campanie");
            editCampaign2.addActionListener(e -> editCampaign2ActionPerformed(e));

            //---- label33 ----
            label33.setText("Buget");

            //---- searchCampaign2 ----
            searchCampaign2.setText("Cauta campanie");
            searchCampaign2.addActionListener(e -> searchCampaign2ActionPerformed(e));

            GroupLayout closeCampaignPageLayout = new GroupLayout(closeCampaignPage);
            closeCampaignPage.setLayout(closeCampaignPageLayout);
            closeCampaignPageLayout.setHorizontalGroup(
                closeCampaignPageLayout.createParallelGroup()
                    .addGroup(closeCampaignPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label28, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Inapoi5, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button19)
                        .addGap(38, 38, 38)
                        .addComponent(Username8, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(closeCampaignPageLayout.createSequentialGroup()
                        .addGroup(closeCampaignPageLayout.createParallelGroup()
                            .addGroup(closeCampaignPageLayout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addGroup(closeCampaignPageLayout.createParallelGroup()
                                    .addComponent(label29, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(numeCampanie3, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label30, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(newDescriereCampanie2, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label33, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(editBuget2, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE))
                                .addGroup(closeCampaignPageLayout.createParallelGroup()
                                    .addGroup(closeCampaignPageLayout.createSequentialGroup()
                                        .addGap(63, 63, 63)
                                        .addGroup(closeCampaignPageLayout.createParallelGroup()
                                            .addComponent(newDataFinalizare2, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label32, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(newDataStart2, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label31, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(closeCampaignPageLayout.createSequentialGroup()
                                        .addGap(49, 49, 49)
                                        .addComponent(searchCampaign2, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE))))
                            .addGroup(closeCampaignPageLayout.createSequentialGroup()
                                .addGap(187, 187, 187)
                                .addComponent(editCampaign2, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            closeCampaignPageLayout.setVerticalGroup(
                closeCampaignPageLayout.createParallelGroup()
                    .addGroup(closeCampaignPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(closeCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Username8, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label28)
                            .addComponent(button19)
                            .addComponent(Inapoi5))
                        .addGap(18, 18, 18)
                        .addGroup(closeCampaignPageLayout.createParallelGroup()
                            .addGroup(closeCampaignPageLayout.createSequentialGroup()
                                .addComponent(label29)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numeCampanie3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(searchCampaign2))
                        .addGap(18, 18, 18)
                        .addGroup(closeCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label30)
                            .addComponent(label31))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(closeCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(newDescriereCampanie2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(newDataStart2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(closeCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label33)
                            .addComponent(label32))
                        .addGap(6, 6, 6)
                        .addGroup(closeCampaignPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(editBuget2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(newDataFinalizare2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(editCampaign2)
                        .addContainerGap(24, Short.MAX_VALUE))
            );
        }

        //======== beforeVoucherAdminPage ========
        {

            //---- Username9 ----
            Username9.setText("Username");

            //---- label34 ----
            label34.setText("Voucher Management System");

            //---- Inapoi6 ----
            Inapoi6.setText("Inapoi ");
            Inapoi6.addActionListener(e -> {
			InapoiActionPerformed(e);
			Inapoi2ActionPerformed(e);
		});

            //---- button13 ----
            button13.setText("LOGOUT");
            button13.addActionListener(e -> button3ActionPerformed(e));

            //---- label35 ----
            label35.setText("Nume Campanie");

            //---- searchCampaign3 ----
            searchCampaign3.setText("Alege campanie");
            searchCampaign3.addActionListener(e -> {
			searchCampaign3ActionPerformed(e);
			searchCampaign3ActionPerformed(e);
		});

            GroupLayout beforeVoucherAdminPageLayout = new GroupLayout(beforeVoucherAdminPage);
            beforeVoucherAdminPage.setLayout(beforeVoucherAdminPageLayout);
            beforeVoucherAdminPageLayout.setHorizontalGroup(
                beforeVoucherAdminPageLayout.createParallelGroup()
                    .addGroup(beforeVoucherAdminPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label34, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Inapoi6, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button13)
                        .addGap(38, 38, 38)
                        .addComponent(Username9, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(beforeVoucherAdminPageLayout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addGroup(beforeVoucherAdminPageLayout.createParallelGroup()
                            .addComponent(label35, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                            .addGroup(beforeVoucherAdminPageLayout.createSequentialGroup()
                                .addComponent(numeCampanie4, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35)
                                .addComponent(searchCampaign3, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            beforeVoucherAdminPageLayout.setVerticalGroup(
                beforeVoucherAdminPageLayout.createParallelGroup()
                    .addGroup(beforeVoucherAdminPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(beforeVoucherAdminPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Username9, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label34)
                            .addComponent(button13)
                            .addComponent(Inapoi6))
                        .addGap(74, 74, 74)
                        .addComponent(label35)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(beforeVoucherAdminPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(numeCampanie4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchCampaign3))
                        .addContainerGap(116, Short.MAX_VALUE))
            );
        }

        //======== adminVoucherPage ========
        {

            //---- Username10 ----
            Username10.setText("Username");

            //---- label36 ----
            label36.setText("Voucher Management System");

            //---- label37 ----
            label37.setText("Voucherele din campanie");
            label37.setHorizontalAlignment(SwingConstants.CENTER);
            label37.setFont(label37.getFont().deriveFont(label37.getFont().getSize() + 4f));

            //======== normalUserVoucherHolder3 ========
            {
                normalUserVoucherHolder3.setViewportView(table3);
            }

            //---- Inapoi7 ----
            Inapoi7.setText("Inapoi ");
            Inapoi7.addActionListener(e -> {
			InapoiActionPerformed(e);
			Inapoi7ActionPerformed(e);
		});

            //---- button20 ----
            button20.setText("LOGOUT");
            button20.addActionListener(e -> button3ActionPerformed(e));

            //---- redeem ----
            redeem.setText("Redeem");
            redeem.addActionListener(e -> {
			InapoiActionPerformed(e);
			redeemActionPerformed(e);
		});

            //---- generare ----
            generare.setText("Generare");
            generare.addActionListener(e -> {
			InapoiActionPerformed(e);
			generareActionPerformed(e);
		});

            GroupLayout adminVoucherPageLayout = new GroupLayout(adminVoucherPage);
            adminVoucherPage.setLayout(adminVoucherPageLayout);
            adminVoucherPageLayout.setHorizontalGroup(
                adminVoucherPageLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, adminVoucherPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label36, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                        .addComponent(button20)
                        .addGap(86, 86, 86)
                        .addComponent(Username10, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(adminVoucherPageLayout.createSequentialGroup()
                        .addGroup(adminVoucherPageLayout.createParallelGroup()
                            .addGroup(adminVoucherPageLayout.createSequentialGroup()
                                .addGap(110, 110, 110)
                                .addComponent(label37, GroupLayout.PREFERRED_SIZE, 353, GroupLayout.PREFERRED_SIZE))
                            .addGroup(adminVoucherPageLayout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(normalUserVoucherHolder3, GroupLayout.PREFERRED_SIZE, 507, GroupLayout.PREFERRED_SIZE))
                            .addGroup(adminVoucherPageLayout.createSequentialGroup()
                                .addGap(107, 107, 107)
                                .addComponent(generare, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(redeem, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(Inapoi7, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(55, Short.MAX_VALUE))
            );
            adminVoucherPageLayout.setVerticalGroup(
                adminVoucherPageLayout.createParallelGroup()
                    .addGroup(adminVoucherPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(adminVoucherPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Username10, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label36)
                            .addComponent(button20))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label37, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(normalUserVoucherHolder3, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(adminVoucherPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(generare)
                            .addComponent(redeem)
                            .addComponent(Inapoi7))
                        .addContainerGap(37, Short.MAX_VALUE))
            );
        }

        //======== redeemVoucherPage ========
        {

            //---- Username11 ----
            Username11.setText("Username");

            //---- label38 ----
            label38.setText("Voucher Management System");

            //---- Inapoi8 ----
            Inapoi8.setText("Inapoi ");
            Inapoi8.addActionListener(e -> {
			InapoiActionPerformed(e);
			Inapoi8ActionPerformed(e);
		});

            //---- button21 ----
            button21.setText("LOGOUT");
            button21.addActionListener(e -> button3ActionPerformed(e));

            //---- label39 ----
            label39.setText(" Cod Voucher");

            //---- redeemVoucher ----
            redeemVoucher.setText("Redeem Voucher");
            redeemVoucher.addActionListener(e -> redeemVoucherActionPerformed(e));

            GroupLayout redeemVoucherPageLayout = new GroupLayout(redeemVoucherPage);
            redeemVoucherPage.setLayout(redeemVoucherPageLayout);
            redeemVoucherPageLayout.setHorizontalGroup(
                redeemVoucherPageLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, redeemVoucherPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label38, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button21)
                        .addGap(86, 86, 86)
                        .addComponent(Username11, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(redeemVoucherPageLayout.createSequentialGroup()
                        .addGroup(redeemVoucherPageLayout.createParallelGroup()
                            .addGroup(redeemVoucherPageLayout.createSequentialGroup()
                                .addGap(120, 120, 120)
                                .addGroup(redeemVoucherPageLayout.createParallelGroup()
                                    .addGroup(redeemVoucherPageLayout.createSequentialGroup()
                                        .addComponent(VoucherCode, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
                                        .addGap(44, 44, 44)
                                        .addComponent(redeemVoucher))
                                    .addComponent(label39, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(redeemVoucherPageLayout.createSequentialGroup()
                                .addGap(217, 217, 217)
                                .addComponent(Inapoi8, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            redeemVoucherPageLayout.setVerticalGroup(
                redeemVoucherPageLayout.createParallelGroup()
                    .addGroup(redeemVoucherPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(redeemVoucherPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Username11, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label38)
                            .addComponent(button21))
                        .addGap(78, 78, 78)
                        .addComponent(label39)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(redeemVoucherPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(VoucherCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(redeemVoucher))
                        .addGap(33, 33, 33)
                        .addComponent(Inapoi8)
                        .addContainerGap(135, Short.MAX_VALUE))
            );
        }

        //======== generateVoucherPage ========
        {

            //---- Username12 ----
            Username12.setText("Username");

            //---- label40 ----
            label40.setText("Voucher Management System");

            //---- Inapoi9 ----
            Inapoi9.setText("Inapoi ");
            Inapoi9.addActionListener(e -> {
			InapoiActionPerformed(e);
			Inapoi8ActionPerformed(e);
		});

            //---- button22 ----
            button22.setText("LOGOUT");
            button22.addActionListener(e -> button3ActionPerformed(e));

            //---- label41 ----
            label41.setText("Email utilizator");

            //---- generateVoucher ----
            generateVoucher.setText("Generare Voucher");
            generateVoucher.addActionListener(e -> generateVoucherActionPerformed(e));

            //---- label42 ----
            label42.setText("Gift/Loyalty");

            //---- label43 ----
            label43.setText("Valoare");

            GroupLayout generateVoucherPageLayout = new GroupLayout(generateVoucherPage);
            generateVoucherPage.setLayout(generateVoucherPageLayout);
            generateVoucherPageLayout.setHorizontalGroup(
                generateVoucherPageLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, generateVoucherPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label40, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button22)
                        .addGap(86, 86, 86)
                        .addComponent(Username12, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(generateVoucherPageLayout.createSequentialGroup()
                        .addGap(120, 120, 120)
                        .addGroup(generateVoucherPageLayout.createParallelGroup()
                            .addComponent(label43, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
                            .addComponent(value, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label41, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
                            .addComponent(userMail, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label42, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
                            .addComponent(voucherType, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
                            .addGroup(generateVoucherPageLayout.createSequentialGroup()
                                .addComponent(generateVoucher)
                                .addGap(28, 28, 28)
                                .addComponent(Inapoi9, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            generateVoucherPageLayout.setVerticalGroup(
                generateVoucherPageLayout.createParallelGroup()
                    .addGroup(generateVoucherPageLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(generateVoucherPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Username12, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label40)
                            .addComponent(button22))
                        .addGap(18, 18, 18)
                        .addComponent(label41)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(userMail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(label42)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(voucherType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label43)
                        .addGap(12, 12, 12)
                        .addComponent(value, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(generateVoucherPageLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(generateVoucher)
                            .addComponent(Inapoi9))
                        .addContainerGap(70, Short.MAX_VALUE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(generateVoucherPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(redeemVoucherPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(adminVoucherPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(beforeVoucherAdminPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeCampaignPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(editCampaignPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addCampaignPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(AdminCampaignPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(AdminUserPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(normalUserPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(normalUserVoucherPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap(43, Short.MAX_VALUE)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(layeredPane1, GroupLayout.PREFERRED_SIZE, 712, GroupLayout.PREFERRED_SIZE))
                    .addGap(23, 23, 23))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(generateVoucherPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(redeemVoucherPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(adminVoucherPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(beforeVoucherAdminPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeCampaignPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(editCampaignPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addCampaignPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(AdminCampaignPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(AdminUserPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(normalUserPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createParallelGroup()
                    .addGroup(contentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(normalUserVoucherPage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(37, 37, 37)
                    .addComponent(layeredPane1, GroupLayout.PREFERRED_SIZE, 378, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Necșoiu Robert-Emanoil
    private JLayeredPane layeredPane1;
    private JPanel panel1;
    private JLabel label3;
    private JLabel label1;
    private JButton Users;
    private JLabel label2;
    private JButton Campaigns;
    private JButton button1;
    private JPanel panel2;
    private JLabel label4;
    private JLabel label5;
    private JTextField Username;
    private JTextField Password;
    private JLabel label6;
    private JButton button2;
    private JPanel normalUserVoucherPage;
    private JLabel Username3;
    private JLabel label9;
    private JLabel label10;
    private JScrollPane normalUserVoucherHolder;
    private JTable table1;
    private JButton Inapoi;
    private JButton button3;
    private JPanel normalUserPage;
    private JLabel Username2;
    private JLabel label7;
    private JLabel label8;
    private JButton seeCampaigns;
    private JButton seeVouchers;
    private JButton button4;
    private JPanel AdminUserPage;
    private JLabel Username4;
    private JLabel label11;
    private JLabel label12;
    private JButton button5;
    private JButton button6;
    private JButton button7;
    private JPanel AdminCampaignPage;
    private JLabel Username5;
    private JLabel label13;
    private JScrollPane normalUserVoucherHolder2;
    private JTable table2;
    private JButton Inapoi2;
    private JButton button8;
    private JButton button9;
    private JButton button10;
    private JButton button11;
    private JButton button12;
    private JButton button14;
    private JButton button15;
    private JButton button16;
    private JPanel addCampaignPage;
    private JLabel Username6;
    private JLabel label14;
    private JButton Inapoi3;
    private JButton button17;
    private JTextField numeCampanie;
    private JLabel label15;
    private JLabel label16;
    private JTextField descriereCampanie;
    private JLabel label17;
    private JTextField dataStart;
    private JTextField dataFinalizare;
    private JLabel label18;
    private JButton addCampaign;
    private JLabel label19;
    private JLabel label20;
    private JTextField addBuget;
    private JPanel editCampaignPage;
    private JLabel Username7;
    private JLabel label21;
    private JButton Inapoi4;
    private JButton button18;
    private JTextField numeCampanie2;
    private JLabel label22;
    private JLabel label23;
    private JTextField newDescriereCampanie;
    private JLabel label24;
    private JTextField newDataStart;
    private JTextField newDataFinalizare;
    private JLabel label25;
    private JButton editCampaign;
    private JLabel label26;
    private JLabel label27;
    private JTextField editBuget;
    private JButton searchCampaign;
    private JPanel closeCampaignPage;
    private JLabel Username8;
    private JLabel label28;
    private JButton Inapoi5;
    private JButton button19;
    private JTextField numeCampanie3;
    private JLabel label29;
    private JLabel label30;
    private JTextField newDescriereCampanie2;
    private JLabel label31;
    private JTextField newDataStart2;
    private JTextField newDataFinalizare2;
    private JLabel label32;
    private JButton editCampaign2;
    private JLabel label33;
    private JTextField editBuget2;
    private JButton searchCampaign2;
    private JPanel beforeVoucherAdminPage;
    private JLabel Username9;
    private JLabel label34;
    private JButton Inapoi6;
    private JButton button13;
    private JTextField numeCampanie4;
    private JLabel label35;
    private JButton searchCampaign3;
    private JPanel adminVoucherPage;
    private JLabel Username10;
    private JLabel label36;
    private JLabel label37;
    private JScrollPane normalUserVoucherHolder3;
    private JTable table3;
    private JButton Inapoi7;
    private JButton button20;
    private JButton redeem;
    private JButton generare;
    private JPanel redeemVoucherPage;
    private JLabel Username11;
    private JLabel label38;
    private JButton Inapoi8;
    private JButton button21;
    private JTextField VoucherCode;
    private JLabel label39;
    private JButton redeemVoucher;
    private JPanel generateVoucherPage;
    private JLabel Username12;
    private JLabel label40;
    private JButton Inapoi9;
    private JButton button22;
    private JTextField userMail;
    private JLabel label41;
    private JButton generateVoucher;
    private JLabel label42;
    private JTextField voucherType;
    private JLabel label43;
    private JTextField value;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    //metoda main pentru pornirea aplicatiei
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Home2 GUI = new Home2();
                GUI.setSize(700,600);
                GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                GUI.setVisible(true);
            }
        });
    }
}
