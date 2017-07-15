/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

/**
 *
 * @author DevSingh
 */
public class UserService {
    SQLQuery sqlquery = new SQLQuery();
    
    /* returns user id if login success, else -1*/
    public int login(String username, String password){
        return sqlquery.login(username, password);
    }
    
    /*Creates new user*/
    public void create(String username,String password){
        sqlquery.create(username, password);
    }
    
    public boolean userExists(String username){
        return sqlquery.exists(username);
    }
}
