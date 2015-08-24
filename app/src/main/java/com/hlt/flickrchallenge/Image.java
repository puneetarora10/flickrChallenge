package com.hlt.flickrchallenge;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Image object
 * for storing Image's metadata (will be persisted in to SQLite)
 * Created by parora on 8/24/15.
 */
public class Image implements Parcelable {
    // to store id
    private String id;
    // to store owner
    private String owner;
    // to store owner's userName
    private String ownersUserName;
    // to store secret
    private String secret;
    // to store server
    private String server;
    // to store farm
    private String farm;
    // to store url
    // storing it as a String so that it can be persisted to SQLite seamlessly
    private String urlStringForPhone;
    // to store url
    // storing it as a String so that it can be persisted to SQLite seamlessly
    // bigger size
    private String urlStringForTablet;
    // localName of this image (cached)
    private String localName;
    // true if download is completed
    private Boolean downloadCompleted;
    // true if download has started
    private Boolean downloadStarted;


    /**
     * getters
     */
    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwnersUserName() {
        return ownersUserName;
    }

    public String getSecret() {
        return secret;
    }

    public String getServer() {
        return server;
    }

    public String getFarm() {
        return farm;
    }

    public String getUrlStringForPhone() {
        return urlStringForPhone;
    }

    public String getUrlStringForTablet() {
        return urlStringForTablet;
    }

    public String getLocalName() {
        return localName;
    }

    public Boolean getDownloadCompleted() {
        return downloadCompleted;
    }

    public Boolean getDownloadStarted() {
        return downloadStarted;
    }

    /**
     * setters
     */
    public void setId(String id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOwnersUserName(String ownersUserName) {
        this.ownersUserName = ownersUserName;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public void setUrlStringForPhone(String urlStringForPhone) {
        this.urlStringForPhone = urlStringForPhone;
    }

    public void setUrlStringForTablet(String urlStringForTablet) {
        this.urlStringForTablet = urlStringForTablet;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public void setDownloadCompleted(Boolean downloadCompleted) {
        this.downloadCompleted = downloadCompleted;
    }

    public void setDownloadStarted(Boolean downloadStarted) {
        this.downloadStarted = downloadStarted;
    }

    /**
     * constructors
     */
    public Image() {

    }

    public Image(String _id, String _owner, String _ownersUserName, String _secret, String _server, String _farm, String _urlStringForPhone, String _urlStringForTablet, String _localName, Boolean _downloadCompleted, Boolean _downloadStarted) {
        id = _id;
        owner = _owner;
        ownersUserName = _ownersUserName;
        secret = _secret;
        server = _server;
        farm = _farm;
        urlStringForPhone = _urlStringForPhone;
        urlStringForTablet = _urlStringForTablet;
        localName = _localName;
        downloadCompleted = _downloadCompleted;
        downloadStarted = _downloadStarted;
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
        pc.writeString(owner);
        pc.writeString(ownersUserName);
        pc.writeString(secret);
        pc.writeString(server);
        pc.writeString(farm);
        pc.writeString(urlStringForPhone);
        pc.writeString(urlStringForTablet);
        pc.writeString(localName);
        pc.writeValue(downloadCompleted);
        pc.writeValue(downloadStarted);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    // only writing necessary properties from the parcel
    private Image(Parcel in) {
        id = in.readString();
        owner = in.readString();
        ownersUserName = in.readString();
        secret = in.readString();
        server = in.readString();
        farm = in.readString();
        urlStringForPhone = in.readString();
        urlStringForTablet = in.readString();
        localName = in.readString();
        downloadCompleted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        downloadStarted = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }
}
