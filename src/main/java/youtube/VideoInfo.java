/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youtube;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.math.BigInteger;

/**
 *
 * @author DevSingh
 */
@DynamoDBTable(tableName = "favoriteVideos")
public class VideoInfo {
    
    private String id;
    private String title;
    private String views;
    private String uploader;
    private String username;
    public VideoInfo(){
        
    }
    
    public VideoInfo(String id, String title, String views, String uploader){
        this.id = id;
        this.title = title;
        this.uploader = uploader;
        this.views = views;
    }
    
    
    @DynamoDBHashKey(attributeName = "username")
    public String getUsername(){
        return username;
    }
    
    public void setUsername(String username) {
            this.username = username;
    }
    
    @DynamoDBRangeKey(attributeName = "video_id")
    public String getID(){
        return id;
    }
    
    public void setID(String id) {
            this.id = id;
    }
    
    @DynamoDBAttribute(attributeName = "title")
    public String getTitle(){
        return title;
    }
    
    public void setTitle(String title) {
            this.title = title;
    }
    
    @DynamoDBAttribute(attributeName = "uploader")
    public String getUploader(){
        return uploader;
    }
    
    public void setUploader(String uploader) {
            this.uploader = uploader;
    }
    
    @DynamoDBAttribute(attributeName = "views")
    public String getViews(){
        return views;
    }
    
    public void setViews(String views) {
            this.views = views;
    }
    
    
}
