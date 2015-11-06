package com.qppacket.ekalavya4G.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.qppacket.ekalavya4G.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by gorillalogic on 7/4/15.
 */
public class SelectionAdapter extends BaseAdapter {

    private List<String> mItems;
    private Context mContext;
    private int mSelection=-1;
    private boolean mVerify, mIsRight;
    private File IMG_DOWNLOAD_DIR;

    public SelectionAdapter(Context context, List<String> items) {
        super();
        mItems = items;
        mContext = context;
        IMG_DOWNLOAD_DIR = context.getExternalFilesDir(null) != null ? context.getExternalFilesDir(null) : context.getCacheDir();
    }

    public void setSelectedItemPosition(int pos) {
        mSelection = pos;
    }

    public void setVerify(boolean verify, boolean isRight) {
        mVerify = verify;
        mIsRight = isRight;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_selector, null);
            holder = new ViewHolder();
            holder.item = (TextView) view.findViewById(R.id.item);
            holder.radioButton = (ImageView) view.findViewById(R.id.radioButton);
            holder.item_image = (SubsamplingScaleImageView) view.findViewById(R.id.item_image);
            holder.item_correct = (ImageView) view.findViewById(R.id.item_correct);
            holder.item_wrong = (ImageView) view.findViewById(R.id.item_wrong);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String option = mItems.get(i);
        if (option.contains(IMG_DOWNLOAD_DIR.getAbsolutePath())) {
            Uri uri = Uri.fromFile(new File(option));
            holder.item_image.setImage(ImageSource.uri(uri));
//            Picasso.with(mContext).load(uri).into(holder.item_image);
            holder.item.setVisibility(View.GONE);
            holder.item_image.setVisibility(View.VISIBLE);
        } else {
            holder.item.setText(option);
            holder.item.setVisibility(View.VISIBLE);
            holder.item_image.setVisibility(View.GONE);
        }
        holder.radioButton.setImageResource(mSelection == i ? R.drawable.radio_button_enabled : R.drawable.radio_button_disabled);
        holder.item_correct.setVisibility(mSelection == i && mVerify && mIsRight ? View.VISIBLE : View.GONE);
        holder.item_wrong.setVisibility(mSelection == i && mVerify && !mIsRight ? View.VISIBLE : View.GONE);

        return view;
    }

    private class ViewHolder {
        TextView item;
        ImageView radioButton, item_correct, item_wrong;
        SubsamplingScaleImageView item_image;
    }
}
