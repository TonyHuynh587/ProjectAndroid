package com.skylab.donepaper.donepaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.rest.model.FileDetailItemLibrary;

import java.text.ParseException;
import java.util.List;

public class DetailLibraryAdapter extends BaseAdapter {

    private List<FileDetailItemLibrary> arrayList;
    private LayoutInflater mInflater;

    public DetailLibraryAdapter(List<FileDetailItemLibrary> arrayList, Context mContext) {
        this.arrayList = arrayList;
        this.mInflater = LayoutInflater.from(mContext);
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

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_detail_library, parent, false);
            holder = new ViewHolder();
            holder.titleText = (TextView)convertView.findViewById(R.id.lib_file_name_text);
            holder.subtitleText = (TextView)convertView.findViewById(R.id.lib_file_des_text);
            holder.dateText = (TextView)convertView.findViewById(R.id.create_date_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.titleText.setText(arrayList.get(position).getFileName());
        holder.subtitleText.setText(arrayList.get(position).getDescription());
        try {
            holder.dateText.setText(arrayList.get(position).getCreateDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView titleText, subtitleText, dateText;
    }

    public List<FileDetailItemLibrary> getArrayList() {
        return arrayList;
    }

    public void addFileItem(List<FileDetailItemLibrary> arrayDetailLibrary) {
        arrayList.addAll(arrayDetailLibrary);
        notifyDataSetChanged();
    }
}
