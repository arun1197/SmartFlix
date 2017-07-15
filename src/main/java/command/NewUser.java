/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package command;

import java.util.Scanner;
import service.UserService;
import speechstream.SpeechStream;

/**
 *
 * @author DevSingh
 */
public class NewUser implements Command{
    
    UserService userService;
    
    public NewUser(UserService userService){
        this.userService = userService;
    }

    /* 
        - Handle parsing and printing response
        - allows newuser if all conditions met
    */
    @Override
    public void apply(Scanner line) {
        if (isLoggedIn()){
            System.out.println("Logout to create new user");
            return;
        }
        String tokens = line.nextLine();
        String parts[] = tokens.split("\\s+");
        if (parts.length != 4){
            System.out.println("usage: newuser <username> password <password>");
        }else{
            if (!parts[2].equals("password")){
                System.out.println("usage: newuser <username> password <password>");
            }else{
                if (usernameCheck(parts[1]) && passwordCheck(parts[3])){
                    //calls user service to create
                    userService.create(parts[1], parts[3]);
                }
            }
        }
    }
    
    /*Checks length of username and if the first character is a letter*/
    private boolean usernameCheck(String username){
        if (username.length() < 5 || username.length() > 16){
            System.out.println("username length between 5 and 16 characters");
            return false;
        } else if (!Character.isLetter(username.charAt(0))){
            System.out.println("Username must start with an alphabet");
            return false;
        } else{
            return true;
        }
    }
    
    /*Checks password length*/
    private boolean passwordCheck(String password){
        if (password.length() < 5){
            System.out.println("Password length should be greater than 5");
            return false;
        }
        return true;
    }
    
    public boolean isLoggedIn(){
        if (SpeechStream.status == -1){
            return false;
        }else{
            return true;
        }
    }
    
}
