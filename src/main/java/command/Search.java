/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package command;
//import java.io.File;
import speechtotext.JavaSoundRecorder;
import speechtotext.SpeechConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import speechstream.SpeechStream;
import youtube.SearchYoutube;

public class Search implements Command{
    JavaSoundRecorder rec;
    SpeechConverter con;
    SearchYoutube finder;
    private final List<String> orders = new ArrayList<String>() {{
        add("date");
        add("rating");
        add("relevance");
        add("viewcount");
    }};
    
    public Search(JavaSoundRecorder rec, SpeechConverter con, SearchYoutube finder){
        this.rec = rec;
        this.con = con;
        this.finder = finder;
    }
    
    @Override
    public void apply(Scanner line){
        if (!isLoggedIn()){
            System.out.println("Login to use this command");
            return;
        }
        String tokens = line.nextLine();
        String parts[] = tokens.split("\\s+");
        String searchText = null;
        String searchOrder = null;
        int seconds;
        int videoNumber;
        boolean valid;
        
        if (parts.length != 2){
            System.out.println("usage: search <seconds>");
        }else if (parts.length == 2){
            if (isNumeric(parts[1])){
                seconds =Integer.parseInt(parts[1]);
            }else{
                seconds = 4;
            }
            if (seconds < 4){
                System.out.println("- Must record for a minimum of 4 seconds");
            }else{
                searchText = recordAndConvert(rec, con, seconds);
                valid = checkSearchString(searchText);
                if (valid){
                    searchOrder = searchBy();
                    int searchNum = videosToSearch();
                    finder.searchVideo(searchText, searchOrder, searchNum);
                }
            }
        }
    }
    
    public String recordAndConvert(JavaSoundRecorder rec,SpeechConverter con, int seconds){
        String sb = "";
        try{
            rec.record(seconds);
            sb = con.convert();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return sb;
    }
    
    public boolean checkSearchString(String s){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("---> Proceed search for \"" + s  +"\"? (Y/N)");
            String commandLine = scanner.nextLine();
            if (commandLine.toUpperCase().equals("Y")){
                System.out.println("- Searching for videos");
                return true;
            }else if (commandLine.toUpperCase().equals("N")){
                return false;
            }else{
                System.out.println("- Invalid response, please use either Y or N");
                System.out.println("=======================================");
            }
        }
    }
    
    public String searchBy(){
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        while (true){
            System.out.println(" - Search by [date, rating, relevance, viewcount]");
            String commandLine = scanner.nextLine();
            if (orders.contains(commandLine)){
                return commandLine;
            }
            if (i == 2){
                System.out.println("Too many tries, defaulting to relevance");
                return "relevance";
            }
            i++;
        }
    }
    
    public int videosToSearch(){
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        while (true){
            System.out.println("Number of video searches ?");
            if (i==2){
                System.out.println("- Default: searching for 15 videos");
                return 15;
            }
            String commandLine = scanner.nextLine();
            if (isNumeric(commandLine)){
                int n = Integer.parseInt(commandLine);
                if (n <= 0 || n > 30){
                    System.out.println("- Can request 1 - 30 videos");
                }else{
                    return n;
                }
            }else{
                System.out.println("- Please enter numeric values");
            }
            i++;
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
}
