/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package command;

import com.amazonaws.services.dynamodbv2.document.Table;
import java.util.Scanner;
import speechstream.SpeechStream;
import static speechstream.SpeechStream.dynamoDB;
import youtube.VideoInfo;

/**
 *
 * @author DevSingh
 */
public class Unfav implements Command{
    public void apply(Scanner line){
        if (!isLoggedIn()){
            System.out.println("Login to use this command");
            return;
        }
        String tokens = line.nextLine();
        String parts[] = tokens.split("\\s+");
        
        if (parts.length == 2){
            if (!isNumeric(parts[1])){
                System.out.println("Numeric value is required");
                return;
            }
            int favId = Integer.parseInt(parts[1]);
            if (checkCurrentFavList(favId)){
                unFavVideo(favId);
            }
            
        }else{
            System.out.println("usage: unfav <Fav ID>");
            System.out.println("- can view via `favs`");
        }
    }
    
    public void unFavVideo(int favId){
        VideoInfo videoInfo = SpeechStream.currentFavList.get(favId);
        Table table = dynamoDB.getTable("favoriteVideos");
        table.deleteItem("username", SpeechStream.loggedInUser, "video_id", videoInfo.getID());
        SpeechStream.currentFavList.removeElementAt(favId);
    }
    
    public boolean isLoggedIn(){
        if (SpeechStream.status == -1){
            return false;
        }else{
            return true;
        }
    }
    
    public static boolean isNumeric(String str){
      return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
    
    public boolean checkCurrentFavList(int n){
        int size = SpeechStream.currentFavList.size();
        if (size == 0){
            System.out.println("- No favorite videos at the moment");
            return false;
        }else if (n < 0 || n >= size){
            System.out.println("- Please enter between " + 0 + " and " + (size-1));
            return false;
        }else{
            return true;
        }
    }
}
