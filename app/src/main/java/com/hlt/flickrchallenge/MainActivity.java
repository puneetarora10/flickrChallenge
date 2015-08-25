package com.hlt.flickrchallenge;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    // images
    public static ArrayList<Image> images = new ArrayList<>();
    // gridView
    private static GridView gridView;

    private static File filesDir;

    // dbHelper instance
    private static DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set filesDir
        filesDir = getFilesDir();
        // create dbHelper
        dbHelper = new DBHelper(getApplicationContext());

        // download images' meta data
        downloadImagesMetaData();

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(this, filesDir));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // create and show toast
                Toast toast = Toast.makeText(getApplicationContext(), R.string.showingDetails, Toast.LENGTH_SHORT);
                toast.show();
                // find image for row selected
                final Image image = images.get(position);
                Intent i = new Intent(MainActivity.this, ShowImageDetailsActivity.class);
                i.putExtra("image", image);
                startActivity(i);
            }
        });
    }

    /**
     * starts DownloadImagesService to download images' meta data if Image table had no records
     */
    private void downloadImagesMetaData() {
        // check if images already exist in the database
        // get all attachments using attachment table
        images = dbHelper.returnAllImages();
        if (images.size() == 0) {// start DownloadImagesService as there are no records in Image table
            Intent intent = new Intent(MainActivity.this, DownloadImagesService.class);
            Messenger messenger = new Messenger(handler);
            intent.putExtra("messenger", messenger);
            intent.putExtra("downloadImagesMetaData", true);
            startService(intent);
        }
    }

    /**
     * @return images
     */
    public ArrayList<Image> getImages() {
        return images;
    }

    /**
     * starts DownloadImagesService to download image
     *
     * @param position in gridView
     */
    public void downloadImage(int position) {
        Image image = images.get(position);
        Intent intent = new Intent(MainActivity.this, DownloadImagesService.class);
        Messenger messenger = new Messenger(downloadHandler);
        intent.putExtra("messenger", messenger);
        intent.putExtra("downloadImage", true);
        intent.putExtra("imageToDownload", image);
        intent.putExtra("position", position);
        startService(intent);
    }

    // update gridView
    // persist data
    public static Handler downloadHandler = new Handler() {
        public void handleMessage(Message message) {
            Image downloadedImage = message.getData().getParcelable("downloadedImage");
            int position = message.getData().getInt("position");
            if (downloadedImage != null) {// update image in gridView
                images.set(position, downloadedImage);

                if (checkIfViewIsVisibleInGridView(position)) {// view at positions is still visible
                    try {
                        // get imageView
                        ImageView imageView = (ImageView) gridView.getChildAt(position - gridView.getFirstVisiblePosition());
                        if (imageView != null) {
                            // set bitmap
                            imageView.setImageBitmap(BitmapFactory.decodeFile(filesDir + "/" + downloadedImage.getLocalName()));
                        }
                    } catch (Exception e) {
                        // no need to do anything right now
                    }
                    // persist data
                    persistData(true);
                }
            }
        }
    };

    public static ArrayList<String> imagesIds = new ArrayList<>();
    public static Handler handler = new Handler() {
        public void handleMessage(Message message) {
            images = message.getData().getParcelableArrayList("images");
            // persist data
            persistData(false);
            for (int i = 0; i < images.size(); i++) {
                imagesIds.add(images.get(i).getId());
            }
            // notify imageAdapter
            ImageAdapter imageAdapter = (ImageAdapter) gridView.getAdapter();
            imageAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // login action clicked
        if (id == R.id.action_login) {
            loginActionClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * determine if view at index is visible
     *
     * @param index image's index
     * @return true if index is between firstVisibleRow and lastVisibleRow
     */
    private static Boolean checkIfViewIsVisibleInGridView(int index) {
        Boolean viewVisible = false;
        // get firstVisibleRow
        int firstVisibleRow = gridView.getFirstVisiblePosition();
        // get lastVisibleRow
        int lastVisibleRow = gridView.getLastVisiblePosition();

        // if index is between firstVisibleRow and lastVisibleRow
        // in other words attachment is still visible in gridView
        if (index >= firstVisibleRow && index <= lastVisibleRow) {// view is visible
            viewVisible = true;
        }
        return viewVisible;
    }

    static Boolean persistenceInProgress = false;
    static int persistDataCounter = 0;

    /**
     * persists data
     * drop and create image table
     *
     * @param checkCounter if false then just persist in other words persistDataCounter check would be ignored
     */
    private static void persistData(Boolean checkCounter) {
        persistDataCounter++;
        if (!persistenceInProgress) {
            if ((persistDataCounter % 8 == 0) || !checkCounter) {// start PersistData thread
                persistenceInProgress = true;
                Thread persistDataThread = new Thread(new PersistData(images, dbHelper));
                persistDataThread.start();
            }
        }
    }

    private static class PersistData implements Runnable {
        private ArrayList<Image> images;
        private DBHelper dbHelper;

        public PersistData(ArrayList<Image> images1, DBHelper dbHelper1) {
            images = images1;
            dbHelper = dbHelper1;
        }

        @Override
        public void run() {
            // drop image table
            dbHelper.dropAndCreateImageTable();
            for (Image image : images) {// loop through images
                // insert image record
                dbHelper.insertImageRecord(image);
            }
            persistenceInProgress = false;
        }
    }

    /**
     * TODO complete -> login using Flickr's OAuth
     * login action clicked (PRO flavor)
     */
    private final static String PLEASE_GO_PRO = "Please Go PRO to see Private Photo Stream...";
    private final static String COMING_SOON = "Coming Soon...";
    private void loginActionClicked() {
        String toastText = PLEASE_GO_PRO;
        if (BuildConfig.ALLOW_LOGIN) {
            toastText = COMING_SOON;
        }
        // create and show toast
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.show();
    }
}
