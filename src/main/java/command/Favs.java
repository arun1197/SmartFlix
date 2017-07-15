/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package command;

import java.util.Scanner;
import speechstream.SpeechStream;
import youtube.VideoInfo;

/**
 *
 * @author DevSingh
 */
public class Favs implements Command{
    public void apply(Scanner line){
        if (!isLoggedIn()){
            System.out.println("Login to use this command");
            return;
        }
        String tokens = line.nextLine();
        String parts[] = tokens.split("\\s+");
        if (parts.length == 1){
            view();
        }else{
            System.out.println("usage: favs");
        }
        
    }
    
    public boolean isLoggedIn(){
        if (SpeechStream.status == -1){
            return false;
        }else{
            return true;
        }
    }
    
    public void view(){
        int size = SpeechStream.currentFavList.size();
        for (int i = 0; i < size; i++){
            VideoInfo vid = SpeechStream.currentFavList.get(i);
            System.out.println("Fav ID #" + i);
            System.out.println("Title: " + vid.getTitle());
            System.out.println("Uploader: " + vid.getUploader());
            System.out.println("Views " + vid.getViews());
            System.out.println("------------");
        }
    }
    

    

}
