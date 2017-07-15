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
public class Current implements Command{
    
    @Override
    public void apply(Scanner line){
        if (!isLoggedIn()){
            System.out.println("Login to use this command");
            return;
        }
        String tokens = line.nextLine();
        String parts[] = tokens.split("\\s+");
        
        if (parts.length != 2){
            System.out.println("usage: current <search/videos>");
        }else{
            if (parts[1].equals("search")){
                if (SpeechStream.prevSearch == null){
                    System.out.println("- No search at the moment.");
                }else{
                    System.out.println("- " + SpeechStream.prevSearch);
                }
            }else if (parts[1].equals("videos")){
                displayVideos();
            }else{
                System.out.println("usage: current <search/videos>");
            }
        }
    }
    
    public void displayVideos(){
        System.out.println("- Displaying video list from last recent search");
        int size = SpeechStream.currentVidList.size();
        if (size == 0){
            System.out.println("- No videos at the moment.");
        }
        for(int i = 0; i < size;i++){
            VideoInfo current = SpeechStream.currentVidList.get(i);
            System.out.println("Play ID #" + i);
//            System.out.println("ID " + current.getID());
            System.out.println("Title: " + current.getTitle());
            System.out.println("Uploader: " + current.getUploader());
            System.out.println("Views " + current.getViews());
            System.out.println("------------");
        }
    }
    
    public boolean isLoggedIn(){
        if (SpeechStream.status == -1){
            return false;
        }else{
            return true;
        }
    }
    
    
}
