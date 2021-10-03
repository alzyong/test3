package Backend;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;


public class DatabaseAccess {
    public static Connection get_Conn() {
        Connection conn = null;
        String password = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }
        try {
            //conn = DriverManager.getConnection("jdbc:mysql://localhost/delivery_system");
            conn = DriverManager.getConnection("jdbc:mysql://dbtest290721.duckdns.org/delivery_sys?user=desktop&password=desktop2021");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return conn;
    }
    public static boolean login(String a,String b){
        String password = "";
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        Boolean pass = false;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT password FROM all_accounts WHERE username = '"+a+"'");
            while (rs.next()) {
                password = rs.getString(1);
                if (b.equals(password))
                {
                    pass = true;
                }
            }
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return pass;
    }
    public static boolean registerAdmin(String uname,String phnum,String pass,String name,String email,String addr){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        int counter = 1;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM all_accounts");
            while (rs.next()) {
                counter++;
            }
            stmt.execute("INSERT INTO all_accounts VALUES ('"+counter+"','"+uname+"','"+phnum+"','"+pass+"','"+name+"','"+email+"','"+addr+"','Admin')");
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return flag;
    }
    public static boolean DeleteAcc(String accID,int choice){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            if (choice == 0)
            {
                stmt.execute("DELETE FROM all_accounts WHERE accountID = "+accID);
            }
            else if (choice == 1)
            {
                stmt.execute("DELETE FROM all_accounts WHERE accountID = "+accID);
                stmt.execute("DELETE FROM receiver_account WHERE accountID = "+accID);
            }
            else if (choice == 2)
            {
                stmt.execute("DELETE FROM all_accounts WHERE accountID = "+accID);
                stmt.execute("DELETE FROM sender_account WHERE accountID = "+accID);
            }
            else
            {
                stmt.execute("DELETE FROM all_accounts WHERE accountID = "+accID);
                stmt.execute("DELETE FROM courier_account WHERE accountID = "+accID);
            }
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return flag;
    }
    public static boolean register(int choice,String uname,String phnum,String pass,String name,String email,String addr,String company,String empID,String funds,String altCP,String altCPnum){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        int counter = 1;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM all_accounts");
            while (rs.next()) {
                counter++;
            }
            if (choice == 1)
            {
                stmt.execute("INSERT INTO all_accounts VALUES ('"+counter+"','"+uname+"','"+phnum+"','"+pass+"','"+name+"','"+email+"','"+addr+"','Receiver')");
                stmt.execute("INSERT INTO receiver_account VALUES ('"+counter+"', '"+altCP+"', '"+altCPnum+"')");
            }
            else if (choice == 2)
            {
                stmt.execute("INSERT INTO all_accounts VALUES ('"+counter+"','"+uname+"','"+phnum+"','"+pass+"','"+name+"','"+email+"','"+addr+"','Sender')");
                stmt.execute("INSERT INTO sender_account VALUES ('"+counter+"', '"+company+"')");
            }
            else
            {
                double DFunds=Double.parseDouble(funds);
                System.out.println(counter);
                stmt.execute("INSERT INTO courier_account VALUES ('"+counter+"', '"+company+"', '"+empID+"', '"+DFunds+"')");
                stmt.execute("INSERT INTO all_accounts VALUES ('"+counter+"','"+uname+"','"+phnum+"','"+pass+"','"+name+"','"+email+"','"+addr+"','Courier')");

            }
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return flag;
    }
    public static boolean EditAdminAcc(String a,String b,String c){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("UPDATE `all_accounts` SET `password`= '"+c+"' WHERE username = '"+a+"' AND email='"+b+"' AND accType='Admin'");
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return flag;
    }
    public static boolean EditAcc(String a,String b,String c,String d,String e,int choice){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            if (choice == 1)
            {
                rs = stmt.executeQuery("UPDATE all_accounts SET username = "+b+", phoneNum = "+c+", password = "+d+", address = "+e+"WHERE accountID = "+a);
                //rs = stmt.executeQuery("UPDATE receiver_account VALUES (a, 'Tom B. Erichsen', 'Skagen 21')");
            }
            else if (choice == 2)
            {
                rs = stmt.executeQuery("UPDATE all_accounts SET username = "+b+", phoneNum = "+c+", password = "+d+", address = "+e+"WHERE accountID = "+a);
                //rs = stmt.executeQuery("UPDATE sender_account VALUES (a, 'Tom B. Erichsen')");
            }
            else
            {
                rs = stmt.executeQuery("UPDATE all_accounts SET username = "+b+", phoneNum = "+c+", password = "+d+", address = "+e+"WHERE accountID = "+a);
                //rs = stmt.executeQuery("UPDATE courier_account VALUES (a, 'Tom B. Erichsen', 'Skagen 21','test','tset')");
            }
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return flag;
    }
    public static ResultSet Account_display(int choice){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            if (choice == 1)
            {
                rs = stmt.executeQuery("SELECT * FROM all_accounts WHERE accType = 'Receiver'");
            }
            else if (choice == 2)
            {
                rs = stmt.executeQuery("SELECT * FROM all_accounts WHERE accType = 'Sender'");
            }
            else
            {
                rs = stmt.executeQuery("SELECT * FROM all_accounts WHERE accType = 'Courier'");
            }
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return rs;
    }
    public static ArrayList SpecificAccountDisplay(String AccID){
        Connection conn = get_Conn();
        ArrayList<String> TransactionDetail = new ArrayList<String>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            System.out.println(AccID);
            rs = stmt.executeQuery("SELECT * FROM all_accounts WHERE accountID = '"+AccID+"'");
            while(rs.next())
            {
                TransactionDetail.add(rs.getString(1));
                TransactionDetail.add(rs.getString(2));
                TransactionDetail.add(rs.getString(3));
                TransactionDetail.add(rs.getString(4));
                TransactionDetail.add(rs.getString(5));
                TransactionDetail.add(rs.getString(6));
                TransactionDetail.add(rs.getString(7));
                TransactionDetail.add(rs.getString(8));
            }
            try {
                rs.close();
            } catch (SQLException sqlEx) {
            }
            if (TransactionDetail.get(7).equals("Receiver"))
            {
                rs = stmt.executeQuery("SELECT * FROM receiver_account WHERE accountID = '"+AccID+"'");
                while(rs.next())
                {
                    TransactionDetail.add(rs.getString(1));
                    TransactionDetail.add(rs.getString(2));
                }
            }
            else if (TransactionDetail.get(7).equals("Sender"))
            {
                rs = stmt.executeQuery("SELECT * FROM sender_account WHERE accountID = '"+AccID+"'");
                while(rs.next())
                {
                    TransactionDetail.add(rs.getString(1));
                }
            }
            else if (TransactionDetail.get(7).equals("Courier"))
            {
                rs = stmt.executeQuery("SELECT * FROM courier_account WHERE accountID = '"+AccID+"'");
                while(rs.next())
                {
                    //int choice,String uname,String phnum,String pass,String name,String email,String addr,String company,String empID,String funds,String altCP,String altCPnum
                    TransactionDetail.add(rs.getString(1));
                    TransactionDetail.add(rs.getString(2));
                    TransactionDetail.add(rs.getString(3));
                }
            }
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return TransactionDetail;
    }
    public static ResultSet Transaction_display(){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM transaction");
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return rs;
    }
    public static ResultSet SpecificTransaction_display(String Tid){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM transaction WHERE transactionID = '"+Tid+"'");
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return rs;
    }
    public static boolean Transaction_add(String a,String b,String c,String d,String e,String f,String h,String i,String k){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        Boolean flag = true;
        double dou=Double.parseDouble(k);
        try {
            stmt = conn.createStatement();
            String geocode = GeocodingExample.Geocode(i);
            stmt.execute("INSERT INTO transaction VALUES('"+a+"','"+b+"','"+c+"','"+d+"','"+e+"','"+f+"',0,'"+h+"','"+i+"','"+geocode+"',0,"+dou+")");
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (IOException ioException) {
            flag = false;
            ioException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            flag = false;
            interruptedException.printStackTrace();
        }
        return flag;
    }
    public static boolean Transaction_edit(String a,String b,String c,String d,String e,String f,String g,String h,String i,String j,String k,String l){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        Boolean flag = true;
        double dou=Double.parseDouble(k);
        int tempi = Integer. parseInt(g);
        int tempi2 = Integer. parseInt(j);
        try {
            stmt = conn.createStatement();
            String geocode = GeocodingExample.Geocode(i);
            stmt.execute("UPDATE transaction SET transactionID='"+a+"',deviceID='"+b+"',senderAcc='"+c+"',pickupAddress='"+d+"',productID='"+e+"',courierAcc='"+f+"',pickedUp="+tempi+",receiverAcc='"+h+"',deliveryAddress='"+i+"',deliveryCoord='"+geocode+"',delivered="+tempi2+",deliveryFunds="+dou+" WHERE transactionID='"+l+"'");
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (IOException ioException) {
            flag = false;
            ioException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            flag = false;
            interruptedException.printStackTrace();
        }
        return flag;
    }
    public static boolean Transaction_delete(String a){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("DELETE FROM transaction where transactionID = "+a);
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return flag;
    }
    public static boolean add_product(String a,String b,String c,String d){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        int counter = 1;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM product_info");
            while (rs.next()) {
                counter++;
            }
            System.out.println(counter);
            if (stmt.execute("INSERT INTO product_info VALUES ("+counter+",'"+a+"','"+b+"','"+c+"','"+d+"')")) {
                rs2 = stmt.getResultSet();
            }
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return flag;
    }
    public static ResultSet view_product(){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        int counter = 1;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM product_info");
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return rs;
    }
    /*public static void test(){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        int counter = 1;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM all_accounts");
            while (rs.next()) {
                counter++;
            }
            System.out.println(counter);
            if (stmt.execute("INSERT INTO all_accounts VALUES ('"+counter+"', 'Tom B. Erichsen', 'Skagen 21', 'Stavanger', '4006', 'Norway')")) {
                rs2 = stmt.getResultSet();
            }
            try {
                rs.close();
            } catch (SQLException sqlEx) { } // ignore
            if (stmt.execute("INSERT INTO courier_account VALUES ('E3402', 'Tom B. Erichsen', 'Skagen 21', 'Stavanger', 4006)")) {
                rs = stmt.getResultSet();
            }
            if (stmt.execute("INSERT INTO receiver_account VALUES ('C7893', 'Tom B. Erichsen', 'Skagen 21')")) {
                rs2 = stmt.getResultSet();
            }
            if (stmt.execute("INSERT INTO sender_account VALUES ('M2893', 'Tom B. Erichsen')")) {
                rs = stmt.getResultSet();
            }

        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }
        }
    }*/
}


