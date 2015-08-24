package com.hlt.flickrchallenge;


import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;


/**
 * Handles all interactions with the server
 * Created by parora on 8/24/15.
 */
public class WebAPI {

    private final static String FLICKR_GET_RECENT_PHOTOS_METHOD_NAME = "flickr.photos.getRecent";
    private final static String FLICKR_GET_PEOPLE_INFO_METHOD_NAME = "flickr.people.getInfo";
    private final static String FLICKR_GET_PHOTOS_COMMENTS = "flickr.photos.comments.getList";
    private final static String FLICKR_SERVER_URL_STRING = "https://api.flickr.com/services/rest/?api_key=672652f439d3580bcb476260d3638680&per_page=500&page=1&format=json&nojsoncallback=1";
    // returns a default instance of WebService to implement singleton
    // in other words only one instance of WebService object exists in the application
    private static WebAPI webAPI;

    public static WebAPI getDefaultInstance() {
        if (webAPI == null) {
            webAPI = new WebAPI();
        }

        return webAPI;
    }

    /**
     * make a web service call to get photos' data
     * if data is received from server then return that data
     * else errorMessage
     * @return JSONArray if photosData is returned from the server
     */
    public Object getPhotosData() {
        Object dataToBeReturned = null;
        HttpURLConnection httpURLConnection = null;
        try {
            String urlString = FLICKR_SERVER_URL_STRING + "&method="+FLICKR_GET_RECENT_PHOTOS_METHOD_NAME;
            // create URL
            URL url = new URL(urlString);
            // open connection
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    dataToBeReturned = dataReturnedFromServer;
                } else {// photos' data couldn't be downloaded // append generic errorMessage
                    dataToBeReturned = new LinkedHashMap<String, String>();
                    HelperAPI.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            dataToBeReturned = new LinkedHashMap<String, String>();
            HelperAPI.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return dataToBeReturned;
    }

    /**
     * make a web service call to get owner's userName
     * if data is received from server then return that data
     * else errorMessage
     * @param userId -> owner
     * @return JSONArray if photosData is returned from the server
     */
    public Object getOwnersUserName(String userId) {
        Object dataToBeReturned = null;
        HttpURLConnection httpURLConnection = null;
        try {
            String urlString = FLICKR_SERVER_URL_STRING + "&method="+FLICKR_GET_PEOPLE_INFO_METHOD_NAME + "&user_id="+userId;
            // create URL
            URL url = new URL(urlString);
            // open connection
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    dataToBeReturned = dataReturnedFromServer;
                } else {// data couldn't be downloaded // append generic errorMessage
                    dataToBeReturned = new LinkedHashMap<String, String>();
                    HelperAPI.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            dataToBeReturned = new LinkedHashMap<String, String>();
            HelperAPI.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return dataToBeReturned;
    }

    /**
     * make a web service call to get image's comments
     * if data is received from server then return that data
     * else errorMessage
     * @param imageId -> image's id
     * @return JSONArray if photosData is returned from the server
     */
    public Object getImagesComments(String imageId) {
        Object dataToBeReturned = null;
        HttpURLConnection httpURLConnection = null;
        try {
            String urlString = FLICKR_SERVER_URL_STRING + "&method="+FLICKR_GET_PHOTOS_COMMENTS + "&photo_id="+imageId;
            // create URL
            URL url = new URL(urlString);
            // open connection
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    dataToBeReturned = dataReturnedFromServer;
                } else {// data couldn't be downloaded // append generic errorMessage
                    dataToBeReturned = new LinkedHashMap<String, String>();
                    HelperAPI.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            dataToBeReturned = new LinkedHashMap<String, String>();
            HelperAPI.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return dataToBeReturned;
    }
}
