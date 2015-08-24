package com.hlt.flickrchallenge;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * DownloadImageDetailsService
 * ---> Downloads Image's Owner's userName
 * ---> Downloads Image's comments
 */
public class DownloadImageDetailsService extends Service {
    private Messenger uploadedByMessenger;
    private Messenger commentsDownloadedMessenger;
    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // A queue of Runnables
    private final BlockingQueue<Runnable> mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();

    public DownloadImageDetailsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // get extras using intent
        uploadedByMessenger = intent.getParcelableExtra("uploadedByMessenger");
        commentsDownloadedMessenger = intent.getParcelableExtra("commentsDownloadedMessenger");
        Image image = intent.getParcelableExtra("image");
        Boolean downloadComments = intent.getBooleanExtra("downloadComments", false);

        // threadPoolExecutor
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);

        Thread downloadImagesOwnersDetailsThread = new Thread(new DownloadImagesOwnersDetailsThread(image));
        threadPoolExecutor.execute(downloadImagesOwnersDetailsThread);

        if (downloadComments) {
            Thread downloadImagesCommentsThread = new Thread(new DownloadImagesCommentsThread(image));
            threadPoolExecutor.execute(downloadImagesCommentsThread);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * DownloadImagesOwnersDetailsThread
     */
    private class DownloadImagesOwnersDetailsThread implements Runnable {
        private Image image;

        public DownloadImagesOwnersDetailsThread(Image image1) {
            image = image1;
        }

        @Override
        public void run() {
            Object dataReturnedFromWebService = WebAPI.getDefaultInstance().getOwnersUserName(image.getOwner());
            try {
                // get ownersUserName
                String ownersUserName = ((JSONObject) dataReturnedFromWebService).getJSONObject("person").getJSONObject("username").getString("_content");
                // return to Activity
                Bundle data = new Bundle();
                data.putString("ownersUserName", ownersUserName);
                Message message = Message.obtain();
                message.setData(data);
                uploadedByMessenger.send(message);
            } catch (Exception e) {
                // no need to do anything right now
            }
        }
    }

    /**
     * DownloadImagesCommentsThread
     */
    private class DownloadImagesCommentsThread implements Runnable {
        private Image image;

        public DownloadImagesCommentsThread(Image image1) {
            image = image1;
        }

        @Override
        public void run() {
            Object dataReturnedFromWebService = WebAPI.getDefaultInstance().getImagesComments(image.getId());

            ArrayList<Comment> comments = new ArrayList<>();
            // parse json
            try {
                // get commentsData
                JSONObject _commentsData = ((JSONObject) dataReturnedFromWebService).getJSONObject("comments");
                JSONArray commentsData = null;
                if (!_commentsData.optString("comment").isEmpty()) {
                    commentsData = _commentsData.getJSONArray("comment");
                }

                if (commentsData != null) {
                    // loop through commentsData and create Comment Objects
                    for (int i = 0; i < commentsData.length(); i++) {
                        Comment comment = new Comment();
                        JSONObject commentData = commentsData.getJSONObject(i);
                        // set properties
                        if (!commentData.optString("id").isEmpty()) {// set id if exists
                            comment.setId(commentData.getString("id"));
                        }
                        if (!commentData.optString("authorname").isEmpty()) {// set authorname if exists
                            comment.setAuthorName(commentData.getString("authorname"));
                        }
                        if (!commentData.optString("_content").isEmpty()) {// set _content if exists
                            comment.setContent(commentData.getString("_content"));
                        }
                        comment.setImageId(image.getId());
                        comments.add(comment);
                    }
                }
            } catch (Exception e) {
                // no need to do anything right now
            }

            try {
                // return to Activity
                Bundle data = new Bundle();
                data.putParcelableArrayList("comments", comments);
                Message message = Message.obtain();
                message.setData(data);
                commentsDownloadedMessenger.send(message);
            } catch (Exception e) {
                // no need to do anything right now
            }
        }
    }
}
