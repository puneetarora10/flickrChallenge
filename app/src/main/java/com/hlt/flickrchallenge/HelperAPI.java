package com.hlt.flickrchallenge;

import java.net.URL;
import java.util.LinkedHashMap;

/**
 * Helper Methods
 * Created by parora on 8/24/15.
 */
public class HelperAPI {
    // global variables
    private final static String GENERIC_ERROR_MESSAGE = "Sorry, Slow Internet Connection on your device!!";

    // returns a default instance of HelperService to implement singleton
    // in other words only one instance of HelperService object exists in the application
    private static HelperAPI helperAPI;

    public static HelperAPI getDefaultInstance() {
        if (helperAPI == null) {
            helperAPI = new HelperAPI();
        }

        return helperAPI;
    }

    // returns GENERIC_ERROR_MESSAGE
    public String returnGenericErrorMessage() {
        return GENERIC_ERROR_MESSAGE;
    }

    /**
     * appends generic errorMessage
     *
     * @param appendGenericErrorMessageToIt hashMap to be which generic errorMessage will be appended
     */
    public void appendGenericErrorMessage(LinkedHashMap<String, String> appendGenericErrorMessageToIt) {
        appendGenericErrorMessageToIt.put("errorMessage", GENERIC_ERROR_MESSAGE);
    }


    // 240 * 240
    private final static String IMAGE_SIZE_FOR_PHONE = "q";
    // 640 * 640
    private final static String IMAGE_SIZE_FOR_TABLET = "z";

    /**
     * create Image's URL using https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
     * @param farm -> image's farm
     * @param server -> image's server
     * @param id -> image's id
     * @param secret -> image's secret
     * @param forTablet -> image's tablet
     * @return urlString
     */
    public String returnImagesURLString(String farm, String server, String id, String secret, Boolean forTablet) {
        String size = IMAGE_SIZE_FOR_PHONE;
        if (forTablet) {
            size = IMAGE_SIZE_FOR_TABLET;
        }
        return "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+"_"+size+".jpg";
    }

    /**
     *
     * @param url an absolute url given to this method
     * @return suggested fileName
     */
    public String returnFileNameFromUrl(URL url) {
        // get the file of this url
        String urlString = url.getFile();
        return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
    }
}