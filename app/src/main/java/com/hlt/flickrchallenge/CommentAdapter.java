package com.hlt.flickrchallenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Comment's Adapter
 * Created by parora on 8/24/15.
 */
public class CommentAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflator;

    public CommentAdapter(Context c) {
        mContext = c;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        ShowImageDetailsActivity showImageDetailsActivity = (ShowImageDetailsActivity) mContext;
        return showImageDetailsActivity.getComments().size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflator.inflate(R.layout.comment_item_layout, parent, false);
        }

        // get comment
        ShowImageDetailsActivity showImageDetailsActivity = (ShowImageDetailsActivity) mContext;
        Comment comment = showImageDetailsActivity.getComments().get(position);
        // update authorNameTextView
        TextView authorNameTextView = (TextView) convertView.findViewById(R.id.authorNameTextView);
        authorNameTextView.setText(comment.getAuthorName());
        // update contentTextView
        TextView contentTextView = (TextView) convertView.findViewById(R.id.contentTextView);
        contentTextView.setText(comment.getContent());

        return convertView;
    }
}
