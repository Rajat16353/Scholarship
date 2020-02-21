/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dbms.Scholarship;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author gupta
 */
public class Database {

    Connection conn;
    Statement st;

    private static String usrname;
    private static String temp;
    private static String application_id;
    public Database() {
        //usrname=new String();
        init();
    }
    private void init() 
    {
        try {  
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Scholarship","root","mysql");
        } 
        catch (SQLException ex) {
           System.out.println("Connection Exception : "+ex);
        }
        catch (ClassNotFoundException ex) {
           System.out.println("Exception : "+ex);
        }  
    }//Constructor to establish connection
        
    public Boolean userLogin(String userid,String passwd)
    {
        //System.out.println(""+ userid);
        try {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("Select * from login where username='"+userid+"' and password=MD5('"+passwd+"')");
            rs.next();
            rs.getString(2);
                JOptionPane.showMessageDialog(null,"Logged in Successfully....");
                st.executeUpdate("update login set status='in' where username='"+userid+"'");
                usrname=String.valueOf(userid);
                //System.out.println(usrname);
                return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"No match found...!!!!");
            System.out.println("Login Exception : "+ex);
        }
        return false;
    }//check database for student ID and password match
    
   public Boolean adminIDLogin(String adminid,String passwd)
    {
        //System.out.println(""+ adminid);
        try {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("Select * from admin where username='"+adminid+"' and password=MD5('"+passwd+"')");
            rs.next();
            rs.getString(2);
            JOptionPane.showMessageDialog(null,"Logged in Successfully....");
            st.executeUpdate("update admin set status='in' where username='"+adminid+"'");
            usrname=String.valueOf(adminid);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"No match Found...!!!!");
            System.out.println("Admin Exception : "+ex);
        }
        return false;
    }//check database for admin ID and password match
    
    public Boolean getUserName(String userid)
    {
        try {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select * from login where username='"+userid+"'");
            return rs.next();
        } catch (SQLException ex) {
            System.out.println("Username Exception : "+ex);
        }
        return false;
    }//compare the new username with the database to check if its already in use
    
    public Boolean addUser(String name,String userid,String passwd,String email,String mob)
    {   
        try {
            st=conn.createStatement();
            st.executeUpdate("Insert into login(Name,username,password,email,contact_no,status) values('"+name+"','"+userid+"',MD5('"+passwd+"'),'"+email+"','"+mob+"','in')");
            usrname=String.valueOf(userid);
            return true;
        }
        catch(SQLException ex)
        {
            System.out.println("Registration Exception :"+ex);
            JOptionPane.showMessageDialog(null,ex.getMessage()+"\nEntered email is already present in the database");
            return false;
        }
    }//register new user(student) 
    
    public ResultSet scholarType()
    {
        ResultSet rs=null;
        try {
            st=conn.createStatement();
            rs=st.executeQuery("select * from scholarship_type");
            rs.next();
        } catch (SQLException ex) {
            System.out.println("Scholarship Type Exception :"+ex);
        }
        return rs;
    }//get name and details of scholarships for Home page and displaying list in select scholar class
    
    public void updateLogin_status()
    {
        try
        {
            //System.out.println("user : "+usrname);
            st=conn.createStatement();
            st.executeUpdate("update login set status='out' where username='"+usrname+"'");
        }
        catch(SQLException ex){
            System.out.println("Update Login Status Exception :"+ex);
        }   
    }//to logout the user(student)
    
    public void updateAdminlogin_status()
    {
        try
        {
            //System.out.println("user : "+usrname);
            st=conn.createStatement();
            st.executeUpdate("update admin set status='out' where username='"+usrname+"'");
        }
        catch(SQLException ex){
            System.out.println("Update Admin Status Exception :"+ex);
        }   
    }//to logout the admin
    
    public void createProfile()
    {
        try
        {
            st=conn.createStatement();
            st.executeUpdate("insert into personal_info(username) values('"+usrname+"')");
            st.executeUpdate("insert into caste_details(username) values('"+usrname+"')");
            st.executeUpdate("insert into academic(username) values('"+usrname+"')");
            st.executeUpdate("insert into address_info(username) values('"+usrname+"')");
            st.executeUpdate("insert into documents(username) values('"+usrname+"')");
            System.out.println("done");
        }
        catch(SQLException ex)
        {
            System.out.println("Create Profile Exception :"+ex);
        }
    }//to create a new profile
    
    public void newApplication(String s)
    {
        int app_count=0;
        try{
            st=conn.createStatement();
            ResultSet ps=st.executeQuery("select ID from scholarship_type where scheme_name='"+s+"'");
            ps.next();
            temp=ps.getString(1);
            ResultSet rs=st.executeQuery("select scheme_id from application_info where username='"+usrname+"'");
            while(rs.next())
            {
                if(rs.getString(1).equals(temp))
                {
                    System.out.println("Yes");
                    JOptionPane.showMessageDialog(null,"You have already applied for this scholarship");
                    return;
                }//to avoid applying for same scholarship twice
                app_count++;
            }
            if(app_count<2)
            {
                st.executeUpdate("insert into application_info(scheme_id,username) values((select ID from scholarship_type where scheme_name='"+s+"'),'"+usrname+"')");
                ResultSet as=st.executeQuery("select application_id from application_info where username='"+usrname+"' and scheme_id='"+temp+"'");
                as.next();
                application_id=as.getString(1);
                 st.executeUpdate("insert into account_details(application_id) values('"+application_id+"')");
                System.out.println(application_id);
            }//not allowing more than two application per user
            else
                JOptionPane.showMessageDialog(null,"Only 2 applications allowed per user...");
        }
        catch(SQLException ex){
            System.out.println("New Application Exception :"+ex);
        }
    }//to create new applications 
    
    public String getName()
    {
        try {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select * from login where username='"+usrname+"'");
            rs.next();
            return rs.getString(1);
        } catch (SQLException ex) {
            System.out.println("Name Exception : "+ex);
        }
        return null;
    }//get the name of the user currently logged in
    
    public String getSchemeID()
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select scheme_id from application_info where username='"+usrname+"'");
            rs.next();
            return rs.getString(1);
        } catch (SQLException ex) {
            System.out.println("Scheme ID Exception : "+ex);
        }
        return null;
    }//to apply income constraints for economically backward classes
    
    public void add_academic(String[] a)
    {
        try{
            st=conn.createStatement();
            st.executeUpdate("update academic set Class='"+a[0]+"',College='"+a[1]+"',Course='"+a[2]+"',Result='"+a[3]+"',Passing_year='"+a[4]+"',Percentage='"+a[5]+"'where username='"+usrname+"'");
        }
        catch(SQLException ex){
            System.out.println("Add Academic values Exception :"+ex);
        }
    }//to add academic info for the user
    
    /*public void del_academic(String[] a)
    {
        try
        {
            st=conn.createStatement();
            st.executeUpdate("delete from academic where College='"+a[1]+"' && username='"+usrname+"' && Class='"+a[0]+"'");
        }
        catch(SQLException ex){
            System.out.println("Delete Academic values Exception :"+ex);
        }
    }//to delete academic info for user
    */
    public ResultSet getTable()
    {
        try{
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select Class,College,Course,Result,Passing_year,Percentage from academic where username='"+usrname+"'");
            return rs;
        }
        catch(SQLException ex){
            System.out.println("Get Academic table Exception :"+ex);
        }
        return null;
    }//get the academic table for a user
    
   /* public Boolean checkWindow(String userid)
    {
        try{
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select * from personal_info where username='"+userid+"'");
            return rs.next();
        }
        catch(SQLException ex){
            System.out.println("Check Window : "+ex);
        }
        return false;
    }//to check if already applied for scholarship or not and redirect accordingly to the appropriate window
    */
    public ResultSet getValues( )
    {
             try{
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select Name,email,contact_no from login where username='"+usrname+"'");
            rs.next();
            return rs;
        }
        catch(SQLException ex){
            System.out.println("Get Values from login Exception :"+ex);
        }
        return null;
    }//to auto fill Name, Email, Contact No. 
    
    public void updateReligion(String religion,String category,String casteno,String caste,String issueAuthor,String issueDate)
    {
        try
        {
            st=conn.createStatement();
            System.out.println("update caste_details set religion='"+religion+"' and category='"+category+"' where username='"+usrname+"'");
            st.executeUpdate("update caste_details set religion='"+religion+"', category='"+category+"' where username='"+usrname+"'");
            if(!category.equals("General"))
            {
                st.executeUpdate("update documents set casteno='"+casteno+"', caste_addr='"+caste+"',caste_authority='"+issueAuthor+"',caste_date='"+issueDate+"' where username='"+usrname+"'");
            }
        }
        catch(SQLException ex)
        {
            System.out.println("Update Religion exception :"+ex);
        }
        
    }
    public void updateaccount(String accno,String IFSC,String bName,String bankName,String application_id)
    {
        try
        {
            st=conn.createStatement();
            //System.out.println("update caste_details set religion='"+religion+"' and category='"+category+"' where username='"+usrname+"'");
             ResultSet rs=st.executeQuery("select * from bank_details where IFSC_code='"+IFSC+"'");
             if(rs.next()==false)
             {
            st.executeUpdate("insert into bank_details(IFSC_code,branch_name,bank_name) values('"+IFSC+"','"+bName+"','"+bankName+"')");
             }
            st.executeUpdate("update account_details set account_no='"+accno+"' ,IFSC_code='"+IFSC+"' where application_id= '"+application_id+"'");
            
        }
        catch(SQLException ex)
        {
            System.out.println("Update account exception :"+ex);
        }
        
    }
     public void updatePersonal(String aadharno,String DOB,String age,String gender,String marital_status,String par_contact,String address,String state,String district,String taluka,String village,String pincode )
    {
        try
        {
            st=conn.createStatement();
            //System.out.println("update caste_details set religion='"+religion+"' and category='"+category+"' where username='"+usrname+"'");
            st.executeUpdate("update personal_info set aadhar_no='"+aadharno+"', DOB='"+DOB+"',age='"+age+"',gender='"+gender+"',marital_status='"+marital_status+"',par_contact='"+par_contact+"' where username='"+usrname+"'");
            st.executeUpdate("update address_info set address='"+address+"', state='"+state+"',district='"+district+"',taluka='"+taluka+"',village='"+village+"',pincode='"+pincode+"' where username='"+usrname+"'");
        }
        catch(SQLException ex)
        {
            System.out.println("Update personal exception :"+ex);
        }
        
    }
      public void updateDocuments(String incomeno,String income,String domicileno,String domicile,String incAuthor,String incDate,String domAuthor,String domDate )
    {
        try
        {
            st=conn.createStatement();
            //System.out.println("update caste_details set religion='"+religion+"' and category='"+category+"' where username='"+usrname+"'");
            st.executeUpdate("update documents set incomeno='"+incomeno+"', income_addr='"+income+"',domicileno='"+domicileno+"',domicile_addr='"+domicile+"',income_authority='"+incAuthor+"',income_Date='"+incDate+"',domicile_authority='"+domAuthor+"',domicile_date='"+domDate+"' where username='"+usrname+"'");
            //st.executeUpdate("update address_info set address='"+address+"', state='"+state+"',district='"+district+"',taluka='"+taluka+"',village='"+village+"',pincode='"+pincode+"' where username='"+usrname+"'");
        }
        catch(SQLException ex)
        {
            System.out.println("Update documents exception :"+ex);
        }
        
    }
        public void updateIncome(String income )
    {
        try
        {
            st=conn.createStatement();
            //System.out.println("update caste_details set religion='"+religion+"' and category='"+category+"' where username='"+usrname+"'");
            st.executeUpdate("update personal_info set income='"+income+"' where username='"+usrname+"'");
            //st.executeUpdate("update address_info set address='"+address+"', state='"+state+"',district='"+district+"',taluka='"+taluka+"',village='"+village+"',pincode='"+pincode+"' where username='"+usrname+"'");
        }
        catch(SQLException ex)
        {
            System.out.println("Update income exception :"+ex);
        }
        
    }
        
    public ResultSet getApplication()
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery(" select application_info.application_id,scholarship_type.scheme_name,application_info.application_status,scholarship_type.benefits from application_info inner join scholarship_type on scholarship_type.ID=application_info.scheme_id where application_info.username='"+usrname+"'");
            rs.next();
            return rs;
        }
        catch(SQLException ex)
        {
            System.out.println("Update documents exception :"+ex);
        }
        return null;
    }
        public String calculateAge(String DOB)
    {
        try
        {
            st=conn.createStatement();
            //System.out.println("update caste_details set religion='"+religion+"' and category='"+category+"' where username='"+usrname+"'");
             ResultSet rs=st.executeQuery("select calc_age('"+DOB+"')");
            rs.next();
            return rs.getString(1);
            
        }
        catch(SQLException ex)
        {
            System.out.println("Update account exception :"+ex);
        }
        return null;
    }
        
    public ResultSet fillCompleteForm(String application_id)
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select personal_info.aadhar_no,login.name,login.email,login.contact_no,personal_info.DOB,personal_info.age,personal_info.gender,personal_info.marital_status,personal_info.par_contact,address_info.address,address_info.state,address_info.district,address_info.taluka,address_info.village,address_info.pincode,caste_details.religion,caste_details.category,account_details.account_no,account_details.IFSC_code,academic.class,academic.college,academic.course,academic.result,academic.passing_year,academic.percentage,personal_info.income,documents.casteno,documents.caste_date,documents.caste_authority,documents.caste_addr,documents.incomeno,documents.income_date,documents.income_authority,documents.income_addr,documents.domicileno,documents.domicile_date,documents.domicile_authority,documents.domicile_addr,application_info.application_status from application_info inner join login on login.username=application_info.username inner join personal_info on personal_info.username=application_info.username inner join address_info on address_info.username=personal_info.username inner join caste_details on caste_details.username=personal_info.username inner join account_details on account_details.application_id=application_info.application_id inner join academic on academic.username=personal_info.username inner join documents on documents.username=personal_info.username where application_info.application_id='"+application_id+"'");
            rs.next();
            return rs;
        }
        catch(SQLException ex)
        {
            System.out.println("Fill Complete Form exception :"+ex);
        }
        return null;
    }
    
    public ResultSet getBankdetails(String IFSC)
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select branch_name,bank_name from bank_details where IFSC_code='"+IFSC+"'");
            rs.next();
            return rs;
        }
        catch(SQLException ex)
        {
            System.out.println("Fill Complete Form exception :"+ex);
        }
        return null;
    }
    
    public Boolean updateApplicationStatus(String application_id)
    {
        try
        {
            st=conn.createStatement();
            st.executeUpdate("Update application_info set application_status='SUBMITTED' where application_id='"+application_id+"'");
            return true;
            
        }
        catch(SQLException ex)
        {
            System.out.println("Fill Complete Form exception :"+ex);
        }
        return false;
    }
    
    public ResultSet getAdminApplications()
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select application_info.application_id as 'Application ID',login.Name as 'Student Name',scholarship_type.scheme_name as 'Scheme Name' from application_info inner join scholarship_type on scholarship_type.id=application_info.scheme_id inner join login on login.username=application_info.username where application_info.application_status='SUBMITTED'");
            return rs;
        }
        catch(SQLException ex)
        {
            System.out.println("Admin applicaions exception :"+ex);
        }
        return null;
    }
    
    public void changeStatusToApproved(String applid)
    {
        try
        {
            st=conn.createStatement();
            st.executeUpdate("update application_info set application_status='APPROVED' where application_id='"+applid+"'");
        }
        catch(SQLException ex)
        {
            System.out.println("Change Status to approved exception :"+ex);
        }  
    }
    
    public void changeStatusToRejected(String applid)
    {
        try
        {
            st=conn.createStatement();
            st.executeUpdate("update application_info set application_status='REJECTED' where application_id='"+applid+"'");
        }
        catch(SQLException ex)
        {
            System.out.println("Change Status to rejected exception :"+ex);
        }  
    }
    
    public ResultSet acceptedApplications()
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select application_info.application_id as 'Application ID',login.Name as 'Student Name',scholarship_type.scheme_name as 'Scheme Name' from application_info inner join scholarship_type on scholarship_type.id=application_info.scheme_id inner join login on login.username=application_info.username where application_info.application_status='APPROVED'");
            return rs;
        }
        catch(SQLException ex)
        {
            System.out.println("Accepted applications exception :"+ex);
        }
        return null;
    }
    
    public ResultSet rejectedApplications()
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select reject_applications.application_id as 'Application ID',login.Name as 'Student Name',scholarship_type.scheme_name as 'Scheme Name' from reject_applications inner join scholarship_type on scholarship_type.id=reject_applications.scheme_id inner join login on login.username=reject_applications.username");
            return rs;
        }
        catch(SQLException ex)
        {
            System.out.println("Rejected applications exception :"+ex);
        }
        return null;
    }
    
    public void callTrigger(String applid,String reason)
    {
        try
        {
            st=conn.createStatement();
            st.executeUpdate("delete from account_details where application_id='"+applid+"'");
            st.executeUpdate("delete from application_info where application_id='"+applid+"'");
            st.executeUpdate("update reject_applications set reason='"+reason+"' where application_id='"+applid+"'");
        }
        catch(SQLException ex){
            System.out.println("callTrigger Exception :"+ex);
        }
    }
    
    public ResultSet rejectNotification()
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select reject_applications.application_id as 'Application ID',scholarship_type.scheme_name as 'Scheme Name' from reject_applications inner join scholarship_type on scholarship_type.ID=reject_applications.scheme_id inner join on application_info where application_info.username='"+usrname+"'");
            rs.next();
            return rs;
        }
        catch(SQLException ex)
        {
            System.out.println("Reject notification exception :"+ex);
        }
        return null;
    }
    
    public ResultSet checkStatus()
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select reject_applications.reason,reject_applications.application_id,reject_applications.username,scholarship_type.scheme_name from reject_applications inner join scholarship_type on scholarship_type.ID=reject_applications.scheme_id where reject_applications.username='"+usrname+"'");
            return rs;
        }
        catch(SQLException ex)
        {
            System.out.println("Reject notification exception :"+ex);
        }
        return null;
    }
    
    public void deleteApplication(String appid)
    {
        try
        {
            st=conn.createStatement();
            st.executeUpdate("delete from reject_applications where application_id='"+appid+"'");
        }
        catch(SQLException ex)
        {
            System.out.println("Reject notification exception :"+ex);
        }
    }
    
    public ResultSet searchApplication(String application_id)
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select application_info.application_id as 'Application ID',login.Name as 'Student Name',scholarship_type.scheme_name as 'Scheme Name' from application_info inner join scholarship_type on scholarship_type.id=application_info.scheme_id inner join login on login.username=application_info.username where application_info.application_status='SUBMITTED' && application_id='"+application_id+"'");
            return rs;
        }
        catch(SQLException ex)
        {
            System.out.println("search database exception :"+ex);
        }
        return null;
    }
    
    public ResultSet searchAcceptedApplication(String application_id)
    {
        try
        {
            st=conn.createStatement();
            ResultSet rs=st.executeQuery("select application_info.application_id as 'Application ID',login.Name as 'Student Name',scholarship_type.scheme_name as 'Scheme Name' from application_info inner join scholarship_type on scholarship_type.id=application_info.scheme_id inner join login on login.username=application_info.username where application_info.application_status='APPROVED' && application_id='"+application_id+"'");
            return rs;
        }
        catch(SQLException ex)
        {
            System.out.println("search database exception :"+ex);
        }
        return null;
    }
}