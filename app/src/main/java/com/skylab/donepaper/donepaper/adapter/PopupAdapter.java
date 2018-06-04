package com.skylab.donepaper.donepaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.model.PopUpItem;

import java.util.ArrayList;

public class PopupAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<PopUpItem> arrayList;

    public PopupAdapter(Context mContext, ArrayList<PopUpItem> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }



    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.row_popup, parent, false);
            holder = new ViewHolder();
            holder.titleText = (TextView)convertView.findViewById(R.id.popup_item_text);
            holder.tickImage = (ImageView)convertView.findViewById(R.id.popup_tick_icon_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (arrayList.get(position).isSelected()){
            holder.tickImage.setVisibility(View.VISIBLE);
        } else {
            holder.tickImage.setVisibility(View.GONE);
        }
        holder.titleText.setText(arrayList.get(position).getName());

        return convertView;
    }

    private class ViewHolder {
        private TextView titleText;
        private ImageView tickImage;
    }
}
