package com.skylab.donepaper.donepaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skylab.donepaper.donepaper.R;

import java.util.ArrayList;

public class PaperDetailsAdapter extends BaseAdapter {

    private ArrayList<String> mList;
    private LayoutInflater mInflater;
    private int mSelectedItem;

    public PaperDetailsAdapter(Context context, ArrayList<String> list, int selectedItem) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mSelectedItem = selectedItem;
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dialog_row, parent, false);
            holder = new ViewHolder();
            holder.itemName = (TextView) convertView.findViewById(R.id.dialog_row_item_text);
            holder.tickIcon = (ImageView) convertView.findViewById(R.id.dialog_row_tick_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.itemName.setText(mList.get(position));
        if (position == mSelectedItem) {
            holder.tickIcon.setVisibility(View.VISIBLE);
        } else {
            holder.tickIcon.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView itemName;
        ImageView tickIcon;
    }
}
