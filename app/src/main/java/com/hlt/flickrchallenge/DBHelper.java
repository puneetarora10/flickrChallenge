package com.hlt.flickrchallenge;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * TODO Use ContentProvider
 * DBHelper class
 * does all the interaction with SQLite Database
 */
public class DBHelper extends SQLiteOpenHelper {

    // globals
    public static final String DATABASE_NAME = "PlayWithFlickr.db";
    // IMAGE table
    public static final String IMAGE_TABLE_NAME = "image";
    public static final String ID_COLUMN_NAME = "id";
    public static final String OWNER_COLUMN_NAME = "owner";
    public static final String OWNERS_USER_NAME_COLUMN_NAME = "ownersUserName";
    public static final String SECRET_COLUMN_NAME = "secret";
    public static final String SERVER_COLUMN_NAME = "server";
    public static final String FARM_COLUMN_NAME = "farm";
    public static final String URL_STRING_FOR_PHONE_COLUMN_NAME = "urlStringForPhone";
    public static final String URL_STRING_FOR_TABLET_COLUMN_NAME = "urlStringForTablet";
    public static final String LOCAL_NAME_COLUMN_NAME = "localName";
    public static final String DOWNLOAD_COMPLETED_COLUMN_NAME = "downloadCompleted";
    public static final String DOWNLOAD_STARTED_COLUMN_NAME = "downloadStarted";

    // COMMENT table
    public static final String COMMENT_TABLE_NAME = "comment";
    public static final String IMAGE_ID_COLUMN_NAME = "imageId";
    public static final String AUTHOR_NAME_COLUMN_NAME = "authorName";
    public static final String CONTENT_COLUMN_NAME = "content";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table image
        db.execSQL(
                "create table IF NOT EXISTS image " +
                        "(id text primary key, owner text, ownersUserName text, secret text, " +
                        "server text, farm text, urlStringForPhone text, " +
                        "urlStringForTablet text, localName text, downloadCompleted integer, " +
                        "downloadStarted integer);"
        );

        // create table comment
        db.execSQL(
                "create table IF NOT EXISTS comment " +
                        "(id text primary key, imageId text, authorName text, content text);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop table image
        db.execSQL("DROP TABLE IF EXISTS image");
        // drop table comment
        db.execSQL("DROP TABLE IF EXISTS comment");
        onCreate(db);
    }

    /**
     * drops table attachment and then creates it again
     * faster way to drop all attachment records...
     */
    public void dropAndCreateImageAndCommentTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        // drop table image
        db.execSQL("DROP TABLE IF EXISTS image");
        // drop table comment
        db.execSQL("DROP TABLE IF EXISTS comment");

        onCreate(db);
    }

    public void dropAndCreateImageTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        // drop table image
        db.execSQL("DROP TABLE IF EXISTS image");
        onCreate(db);
    }

    /**
     * inserts image
     *
     * @param id
     * @param owner
     * @param ownersUserName
     * @param secret
     * @param server
     * @param farm
     * @param urlStringForPhone
     * @param urlStringForTablet
     * @param localName
     * @param downloadCompleted
     * @param downloadStarted
     * @return true if image is inserted
     */
    public boolean insertImageRecord(String id, String owner, String ownersUserName, String secret, String server, String farm, String urlStringForPhone, String urlStringForTablet, String localName, Boolean downloadCompleted, Boolean downloadStarted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues imageValues = new ContentValues();
        imageValues.put(ID_COLUMN_NAME, id);
        imageValues.put(OWNER_COLUMN_NAME, owner);
        imageValues.put(OWNERS_USER_NAME_COLUMN_NAME, ownersUserName);
        imageValues.put(SECRET_COLUMN_NAME, secret);
        imageValues.put(SERVER_COLUMN_NAME, server);
        imageValues.put(FARM_COLUMN_NAME, farm);
        imageValues.put(URL_STRING_FOR_PHONE_COLUMN_NAME, urlStringForPhone);
        imageValues.put(URL_STRING_FOR_TABLET_COLUMN_NAME, urlStringForTablet);
        imageValues.put(LOCAL_NAME_COLUMN_NAME, localName);
        imageValues.put(DOWNLOAD_COMPLETED_COLUMN_NAME, downloadCompleted ? 1 : 0);
        imageValues.put(DOWNLOAD_STARTED_COLUMN_NAME, downloadStarted ? 1 : 0);
        // insert in to db
        if (db.insert(IMAGE_TABLE_NAME, null, imageValues) == -1) {
            return false;
        }
        return true;
    }

    /**
     * inserts image record
     *
     * @param image
     * @return true if image record is inserted
     */
    public boolean insertImageRecord(Image image) {
        try {
            // get db
            SQLiteDatabase db = this.getWritableDatabase();
            // get contentValues
            ContentValues imageValues = new ContentValues();
            // put in to imageValues
            imageValues.put(ID_COLUMN_NAME, image.getId());
            imageValues.put(OWNER_COLUMN_NAME, image.getOwner());
            imageValues.put(OWNERS_USER_NAME_COLUMN_NAME, image.getOwnersUserName());
            imageValues.put(SECRET_COLUMN_NAME, image.getSecret());
            imageValues.put(SERVER_COLUMN_NAME, image.getServer());
            imageValues.put(FARM_COLUMN_NAME, image.getFarm());
            imageValues.put(URL_STRING_FOR_PHONE_COLUMN_NAME, image.getUrlStringForPhone());
            imageValues.put(URL_STRING_FOR_TABLET_COLUMN_NAME, image.getUrlStringForTablet());
            imageValues.put(LOCAL_NAME_COLUMN_NAME, image.getLocalName());
            imageValues.put(DOWNLOAD_COMPLETED_COLUMN_NAME, image.getDownloadCompleted() ? 1 : 0);
            imageValues.put(DOWNLOAD_STARTED_COLUMN_NAME, image.getDownloadStarted() ? 1 : 0);

            // insert in to db
            if (db.insert(IMAGE_TABLE_NAME, null, imageValues) == -1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // no need to do anything right now
        }
        return false;
    }

    /**
     * @param id image's id
     * @return image record with id
     */
    public Cursor returnImageRecord(int id) {
        // get db
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from image where id=" + id + "", null);
        return res;
    }

    /**
     * deletes all image records
     */
    public void deleteAllImageRecords() {
        // get db
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + IMAGE_TABLE_NAME);
        db.close();
    }

    /**
     * finds all image records, creates Image and appends it to images
     *
     * @return all images
     */
    public ArrayList<Image> returnAllImages() {
        ArrayList<Image> images = new ArrayList<Image>();

        // get db
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from image", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {// loop and append to images
            // create new image
            Image image = new Image();
            image.setId(res.getString(res.getColumnIndex(ID_COLUMN_NAME)));
            image.setOwner(res.getString(res.getColumnIndex(OWNER_COLUMN_NAME)));
            image.setOwnersUserName(res.getString(res.getColumnIndex(OWNERS_USER_NAME_COLUMN_NAME)));
            image.setSecret(res.getString(res.getColumnIndex(SECRET_COLUMN_NAME)));
            image.setServer(res.getString(res.getColumnIndex(SERVER_COLUMN_NAME)));
            image.setFarm(res.getString(res.getColumnIndex(FARM_COLUMN_NAME)));
            image.setUrlStringForPhone(res.getString(res.getColumnIndex(URL_STRING_FOR_PHONE_COLUMN_NAME)));
            image.setUrlStringForTablet(res.getString(res.getColumnIndex(URL_STRING_FOR_TABLET_COLUMN_NAME)));
            image.setLocalName(res.getString(res.getColumnIndex(LOCAL_NAME_COLUMN_NAME)));
            image.setDownloadCompleted(res.getInt(res.getColumnIndex(DOWNLOAD_COMPLETED_COLUMN_NAME)) == 1);
            image.setDownloadStarted(res.getInt(res.getColumnIndex(DOWNLOAD_STARTED_COLUMN_NAME)) == 1);
            // add image to images
            images.add(image);
            res.moveToNext();
        }

        return images;
    }

    /**
     * finds all image records, creates Image and appends it to images
     *
     * @return all images
     */
    public Image returnImage(String imageId) {

        Image image;
        // get db
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from image where id ='" + imageId + "'", null);
        res.moveToFirst();
        // create new image
        image = new Image();
        image.setId(res.getString(res.getColumnIndex(ID_COLUMN_NAME)));
        image.setOwner(res.getString(res.getColumnIndex(OWNER_COLUMN_NAME)));
        image.setOwnersUserName(res.getString(res.getColumnIndex(OWNERS_USER_NAME_COLUMN_NAME)));
        image.setSecret(res.getString(res.getColumnIndex(SECRET_COLUMN_NAME)));
        image.setServer(res.getString(res.getColumnIndex(SERVER_COLUMN_NAME)));
        image.setFarm(res.getString(res.getColumnIndex(FARM_COLUMN_NAME)));
        image.setUrlStringForPhone(res.getString(res.getColumnIndex(URL_STRING_FOR_PHONE_COLUMN_NAME)));
        image.setUrlStringForTablet(res.getString(res.getColumnIndex(URL_STRING_FOR_TABLET_COLUMN_NAME)));
        image.setLocalName(res.getString(res.getColumnIndex(LOCAL_NAME_COLUMN_NAME)));
        image.setDownloadCompleted(res.getInt(res.getColumnIndex(DOWNLOAD_COMPLETED_COLUMN_NAME)) == 1);
        image.setDownloadStarted(res.getInt(res.getColumnIndex(DOWNLOAD_STARTED_COLUMN_NAME)) == 1);

        res.close();

        return image;
    }

    /**
     * updates image
     *
     * @param image to be updated
     * @return true if image record updated
     */
    public Boolean updateImage(Image image) {
        try {
            String whereClause = "id=" + image.getId();
            // get db
            SQLiteDatabase db = this.getWritableDatabase();
            // get contentValues
            ContentValues imageValues = new ContentValues();
            // put in to imageValues
            imageValues.put(OWNER_COLUMN_NAME, image.getOwner());
            imageValues.put(OWNERS_USER_NAME_COLUMN_NAME, image.getOwnersUserName());
            imageValues.put(SECRET_COLUMN_NAME, image.getSecret());
            imageValues.put(SERVER_COLUMN_NAME, image.getServer());
            imageValues.put(FARM_COLUMN_NAME, image.getFarm());
            imageValues.put(URL_STRING_FOR_PHONE_COLUMN_NAME, image.getUrlStringForPhone());
            imageValues.put(URL_STRING_FOR_TABLET_COLUMN_NAME, image.getUrlStringForTablet());
            imageValues.put(LOCAL_NAME_COLUMN_NAME, image.getLocalName());
            imageValues.put(DOWNLOAD_COMPLETED_COLUMN_NAME, image.getDownloadCompleted() ? 1 : 0);
            imageValues.put(DOWNLOAD_STARTED_COLUMN_NAME, image.getDownloadStarted() ? 1 : 0);

            // insert in to db
            if (db.update(IMAGE_TABLE_NAME, imageValues, whereClause, null) == -1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // no need to do anything right now
        }
        return false;
    }

    /**
     * inserts comment record
     *
     * @param comment
     * @return true if comment record is inserted
     */
    public boolean insertCommentRecord(Comment comment) {
        try {
            // get db
            SQLiteDatabase db = this.getWritableDatabase();
            // get contentValues
            ContentValues commentValues = new ContentValues();
            // put in to commentValues
            commentValues.put(ID_COLUMN_NAME, comment.getId());
            commentValues.put(IMAGE_ID_COLUMN_NAME, comment.getImageId());
            commentValues.put(AUTHOR_NAME_COLUMN_NAME, comment.getAuthorName());
            commentValues.put(CONTENT_COLUMN_NAME, comment.getContent());

            // insert in to db
            if (db.insert(COMMENT_TABLE_NAME, null, commentValues) == -1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // no need to do anything right now
        }
        return false;
    }

    /**
     * deletes all comment records
     */
    public void deleteAllCommentRecords() {
        // get db
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + COMMENT_TABLE_NAME);
        db.close();
    }

    /**
     * finds all comment records for imageId, creates Comment and appends it to comments
     * @param imageId image's id for which comments have to be returned
     * @return all comments
     */
    public ArrayList<Comment> returnAllComments(String imageId) {
        ArrayList<Comment> comments = new ArrayList<>();

        // get db
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from "+COMMENT_TABLE_NAME+" where imageId='"+imageId+"'", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {// loop and append to comments
            // create new comment
            Comment comment = new Comment();
            comment.setId(res.getString(res.getColumnIndex(ID_COLUMN_NAME)));
            comment.setImageId(res.getString(res.getColumnIndex(IMAGE_ID_COLUMN_NAME)));
            comment.setAuthorName(res.getString(res.getColumnIndex(AUTHOR_NAME_COLUMN_NAME)));
            comment.setContent(res.getString(res.getColumnIndex(CONTENT_COLUMN_NAME)));

            // add comment to comments
            comments.add(comment);
            res.moveToNext();
        }
        res.close();

        return comments;
    }
}
