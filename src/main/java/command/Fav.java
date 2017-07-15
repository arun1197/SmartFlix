/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package command;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import java.util.Scanner;
import speechstream.SpeechStream;
import youtube.VideoInfo;

/**
 *
 * @author DevSingh
 */
public class Fav implements Command{
    
    @Override
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
            int playId = Integer.parseInt(parts[1]);
            if (checkCurrentVidList(playId)){
                favVideo(playId);
            }
        }else{
            System.out.println("usage: fav <Play ID>");
        }
    }
    
    public static boolean isNumeric(String str){
      return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
    
    public boolean isLoggedIn(){
        if (SpeechStream.status == -1){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean checkCurrentVidList(int n){
        int size = SpeechStream.currentVidList.size();
        if (size == 0){
            System.out.println("- No current videos at the moment");
            return false;
        }else if (n < 0 || n >= size){
            System.out.println("- Please enter between " + 0 + " and " + (size-1));
            return false;
        }else{
            return true;
        }
    }
    
    public void favVideo(int playId){
        VideoInfo videoInfo = SpeechStream.currentVidList.get(playId);
        Table table = SpeechStream.dynamoDB.getTable("favoriteVideos");
        Item item = new Item().withPrimaryKey("username", SpeechStream.loggedInUser).withString("video_id", videoInfo.getID())
                .withString("uploader", videoInfo.getUploader())
                .withString("views", videoInfo.getViews())
                .withString("title", videoInfo.getTitle());
        table.putItem(item);
        for(int i = 0; i < SpeechStream.currentFavList.size();i++){
            VideoInfo vid = SpeechStream.currentFavList.get(i);
            if (vid.getID().equals(videoInfo.getID())){
                return;
            }
        }
        SpeechStream.currentFavList.add(videoInfo);
    }
    
}
