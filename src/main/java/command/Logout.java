/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package command;

import java.util.Scanner;
import speechstream.SpeechStream;
/**
 *
 * @author DevSingh
 */
public class Logout implements Command {

    /* 
        - Handle parsing and printing response
        - allows logout if all conditions met
    */
    @Override
    public void apply(Scanner line) {
        String tokens = line.nextLine();
        String parts[] = tokens.split("\\s+");
        if (parts.length == 1){
            /* Making the logged in variables null*/
            SpeechStream.status = -1;
            SpeechStream.loggedInUser = null;
            SpeechStream.prevSearch = null;
            SpeechStream.currentVidList.clear();
            SpeechStream.currentFavList.clear();
            System.out.println("OK");
        }else {
            System.out.println("usage: logout");
        }
    }
    

}
