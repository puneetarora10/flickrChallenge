package com.hlt.flickrchallenge;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowImageDetailsActivity extends AppCompatActivity {

    // show details for this image
    private static Image image;
    // DBHelper's instance
    private static DBHelper dbHelper;
    // uploadedByTextView
    private static TextView uploadedByTextView;
    // uploadedBy placeholder
    private final static String uploadedBy = "Uploaded By";
    private final static String commentsNA = " - Comments not available..";
    // comments
    private static ArrayList<Comment> comments;

    // commentsListView
    private static ListView commentsListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_details);

        // get image
        Intent i = getIntent();
        image = (Image) i.getParcelableExtra("image");
        // initialize dbHelper
        dbHelper = new DBHelper(getApplicationContext());
        // initialize uploadedByTextView
        uploadedByTextView = (TextView) findViewById(R.id.uploadedBy);
        // initialize comments
        comments = dbHelper.returnAllComments(image.getId());

        // get commentsList
        commentsListView = (ListView) findViewById(R.id.commentsListView);
        commentsListView.setAdapter(new CommentAdapter(ShowImageDetailsActivity.this));

        // check if ownersUserName exists in database for this image
        if (image.getOwnersUserName() == null) {
            Intent intent = new Intent(ShowImageDetailsActivity.this, DownloadImageDetailsService.class);
            Messenger uploadedByMessenger = new Messenger(uploadedByHandler);
            intent.putExtra("uploadedByMessenger", uploadedByMessenger);
            Messenger commentsDownloadedMessenger = new Messenger(commentsDownloadedHandler);
            intent.putExtra("commentsDownloadedMessenger", commentsDownloadedMessenger);
            intent.putExtra("image", image);
            if (comments.size() == 0) {// comments should be downloaded
                intent.putExtra("downloadComments",true);
            }
            startService(intent);
        } else {
            updateUploadedByTextView(image.getOwnersUserName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_image_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // uploadedByHandler
    public static Handler uploadedByHandler = new Handler() {
        public void handleMessage(Message message) {
            String ownersUserName = message.getData().getString("ownersUserName");
            // update uploadedByTextView
            updateUploadedByTextView(ownersUserName);
            // update image
            image.setOwnersUserName(ownersUserName);
            // persist image
            dbHelper.updateImage(image);
        }
    };

    // commentsDownloadedHandler
    public static Handler commentsDownloadedHandler = new Handler() {
        public void handleMessage(Message message) {
            comments = message.getData().getParcelableArrayList("comments");
            // notify commentAdapter
            if (comments.size() > 0) {
                CommentAdapter commentAdapter = (CommentAdapter) commentsListView.getAdapter();
                commentAdapter.notifyDataSetChanged();
                persistComments();
            }
            // updateUploadedByTextView
            updateUploadedByTextView(image.getOwnersUserName());
        }
    };

    /**
     * updates uploadedByTextView
     *
     * @param ownersUserName image's owner's userName
     */
    private static void updateUploadedByTextView(String ownersUserName) {
        String uploadedByTextViewsText = uploadedBy + " " + ownersUserName;
        if (comments.size() == 0) {
            uploadedByTextViewsText += commentsNA;
        }
        // update uploadedByTextView
        uploadedByTextView.setText(uploadedByTextViewsText);
    }

    /**
     * @return comments
     */
    public ArrayList<Comment> getComments() {
        return comments;
    }

    /**
     * persist comments
     */
    private static void persistComments() {
        Thread persistCommentsThread = new Thread(new PersistCommentsThread(comments, dbHelper));
        persistCommentsThread.start();
    }

    /**
     * persists comments
     */
    private static class PersistCommentsThread implements Runnable {
        private ArrayList<Comment> comments;
        private DBHelper dbHelper;

        public PersistCommentsThread(ArrayList<Comment> comments1, DBHelper dbHelper1) {
            comments = comments1;
            dbHelper = dbHelper1;
        }

        @Override
        public void run() {
            for (Comment comment : comments) {// loop through comments
                // insert comment record
                dbHelper.insertCommentRecord(comment);
            }
        }
    }
}
