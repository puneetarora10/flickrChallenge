package com.hlt.flickrchallenge;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * To store image's comment
 * Created by parora on 8/23/15.
 */
public class Comment implements Parcelable {
    // todo store more fields like date
    // unique id
    String id;
    // to know which image this comment belongs to
    String imageId;
    // to store authorname
    String authorName;
    // to store _content
    String content;

    /**
     * getters
     */

    public String getId() {
        return id;
    }

    public String getImageId() {
        return imageId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    /**
     * setters
     */
    public void setId(String id) {
        this.id = id;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * constructors
     */
    public Comment() {

    }

    // parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // only writing necessary properties to the parcel
    @Override
    public void writeToParcel(Parcel pc, int flags) {
        pc.writeString(id);
        pc.writeString(imageId);
        pc.writeString(authorName);
        pc.writeString(content);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    // only writing necessary properties from the parcel
    public Comment(Parcel in) {
        id = in.readString();
        imageId = in.readString();
        authorName = in.readString();
        content = in.readString();
    }
}