package com.hlt.flickrchallenge;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadImagesService extends Service {
    private Messenger mainActivityMessenger;
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

    // list of images
    ArrayList<Image> images = new ArrayList<>();

    public DownloadImagesService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // get extras using intent
        mainActivityMessenger = intent.getParcelableExtra("messenger");
        Boolean downloadImage = intent.getBooleanExtra("downloadImage", false);
        Boolean downloadImagesMetaData = intent.getBooleanExtra("downloadImagesMetaData", false);
        // threadPoolExecutor
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);
        if (downloadImagesMetaData) {//downloadImagesMetaData
            Thread downloadImagesMetaDataThread = new Thread(new DownloadImagesMetaDataThread());
            threadPoolExecutor.execute(downloadImagesMetaDataThread);
        }

        if (downloadImage) {
            Image imageToDownload = intent.getParcelableExtra("imageToDownload");
            int imagesPosition = intent.getIntExtra("position", 0);
            Runnable downloadImageThread = new DownloadImage(imageToDownload, imagesPosition);
            threadPoolExecutor.execute(downloadImageThread);
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
     * Downloads images' metaData
     */
    private class DownloadImagesMetaDataThread implements Runnable {
        @Override
        public void run() {
            Object dataReturnedFromWebService = WebAPI.getDefaultInstance().getPhotosData();
            // parse json
            try {
                // get allImagesData
                JSONArray allImagesData = ((JSONObject) dataReturnedFromWebService).getJSONObject("photos").getJSONArray("photo");
                // loop through allImagesData and create Image Objects
                for (int i = 0; i < allImagesData.length(); i++) {
                    Image image = new Image();
                    JSONObject imageData = allImagesData.getJSONObject(i);
                    // set properties
                    if (!imageData.optString("id").isEmpty()) {// set id if exists
                        image.setId(imageData.getString("id"));
                    }
                    if (!imageData.optString("owner").isEmpty()) {// set owner if exists
                        image.setOwner(imageData.getString("owner"));
                    }
                    if (!imageData.optString("secret").isEmpty()) {// set secret if exists
                        image.setSecret(imageData.getString("secret"));
                    }
                    if (!imageData.optString("server").isEmpty()) {// set secret if exists
                        image.setServer(imageData.getString("server"));
                    }
                    if (!imageData.optString("farm").isEmpty()) {// set owner if exists
                        image.setFarm(imageData.getString("farm"));
                    }
                    image.setUrlStringForPhone(HelperAPI.getDefaultInstance().returnImagesURLString(image.getFarm(), image.getServer(), image.getId(), image.getSecret(), false));
                    image.setUrlStringForTablet(HelperAPI.getDefaultInstance().returnImagesURLString(image.getFarm(), image.getServer(), image.getId(), image.getSecret(), true));
                    // set downloadCompleted and downloadStarted
                    image.setDownloadCompleted(false);
                    image.setDownloadStarted(false);

                    images.add(image);
                }
            } catch (Exception e) {
                // no need to do anything right now
            }

            // create image objects
            // send images to MainActivity so that MainActivity can start downloading images
            try {
                Bundle data = new Bundle();
                data.putParcelableArrayList("images", images);
                Message message = Message.obtain();
                message.setData(data);
                mainActivityMessenger.send(message);
            } catch (Exception e) {
                // no need to do anything right now
            }
        }
    }

    /**
     * Downloads image
     * sets localPath
     */
    private class DownloadImage implements Runnable {
        private Image image;
        private int position;

        public DownloadImage(Image image1, int position1) {
            image = image1;
            position = position1;
        }

        @Override
        public void run() {
            if (!image.getDownloadStarted() && !image.getDownloadCompleted()) {
                // update downloadStarted
                image.setDownloadStarted(true);
                // TODO check if tablet or phone
                HttpURLConnection httpURLConnection = null;
                try {
                    URL imageUrl = new URL(image.getUrlStringForPhone());
                    // open connection
                    httpURLConnection = (HttpURLConnection) imageUrl.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setInstanceFollowRedirects(true);

                    // handle redirects
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER) {// redirect from http -> https or https -> http
                        // open connection
                        httpURLConnection = (HttpURLConnection) httpURLConnection.getURL().openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.setInstanceFollowRedirects(true);
                    }
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String suggestedFileName = HelperAPI.getDefaultInstance().returnFileNameFromUrl(httpURLConnection.getURL());
                        image.setLocalName(suggestedFileName);

                        // openFileOutput using MODE_APPEND
                        FileOutputStream fOut = new FileOutputStream(new File(getFilesDir(), suggestedFileName));
                        Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();

                        // image downloaded
                        image.setDownloadCompleted(true);
                        // send message to downloadHandler
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("downloadedImage", image);
                        bundle.putInt("position", position);
                        Message message = Message.obtain();
                        message.setData(bundle);
                        mainActivityMessenger.send(message);
                    }
                } catch (Exception e) {
                    // no need to do anything right now
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }

        }
    }
}
