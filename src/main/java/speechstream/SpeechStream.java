/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package speechstream;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import speechtotext.JavaSoundRecorder;
import speechtotext.SpeechConverter;
import command.Command;
import command.Current;
import command.Fav;
import command.Favs;
import command.Play;
import command.Login;
import command.Logout;
import command.NewUser;
import command.PlayFav;
import command.Search;
import command.Unfav;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;
import service.UserService;
import youtube.SearchYoutube;
import youtube.VideoInfo;

/**
 *
 * @author DevSingh
 */
public class SpeechStream {
    
    public static String loggedInUser = null;
    public static int status = -1; /* When logged in shows user id, else -1*/
    public static DynamoDBMapper mapper;
    public static String prevSearch = null;
    public static DynamoDB dynamoDB = null;
    public static Stack<VideoInfo> currentFavList = new Stack<VideoInfo>();
    public static ArrayList<VideoInfo> currentVidList = new ArrayList<VideoInfo>();
    private static UserService userService = new UserService();
    private static JavaSoundRecorder rec = new JavaSoundRecorder();
    private static SpeechConverter con = new SpeechConverter();
    private static SearchYoutube finder = new SearchYoutube();


    /* Classes with same interface put in hashmap for ease of finding command*/
    private static final HashMap<String, Command> commands = new HashMap<String, Command>() {
        {
            put("newuser", new NewUser(userService));
            put("login", new Login(userService));
            put("logout", new Logout());
            put("search", new Search(rec, con,finder));
            put("current", new Current());
            put("play", new Play());
            put("favs", new Favs());
            put("playfav", new PlayFav());
            put("fav", new Fav());
            put("unfav", new Unfav());
        }
    };
    
    public static Command getCommand(String name) {
        return commands.get(name);
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        while (!quit) {
            System.out.print("app> ");
            String commandLine = scanner.nextLine();
            /*Quit*/
            if (commandLine.equals("quit")){
                quit = true;
                System.out.println("======================================================\n" +
                "Thank you for using this app.\n" +
                "See you again!");
                continue;
            }
            /*Shows the logged in status*/
            if (commandLine.equals("show")){
                if (loggedInUser != null && status != -1){
                    System.out.println("user : " + loggedInUser);
                    System.out.println("ID : " + status);
                }else{
                    System.out.println("Not Logged In");
                }
                continue;
            }
            Command command = SpeechStream.getCommand(commandLine.split("\\s")[0]);
            if (null == command) {
                System.out.println("Unknown command");
            } else {
                /*Apply according to the hashmap values selected*/
                command.apply(new Scanner(commandLine));
            }
        }
    }
}
