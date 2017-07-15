/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package command;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import speechstream.SpeechStream;
import youtube.VideoInfo;

/**
 *
 * @author DevSingh
 */
public class PlayFav implements Command{
    public void apply(Scanner line){
        if (!isLoggedIn()){
            System.out.println("Login to use this command");
            return;
        }
        String tokens = line.nextLine();
        String parts[] = tokens.split("\\s+");
        if (!isNumeric(parts[1])){
                System.out.println("- Numeric value is required");
        }
        int favId = Integer.parseInt(parts[1]);
        int size = SpeechStream.currentFavList.size();
        if (favId < 0){
            System.out.println("- playID between 0 and " + (size - 1));
        }else if (favId > size){
            System.out.println("- playID between 0 and " + (size - 1));
        }else{
            if (size != 0){
                VideoInfo vId = SpeechStream.currentFavList.get(favId);
                try{
                    play(vId.getID());
                }catch (Exception e){
                }
            }else{
                System.out.println("- No favourites");
            }
        }
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
    
    public void play(String videoId) throws URISyntaxException{
        URI uri = new URI("http://www.youtube.com/watch?v=" + videoId);
        if (Desktop.isDesktopSupported()) {
          try {
            Desktop.getDesktop().browse(uri);
          }catch (IOException e) { /* TODO: error handling */ }
        }else{ /* TODO: error handling */ }
    }
    
}
