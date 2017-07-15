/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.mindrot.jbcrypt.BCrypt;
import speechstream.SpeechStream;

/**
 *
 * @author DevSingh
 */
public class SQLQuery {
    
    /*
        Login matching username and password
        If success - returns user id
        Else - returns -1
    */
    public int login(String username, String password){
        Connection connection = null;
        try{
            String user = null;
            String userid = null;
            String hashed = null;
            connection = getRemoteConnection();
            PreparedStatement prepStat = connection.prepareStatement("SELECT id,username,password FROM users WHERE username = ?");
            prepStat.setString(1,  username);
            ResultSet rs = prepStat.executeQuery();
            while(rs.next()){
                userid = rs.getString(1);
                user = rs.getString(2);
                hashed = rs.getString(3);
            }
            /*If result set is empty*/
            if (!rs.first()){
                connection.close();
                return -1;
            }
            /*Check if the password matches the hashed password from the DB*/
            if (BCrypt.checkpw(password, hashed)){
                SpeechStream.loggedInUser = user;
                connection.close();
                return Integer.parseInt(userid);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }
    
    /*Create a new user*/
    public void create(String username, String password){
        try{
            ResultSet rs = null;
            /*Hash password to store on DB*/
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            Connection connection = getRemoteConnection();
            /*Statement to get username from DB*/
            PreparedStatement prepStat1 = connection.prepareStatement("SELECT username FROM users WHERE username = ? ");
            prepStat1.setString(1,  username);
            rs = prepStat1.executeQuery();
            String user;
            while(rs.next()){
                user = rs.getString(1);
                /*Conditional to check if username has already existed*/
                if (username.equals(user)){
                    System.out.println("Could not create because username already exists");
                    return;
                }
            }
            /*Statement to INSERT new user*/
            PreparedStatement prepStat2 = connection.prepareStatement("INSERT INTO users (username,password) VALUES (?,?)");
            prepStat2.setString(1,  username);
            prepStat2.setString(2,  hashedPassword);
            prepStat2.executeUpdate();
            System.out.println("OK");
            
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    /*Checks to see if user exists*/
    public boolean exists(String username){
        Connection connection = null;
        try{
            String user = null;
            connection = getRemoteConnection();
            PreparedStatement prepStat = connection.prepareStatement("SELECT username FROM users WHERE username = ?");
            prepStat.setString(1,  username);
            ResultSet rs = prepStat.executeQuery();
            while(rs.next()){
                user = rs.getString(1);
            }
            /*If result set is empty*/
            if (!rs.first()){
                System.out.println("User you want to share with does not exist.");
                connection.close();
                return false;
            }else{
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        System.out.println("User you want to share with does not exist.");
        return false;
    }
    
    /* Returns Connection type to be used in login and create method*/
    private static Connection getRemoteConnection() throws SQLException{
        Connection connection = null;
        try{
            Properties prop = new Properties();
            InputStream in = SQLQuery.class.getResourceAsStream("/dbConnection.properties");
            prop.load(in); /*Load properties from file - looks cleaner*/
            in.close();
            Class.forName("com.mysql.jdbc.Driver");
            String dbName = prop.getProperty("dbName");
            String userName = prop.getProperty("username");
            String password = prop.getProperty("password");
            String hostname = prop.getProperty("hostname");
            String jdbcUrl = "jdbc:mysql://" + hostname + ":3306/DevRDS";
            /*Makes connection*/
            connection = DriverManager.getConnection(jdbcUrl,userName,password); 
            return connection;
        }catch (ClassNotFoundException | IOException | SQLException e) { 
//            connection.close();
            e.printStackTrace();
            connection.close();
            System.out.println("Could not make connection");
        }
        return null;
    }
    
}
