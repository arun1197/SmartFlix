/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package command;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import java.util.List;
import java.util.Scanner;
import service.UserService;
import speechstream.SpeechStream;
import youtube.VideoInfo;
/**
 *
 * @author DevSingh
 */
public class Login implements Command{
    UserService userService;
    public Login(UserService userService){
        this.userService = userService;
    }

    /* 
        - Handle parsing and printing response
        - allows login if all conditions met
    */
    @Override
    public void apply(Scanner line) {
        /* if already logged in, cannot use command */
        if (SpeechStream.status != -1){
            System.out.println("Already logged in -> log out to enter as other user.");
            return;
        }
        String tokens = line.nextLine();
        String parts[] = tokens.split("\\s+");
        int userid;
        if (parts.length != 3){
            System.out.println("usage: login <username> <password>");
        }else{
            if (usernameCheck(parts[1]) && passwordCheck(parts[2])){
                userid = userService.login(parts[1], parts[2]);
                if (userid != -1){
                    SpeechStream.status = userid;
                    AmazonDynamoDB dynamoClient = AmazonDynamoDBClientBuilder.standard()
                        .withRegion(Regions.AP_SOUTHEAST_1)
                        .withCredentials(new ProfileCredentialsProvider())
                        .build();
                    SpeechStream.dynamoDB = new DynamoDB(dynamoClient);
                    SpeechStream.mapper = new DynamoDBMapper(dynamoClient);
                    add();
                    System.out.println("OK");
                }else{
                    System.out.println("Invalid username/password");
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
    
    public void add(){
        VideoInfo vid = new VideoInfo();
        vid.setUsername(SpeechStream.loggedInUser);
        DynamoDBQueryExpression<VideoInfo> queryExpression = new DynamoDBQueryExpression<VideoInfo>()
            .withHashKeyValues(vid);
        List<VideoInfo> itemList = speechstream.SpeechStream.mapper.query(VideoInfo.class, queryExpression);
        for (int j = 0; j < itemList.size(); j++) {
            SpeechStream.currentFavList.add(itemList.get(j));
        }      
    }
    
}
