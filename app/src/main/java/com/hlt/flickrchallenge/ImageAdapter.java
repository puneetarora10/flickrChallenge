package com.hlt.flickrchallenge;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Adapter for Images' GridView
 * Created by parora on 8/24/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private File filesDir;

    public ImageAdapter(Context c, File _filesDir) {
        mContext = c;
        filesDir = _filesDir;
    }

    public int getCount() {
        MainActivity mainActivity = (MainActivity) mContext;
        return mainActivity.getImages().size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        // check if image has been downloaded
        MainActivity mainActivity = (MainActivity) mContext;
        ArrayList<Image> images = mainActivity.getImages();
        Image imageAtPosition = null;
        if (images.size() > position) {
            imageAtPosition = images.get(position);
            //Log.d("ImageAdapter","imageAtPosition"+imageAtPosition);
            if (imageAtPosition.getDownloadCompleted()) {// load image
                imageView.setImageBitmap(BitmapFactory.decodeFile(filesDir + "/" + imageAtPosition.getLocalName()));
            } else {// download image
                // loading image / placeholder
                imageView.setImageResource(R.drawable.flip_flop_loading);
                mainActivity.downloadImage(position);
            }
        }

        return imageView;
    }
}