package Backend;

import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;


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
    public static void Close_Conn_rs(ResultSet rs,Statement stmt,Connection conn) {
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
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) { /* Ignored */}
        }
    }
    public static boolean login(String a,String b){
        String password = "";
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        Boolean pass = false;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT password FROM all_accounts WHERE username = '"+a+"' AND accType = 'Admin'");
            while (rs.next()) {
                password = rs.getString(1);
                String HashPass = HashPassword(b);
                if (HashPass.equals(password))
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
        finally {
            Close_Conn_rs(rs,stmt,conn);
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
            String HashPass = HashPassword(pass);
            stmt.execute("INSERT INTO all_accounts VALUES ('A"+counter+"','"+uname+"','"+phnum+"','"+HashPass+"','-','"+email+"','-','Admin')");
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            Close_Conn_rs(rs,stmt,conn);
        }
        return flag;
    }
    public static boolean DeleteAcc(String accID){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("DELETE FROM all_accounts WHERE accountID = "+accID);
            stmt.execute("DELETE FROM receiver_account WHERE accountID = "+accID);
            stmt.execute("DELETE FROM sender_account WHERE accountID = "+accID);
            stmt.execute("DELETE FROM courier_account WHERE accountID = "+accID);
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            Close_Conn_rs(rs,stmt,conn);
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
            String HashPass = HashPassword(pass);
            if (choice == 1)
            {
                stmt.execute("INSERT INTO all_accounts VALUES ('"+counter+"','"+uname+"','"+phnum+"','"+HashPass+"','"+name+"','"+email+"','"+addr+"','Receiver')");
                stmt.execute("INSERT INTO receiver_account VALUES ('"+counter+"', '"+altCP+"', '"+altCPnum+"')");
            }
            else if (choice == 2)
            {
                stmt.execute("INSERT INTO all_accounts VALUES ('"+counter+"','"+uname+"','"+phnum+"','"+HashPass+"','"+name+"','"+email+"','"+addr+"','Sender')");
                stmt.execute("INSERT INTO sender_account VALUES ('"+counter+"', '"+company+"')");
            }
            else
            {
                double DFunds=Double.parseDouble(funds);
                System.out.println(counter);
                stmt.execute("INSERT INTO all_accounts VALUES ('"+counter+"','"+uname+"','"+phnum+"','"+HashPass+"','"+name+"','"+email+"','"+addr+"','Courier')");
                stmt.execute("INSERT INTO courier_account VALUES ('"+counter+"', '"+company+"', '"+empID+"', '"+DFunds+"')");
            }
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            Close_Conn_rs(rs,stmt,conn);
        }
        return flag;
    }
    public static boolean checkAdminAcc(String a,String b){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        int counter = 0;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM all_accounts WHERE username = '" + a + "' AND email='" + b + "' AND accType='Admin'");
            while (rs.next()) {
                counter++;
            }
            if (counter != 1)
            {
                flag = false;
            }
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            Close_Conn_rs(rs,stmt,conn);
        }
        return flag;
    }
    public static boolean EditAdminAcc(String a,String b,String c){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String HashPass = HashPassword(c);
            stmt = conn.createStatement();
            stmt.execute("UPDATE all_accounts SET password= '"+HashPass+"' WHERE username = '"+a+"' AND email='"+b+"' AND accType='Admin'");
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            Close_Conn_rs(rs,stmt,conn);
        }
        return flag;
    }
    public static boolean EditAcc(String update,String AccID,int choice){
        Connection conn = get_Conn();
        Boolean flag = true;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            if (choice == 1)
            {
                stmt.execute("UPDATE receiver_account SET "+update+" WHERE accountID = '"+AccID+"'");
            }
            else if (choice == 2)
            {
                stmt.execute("UPDATE sender_account SET "+update+" WHERE accountID = '"+AccID+"'");
            }
            else if (choice == 3)
            {
                stmt.execute("UPDATE courier_account SET "+update+" WHERE accountID = '"+AccID+"'");
            }
            else
            {
                stmt.execute("UPDATE all_accounts SET "+update+" WHERE accountID = '"+AccID+"'");
            }
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            Close_Conn_rs(rs,stmt,conn);
        }
        return flag;
    }
    public static ResultSet CAccount_display(){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM all_accounts INNER JOIN courier_account ON courier_account.accountID=all_accounts.accountID;");
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return rs;
    }
    public static ResultSet RAccount_display(){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM all_accounts INNER JOIN receiver_account ON receiver_account.accountID=all_accounts.accountID;");
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return rs;
    }
    public static ResultSet SAccount_display(){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM all_accounts INNER JOIN sender_account ON sender_account.accountID=all_accounts.accountID;");
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return rs;
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
    public static boolean Transaction_add(String b,String c,String d,String e,String f,String h,String i,String k){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        Boolean flag = true;
        ResultSet rs2 = null;
        int counter = 1;
        double dou=Double.parseDouble(k);
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM transaction");
            while (rs.next()) {
                counter++;
            }
            System.out.println(counter);
            String geocode = GeocodingExample.Geocode(i);
            stmt.execute("INSERT INTO transaction VALUES('"+counter+"','"+b+"','"+c+"','"+d+"','"+e+"','"+f+"',0,'"+h+"','"+i+"','"+geocode+"',0,"+dou+")");
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
        } catch (ParseException parseException) {
            flag = false;
            parseException.printStackTrace();
        } finally {
            Close_Conn_rs(rs,stmt,conn);
        }
        return flag;
    }
    public static boolean Transaction_edit(String update,String TranID){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        Boolean flag = true;
        try {
            stmt = conn.createStatement();
            stmt.execute("UPDATE transaction SET "+update+" WHERE transactionID  = '"+TranID+"'");
        }
        catch (SQLException ex){
            flag = false;
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }  finally {
            Close_Conn_rs(rs,stmt,conn);
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
        finally {
            Close_Conn_rs(rs,stmt,conn);
        }
        return flag;
    }
    public static ResultSet view_product(){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
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
    public static ResultSet view_device(){
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM device_specifications");
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return rs;
    }
    public static ArrayList TemperatureGet(String devID){
        ArrayList<String> Tempe = new ArrayList<String>();
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT MAX(dhtTempData),MIN(dhtTempData) FROM `iot_data` WHERE deviceID = '"+devID+"'");
            while (rs.next()) {
                Tempe.add(rs.getString(1));
                Tempe.add(rs.getString(2));
            }
            rs.close();
            rs = stmt.executeQuery("SELECT dhtTempData FROM iot_data WHERE deviceID = '"+devID+"' ORDER BY datetime DESC LIMIT 1");
            while (rs.next()) {
                Tempe.add(rs.getString(1));
            }
        }
        catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            Close_Conn_rs(rs,stmt,conn);
        }
        return Tempe;
    }
    public static ArrayList HumidityGet(String devID){
        ArrayList<String> Humidity = new ArrayList<String>();
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT MAX(dhtHumidityData),MIN(dhtHumidityData) FROM `iot_data` WHERE deviceID = '"+devID+"'");
            while (rs.next()) {
                Humidity.add(rs.getString(1));
                Humidity.add(rs.getString(2));
            }
            rs.close();
            rs = stmt.executeQuery("SELECT dhtHumidityData FROM iot_data WHERE deviceID = '"+devID+"' ORDER BY datetime DESC LIMIT 1");
            while (rs.next()) {
                Humidity.add(rs.getString(1));
            }
        }
        catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            Close_Conn_rs(rs,stmt,conn);
        }
        return Humidity;
    }
    public static ArrayList CurrentGPS(String devID){
        String temp = "";
        Connection conn = get_Conn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT gpsData FROM iot_data WHERE deviceID = '"+devID+"' ORDER BY datetime DESC LIMIT 1");
            while (rs.next()) {
                temp = rs.getString(1);
            }
        }
        catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            Close_Conn_rs(rs,stmt,conn);
        }
        ArrayList<String> GPS = new ArrayList<>(Arrays.asList(temp.split(",")));
        return GPS;
    }
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException{
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }
    public static String toHexString(byte[] hash){
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
    public static String HashPassword(String pwrd){
        String hashpass = "";
        try {
            hashpass = toHexString(getSHA(pwrd));
            System.out.println("\n" + pwrd + " : " + hashpass);
        }
        catch (NoSuchAlgorithmException ex){
            System.out.println("SQLException: " + ex.getMessage());
        }
        //continue tmr
        return hashpass;
    }

}


