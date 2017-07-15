/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youtube;

/**
 *
 * @author DevSingh
 */
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import speechstream.SpeechStream;

/**
 * Created by Don on 7/8/2017 AD.
 */
public class SearchYoutube {
    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 15;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     * @param args command line args.
     */
    public void searchVideo(String s, String order, long nVideos) {
        // Read the developer key from the properties file.
        SpeechStream.currentVidList.clear();
        Properties properties = new Properties();
        String youtubeProp = "/" + PROPERTIES_FILENAME;
        try {
            InputStream in = SearchYoutube.class.getResourceAsStream(youtubeProp);
//            System.out.println(youtubeProp);
//            System.out.println(in);
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            // Prompt the user to enter a query term.
            String queryTerm = s;

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");
//            search.setOrder("rating"); //Allowed values: [date, rating, relevance, title, videocount, viewcount]
            if (order.equals("default")){
                search.setOrder("relevance");
            }else{
                search.setOrder(order);
            }
            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
//            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setFields("items(id/kind,id/videoId,id/channelId,snippet/title,snippet/channelTitle)");
            search.setMaxResults(nVideos);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                SpeechStream.prevSearch = s;
                prettyPrint(searchResultList.iterator(), queryTerm, apiKey);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query, String apiKey) throws IOException {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }
        int i = 0;
        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();
            

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                YouTube.Videos.List list = youtube.videos().list("statistics");
                String vId = rId.getVideoId();
                list.setId(vId);
                list.setKey(apiKey);
                Video v = list.execute().getItems().get(0);
                String vTitle = singleVideo.getSnippet().getTitle();
                BigInteger viewcount = v.getStatistics().getViewCount();
                String uploader = singleVideo.getSnippet().getChannelTitle();
                System.out.println("Play id #" +i);
                System.out.println("Video Id: " + vId);
                System.out.println("Uploader -> " + uploader);
                System.out.println(" Title: " + vTitle);            
                System.out.println("The view count is: "+ viewcount);
                SpeechStream.currentVidList.add(new VideoInfo(vId,vTitle,viewcount.toString(),uploader));
                System.out.println("\n-------------------------------------------------------------\n");
                i++;
            }
        }
    }
}

